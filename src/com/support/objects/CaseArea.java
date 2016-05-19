package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class CaseArea implements Serializable{
    private int CaseReasonID;
    private int Priority;
    private String ReasonDesc;
    private int CommentTypeID;

    public CaseArea(int mCaseReasonID, String mReasonDesc) {
        CaseReasonID = mCaseReasonID;
        ReasonDesc = mReasonDesc;
    }

    public int getCaseReasonID() {
        return CaseReasonID;
    }

    public void setCaseReasonID(int caseReasonID) {
        CaseReasonID = caseReasonID;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public String getReasonDesc() {
        return ReasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        ReasonDesc = reasonDesc;
    }

    public int getCommentTypeID() {
        return CommentTypeID;
    }

    public void setCommentTypeID(int commentTypeID) {
        CommentTypeID = commentTypeID;
    }
}
