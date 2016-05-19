package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class CaseStatus implements Serializable{
    private int CaseStatusID;
    private String CaseStatusDesc;
    private int ClientCaseStatusID;
    private String ClientCaseStatusDesc;
    private int DefaultInd;
    public CaseStatus(int caseStatusID, String caseStatusDesc, int clientCaseStatusID, String clientCaseStatusDesc) {
        CaseStatusID = caseStatusID;
        CaseStatusDesc = caseStatusDesc;
        ClientCaseStatusID = clientCaseStatusID;
        ClientCaseStatusDesc = clientCaseStatusDesc;
    }

    public CaseStatus(int caseStatusID, String caseStatusDesc) {
        CaseStatusID = caseStatusID;
        CaseStatusDesc = caseStatusDesc;
    }

    public int getCaseStatusID() {
        return CaseStatusID;
    }

    public void setCaseStatusID(int caseStatusID) {
        CaseStatusID = caseStatusID;
    }

    public String getCaseStatusDesc() {
        return CaseStatusDesc;
    }

    public void setCaseStatusDesc(String caseStatusDesc) {
        CaseStatusDesc = caseStatusDesc;
    }

    public int getClientCaseStatusID() {
        return ClientCaseStatusID;
    }

    public void setClientCaseStatusID(int clientCaseStatusID) {
        ClientCaseStatusID = clientCaseStatusID;
    }

    public String getClientCaseStatusDesc() {
        return ClientCaseStatusDesc;
    }

    public void setClientCaseStatusDesc(String clientCaseStatusDesc) {
        ClientCaseStatusDesc = clientCaseStatusDesc;
    }

    public int getDefaultInd() {
        return DefaultInd;
    }

    public void setDefaultInd(int defaultInd) {
        DefaultInd = defaultInd;
    }
}
