package com.example.jeko.climberbux;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PAYMENT_LOADER = 0;
    Calendar currentDate = Calendar.getInstance();

    private IncomeAdapter mIncomeAdapter;
    private ArrayList<PayedByMonth> payedByMonthArrayList = new ArrayList<>();

    @BindView(R.id.list_view_payed)
    ListView payedListView;
//    @BindView(R.id.current_income_me)
//    TextView currentIncomeMeTextView;
//    @BindView(R.id.last_income_gran)
//    TextView lastIncomeGranTextView;
//    @BindView(R.id.last_income_me)
//    TextView lastIncomeMeTextView;

    private String mCurrentMonth = String.valueOf(currentDate.get(Calendar.MONTH) + 1);
    private String mCurrentYear = String.valueOf(currentDate.get(Calendar.YEAR));
    private String mLastMonth = String.valueOf(currentDate.get(Calendar.MONTH));
    private String mLastYear = String.valueOf(currentDate.get(Calendar.YEAR) - 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        ButterKnife.bind(this);

        if (mLastMonth.equals(getString(R.string.calendar_december))) {
            mLastMonth = getString(R.string.december);
//            mLastYear = String.valueOf(currentDate.get(Calendar.YEAR) - 1);
        }

        getLoaderManager().initLoader(PAYMENT_LOADER, null, this);

        mIncomeAdapter = new IncomeAdapter(this, payedByMonthArrayList);
        payedListView.setAdapter(mIncomeAdapter);

//        currentIncomeGranTextView.setText(String.valueOf(currentIncomeGran));
//        currentIncomeMeTextView.setText(String.valueOf(currentIncomeMe));
//        lastIncomeGranTextView.setText(String.valueOf(lastIncomeGran));
//        lastIncomeMeTextView.setText(String.valueOf(lastIncomeMe));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                PaymentsEntry._ID,
                PaymentsEntry.COLUMN_PAYED_TO_ME,
                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                PaymentsEntry.COLUMN_DATE
        };

        return new CursorLoader(this,
                PaymentsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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

            Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
            Matcher matcher = pattern.matcher(date);

            String month = null;
            String year = null;
            String day = null;

            if (matcher.find()) {
                day = matcher.group(1);
                month = matcher.group(2);
                year = matcher.group(3);
            }
            if (month.equals(mCurrentMonth) && year.equals(mCurrentYear)) {
                currentIncomeGran += payedToGran;
                currentIncomeMe += payedToMe;
            } else if (month.equals(mLastMonth) && year.equals(mCurrentYear)) {
                lastIncomeGran += payedToGran;
                lastIncomeMe += payedToMe;
            }
        }

        payedByMonthArrayList.add(new PayedByMonth(mCurrentMonth, mCurrentYear, currentIncomeGran, currentIncomeMe));
        payedByMonthArrayList.add(new PayedByMonth(mLastMonth, mLastYear, lastIncomeGran, lastIncomeMe));

        mIncomeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        payedByMonthArrayList.removeAll(payedByMonthArrayList);
        mIncomeAdapter.notifyDataSetChanged();
    }
}
