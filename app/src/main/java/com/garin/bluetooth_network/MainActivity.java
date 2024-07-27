package com.garin.bluetooth_network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.garin.bluetooth_network.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private EditText editTextMessage;
    private TextView viewTextMessage;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            this.showNoBluetoothSupportMessage();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                // For Android versions prior to Marshmallow (API level 23), permissions are granted at install time
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    // For Android Marshmallow (API level 23) and above, check and request permissions at runtime
                    if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT},
                                REQUEST_BLUETOOTH_PERMISSION);
                    } else {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
        }


        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        editTextMessage = findViewById(R.id.textview_first);

        binding.fab.setOnClickListener(view -> {
            Snackbar.make(view, "Sending SIP MESSAGE...", Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();

            sendSaveAndDisplay();

            editTextMessage.setText("");
        });

        this.registerReceiver(BroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                this.showBluetoothIsEnabledMessage();
            } else {
                this.showBluetoothDeniedMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void showNoBluetoothSupportMessage() {
        Toast.makeText(
                getApplicationContext(),
                "This device doesn't support Bluetooth! Messaging capabilities will be limited.",
                Toast.LENGTH_LONG
        ).show();
    }

    protected void showBluetoothDeniedMessage() {
        Toast.makeText(
                getApplicationContext(),
                "Bluetooth access denied. Messaging capabilities will be limited.",
                Toast.LENGTH_LONG
        ).show();
    }

    protected void showBluetoothIsEnabledMessage() {
        Toast.makeText(
                getApplicationContext(),
                "Bluetooth access has been granted.",
                Toast.LENGTH_LONG
        ).show();
    }

    protected void sendSaveAndDisplay()
    {
        String message = editTextMessage.getText().toString();

        this.saveSipMessage(message);
        this.sendSipMessage(message);
        this.displayMessages();
    }

    protected void saveSipMessage(String message)
    {
        new Thread(() -> {
            MessagesDatabase db = Room.databaseBuilder(
                    getApplicationContext(),
                    MessagesDatabase.class,
                    "messages-db"
            ).build();

            MessagesDao messagesDao = db.messagesDao();

            MessagesEntity newMessage = new MessagesEntity();
            newMessage.text = message;
            messagesDao.insert(newMessage);
        }).start();
    }

    protected void displayMessages() {
        new Thread(() -> {
            try {
                this.populateMessagesList();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching messages: " + e.getMessage());
            }
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void populateMessagesList()
    {
        MessagesDatabase db = Room.databaseBuilder(
                getApplicationContext(),
                MessagesDatabase.class,
                "messages-db"
        ).build();

        MessagesDao messagesDao = db.messagesDao();
        RecyclerView recyclerView = findViewById(R.id.view_messages);

        List<Message> messageList = messagesDao.getAllMessages();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MessageAdapter adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        for (Message message : messageList) {
            Log.e(TAG, "message:" + message.getText());
        }
        Log.e(TAG, "adapter:" + adapter.getItemCount());

        //adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(0);

    }

    protected void sendSipMessage(String message)
    {
        SipMessage sipMessage = new SipMessage();

        sipMessage.send(message);
    }

    private final BroadcastReceiver BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            String action = intent.getAction();

            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    this.showDebug();
                    if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                                REQUEST_BLUETOOTH_PERMISSION);
                    } else {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }

            }
        }

        private void showDebug() {
            Toast.makeText(
                    getApplicationContext(),
                    "Debug",
                    Toast.LENGTH_LONG
            ).show();
        }
    };

}