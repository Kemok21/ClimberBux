package com.example.jeko.climberbux;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorTrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CLIMBER_LOADER = 0;
    @BindView(R.id.list_view_climbers_of_training)
    ListView trainingListView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private LocalDate mTrainingDate;
    private CalendarDay mChangedDate;
    // climberArrayList bond to adapter
    private ArrayList<Climber> climberArrayList = new ArrayList<>();
    private TrainingAdapter trainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.bind(this);

        fab.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        getLoaderManager().initLoader(CLIMBER_LOADER, null, this);

        trainingAdapter = new TrainingAdapter(this, climberArrayList);
        trainingListView.setAdapter(trainingAdapter);
        trainingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorTrainingActivity.this);
                builder.setTitle("Edit field");
                builder.setMessage("Save new payment or Remove a climber from the training?");

                LinearLayout layout = new LinearLayout(EditorTrainingActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                // EditText for payment to Gran
                final EditText inputGran = new EditText(EditorTrainingActivity.this);
                inputGran.setHint("Payment to Gran");
                inputGran.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputGran.setLayoutParams(lp);
                String toGran = climberArrayList.get(position).getPaymentGran();
                if (toGran.equals("0")) {
                    inputGran.setText("");
                } else inputGran.setText(toGran);
                layout.addView(inputGran);

                // EditText for payment to Me
                final EditText inputMe = new EditText(EditorTrainingActivity.this);
                inputMe.setHint("Payment to Me");
                inputMe.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputMe.setLayoutParams(lp);
                String toMe = climberArrayList.get(position).getPaymentMe();
                if (toMe.equals("0")) {
                    inputMe.setText("");
                } else inputMe.setText(toMe);
                layout.addView(inputMe);

                builder.setView(layout);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long keyId = climberArrayList.get(position).getId();
                        Log.v("abrakadabra", String.valueOf(keyId));
                        String paymentToGran = inputGran.getText().toString();
                        if (paymentToGran.equals("")) paymentToGran = "0";
                        String paymentToMe = inputMe.getText().toString();
                        if (paymentToMe.equals("")) paymentToMe = "0";
                        Log.v("kadabraabra", paymentToGran);

                        Uri currentPaymentUri = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, keyId);
                        ContentValues paymentValues = new ContentValues();
                        paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_GRAN, Integer.valueOf(paymentToGran));
                        paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_ME, Integer.valueOf(paymentToMe));
                        int up = getContentResolver().update(currentPaymentUri, paymentValues, null, null);
                        Log.v("Update", String.valueOf(up));

                    }
                });

                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long keyId = climberArrayList.get(position).getId();

                        Uri currentPaymentUri = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, keyId);
                        getContentResolver().delete(currentPaymentUri, null, null);
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date_of_training:
                dateOfTraining();///////EDIT!!!!!//////
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorTrainingActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Добавляет Climbers в climberArrayList из mCursor
    private void addClimberToArrayList(Cursor cursor) {
        while (cursor.moveToNext()) {
            int paymentIdColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry._ID);
            int climberNameColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_CLIMBER_NAME);
            int payedToGranColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
            int payedToMeColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);

            long paymentId = cursor.getLong(paymentIdColumnIndex);
            String climberName = cursor.getString(climberNameColumnIndex);
            int payedToGran = cursor.getInt(payedToGranColumnIndex);
            int payedToMe = cursor.getInt(payedToMeColumnIndex);

            Climber climber = new Climber(this,
                    paymentId,
                    climberName,
                    0,
                    payedToGran,
                    payedToMe
            );
            climberArrayList.add(climber);
        }
    }

    private void dateOfTraining() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditorTrainingActivity.this);
        builder.setTitle("Choose a date ");

        LinearLayout layout = new LinearLayout(EditorTrainingActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        MaterialCalendarView calendarView = new MaterialCalendarView(EditorTrainingActivity.this);

        calendarView.setSelectedDate(mTrainingDate);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mChangedDate = date;
            }
        });

        calendarView.setLayoutParams(lp);
        layout.addView(calendarView);
        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = mChangedDate.getYear();
                int month = mChangedDate.getMonth();
                int day = mChangedDate.getDay();
                String date = day + "." + month + "." + year;

                String selection = PaymentsEntry.COLUMN_DATE + " = ?";
                ContentValues contentValues = new ContentValues();
                contentValues.put(PaymentsEntry.COLUMN_DATE, date);
                String[] selectionArgs = {mTrainingDate.getDayOfMonth() + "." + mTrainingDate.getMonthValue() + "." + mTrainingDate.getYear()};

                getContentResolver().update(PaymentsEntry.CONTENT_URI, contentValues, selection, selectionArgs);

                mTrainingDate = LocalDate.of(year, month, day);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        String selection = PaymentsEntry.COLUMN_DATE + " = ?";

        Intent intent = getIntent();
        Uri currentUri = intent.getData();
        Cursor cursor = getContentResolver().query(currentUri, projection, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE));

        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(date);

        int day;
        int month;
        int year;

        if (matcher.find()) {
            day = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            year = Integer.parseInt(matcher.group(3));
            mTrainingDate = LocalDate.of(year, month, day);
        }

        String[] selectionArgs = {date};

        return new CursorLoader(
                this,
                PaymentsEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        climberArrayList.removeAll(climberArrayList);
        addClimberToArrayList(cursor);
        trainingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        climberArrayList.removeAll(climberArrayList);
        trainingAdapter.notifyDataSetChanged();
    }
}
