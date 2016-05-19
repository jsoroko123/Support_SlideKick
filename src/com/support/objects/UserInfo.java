package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 3/15/2016.
 */
public class UserInfo implements Serializable {
    private int UserID;
    private String UserName;
    private String LastName;
    private String FirstName;
    private String Email;
    private String Phone;
    private String Extension;
    private String CellPhone;
    private int ClientID;
    private boolean ClientAdmin;
    private boolean ActiveInd;
    private boolean EmailErrors;
    private boolean ReadAcknowledgement;
    private String UserPassword;
    private boolean CaseApproval;
    private boolean SupportAdmin;
    private boolean SuperUser;

    public UserInfo(int userID, String userName, String lastName, String firstName, String email, String phone,
                        String extension, String cellPhone, int clientID, boolean clientAdmin, boolean activeInd,
                            boolean emailErrors, boolean readAcknowledgement, String userPassword, boolean caseApproval,
                                boolean supportAdmin, boolean superUser) {
        UserID = userID;
        UserName = userName;
        LastName = lastName;
        FirstName = firstName;
        Email = email;
        Phone = phone;
        Extension = extension;
        CellPhone = cellPhone;
        ClientID = clientID;
        ClientAdmin = clientAdmin;
        ActiveInd = activeInd;
        EmailErrors = emailErrors;
        ReadAcknowledgement = readAcknowledgement;
        UserPassword = userPassword;
        CaseApproval = caseApproval;
        SupportAdmin = supportAdmin;
        SuperUser = superUser;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getExtension() {
        return Extension;
    }

    public void setExtension(String extension) {
        Extension = extension;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public int getClientID() {
        return ClientID;
    }

    public void setClientID(int clientID) {
        ClientID = clientID;
    }

    public boolean isClientAdmin() {
        return ClientAdmin;
    }

    public void setClientAdmin(boolean clientAdmin) {
        ClientAdmin = clientAdmin;
    }

    public boolean isActiveInd() {
        return ActiveInd;
    }

    public void setActiveInd(boolean activeInd) {
        ActiveInd = activeInd;
    }

    public boolean isEmailErrors() {
        return EmailErrors;
    }

    public void setEmailErrors(boolean emailErrors) {
        EmailErrors = emailErrors;
    }

    public boolean isReadAcknowledgement() {
        return ReadAcknowledgement;
    }

    public void setReadAcknowledgement(boolean readAcknowledgement) {
        ReadAcknowledgement = readAcknowledgement;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public boolean isCaseApproval() {
        return CaseApproval;
    }

    public void setCaseApproval(boolean caseApproval) {
        CaseApproval = caseApproval;
    }

    public boolean isSupportAdmin() {
        return SupportAdmin;
    }

    public void setSupportAdmin(boolean supportAdmin) {
        SupportAdmin = supportAdmin;
    }

    public boolean isSuperUser() {
        return SuperUser;
    }

    public void setSuperUser(boolean superUser) {
        SuperUser = superUser;
    }
}
