package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class UserDrop implements Serializable{
    private int userID;
    private String Username;

    public UserDrop(int mUserID, String mUsername) {
        userID = mUserID;
        Username = mUsername;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        userID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
