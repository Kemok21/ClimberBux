package com.example.jeko.climberbux;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeActivity extends AppCompatActivity {

    @BindView(R.id.income_gran)
    TextView incomeGranTextView;
    @BindView(R.id.income_me)
    TextView incomeMeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        String[] projection = new String[] {
                PaymentsEntry._ID,
                PaymentsEntry.COLUMN_PAYED_TO_ME,
                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                PaymentsEntry.COLUMN_DATE
        };

        Cursor cursor = getContentResolver().query(PaymentsEntry.CONTENT_URI, projection, null, null, null);
        int incomeGran = 0;
        int incomeMe = 0;
        while (cursor.moveToNext()) {
            int indexPayedToGran = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
            int indexPayedToMe = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);
            int indexDate = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE);

            int payedToGran = cursor.getInt(indexPayedToGran);
            int payedToMe = cursor.getInt(indexPayedToMe);
            String date = cursor.getString(indexDate);

            Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
            Matcher matcher = pattern.matcher(date);

            if (matcher.find()) {
                String day = matcher.group(1);
                String month = matcher.group(2);
                String year = matcher.group(3);
            }
            //Подумать над логикой
            incomeGran += payedToGran;
            incomeMe += payedToMe;
        }

        incomeGranTextView.setText(String.valueOf(incomeGran));
        incomeMeTextView.setText(String.valueOf(incomeMe));
    }
}
