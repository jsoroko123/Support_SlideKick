package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 11/5/2015.
 */
public class CaseSeverity implements Serializable{
    private int CaseSeverityID;
    private String CaseSeverityDesc;
    private String Color;
    private int OrderBy;

    public CaseSeverity(int mSeverityID, String mCaseSeverityDesc, String mColor) {
        CaseSeverityID = mSeverityID;
        CaseSeverityDesc = mCaseSeverityDesc;
        Color = mColor;
    }

    public int getCaseSeverityID() {
        return CaseSeverityID;
    }

    public void setCaseSeverityID(int caseSeverityID) {
        CaseSeverityID = caseSeverityID;
    }

    public String getCaseSeverityDesc() {
        return CaseSeverityDesc;
    }

    public void setCaseSeverityDesc(String caseSeverityDesc) {
        CaseSeverityDesc = caseSeverityDesc;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public int getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(int orderBy) {
        OrderBy = orderBy;
    }
}
