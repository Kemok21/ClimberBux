package com.example.jeko.climberbux;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OnDatesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PAYMENT_LOADER = 0;
    CursorAdapter mCursorAdapter;

    @BindView(R.id.list_view_on_dates)
    ListView onDateListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_dates);
        ButterKnife.bind(this);

        mCursorAdapter = new OnDateCursorAdapter(this, null);
        onDateListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(PAYMENT_LOADER, null, this);

//        String onDates = "";
//        Cursor cursor = getContentResolver().query(
//                PaymentsEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null);
//        while (cursor.moveToNext()) {
//            int idIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_CLIMBER_ID);
//            int dateIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE);
//            int payedIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED);
//            long id = cursor.getLong(idIndex);
//            String date = cursor.getString(dateIndex);
//            int payed = cursor.getInt(payedIndex);
//
//            onDates += String.valueOf(id)+";"+String.valueOf(date)+";"+String.valueOf(payed)+"\n";
//        }
//        TextView textView = findViewById(R.id.dates_text_view);
//        textView.setText(onDates);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {
                PaymentsEntry._ID,
                PaymentsEntry.COLUMN_CLIMBER_ID,
                PaymentsEntry.COLUMN_CLIMBER_NAME,
                PaymentsEntry.COLUMN_DATE,
                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                PaymentsEntry.COLUMN_PAYED_TO_ME
        };
        return new CursorLoader(
                this,
                PaymentsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
