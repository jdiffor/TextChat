package com.johndiffor.textchat;

/**
 * Created by johndiffor on 4/25/17.
 */

public class Message {

    private String message;
    private String sender;

    /**
     * Empty constructor for Message (necessary - don't delete)
     */
    public Message() {

    }

    /**
     * Basic constructor for Message
     *
     * @param message Message to be sent
     * @param sender User sending the message
     */
    public Message(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
