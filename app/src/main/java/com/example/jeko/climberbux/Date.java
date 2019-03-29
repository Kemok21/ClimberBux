package com.example.jeko.climberbux;

import java.util.ArrayList;

public class Date {

    private String mTrainingDate;
    private int mCountClimber;
    private int mTrainingIncome;
    private ArrayList<Long> mPaymentIdList;

    public Date(String trainingDate, int countClimber, int trainingIncome, ArrayList<Long> paymentIdList) {
        mTrainingDate = trainingDate;
        mCountClimber = countClimber;
        mTrainingIncome = trainingIncome;
        mPaymentIdList = paymentIdList;
    }

    public String getTrainingDate() {
        return mTrainingDate;
    }

    public int getCountClimber() {
        return mCountClimber;
    }

    public int getTrainingIncome() {
        return mTrainingIncome;
    }

    public ArrayList<Long> getPaymentIdList() {
        return mPaymentIdList;
    }
}
