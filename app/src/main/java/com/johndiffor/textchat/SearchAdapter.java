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
 * Adapter for the RecyclerView that holds the users that contain the search term in their
 * display name
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String USER1_EXTRA ="USER1";
    private static final String USER2_EXTRA ="USER2";
    private static final String USER1_KEY_EXTRA ="USER1KEY";
    private static final String USER2_KEY_EXTRA ="USER2KEY";

    ArrayList<User> users;
    ArrayList<String> keys;
    String currentUser;
    String user1Key;

    public SearchAdapter(ArrayList<User> users, ArrayList<String> keys) {
        this.users = users;
        this.keys = keys;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View userListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(userListItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = users.get(position);
        holder.userTextView.setText(user.getDisplayName());

        //When a user is clicked, go to the Chat between that user sand the logged in user
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MessageActivity.class);

                Bundle extras = new Bundle();
                extras.putString(USER1_EXTRA, currentUser);
                extras.putString(USER2_EXTRA, holder.userTextView.getText().toString());
                extras.putString(USER1_KEY_EXTRA, user1Key);
                extras.putString(USER2_KEY_EXTRA, keys.get(position));
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
            userTextView = (TextView) itemView.findViewById(R.id.searchItemTextView);

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

    /**
     * Called from an activity to let the adapter know where to find the logged in user
     * in the database
     *
     * @param user1Key
     */
    public void setUser1Key(String user1Key) {
        this.user1Key = user1Key;
    }
}
