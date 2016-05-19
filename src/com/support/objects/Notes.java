package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 3/11/2016.
 */
public class Notes implements Serializable{

    private int CaseReminderID;
    private String Reminder;
    private int UserID;
    private String DateCreated;


    public int getCaseReminderID() {
        return CaseReminderID;
    }

    public void setCaseReminderID(int caseReminderID) {
        CaseReminderID = caseReminderID;
    }

    public String getReminder() {
        return Reminder;
    }

    public void setReminder(String reminder) {
        Reminder = reminder;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }



}
