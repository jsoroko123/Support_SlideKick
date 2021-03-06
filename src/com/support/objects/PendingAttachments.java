package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 8/26/2015.
 */
public class PendingAttachments implements Serializable {
    private int PendingAttachmentID;
    private int CommentID;
    private int CompanyID;
    private String dt_Created;
    private String Path;
    private String tempCommentID;
    private int UserID;

    public PendingAttachments(int pendingAttachmentID, int commentID, int companyID, String dt_Created, String path, String tempCommentID, int userID) {
        PendingAttachmentID = pendingAttachmentID;
        CommentID = commentID;
        CompanyID = companyID;
        this.dt_Created = dt_Created;
        Path = path;
        this.tempCommentID = tempCommentID;
        UserID = userID;
    }

    public int getAttachmentID() {
        return PendingAttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        PendingAttachmentID = attachmentID;
    }

    public int getCommentID() {
        return CommentID;
    }

    public void setCommentID(int commentID) {
        CommentID = commentID;
    }

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public String getDt_Created() {
        return dt_Created;
    }

    public void setDt_Created(String dt_Created) {
        this.dt_Created = dt_Created;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getTempCommentID() {
        return tempCommentID;
    }

    public void setTempCommentID(String tempCommentID) {
        this.tempCommentID = tempCommentID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }
}
