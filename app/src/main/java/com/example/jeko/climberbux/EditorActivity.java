package com.example.jeko.climberbux;

import android.content.ContentValues;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CLIMBER_LOADER = 0;

    @BindView(R.id.spinner_gender)
    Spinner mGenderSpinner;

    @BindView(R.id.spinner_rank)
    Spinner mRankSpinner;

    @BindView(R.id.spinner_payment)
    Spinner mPaymentSpinner;

    @BindView(R.id.edit_name)
    EditText mNameEditText;

    @BindView(R.id.edit_age)
    EditText mAgeEditText;

    private Uri mCurrentClimberUri;

    private int mGender = ClimbersEntry.GENDER_UNKNOWN;
    private int mRank = ClimbersEntry.RANK_BR;
    private int mPayment = ClimbersEntry.TYPE_PAYMENT_SINGLE;

    //Listener for setupSpinner of gender
    private AdapterView.OnItemSelectedListener genderSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selection = (String) parent.getItemAtPosition(position);
            if (!TextUtils.isEmpty(selection)) {
                if (selection.equals("Male")) {
                    mGender = ClimbersEntry.GENDER_MALE;
                } else if (selection.equals("Female")) {
                    mGender = ClimbersEntry.GENDER_FEMALE;
                } else {
                    mGender = ClimbersEntry.GENDER_UNKNOWN;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mGender = ClimbersEntry.GENDER_UNKNOWN;
        }
    };

    //Listener for setupSpinner of rank
    private AdapterView.OnItemSelectedListener rankSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selection = (String) parent.getItemAtPosition(position);
            if (!TextUtils.isEmpty(selection)) {
                if (selection.equals("3")) {
                    mRank = ClimbersEntry.RANK_THREE;
                } else if (selection.equals("2")) {
                    mRank = ClimbersEntry.RANK_TWO;
                } else if (selection.equals("1")) {
                    mRank = ClimbersEntry.RANK_ONE;
                } else if (selection.equals("KMS")) {
                    mRank = ClimbersEntry.RANK_KMS;
                } else if (selection.equals("MS")) {
                    mRank = ClimbersEntry.RANK_MS;
                } else {
                    mRank = ClimbersEntry.RANK_BR;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mRank = ClimbersEntry.RANK_BR;
        }
    };

    //Listener for setupSpinner of payment
    private AdapterView.OnItemSelectedListener paymentSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selection = (String) parent.getItemAtPosition(position);
            if (!TextUtils.isEmpty(selection)) {
                if (selection.equals("Subscription")) {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION;
                } else if (selection.equals("Certificate")) {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_CERTIFICATE;
                } else if (selection.equals("Special")) {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_SPECIAL;
                } else {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_SINGLE;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mPayment = ClimbersEntry.TYPE_PAYMENT_SINGLE;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        //Get data of the intent
        Intent intent = getIntent();
        mCurrentClimberUri = intent.getData();

        setupSpinner(R.array.array_gender_option, mGenderSpinner, genderSelectedListener);
        setupSpinner(R.array.array_rank_option, mRankSpinner, rankSelectedListener);
        setupSpinner(R.array.array_payment_option, mPaymentSpinner, paymentSelectedListener);

        // Check get data of intent
        if (mCurrentClimberUri == null) {
            setTitle("Add a Climber");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Climber");
            getSupportLoaderManager().initLoader(CLIMBER_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new climber, hide the "Delete" menu item.
        if (mCurrentClimberUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveClimber();
                return true;
            case R.id.action_delete:
                deleteClimber();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveClimber() {
        // Read from input fields
        String nameString = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) return;
        String ageString = mAgeEditText.getText().toString();

        ContentValues values = new ContentValues();

        int age = 0;
        values.put(ClimbersEntry.COLUMN_NAME, nameString);
        if (!TextUtils.isEmpty(ageString)) {
            age = Integer.parseInt(ageString);
        }

        values.put(ClimbersEntry.COLUMN_AGE, age);
        values.put(ClimbersEntry.COLUMN_GENDER, mGender);
        values.put(ClimbersEntry.COLUMN_RANK, mRank);
        values.put(ClimbersEntry.COLUMN_TYPE_PAYMENT, mPayment);

        if (mCurrentClimberUri == null) {
            // Insert a new climber into the provider, returning the content URI for the new climber.
            // Insert a new row for climber in the database, returning the ID of that new row.
            Uri newUri = getContentResolver().insert(ClimbersEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Climber saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentClimberUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating climber", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Climber updated", Toast.LENGTH_SHORT).show();
            }
        }
        // Close the Activity
        finish();
    }

    private void deleteClimber() {
        if (mCurrentClimberUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentClimberUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting climber", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Climber deleted", Toast.LENGTH_SHORT).show();
            }
        }
        //Close the Activity
        finish();
    }


    private void setupSpinner(int arrayId, Spinner spinner, AdapterView.OnItemSelectedListener listener) {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,
                arrayId, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(spinnerAdapter);

        // Set the integer mSelected to the constant values
        spinner.setOnItemSelectedListener(listener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // These are the Contacts rows that we will retrieve
        String[] projection = new String[]{
                ClimbersEntry._ID,
                ClimbersEntry.COLUMN_NAME,
                ClimbersEntry.COLUMN_GENDER,
                ClimbersEntry.COLUMN_AGE,
                ClimbersEntry.COLUMN_RANK,
                ClimbersEntry.COLUMN_TYPE_PAYMENT,
                ClimbersEntry.COLUMN_VISITS,
                ClimbersEntry.COLUMN_PAYED,
                ClimbersEntry.COLUMN_PHOTO
        };
        return new CursorLoader(
                this,
                mCurrentClimberUri,
                projection,
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ClimbersEntry.COLUMN_NAME);
            int genderColumnIndex = cursor.getColumnIndex(ClimbersEntry.COLUMN_GENDER);
            int ageColumnIndex = cursor.getColumnIndex(ClimbersEntry.COLUMN_AGE);
            int rankColumnIndex = cursor.getColumnIndex(ClimbersEntry.COLUMN_RANK);
            int paymentColumnIndex = cursor.getColumnIndex(ClimbersEntry.COLUMN_TYPE_PAYMENT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int age = cursor.getInt(ageColumnIndex);
            int rank = cursor.getInt(rankColumnIndex);
            int payment = cursor.getInt(paymentColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAgeEditText.setText(Integer.toString(age));
            switch (gender) {
                case ClimbersEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case ClimbersEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
            switch (rank) {
                case ClimbersEntry.RANK_THREE:
                    mRankSpinner.setSelection(1);
                    break;
                case ClimbersEntry.RANK_TWO:
                    mRankSpinner.setSelection(2);
                    break;
                case ClimbersEntry.RANK_ONE:
                    mRankSpinner.setSelection(3);
                    break;
                case ClimbersEntry.RANK_KMS:
                    mRankSpinner.setSelection(4);
                    break;
                case ClimbersEntry.RANK_MS:
                    mRankSpinner.setSelection(5);
                    break;
                default:
                    mRankSpinner.setSelection(0);
                    break;
            }
            switch (payment) {
                case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                    mPaymentSpinner.setSelection(1);
                    break;
                case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                    mPaymentSpinner.setSelection(2);
                    break;
                case ClimbersEntry.TYPE_PAYMENT_SPECIAL:
                    mPaymentSpinner.setSelection(3);
                    break;
                default:
                    mPaymentSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mGenderSpinner.setSelection(0);
        mAgeEditText.setText("");
        mRankSpinner.setSelection(0);
        mPaymentSpinner.setSelection(0);


    }
}
