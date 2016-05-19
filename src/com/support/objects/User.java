package com.support.objects;


import java.io.Serializable;

public class User implements Serializable{

	private String ClientName;
	private int ClientID;
	private int CompanyID;
	private String CompanyName;
	private String UserName;
	private String Password;
	private boolean SupportAdmin;
	private int ID;
	private boolean Administrator;
	private String Email;
	private boolean SuperUser;
	private int CommentTypeID;
	private int CaseStatusID;
	private boolean CaseApproval;
	private boolean ShowErrors;
	private String FullName;




	public User(String clientName, int clientID, int companyID, String companyName, String userName,
					String password, boolean supportAdmin, int ID, boolean administrator, String email,
						boolean superUser, int commentTypeID, int caseStatusID, boolean caseApproval, boolean mShowErrors) {
		ClientName = clientName;
		ClientID = clientID;
		CompanyID = companyID;
		CompanyName = companyName;
		UserName = userName;
		Password = password;
		SupportAdmin = supportAdmin;
		this.ID = ID;
		Administrator = administrator;
		Email = email;
		SuperUser = superUser;
		CommentTypeID = commentTypeID;
		CaseStatusID = caseStatusID;
		CaseApproval = caseApproval;
		ShowErrors = mShowErrors;
	}

	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}

	public int getClientID() {
		return ClientID;
	}

	public void setClientID(int clientID) {
		ClientID = clientID;
	}

	public int getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getUsername() {return UserName;}

	public void setUsername(String username) {
		UserName = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public boolean isSupportAdmin() {
		return SupportAdmin;
	}

	public void setSupportAdmin(boolean supportAdmin) {
		SupportAdmin = supportAdmin;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public boolean isAdministrator() {
		return Administrator;
	}

	public void setAdministrator(boolean administrator) {
		Administrator = administrator;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public boolean isSuperUser() {
		return SuperUser;
	}

	public void setSuperUser(boolean superUser) {
		SuperUser = superUser;
	}

	public int getCommentTypeID() {
		return CommentTypeID;
	}

	public void setCommentTypeID(int topCommentTypeID) {
		CommentTypeID = topCommentTypeID;
	}

	public int getCaseStatusID() {
		return CaseStatusID;
	}

	public void setCaseStatusID(int caseStatusID) {
		CaseStatusID = caseStatusID;
	}

	public boolean isCaseApproval() {
		return CaseApproval;
	}

	public void setCaseApproval(boolean caseApproval) {
		CaseApproval = caseApproval;
	}

	public boolean isShowErrors() {
		return ShowErrors;
	}

	public void setShowErrors(boolean showErrors) {
		ShowErrors = showErrors;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}
}
