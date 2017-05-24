package com.johndiffor.textchat;

import android.os.Parcel;

import java.util.HashMap;

/**
 * Created by johndiffor on 4/25/17.
 */

public class User {

    private static final String DEFAULT_KEY = "initial";
    private static final String DEFAULT_VALUE = "none";

    private String displayName;
    private String uid;
    private HashMap<String, String> chats;

    public User() {

    }

    public User(String displayName, String uid) {
        this.displayName = displayName;
        this.uid = uid;
        chats = new HashMap<>();
        chats.put(DEFAULT_KEY, DEFAULT_VALUE);
    }

    public User(Parcel in) {
        displayName = in.readString();
        uid = in.readString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    public HashMap<String, String> getChats() {
        return chats;
    }
}
