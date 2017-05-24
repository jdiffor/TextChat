package com.johndiffor.textchat;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by johndiffor on 4/25/17.
 *
 * Adapter for the RecyclerView that holds the Messages that have been sent between two users
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> messages;
    String currentUser;

    public MessageAdapter(ArrayList<Message> messages) { this.messages = messages; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View messageListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(messageListItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());
        holder.senderTextView.setText(message.getSender());

        //Change layout based on who sent the Message
        if(currentUser.equals(message.getSender())) {
            holder.messageTextView.setGravity(Gravity.RIGHT);
            holder.senderTextView.setGravity(Gravity.RIGHT);
        } else {
            holder.messageTextView.setGravity(Gravity.LEFT);
            holder.senderTextView.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTextView;
        public TextView senderTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            senderTextView = (TextView) itemView.findViewById(R.id.senderTextView);
        }
    }

    /**
     * Called from an activity to let the adapter know who the logged in User is
     *
     * @param currentUser
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
