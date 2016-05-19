package com.support.objects;

import java.io.Serializable;

public class Details implements Serializable {
	
	private int CaseID;
	private String CaseSeverityDesc;
	private String CaseStatusDesc;
	private String ClientAddress;
	private String ClientCity;
	private String ClientEmail;
	private String ClientErrorsExist;
	private int ClientID;
	private String ClientName;
	private String ClientPhone;
	private String ClientState;
	private String ClientZip;
	private String CommentTitle;
	private String Email;
	private String Name;
	private String Phone;
	private String Response;
	private int ResponseID;
	private int supportuserid;
	private String SupportUserName;
	private int UserID;

	public int getCaseID() {
		return CaseID;
	}

	public void setCaseID(int caseID) {
		CaseID = caseID;
	}

	public String getCaseSeverityDesc() {
		return CaseSeverityDesc;
	}

	public void setCaseSeverityDesc(String caseSeverityDesc) {
		CaseSeverityDesc = caseSeverityDesc;
	}

	public String getCaseStatusDesc() {
		return CaseStatusDesc;
	}

	public void setCaseStatusDesc(String caseStatusDesc) {
		CaseStatusDesc = caseStatusDesc;
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

	public String getClientErrorsExist() {
		return ClientErrorsExist;
	}

	public void setClientErrorsExist(String clientErrorsExist) {
		ClientErrorsExist = clientErrorsExist;
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

	public String getCommentTitle() {
		return CommentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		CommentTitle = commentTitle;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getResponse() {
		return Response;
	}

	public void setResponse(String response) {
		Response = response;
	}

	public int getResponseID() {
		return ResponseID;
	}

	public void setResponseID(int responseID) {
		ResponseID = responseID;
	}

	public int getSupportuserid() {
		return supportuserid;
	}

	public void setSupportuserid(int supportuserid) {
		this.supportuserid = supportuserid;
	}

	public String getSupportUserName() {
		return SupportUserName;
	}

	public void setSupportUserName(String supportUserName) {
		SupportUserName = supportUserName;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}
}
