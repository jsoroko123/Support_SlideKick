package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class SupportUser implements Serializable{
    private int UserID;
    private String UserName;

    public SupportUser(int mUserID, String mUsername) {
        UserID = mUserID;
        UserName = mUsername;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        userID = userID;
    }

    public String getUsername() {
        return UserName;
    }

    public void setUsername(String username) {
        UserName = username;
    }
}
