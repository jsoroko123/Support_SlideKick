package com.support.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jeffery on 1/13/2016.
 */
public class Client implements Serializable {

    private int ClientID;
    private String ClientName;
    private String ClientAddress;
    private String ClientCity;
    private String ClientEmail;
    private String ClientPhone;
    private String ClientState;
    private String ClientZip;
    private boolean IndividualPassword;
    private String Password;
    private boolean ActiveInd;

    private ArrayList<UserInfo> children = new ArrayList<>();

    public Client(int mCLientID, String mClientName, String mAddress, String mCity, String mState, String mZip, String mEmail, String mPassword, String mPhone, boolean mIndividualPassword, ArrayList<UserInfo> childList){
        this.ClientID = mCLientID;
        this.ClientName = mClientName;
        this.ClientAddress = mAddress;
        this.ClientCity = mCity;
        this.ClientState = mState;
        this.ClientZip = mZip;
        this.ClientEmail = mEmail;
        this.Password = mPassword;
        this.ClientPhone = mPhone;
        this.IndividualPassword = mIndividualPassword;
        this.children = childList;
    }

    public Client() {

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

    public String getClientAddress() {
        return ClientAddress;
    }

    public void setClientAddress(String clientAddress) {
        ClientAddress = clientAddress;
    }

    public String getClientCity() {
        return ClientCity;
    }

    public void setClientCity(String clientCity) {
        ClientCity = clientCity;
    }

    public String getClientEmail() {
        return ClientEmail;
    }

    public void setClientEmail(String clientEmail) {
        ClientEmail = clientEmail;
    }

    public String getClientPhone() {
        return ClientPhone;
    }

    public void setClientPhone(String clientPhone) {
        ClientPhone = clientPhone;
    }

    public String getClientState() {
        return ClientState;
    }

    public void setClientState(String clientState) {
        ClientState = clientState;
    }

    public String getClientZip() {
        return ClientZip;
    }

    public void setClientZip(String clientZip) {
        ClientZip = clientZip;
    }

    public boolean isIndividualPassword() {
        return IndividualPassword;
    }

    public void setIndividualPassword(boolean individualPassword) {
        IndividualPassword = individualPassword;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isActiveInd() {
        return ActiveInd;
    }

    public void setActiveInd(boolean activeInd) {
        ActiveInd = activeInd;
    }

    public ArrayList<UserInfo> getChildList() {
        return children;
    }
    public void setChildList(ArrayList<UserInfo> childList) {
        this.children = childList;
    }
}
