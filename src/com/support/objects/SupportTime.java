package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 4/26/2016.
 */
public class SupportTime implements Serializable {
    private String ClientName;
    private int CommentID;
    private String BeginTime;
    private String EndTime;
    private int Duration;

    public SupportTime(String clientName, int commentID, String beginTime, String endTime, int duration) {
        ClientName = clientName;
        CommentID = commentID;
        BeginTime = beginTime;
        EndTime = endTime;
        Duration = duration;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public int getCommentID() {
        return CommentID;
    }

    public void setCommentID(int commentID) {
        CommentID = commentID;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }
}
