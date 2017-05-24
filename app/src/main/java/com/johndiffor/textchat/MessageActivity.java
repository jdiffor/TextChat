package com.johndiffor.textchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private static final String USER1_EXTRA ="USER1";
    private static final String USER2_EXTRA ="USER2";
    private static final String USER1_KEY_EXTRA ="USER1KEY";
    private static final String USER2_KEY_EXTRA ="USER2KEY";


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference chatsRef = database.getReference("Chats");
    DatabaseReference specificChatRef;

    EditText messageEditText;
    ImageButton sendMessageImageButton;

    String user1;
    String user2;
    String user1KeyString;
    String user2KeyString;

    boolean chatFound = false;

    RecyclerView recyclerView;
    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);

        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendMessageImageButton = (ImageButton) findViewById(R.id.sendMessageImageButton);

        //Get information from the previous activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        user1 = extras.getString(USER1_EXTRA);
        user2 = extras.getString(USER2_EXTRA);
        user1KeyString = extras.getString(USER1_KEY_EXTRA);
        user2KeyString = extras.getString(USER2_KEY_EXTRA);

        messageAdapter.setCurrentUser(user1);

        //Find the Chat that is shared between user1 and user2
        chatsRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                if (chat.getUser1().equals(user1) && chat.getUser2().equals(user2) || chat.getUser1().equals(user2) && chat.getUser2().equals(user1)) {
                    chatFound = true;
                    specificChatRef = database.getReference("Chats/" + dataSnapshot.getKey() + "/messages");

                    //Listen for all the messages of the shared Chat
                    specificChatRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Message message = dataSnapshot.getValue(Message.class);
                            messages.add(message);
                            messageAdapter.notifyDataSetChanged();

                            //Auto-scroll to bottom of RecyclerView to show most recent messages
                            recyclerView.scrollToPosition(messages.size() - 1);
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

        //Send message when send button is clicked
        sendMessageImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(messageEditText.getText().toString())) {
                    String text = messageEditText.getText().toString();
                    String sender = user1;
                    Message newMessage = new Message(text, sender);

                    //If the Chat exists, simply send the new Message
                    if (chatFound) {
                        specificChatRef.push().setValue(newMessage);

                    //Otherwise, create a new Chat and send the Message to the new Chat
                    } else {
                        Chat newChat = new Chat(user1, user2, newMessage);
                        String key = chatsRef.push().getKey();
                        chatsRef.child(key).setValue(newChat);

                        specificChatRef = database.getReference("Chats/" + key + "/messages");
                        chatFound = true;

                        //Add Users to each others chats list
                        DatabaseReference user1Key = database.getReference("Users/" + user1KeyString + "/chats");
                        user1Key.push().setValue(user2);

                        DatabaseReference user2Key = database.getReference("Users/" + user2KeyString + "/chats");
                        user2Key.push().setValue(user1);
                    }
                    messageEditText.setText("");
                }
            }
        });
    }
}
