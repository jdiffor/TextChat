package com.johndiffor.textchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserHomeActivity extends AppCompatActivity {

    private static final String DISPLAY_NAME_EXTRA = "DISPLAYNAME";
    private static final String USER_KEY_EXTRA = "USERKEY";
    private static final String TAG = "EmailPassword";
    private static final String HELLO = "Hello, ";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Users");
    DatabaseReference specificUserRef;

    private TextView helloTextView;
    private Button logoutButton;
    private ImageButton searchButton;

    User currentUser;
    String userKey;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    RecyclerView recyclerView;
    ArrayList<String> userList = new ArrayList<>();
    ChatsAdapter chatsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        recyclerView = (RecyclerView) findViewById(R.id.chatsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        chatsAdapter = new ChatsAdapter(userList);
        recyclerView.setAdapter(chatsAdapter);

        helloTextView = (TextView) findViewById(R.id.userHelloTextView);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        searchButton = (ImageButton) findViewById(R.id.searchImageButton);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {

                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    final String userUID = user.getUid().toString();

                    //Find the user from the database whose UID matches that of the logged in user
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //Loop through users to find match
                            for(DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                                User user1 = postDataSnapshot.getValue(User.class);

                                //When found, set currentUser to that user
                                if(user1.getUid().equals(userUID)) {

                                    currentUser = user1;
                                    chatsAdapter.setCurrentUser(currentUser.getDisplayName());

                                    specificUserRef = database.getReference("Users/" + postDataSnapshot.getKey() + "/chats");
                                    userKey = postDataSnapshot.getKey();

                                    userList.clear();

                                    //Find all the people that the current user has messages with
                                    specificUserRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            if(!dataSnapshot.getKey().equals("initial")) {
                                                userList.add(dataSnapshot.getValue().toString());
                                                chatsAdapter.notifyDataSetChanged();
                                            }

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                            helloTextView.setText(HELLO + currentUser.getDisplayName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        //Add functionality for buttons
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, SearchActivity.class);
                intent.putExtra(DISPLAY_NAME_EXTRA, currentUser.getDisplayName());
                intent.putExtra(USER_KEY_EXTRA, userKey);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signOut() {
        mAuth.signOut();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    //If user is signed out, go back to login page
    private void updateUI(FirebaseUser user) {
        if(user == null) {
            Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}
