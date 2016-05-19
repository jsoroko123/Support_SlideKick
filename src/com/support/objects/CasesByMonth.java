package com.support.objects;

import java.io.Serializable;

/**
 * Created by Jeffery on 4/14/2016.
 */
public class CasesByMonth implements Serializable {
    private String Month;
    private int MonthCount;

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public int getMonthCount() {
        return MonthCount;
    }

    public void setMonthCount(int monthCount) {
        MonthCount = monthCount;
    }
}
