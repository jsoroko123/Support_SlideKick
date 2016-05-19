package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class ClientDrop implements Serializable{
    private int ClientID;
    private String ClientName;

    public ClientDrop(int mClientID, String mClientName) {
        ClientID = mClientID;
        ClientName = mClientName;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }
}
