package com.example.jeko.climberbux;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeActivity extends AppCompatActivity {
    
    Calendar currentDate = Calendar.getInstance();
    private String mCurrentMonth = String.valueOf(currentDate.get(Calendar.MONTH) + 1);
    private String mCurrentYear = String.valueOf(currentDate.get(Calendar.YEAR));
    private String mLastMonth = String.valueOf(currentDate.get(Calendar.MONTH));
    protected String mLastYear = String.valueOf(currentDate.get(Calendar.YEAR));

    @BindView(R.id.current_income_gran)
    TextView currentIncomeGranTextView;
    @BindView(R.id.current_income_me)
    TextView currentIncomeMeTextView;
    @BindView(R.id.last_income_gran)
    TextView lastIncomeGranTextView;
    @BindView(R.id.last_income_me)
    TextView lastIncomeMeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        if (mLastMonth.equals("0")) {
            mLastMonth = "12";
            mLastYear = String.valueOf(currentDate.get(Calendar.YEAR) - 1);
        }

        String[] projection = new String[] {
                PaymentsEntry._ID,
                PaymentsEntry.COLUMN_PAYED_TO_ME,
                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                PaymentsEntry.COLUMN_DATE
        };
        Cursor cursor = getContentResolver().query(PaymentsEntry.CONTENT_URI, projection, null, null, null);

        int currentIncomeGran = 0;
        int currentIncomeMe = 0;
        int lastIncomeGran = 0;
        int lastIncomeMe = 0;

        while (cursor.moveToNext()) {

            int indexPayedToGran = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
            int indexPayedToMe = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);
            int indexDate = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE);

            int payedToGran = cursor.getInt(indexPayedToGran);
            int payedToMe = cursor.getInt(indexPayedToMe);
            String date = cursor.getString(indexDate);

            Pattern pattern = Pattern.compile("\\d+\\.(\\d+)\\.(\\d+)");
            Matcher matcher = pattern.matcher(date);

            String month = null;
            String year = null;

            if (matcher.find()) {
                month = matcher.group(1);
                year = matcher.group(2);
            }
            if (month.equals(mCurrentMonth) && year.equals(mCurrentYear)) {
            	currentIncomeGran += payedToGran;
            	currentIncomeMe += payedToMe;
            } else if (month.equals(mLastMonth) && year.equals(mCurrentYear)) {
                lastIncomeGran += payedToGran;
                lastIncomeMe += payedToMe;
            }
        }

        currentIncomeGranTextView.setText(String.valueOf(currentIncomeGran));
        currentIncomeMeTextView.setText(String.valueOf(currentIncomeMe));
        lastIncomeGranTextView.setText(String.valueOf(lastIncomeGran));
        lastIncomeMeTextView.setText(String.valueOf(lastIncomeMe));
    }
}
