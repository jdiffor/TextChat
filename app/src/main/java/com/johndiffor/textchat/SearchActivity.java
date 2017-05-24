package com.johndiffor.textchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String DISPLAY_NAME_EXTRA = "DISPLAYNAME";
    private static final String USER_KEY_EXTRA = "USERKEY";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Users");

    EditText searchField;
    ImageButton searchButton;
    String currentUser;
    String currentUserKey;

    RecyclerView recyclerView;
    ArrayList<User> usersList = new ArrayList<>();
    SearchAdapter searchAdapter;

    ArrayList<String> userKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        searchAdapter = new SearchAdapter(usersList, userKeys);
        recyclerView.setAdapter(searchAdapter);

        searchField = (EditText) findViewById(R.id.searchField);
        searchButton = (ImageButton) findViewById(R.id.searchButton);

        //Get information from previous activity
        Intent intent = getIntent();
        currentUser = intent.getStringExtra(DISPLAY_NAME_EXTRA);
        currentUserKey = intent.getStringExtra(USER_KEY_EXTRA);

        //Let the adapter know what user is logged in and where to find them in the database
        searchAdapter.setCurrentUser(currentUser);
        searchAdapter.setUser1Key(currentUserKey);

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //If the logged in User has searched for something
                if (!TextUtils.isEmpty(searchField.getText().toString())) {

                    usersList.clear();

                    final String searchTerm = searchField.getText().toString();

                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                                User user1 = postDataSnapshot.getValue(User.class);

                                //If a user contains the search term, add them to the list
                                //That is, as long as it's not the currently logged in user
                                if (user1.getDisplayName().toLowerCase().contains(searchTerm.toLowerCase()) && !user1.getDisplayName().equals(currentUser)) {
                                    usersList.add(user1);
                                    userKeys.add(postDataSnapshot.getKey());
                                    Log.d("SEARCH", "User added " + user1.getDisplayName());
                                }
                            }
                            searchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
