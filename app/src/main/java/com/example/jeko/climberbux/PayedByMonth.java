package com.example.jeko.climberbux;

public class PayedByMonth {

    private String mMonth;
    private int mIncomeToGran;
    private int mIncomeToMe;

    public PayedByMonth(String month, int incomeToGran, int incomeToMe) {
        mMonth = month;
        mIncomeToGran = incomeToGran;
        mIncomeToMe = incomeToMe;
    }

    public String getMonth() {
        return mMonth;
    }

    public int getIncomeToGran() {
        return mIncomeToGran;
    }

    public int getIncomeToMe() {
        return mIncomeToMe;
    }
}

