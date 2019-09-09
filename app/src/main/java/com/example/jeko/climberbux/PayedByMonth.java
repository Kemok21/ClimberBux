package com.example.jeko.climberbux;

public class PayedByMonth {

    private String mMonth;
    private String mYear;
    private int mIncomeToGran;
    private int mIncomeToMe;

    public PayedByMonth(String month, String year, int incomeToGran, int incomeToMe) {
        mMonth = month;
        mYear = year;
        mIncomeToGran = incomeToGran;
        mIncomeToMe = incomeToMe;
    }

    public String getMonth() {
        return mMonth;
    }

    public String getYear() {
        return mYear;
    }

    public int getIncomeToGran() {
        return mIncomeToGran;
    }

    public int getIncomeToMe() {
        return mIncomeToMe;
    }
}

