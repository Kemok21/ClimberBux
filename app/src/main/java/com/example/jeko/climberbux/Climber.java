package com.example.jeko.climberbux;

import android.content.Context;
import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;

public class Climber {

    Context context;
    private int mId;
    private String mName;
    private int mTypePayment;
    private int mPaymentGran;
    private int mPaymentMe;

    public Climber(int id, String name, int typePayment, int paymentGran, int paymentMe) {
        mId = id;
        mName = name;
        mTypePayment = typePayment;
        mPaymentGran = paymentGran;
        mPaymentMe = paymentMe;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTypePayment() {
        switch (mTypePayment) {
            case ClimbersEntry.TYPE_PAYMENT_SINGLE:
                return context.getString(R.string.single);
            case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                return context.getString(R.string.subscription);
            case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                return context.getString(R.string.certificate);
            default:
                return context.getString(R.string.special);
        }
    }

    public String getPaymentGran() {
        return String.valueOf(mPaymentGran);
    }

    public String getPaymentMe() {
        return String.valueOf(mPaymentMe);
    }

    @Override
    public String toString() {
        return mName;
    }
}
