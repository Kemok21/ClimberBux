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
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CLIMBER_LOADER = 0;
    public final static String FILENAME = "training.json"; // имя файла
    private Cursor mCursor;
    private JSONObject trainingJsonObject = new JSONObject();
    // climberArrayList bond to adapter
    private ArrayList<Climber> climberArrayList = new ArrayList<>();
    private TrainingAdapter trainingAdapter;

    @BindView(R.id.list_view_climbers_of_training)
    ListView trainingListView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

//    @BindView(R.id.payment)
//    TextView paymentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.bind(this);

        // Чтение из training.json
        trainingJsonObject = readFileTraining();

        emptyView.setText(getResources().getString(R.string.empty_training));

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
                inputGran.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                inputGran.setLayoutParams(lp);
                String toGran = climberArrayList.get(position).getPaymentGran();
                if (toGran.equals("0")) {
                    inputGran.setText("");
                } else inputGran.setText(toGran);
                layout.addView(inputGran);
                // EditText for payment to Me
                final EditText inputMe = new EditText(TrainingActivity.this);
                inputMe.setHint("Payment to Me");
                inputMe.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
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
                        String paymentToGran = inputGran.getText().toString();
                        if (paymentToGran.equals("")) paymentToGran = "0";
                        String paymentToMe = inputMe.getText().toString();
                        if (paymentToMe.equals("")) paymentToMe = "0";

                        // Добавляем новое значение в trainingJsonArray и climberArrayList
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
                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyId = String.valueOf(climberArrayList.get(position).getId());
                        trainingJsonObject.remove(keyId);
                        climberArrayList.remove(position);
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
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(TrainingActivity.this);
                builder.setTitle("Choose a climber");
                builder.setCancelable(true);

                final ArrayList<Uri> listTrue = new ArrayList<>();
                final ArrayList<Uri> listFalse = new ArrayList<>();

                DialogInterface.OnMultiChoiceClickListener multiChoice = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        ListView lv = ((AlertDialog) dialog).getListView();
                        Log.v("WHICH", String.valueOf(which));
                        if (isChecked) {
                            Log.v("isChecked", String.valueOf(which));

                            mCursor.moveToPosition(which);

                            int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(ClimbersEntry._ID));
                            Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, id);
                            listTrue.add(currentClimberUri);
                            if (listFalse.contains(currentClimberUri)) listFalse.remove(currentClimberUri);

                        } else {
                            Log.v("isNoChecked", String.valueOf(which));

                            mCursor.moveToPosition(which);

                            int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(ClimbersEntry._ID));
                            Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, id);
                            listFalse.add(currentClimberUri);
                            if (listTrue.contains(currentClimberUri)) listTrue.remove(currentClimberUri);
                        }

//                        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(ClimbersEntry._ID));
//                        Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, id);
//                        int rowsAffected = getContentResolver().update(currentClimberUri, values, null, null);
                    }
                };

                builder.setMultiChoiceItems(mCursor, ClimbersEntry.COLUMN_IS_CHECKED, ClimbersEntry.COLUMN_NAME, multiChoice);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues valuesTrue = new ContentValues();
                        valuesTrue.put(ClimbersEntry.COLUMN_IS_CHECKED, 1);
                        ContentValues valuesFalse = new ContentValues();
                        valuesFalse.put(ClimbersEntry.COLUMN_IS_CHECKED, 0);
                        // Снятие и удаление анчекнутых Climbers
                        for (int i = 0; i < listFalse.size(); i++) {
                            getContentResolver().update(listFalse.get(i), valuesFalse, null, null);
                            // Цикл для нахождения индекса элемента в climberArrayList
                            for (int j = 0; j < climberArrayList.size(); j++) {
                                Climber climber = climberArrayList.get(j);
                                if (listFalse.get(i).equals(
                                        ContentUris.withAppendedId(
                                                ClimbersEntry.CONTENT_URI,
                                                climber.getId()))) {
                                    climberArrayList.remove(j);
                                    trainingJsonObject.remove(String.valueOf(climber.getId()));
                                    break;
                                }
                            }
                        }
                        // Добавление чекнутых Climbers
                        for (int i = 0; i < listTrue.size(); i++) {
                            getContentResolver().update(listTrue.get(i), valuesTrue, null, null);
                            String token = listTrue.get(i).getLastPathSegment();
                            addChoiceToJson(Integer.parseInt(token));
                        }

                        trainingAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
//                builder.setCursor(cursor1, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        cursor1.moveToPosition(which);
//                        addChoiceToJson(cursor1.getInt(0));
//                    }

//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.v("CHOICE ", String.valueOf(which));
//                        // Реализовать добовление выбранного скалолаза в trainingJsonObject
//                        cursor1.moveToPosition(which);
//                        addChoiceToJson(cursor1.getInt(0));
////                        AlertDialog.Builder builderInner = new AlertDialog.Builder(TrainingActivity.this);
////                        builderInner.setTitle("Your selected Item is");
////                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////
////                                dialog.dismiss();
////                            }
////                        });
//                    }
//                }, ClimbersEntry.COLUMN_NAME);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getLoaderManager().initLoader(CLIMBER_LOADER, null, this);
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
                Log.v("Popalo_0", "knlkjnl");
                endOfTraining();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(TrainingActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Добавляет Climber в climberArrayList из trainingJsonArray по индексу
    private void addClimberToArrayList(int i) {
        try {
            String keyId = trainingJsonObject.names().getString(i);
            JSONObject climberJson = trainingJsonObject.getJSONObject(keyId);
            Climber climber = new Climber(
                    climberJson.getInt("id"),
                    climberJson.getString("name"),
                    climberJson.getInt("type_payment"),
                    climberJson.getInt("payment_to_gran"),
                    climberJson.getInt("payment_to_me")
            );
            climberArrayList.add(i, climber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addChoiceToJson(int choice) {
        Log.v("ID", String.valueOf(choice));

        String[] projection = {ClimbersEntry._ID, ClimbersEntry.COLUMN_NAME, ClimbersEntry.COLUMN_TYPE_PAYMENT};
        String selection = ClimbersEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(choice)};
        Cursor cursor = getContentResolver().query(ClimbersEntry.CONTENT_URI, projection, selection, selectionArgs, null);

        int idColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_NAME);
        int typePaymentColumnIndex = cursor.getColumnIndexOrThrow(ClimbersEntry.COLUMN_TYPE_PAYMENT);

        if(cursor.moveToFirst()) {
            // достаем данные из курсора
            final long climberId = cursor.getLong(idColumnIndex);
            final String climberName = cursor.getString(nameColumnIndex);
            final int typePayment = cursor.getInt(typePaymentColumnIndex);
            cursor.close();

            try {
                JSONObject climber = new JSONObject();
                climber.put("id", climberId);
                climber.put("name", climberName);
                climber.put("type_payment", typePayment);
                switch (typePayment) {
                    case ClimbersEntry.TYPE_PAYMENT_SINGLE:
                        climber.put("payment_to_gran", 100);
                        climber.put("payment_to_me", 100);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                        climber.put("payment_to_gran", 0);
                        climber.put("payment_to_me", 0);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                        climber.put("payment_to_gran", -100);
                        climber.put("payment_to_me", 100);
                        break;
                    case ClimbersEntry.TYPE_PAYMENT_SPECIAL:
                        climber.put("payment_to_gran", 100);
                        climber.put("payment_to_me", 0);
                }
//                trainingAdapter.setNotifyOnChange(true);
                trainingJsonObject.put(String.valueOf(climberId), climber);
                // Добавляет новый элемент trainingJsonObject в climberArrayList
                addClimberToArrayList(trainingJsonObject.names().length()-1);
                Log.v("climberArrayList in -1", climberArrayList.get(climberArrayList.size()-1).toString());
                // Обновляет список в TrainingActivity
                trainingAdapter.notifyDataSetChanged();
//                trainingAdapter.add(climberArrayList.get(climberArrayList.size()-1));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v("JsonObject", trainingJsonObject.toString());
        }
    }

    // Read File training.json
    private JSONObject readFileTraining() {
        JSONObject jsonObject = new JSONObject();
//        JSONArray jsonArray = new JSONArray();

        try {
            InputStream inputStream = openFileInput(FILENAME);
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
//                String line;
//                StringBuilder builder = new StringBuilder();
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line + "\n");
//                }
//                inputStream.close();
//                Log.v("NEXT", builder.toString());
//                jsonArray = new JSONArray(builder.toString());
                String text = reader.readLine();
                if (text.equals("[]")) text = "{}";
                Log.v("NEXT", text);

                jsonObject = new JSONObject(text);
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }

        return jsonObject;
    }

    // Write File training.json
    private void saveTrainingJsonObject(){
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
        Calendar currentDate = Calendar.getInstance();
        String day = String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(currentDate.get(Calendar.MONTH) + 1);
        String year = String.valueOf(currentDate.get(Calendar.YEAR));
        String date = day+"."+month+"."+year;
        Log.v("date", date);
        int count = climberArrayList.size();
        for (int i = count - 1; i >= 0; i--) {
            Climber climber = climberArrayList.get(i);
            int climberId = climber.getId();
            String climberName = climber.getName();
            int payedGran = Integer.parseInt(climber.getPaymentGran());
            int payedMe = Integer.parseInt(climber.getPaymentMe());
            // Добавление в Payments
            ContentValues paymentValues = new ContentValues();
            paymentValues.put(PaymentsEntry.COLUMN_CLIMBER_ID, climberId);
            paymentValues.put(PaymentsEntry.COLUMN_CLIMBER_NAME, climberName);
            paymentValues.put(PaymentsEntry.COLUMN_DATE, date);
            paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_GRAN, payedGran);
            paymentValues.put(PaymentsEntry.COLUMN_PAYED_TO_ME, payedMe);
            Uri newUri = getContentResolver().insert(PaymentsEntry.CONTENT_URI, paymentValues);
            // Изменить Climber isChecked
            Uri currentClimberUri = ContentUris.withAppendedId(ClimbersEntry.CONTENT_URI, climberId);
            ContentValues climberValues = new ContentValues();
            climberValues.put(ClimbersEntry.COLUMN_IS_CHECKED, 0);
            int updated = getContentResolver().update(currentClimberUri, climberValues, null, null);

            trainingJsonObject.remove(String.valueOf(climberId));
            climberArrayList.remove(i);
        }
        trainingAdapter.notifyDataSetChanged();
        saveTrainingJsonObject();
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
        String[] projection = new String[] {
                ClimbersEntry._ID,
                ClimbersEntry.COLUMN_NAME,
                ClimbersEntry.COLUMN_IS_CHECKED
        };
        return new CursorLoader(
                this,
                ClimbersEntry.CONTENT_URI,
                projection,
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }


    //    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] projection = new String[] {
//                ClimbersEntry._ID,
//                ClimbersEntry.COLUMN_NAME,
//                ClimbersEntry.COLUMN_TYPE_PAYMENT
//        };
//
//        String selection = ClimbersEntry._ID + "=?";
//        File internalStorageDir = getFilesDir();
////        File training = new File(internalStorageDir, "training.csv");
//        //Продолжить!
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
}
