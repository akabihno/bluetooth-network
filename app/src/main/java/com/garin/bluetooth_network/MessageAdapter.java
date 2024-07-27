package com.garin.bluetooth_network;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;

        MessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
        }

        void bind(Message message) {
            textViewMessage.setText(message.getText());
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

