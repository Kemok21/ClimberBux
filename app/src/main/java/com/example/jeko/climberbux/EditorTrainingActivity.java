package com.example.jeko.climberbux;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
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

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
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

        setTitle(getString(R.string.training_on_date) + mTrainingDate);

        trainingAdapter = new TrainingAdapter(this, climberArrayList);
        trainingListView.setAdapter(trainingAdapter);
        trainingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Climber climber = climberArrayList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorTrainingActivity.this);
                builder.setTitle(getString(R.string.edit_field));
                builder.setMessage(getString(R.string.save_payment_or_remove_climber));

                LinearLayout layout = new LinearLayout(EditorTrainingActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                // EditText for payment to Gran
                final EditText inputGran = new EditText(EditorTrainingActivity.this);
                inputGran.setHint(getString(R.string.payment_to_gran));
                inputGran.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputGran.setLayoutParams(lp);
                String toGran = climber.getPaymentGran();
                if (toGran.equals(getString(R.string.non_payment))) {
                    inputGran.setText("");
                } else inputGran.setText(toGran);
                layout.addView(inputGran);

                // EditText for payment to Me
                final EditText inputMe = new EditText(EditorTrainingActivity.this);
                inputMe.setHint(getString(R.string.payment_to_me));
                inputMe.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputMe.setLayoutParams(lp);
                String toMe = climber.getPaymentMe();
                if (toMe.equals(getString(R.string.non_payment))) {
                    inputMe.setText("");
                } else inputMe.setText(toMe);
                layout.addView(inputMe);

                builder.setView(layout);

                builder.setPositiveButton(getString(R.string.save_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long keyId = climber.getPaymentId();
                        String paymentToGran = inputGran.getText().toString();
                        if (paymentToGran.equals("")) paymentToGran = getString(R.string.non_payment);
                        String paymentToMe = inputMe.getText().toString();
                        if (paymentToMe.equals("")) paymentToMe = getString(R.string.non_payment);

                        Uri currentPaymentUri = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, keyId);
                        Log.v("Current Uri", String.valueOf(keyId));
                        Log.v("keyID", String.valueOf(currentPaymentUri));
                        ContentValues paymentValues = new ContentValues();
                        paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_GRAN, Integer.valueOf(paymentToGran));
                        paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_ME, Integer.valueOf(paymentToMe));
                        int updatePayment = getContentResolver().update(currentPaymentUri, paymentValues, null, null);
                        Log.v("updatePayment", String.valueOf(updatePayment));

                        int payment = Integer.parseInt(climber.getPaymentGran()) + Integer.parseInt(climber.getPaymentMe());
                        int inequality = Integer.parseInt(paymentToGran) + Integer.parseInt(paymentToMe) - payment;

                        Uri climberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, climber.getClimberId());
                        ContentValues climberValues = new ContentValues();
                        climberValues.put(ClimbersEntry.COLUMN_PAYED, climber.getPayed() + inequality);
                        int updateClimber = getContentResolver().update(climberUri, climberValues, null, null);
                        Log.v("updateClimber", String.valueOf(updateClimber));

                        trainingAdapter.notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton(getString(R.string.remove_button), new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long keyId = climber.getPaymentId();

                        Uri currentPaymentUri = ContentUris.withAppendedId(PaymentsEntry.CONTENT_URI, keyId);
                        getContentResolver().delete(currentPaymentUri, null, null);
                        trainingAdapter.notifyDataSetChanged();
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
                dateOfTraining();
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
            int climberIdColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_CLIMBER_ID);
            int climberNameColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_CLIMBER_NAME);
            int payedToGranColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_GRAN);
            int payedToMeColumnIndex = cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_PAYED_TO_ME);

            long paymentId = cursor.getLong(paymentIdColumnIndex);
            long climberId = cursor.getLong(climberIdColumnIndex);
            String climberName = cursor.getString(climberNameColumnIndex);
            int payedToGran = cursor.getInt(payedToGranColumnIndex);
            int payedToMe = cursor.getInt(payedToMeColumnIndex);

            String[] projection = new String[]{
                    ClimbersEntry._ID,
                    ClimbersEntry.COLUMN_TYPE_PAYMENT,
                    ClimbersEntry.COLUMN_PAYED
            };
            String selection = ClimbersEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(climberId)};

            Cursor climberCursor = getContentResolver().query(ClimbersEntry.CONTENT_URI, projection, selection, selectionArgs, null);

            climberCursor.moveToFirst();
            int payed = climberCursor.getInt(climberCursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_PAYED));
            int typePayment = climberCursor.getInt(climberCursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_TYPE_PAYMENT));


            Climber climber = new Climber(
                    this,
                    paymentId,
                    climberId,
                    climberName,
                    typePayment,
                    payedToGran,
                    payedToMe,
                    payed
            );
            climberArrayList.add(climber);
        }
    }

    private void dateOfTraining() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditorTrainingActivity.this);
        builder.setTitle(getString(R.string.choose_a_date_title));

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

        builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
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

        builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
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
        Log.v("Current Uri", String.valueOf(currentUri));
        Cursor cursor = getContentResolver().query(currentUri, projection, null, null, null);
        cursor.moveToFirst();
        String date = cursor.getString(cursor.getColumnIndexOrThrow(PaymentsEntry.COLUMN_DATE));
        cursor.close();

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

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("OnStop", "activate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("OnDestroy", "activate");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("OnPause", "activate");
    }
}
