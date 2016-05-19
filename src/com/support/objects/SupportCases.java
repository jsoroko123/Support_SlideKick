package com.support.objects;

import java.io.Serializable;



public class SupportCases implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3613343224583678170L;

	private String CommentID;
	private String ClientName;
	private String UserName;
	private String Email;
	private String PHONE;
	private String CaseStatusDesc;
	private int CaseStatusID;
	private String dt_created;
	private String CaseSeverityDesc;
	private String Issue;
	private int SupportUserID;
	private String CommentTitle;
	private String ResponseStatus;
	private String SupportUserName;
	private String CLIENTREPLIED;
	private String HasAttachment;
	private String HasNotes;
	private String PHONECOMBO;
	private String ReasonDesc;
	private String Color;
	private String CommentTypeDesc;
	private String CUser;
	private String WorkingCase;

	public String getSUser() {
		return SUser;
	}

	public void setSUser(String SUser) {
		this.SUser = SUser;
	}

	public String getCUser() {
		return CUser;
	}

	public void setCUser(String CUser) {
		this.CUser = CUser;
	}

	private String SUser;

	public String getCommentTypeDesc() {
		return CommentTypeDesc;
	}

	public void setCommentTypeDesc(String commentTypeDesc) {
		CommentTypeDesc = commentTypeDesc;
	}

	public String getCommentID() {
		return CommentID;
	}

	public void setCommentID(String commentID) {
		CommentID = commentID;
	}

	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}

	public String getUsername() {
		return UserName;
	}

	public void setUsername(String username) {
		UserName = username;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String PHONE) {
		this.PHONE = PHONE;
	}

	public String getCaseStatusDesc() {
		return CaseStatusDesc;
	}

	public void setCaseStatusDesc(String caseStatusDesc) {
		CaseStatusDesc = caseStatusDesc;
	}

	public int getCaseStatusID() {
		return CaseStatusID;
	}

	public void setCaseStatusID(int caseStatusID) {
		CaseStatusID = caseStatusID;
	}

	public String getDt_created() {
		return dt_created;
	}

	public void setDt_created(String dt_created) {
		this.dt_created = dt_created;
	}

	public String getCaseSeverityDesc() {
		return CaseSeverityDesc;
	}

	public void setCaseSeverityDesc(String caseSeverityDesc) {
		CaseSeverityDesc = caseSeverityDesc;
	}

	public String getIssue() {
		return Issue;
	}

	public void setIssue(String issue) {
		Issue = issue;
	}

	public int getSupportUserID() {
		return SupportUserID;
	}

	public void setSupportUserID(int supportUserID) {
		SupportUserID = supportUserID;
	}

	public String getCommentTitle() {
		return CommentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		CommentTitle = commentTitle;
	}

	public String getResponseStatus() {
		return ResponseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		ResponseStatus = responseStatus;
	}

	public String getSupportUserName() {
		return SupportUserName;
	}

	public void setSupportUserName(String supportUserName) {
		SupportUserName = supportUserName;
	}

	public String getCLIENTREPLIED() {
		return CLIENTREPLIED;
	}

	public void setCLIENTREPLIED(String CLIENTREPLIED) {
		this.CLIENTREPLIED = CLIENTREPLIED;
	}

	public String getHasAttachment() {
		return HasAttachment;
	}

	public void setHasAttachment(String hasAttachment) {
		HasAttachment = hasAttachment;
	}

	public String getHasNotes() {
		return HasNotes;
	}

	public void setHasNotes(String hasNotes) {
		HasNotes = hasNotes;
	}

	public String getPHONECOMBO() {
		return PHONECOMBO;
	}

	public void setPHONECOMBO(String PHONECOMBO) {
		this.PHONECOMBO = PHONECOMBO;
	}

	public String getReasonDesc() {
		return ReasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		ReasonDesc = reasonDesc;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		Color = color;
	}

	public String getWorkingCase() {
		return WorkingCase;
	}

	public void setWorkingCase(String workingCase) {
		WorkingCase = workingCase;
	}
}

