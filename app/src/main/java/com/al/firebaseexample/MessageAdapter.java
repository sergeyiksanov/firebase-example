package com.al.firebaseexample;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;

    @SuppressLint("NewApi")
    public MessageAdapter(List<Message> messages) {
        this.messages = messages.reversed();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.content);
        holder.textViewAuthor.setText(message.author);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @SuppressLint({"NotifyDataSetChanged", "NewApi"})
    public void setMessages(ArrayList<Message> newMessages) {
        this.messages = newMessages.reversed();
        notifyDataSetChanged();
    }
}
