package com.example.jeko.climberbux;

import android.content.Context;
import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;

public class Climber {

    private long mId;
    private String mName;
    private int mTypePayment;
    private int mPaymentGran;
    private int mPaymentMe;
    private int mPayed;
    private Context mContext;

    public Climber(Context context, long id, String name, int typePayment, int paymentGran, int paymentMe, int payed) {
        mContext = context;
        mId = id;
        mName = name;
        mTypePayment = typePayment;
        mPaymentGran = paymentGran;
        mPaymentMe = paymentMe;
        mPayed = payed;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getTypePayment() {
        switch (mTypePayment) {
            case ClimbersEntry.TYPE_PAYMENT_SINGLE:
                return mContext.getString(R.string.single);
            case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                return mContext.getString(R.string.subscription);
            case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                return mContext.getString(R.string.certificate);
            default:
                return mContext.getString(R.string.special);
        }
    }

    public String getPaymentGran() {
        return String.valueOf(mPaymentGran);
    }

    public String getPaymentMe() {
        return String.valueOf(mPaymentMe);
    }

    public int getPayed() {
        return mPayed;
    }

    public void setPaymentGran(int paymentGran) {
        mPaymentGran = paymentGran;
    }

    public void setPaymentMe(int paymentMe) {
        mPaymentMe = paymentMe;
    }

    @Override
    public String toString() {
        return mName;
    }
}
