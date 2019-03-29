package com.example.jeko.climberbux;

import android.content.Context;
import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;

public class Climber {

    private int mId;
    private String mName;
    private int mTypePayment;
    private int mPaymentGran;
    private int mPaymentMe;
    private Context mContext;

    public Climber(Context context, int id, String name, int typePayment, int paymentGran, int paymentMe) {
        mContext = context;
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

    @Override
    public String toString() {
        return mName;
    }
}
