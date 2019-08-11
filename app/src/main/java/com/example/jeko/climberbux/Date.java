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

    public String getCountClimber() {
        return String.valueOf(mCountClimber);
    }

    public String getTrainingIncome() {
        return String.valueOf(mTrainingIncome);
    }

    public ArrayList<Long> getPaymentIdList() {
        return mPaymentIdList;
    }
}
