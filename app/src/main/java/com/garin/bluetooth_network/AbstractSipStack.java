package com.garin.bluetooth_network;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import io.github.cdimascio.dotenv.Dotenv;

public class AbstractSipStack {
    protected void sipSend(String sipData)
    {
        new Thread(new Runnable() {
            private static final String TAG = "[AbstractSipStack]";
            @Override
            public void run() {
                String sipHost = getSipHost();

                try {
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress address = InetAddress.getByName(sipHost);
                    Log.i(TAG, "SIP host:");
                    Log.i(TAG, String.valueOf(address));
                    byte[] buffer = sipData.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, getSipPort());

                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    protected String getSipHost()
    {
        Dotenv dotenv = Dotenv.configure()
                .directory("/assets")
                .filename("env")
                .load();

        return dotenv.get("SIP_HOST");

    }

    protected int getSipPort()
    {
        Dotenv dotenv = Dotenv.configure()
                .directory("/assets")
                .filename("env")
                .load();

        return Integer.parseInt(dotenv.get("SIP_PORT"));
    }


}
