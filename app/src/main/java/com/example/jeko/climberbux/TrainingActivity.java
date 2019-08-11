package com.example.jeko.climberbux;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String FILENAME = "training.json"; // имя файла
    private static final int CLIMBER_LOADER = 0;
    @BindView(R.id.list_view_climbers_of_training)
    ListView trainingListView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Cursor mCursor;
    private CalendarDay mCurrentDay = CalendarDay.today();
    private JSONObject trainingJsonObject = new JSONObject();
    // climberArrayList bond to adapter
    private ArrayList<Climber> climberArrayList = new ArrayList<>();
    private TrainingAdapter trainingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.bind(this);

        // Чтение из training.json
        trainingJsonObject = readFileTraining();

        trainingListView.setEmptyView(emptyView);

        for (int i = 0; i < trainingJsonObject.length(); i++) {
            addClimberToArrayList(i);
        }

        trainingAdapter = new TrainingAdapter(this, climberArrayList);
        trainingListView.setAdapter(trainingAdapter);
        trainingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
                builder.setTitle("Edit field");
                builder.setMessage("Save new payment or Remove a climber from the training?");

                LinearLayout layout = new LinearLayout(TrainingActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                // EditText for payment to Gran
                final EditText inputGran = new EditText(TrainingActivity.this);
                inputGran.setHint("Payment to Gran");
                inputGran.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputGran.setLayoutParams(lp);
                String toGran = climberArrayList.get(position).getPaymentGran();
                if (toGran.equals("0")) {
                    inputGran.setText("");
                } else inputGran.setText(toGran);
                layout.addView(inputGran);

                // EditText for payment to Me
                final EditText inputMe = new EditText(TrainingActivity.this);
                inputMe.setHint("Payment to Me");
                inputMe.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputMe.setLayoutParams(lp);
                String toMe = climberArrayList.get(position).getPaymentMe();
                if (toMe.equals("0")) {
                    inputMe.setText("");
                } else inputMe.setText(toMe);
                layout.addView(inputMe);

                builder.setView(layout);

                builder.setPositiveButton(getString(R.string.save_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String paymentToGran = inputGran.getText().toString();
                        if (paymentToGran.equals("")) paymentToGran = "0";
                        String paymentToMe = inputMe.getText().toString();
                        if (paymentToMe.equals("")) paymentToMe = "0";

                        // Добавляем новое значение в trainingJsonObject и climberArrayList
                        try {
                            String keyId = String.valueOf(climberArrayList.get(position).getId());
                            JSONObject climberJsonObject = trainingJsonObject.getJSONObject(keyId);
                            climberJsonObject.put("payment_to_gran", Integer.parseInt(paymentToGran));
                            climberJsonObject.put("payment_to_me", Integer.parseInt(paymentToMe));
                            trainingJsonObject.put(keyId, climberJsonObject);
                            climberArrayList.remove(position);
                            addClimberToArrayList(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Записываем измененный trainingJsonArray в json-файл
                        saveTrainingJsonObject();
                        trainingAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(getString(R.string.remove_button), new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long keyId = climberArrayList.get(position).getId();
                        trainingJsonObject.remove(String.valueOf(keyId));
                        climberArrayList.remove(position);

                        Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, keyId);
                        ContentValues valuesFalse = new ContentValues();
                        valuesFalse.put(ClimbersEntry.COLUMN_IS_CHECKED, 0);
                        getContentResolver().update(currentClimberUri, valuesFalse, null, null);

                        trainingAdapter.notifyDataSetChanged();
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                ArrayList<String> listNames = new ArrayList<>();
                ArrayList<Long> listIds = new ArrayList<>();
                ArrayList<Integer> listChecks = new ArrayList<>();

                // Наполнение списков listNames, listIds, listChecks
                mCursor.moveToPosition(-1);
                while (mCursor.moveToNext()) {
                    long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(ClimbersEntry._ID));
                    String name = mCursor.getString(mCursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_NAME));
                    int checked = mCursor.getInt(mCursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_IS_CHECKED));

                    listIds.add(id);
                    listNames.add(name);
                    listChecks.add(checked);
                }
                // Списки в массивы names, ids, isChecks
                String[] names = new String[listNames.size()];
                names = listNames.toArray(names);
                Long[] ids = new Long[listIds.size()];
                ids = listIds.toArray(ids);
                boolean isChecks[] = new boolean[listChecks.size()];
                for (int i = 0; i < isChecks.length; i++) {
                    isChecks[i] = (listChecks.get(i) == 1) ? true : false;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
                builder.setTitle("Choose a climber");
                builder.setCancelable(false);

                final Long[] finalIds = ids;
                builder.setMultiChoiceItems(names, isChecks, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        changeRec(finalIds[which], isChecked);

                        if (isChecked) {
                            addChoiceToJson(finalIds[which]);

                        } else {
                            // Удаляет кликнутый элемент из climberArrayList и trainingJsonObject
                            for (int i = 0; i < climberArrayList.size(); i++) {
                                Climber climber = climberArrayList.get(i);
                                if (climber.getId() == finalIds[which]) {
                                    climberArrayList.remove(i);
                                    trainingJsonObject.remove(String.valueOf(climber.getId()));
                                    break;
                                }
                            }
                        }
                    }

                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        trainingAdapter.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getLoaderManager().initLoader(CLIMBER_LOADER, null, this);
    }

    //Метод для добовления изменения isChecked в базу данных
    public void changeRec(long id, boolean isChecked) {

        Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, id);

        ContentValues values = new ContentValues();
        values.put(ClimbersEntry.COLUMN_IS_CHECKED, (isChecked) ? 1 : 0);
        getContentResolver().update(currentClimberUri, values, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end_of_training:
                showEndOfTrainingConfirmationDialog();
                return true;
            case R.id.action_date_of_training:
                dateOfTraining();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(TrainingActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEndOfTrainingConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.end_of_training_dialog_massage);
        builder.setPositiveButton(R.string.end_of_training, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Delete" button, so delete the book.
                endOfTraining();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Добавляет Climber в climberArrayList из trainingJsonArray по индексу
    private void addClimberToArrayList(int i) {
        try {
            String keyId = trainingJsonObject.names().getString(i);
            JSONObject climberJson = trainingJsonObject.getJSONObject(keyId);
            Climber climber = new Climber(this,
                    climberJson.getInt("id"),
                    climberJson.getString("name"),
                    climberJson.getInt("type_payment"),
                    climberJson.getInt("payment_to_gran"),
                    climberJson.getInt("payment_to_me"),
//                    Integer.valueOf(climberJson.getString("visits")),
                    climberJson.getInt("payed")
            );
            climberArrayList.add(i, climber);
            Log.v("CLIMBERARRAYLIST", climberArrayList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addChoiceToJson(long climberId) {
        Log.v("ID", String.valueOf(climberId));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int singleCost = Integer.parseInt(sharedPrefs.getString(getString(R.string.settings_single_cost_key), "200"))/2;
        int subscriptionCost = Integer.parseInt(sharedPrefs.getString(getString(R.string.settings_subscription_cost_key), "1600"))/2;
        int certificateCost = Integer.parseInt(sharedPrefs.getString(getString(R.string.settings_single_cost_key), "200"))/-2;

        String[] projection = {
                ClimbersEntry._ID,
                ClimbersEntry.COLUMN_NAME,
                ClimbersEntry.COLUMN_TYPE_PAYMENT,
                ClimbersEntry.COLUMN_PAYED};

        String selection = ClimbersEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(climberId)};

        Cursor cursor = getContentResolver().query(
                ClimbersEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        int nameColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_NAME);
        int typePaymentColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_TYPE_PAYMENT);
        int payedColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_PAYED);

        if (cursor.moveToFirst()) {
            // достаем данные из курсора
            final String climberName = cursor.getString(nameColumnIndex);
            final int typePayment = cursor.getInt(typePaymentColumnIndex);
            final int payed = cursor.getInt(payedColumnIndex);
            cursor.close();

            try {
                JSONObject climber = new JSONObject();
                climber.put("id", climberId);
                climber.put("name", climberName);
                climber.put("type_payment", typePayment);
                climber.put("payed", payed);
                switch (typePayment) {
                    case ClimbersEntry.TYPE_PAYMENT_SINGLE:
                        climber.put("payment_to_gran", singleCost);
                        climber.put("payment_to_me", singleCost);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                        climber.put("payment_to_gran", subscriptionCost);
                        climber.put("payment_to_me", subscriptionCost);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                        climber.put("payment_to_gran", certificateCost);
                        climber.put("payment_to_me", singleCost);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_SPECIAL:
                        climber.put("payment_to_gran", singleCost);
                        climber.put("payment_to_me", 0);
                }
                trainingJsonObject.put(String.valueOf(climberId), climber);

                // Добавляет новый элемент trainingJsonObject в climberArrayList
                addClimberToArrayList(trainingJsonObject.names().length() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("JsonObject", trainingJsonObject.toString());
        }
    }

    // Read File training.json
    private JSONObject readFileTraining() {
        JSONObject jsonObject = new JSONObject();

        try {
            InputStream inputStream = openFileInput(FILENAME);
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String text = reader.readLine();

                jsonObject = new JSONObject(text);
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

        return jsonObject;
    }

    // Write File training.json
    private void saveTrainingJsonObject() {
        try {
            OutputStream outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(trainingJsonObject.toString());
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void endOfTraining() {
        String day = String.valueOf(mCurrentDay.getDay());
        String month = String.valueOf(mCurrentDay.getMonth());
        String year = String.valueOf(mCurrentDay.getYear());
        String date = day + "." + month + "." + year;
//        Log.v("DATE", date);
        int count = climberArrayList.size();
        for (int i = count - 1; i >= 0; i--) {
            Climber climber = climberArrayList.get(i);
            long climberId = climber.getId();
            String climberName = climber.getName();
            int payedGran = Integer.parseInt(climber.getPaymentGran());
            int payedMe = Integer.parseInt(climber.getPaymentMe());
//            int visits = climber.getVisits();
            int payed = climber.getPayed();

            // Добавление в Payments
            ContentValues paymentValues = new ContentValues();
            paymentValues.put(PaymentsEntry.COLUMN_CLIMBER_ID, climberId);
            paymentValues.put(PaymentsEntry.COLUMN_CLIMBER_NAME, climberName);
            paymentValues.put(PaymentsEntry.COLUMN_DATE, date);
            paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_GRAN, payedGran);
            paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_ME, payedMe);
            Uri newUri = getContentResolver().insert(PaymentsEntry.CONTENT_URI, paymentValues);

            // Изменить Climber isChecked and payed
            Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, climberId);
            ContentValues climberValues = new ContentValues();
            climberValues.put(ClimbersEntry.COLUMN_IS_CHECKED, 0);
            climberValues.put(ClimbersEntry.COLUMN_PAYED, payed + payedGran + payedMe);
            int updated = getContentResolver().update(currentClimberUri, climberValues, null, null);

            trainingJsonObject.remove(String.valueOf(climberId));
            climberArrayList.remove(i);
        }
        trainingAdapter.notifyDataSetChanged();
        saveTrainingJsonObject();
    }

    private void dateOfTraining() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
        builder.setTitle("Choose a date ");

        LinearLayout layout = new LinearLayout(TrainingActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        MaterialCalendarView calendarView = new MaterialCalendarView(TrainingActivity.this);

        calendarView.setSelectedDate(mCurrentDay);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mCurrentDay = date;
            }
        });

        calendarView.setLayoutParams(lp);
        layout.addView(calendarView);
        builder.setView(layout);

        builder.setPositiveButton("Choose a date and End of training", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEndOfTrainingConfirmationDialog();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentDay = CalendarDay.today();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveTrainingJsonObject();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveTrainingJsonObject();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("OnPause", "activate");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ClimbersEntry._ID,
                ClimbersEntry.COLUMN_NAME,
                ClimbersEntry.COLUMN_IS_CHECKED,
                ClimbersEntry.COLUMN_PAYED,
//                ClimbersEntry.COLUMN_VISITS
        };
        return new CursorLoader(
                this,
                ClimbersEntry.CONTENT_URI,
                projection,
                null, null,
                ClimbersEntry.COLUMN_PAYED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
//        Log.v("CURSOR", cursor.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
