package com.johndiffor.textchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by johndiffor on 4/25/17.
 *
 * Adapter for the RecyclerView that holds the users that have already been initiated
 * in a Chat with the logged in User
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private static final String USER1_EXTRA = "USER1";
    private static final String USER2_EXTRA = "USER2";

    ArrayList<String> users;
    String currentUser;

    public ChatsAdapter(ArrayList<String> users) { this.users = users; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View userListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(userListItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String user = users.get(position);
        holder.userTextView.setText(user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //When an item is clicked, go to the Chat with the two Users
                Intent intent = new Intent(view.getContext(), MessageActivity.class);

                Bundle extras = new Bundle();
                extras.putString(USER1_EXTRA, currentUser);
                extras.putString(USER2_EXTRA, holder.userTextView.getText().toString());
                intent.putExtras(extras);

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            userTextView = (TextView) itemView.findViewById(R.id.chatItemTextView);

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
