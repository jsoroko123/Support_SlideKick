package com.support.objects;


import java.io.Serializable;

public class PendingCases implements Serializable {

	private int PendingCommentID;
	private String Username;
	private String CommentTitle;
	private String Comments;
	private int CaseSeverityID;
	private String CaseSeverityDesc;
	private int CaseReasonID;
	private String ReasonDesc;
	private int CommentTypeID;
	private String CommentTypeDesc;
	private String DateCreated;
	private boolean HasAttachment;
	private String Color;

	public PendingCases(int pendingCommentID, String username, String commentTitle, String comments, int caseSeverityID,
							String caseSeverityDesc, int caseReasonID, String reasonDesc, int commentTypeID, String commentTypeDesc,
								String dateCreated, boolean hasAttachment, String color) {
		PendingCommentID = pendingCommentID;
		Username = username;
		CommentTitle = commentTitle;
		Comments = comments;
		CaseSeverityID = caseSeverityID;
		CaseSeverityDesc = caseSeverityDesc;
		CaseReasonID = caseReasonID;
		ReasonDesc = reasonDesc;
		CommentTypeID = commentTypeID;
		CommentTypeDesc = commentTypeDesc;
		DateCreated = dateCreated;
		HasAttachment = hasAttachment;
		Color = color;
	}

	public int getPendingCommentID() {
		return PendingCommentID;
	}

	public void setPendingCommentID(int pendingCommentID) {
		PendingCommentID = pendingCommentID;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getCommentTitle() {
		return CommentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		CommentTitle = commentTitle;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}


	public String getCaseSeverityDesc() {
		return CaseSeverityDesc;
	}

	public void setCaseSeverityDesc(String caseSeverityDesc) {
		CaseSeverityDesc = caseSeverityDesc;
	}

	public int getCaseReasonID() {
		return CaseReasonID;
	}

	public void setCaseReasonID(int caseReasonID) {
		CaseReasonID = caseReasonID;
	}

	public String getReasonDesc() {
		return ReasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		ReasonDesc = reasonDesc;
	}

	public String getDateCreated() {
		return DateCreated;
	}

	public void setDateCreated(String dateCreated) {
		DateCreated = dateCreated;
	}

	public boolean isHasAttachment() {
		return HasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		HasAttachment = hasAttachment;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		Color = color;
	}

	public int getCaseSeverityID() {
		return CaseSeverityID;
	}

	public void setCaseSeverityID(int caseSeverityID) {
		CaseSeverityID = caseSeverityID;
	}

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
}

