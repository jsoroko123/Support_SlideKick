package com.support.objects;

/**
 * Created by Jeffery on 3/26/2016.
 */
public class UserDepartment {

    private int CommentTypeID;
    private String CommentTypeDesc;
    private int IsActive;

    public int getCommentTypeID() {
        return CommentTypeID;
    }

    public void setCommentTypeID(int commentTypeID) {
        CommentTypeID = commentTypeID;
    }

    public String getCommentTypeDesc() {
        return CommentTypeDesc;
    }

    public void setCommentTypeDesc(String commentTypeDesc) {
        CommentTypeDesc = commentTypeDesc;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }
}
