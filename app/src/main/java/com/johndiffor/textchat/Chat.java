package com.johndiffor.textchat;

import java.util.HashMap;

/**
 * Created by johndiffor on 4/30/17.
 *
 * This class contains the interaction between 2 users and the messages they send to each other
 */

public class Chat {

    private String user1;
    private String user2;
    private HashMap<String,Message> messages;

    /**
     * Empty constructor for Chat (necessary - don't delete)
     */
    public Chat() {

    }

    /**
     * Basic constructor for Chat
     *
     * @param user1 One of the users in the message interaction
     * @param user2 The other user in the message interaction
     * @param initialMessage First message to be sent from one user to the other
     */
    public Chat(String user1, String user2, Message initialMessage) {
        this.user1 = user1;
        this.user2 = user2;
        messages = new HashMap<>();
        messages.put("-J", initialMessage);
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }
}
