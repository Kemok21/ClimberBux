package com.example.jeko.climberbux;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OnDatesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PAYMENT_LOADER = 0;
    private DateAdapter dateAdapter;
    private ArrayList<Date> dateArrayList = new ArrayList<>();

    @BindView(R.id.list_view_on_dates)
    ListView onDateListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_dates);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(PAYMENT_LOADER, null, this);

        dateAdapter = new DateAdapter(this, dateArrayList);
        onDateListView.setAdapter(dateAdapter);

        onDateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Date currentDate = (Date) onDateListView.getAdapter().getItem(position);
                ArrayList<Long> idList = currentDate.getPaymentIdList();
                Long[] idArray = new Long[idList.size()];

                Intent intent = new Intent(OnDatesActivity.this, EditorTrainingActivity.class);

                Uri currentPayment = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, idList.get(0));
                // Set the URI on the data field of the intent
                intent.setData(currentPayment);

                startActivity(intent);
                }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        dateArrayList.removeAll(dateArrayList);
        ArrayList<String> dates = new ArrayList<>();
        // Заполняем dates
        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE));
            if(!dates.contains(date)) dates.add(date);
        }
        // Цикл для заполнения dateArrayList
        for(String date : dates) {

            String[] projection = new String[]{
                    PaymentsEntry._ID,
                    PaymentsEntry.COLUMN_DATE,
                    PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                    PaymentsEntry.COLUMN_PAYED_TO_ME
            };
            String selection = PaymentsEntry.COLUMN_DATE + " = ?";
            String[] selectionArgs = {date};

            Cursor cursorByDate = getContentResolver().query(
                    PaymentsEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            int countClimbers = 0;
            int trainingIncome = 0;
            ArrayList<Long> paymentIdList = new ArrayList<>();

            while(cursorByDate.moveToNext()) {

                int idColumnIndex = cursorByDate.getColumnIndexOrThrow(PaymentsEntry._ID);
                int payedToGranColumnIndex = cursorByDate.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
                int payedToMeColumnIndex = cursorByDate.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);

                long id = cursorByDate.getLong(idColumnIndex);
                int payed = cursorByDate.getInt(payedToGranColumnIndex) + cursorByDate.getInt(payedToMeColumnIndex);

                countClimbers++;
                trainingIncome += payed;
                paymentIdList.add(id);
            }

            dateArrayList.add(new Date(date, countClimbers, trainingIncome, paymentIdList));
        }
        dateAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dateArrayList.removeAll(dateArrayList);
        dateAdapter.notifyDataSetChanged();
    }
}
