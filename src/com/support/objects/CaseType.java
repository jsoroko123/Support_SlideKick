package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class CaseType implements Serializable{
    private String CommentTypeDesc;
    private int CommentTypeID;

    public CaseType(int mCommentTypeID, String mCommentTypeDesc) {
        CommentTypeID = mCommentTypeID;
        CommentTypeDesc = mCommentTypeDesc;
    }

    public String getCommentTypeDesc() {
        return CommentTypeDesc;
    }

    public void setCommentTypeDesc(String commentTypeDesc) {
        CommentTypeDesc = commentTypeDesc;
    }

    public int getCommentTypeID() {
        return CommentTypeID;
    }

    public void setCommentTypeID(int commentTypeID) {
        CommentTypeID = commentTypeID;
    }
}
