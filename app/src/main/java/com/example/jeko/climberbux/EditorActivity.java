package com.example.jeko.climberbux;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CLIMBER_LOADER = 0;
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int GENDER_UNKNOWN = 0;
    private static final int RANK_THREE = 1;
    private static final int RANK_TWO = 2;
    private static final int RANK_ONE = 3;
    private static final int RANK_KMS = 4;
    private static final int RANK_MS = 5;
    private static final int RANK_BR = 0;
    private static final int TYPE_PAYMENT_SUBSCRIPTION = 1;
    private static final int TYPE_PAYMENT_CERTIFICATE = 2;
    private static final int TYPE_PAYMENT_SPECIAL = 3;
    private static final int TYPE_PAYMENT_SINGLE = 0;

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

    @BindView(R.id.list_view_on_dates)
    ListView mDatesListView;

    @BindView(R.id.payments_text_view)
    TextView mPaymentTextView;

    OnDateCursorAdapter mOnDateCursorAdapter;

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
                if (selection.equals(getString(R.string.male))) {
                    mGender = ClimbersEntry.GENDER_MALE;
                } else if (selection.equals(getString(R.string.female))) {
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
                if (selection.equals(getString(R.string.three))) {
                    mRank = ClimbersEntry.RANK_THREE;
                } else if (selection.equals(getString(R.string.tow))) {
                    mRank = ClimbersEntry.RANK_TWO;
                } else if (selection.equals(getString(R.string.one))) {
                    mRank = ClimbersEntry.RANK_ONE;
                } else if (selection.equals(getString(R.string.kms))) {
                    mRank = ClimbersEntry.RANK_KMS;
                } else if (selection.equals(getString(R.string.ms))) {
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
                if (selection.equals(getString(R.string.subscription))) {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION;
                } else if (selection.equals(getString(R.string.certificate))) {
                    mPayment = ClimbersEntry.TYPE_PAYMENT_CERTIFICATE;
                } else if (selection.equals(getString(R.string.special))) {
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
            setTitle(getString(R.string.add_a_climber_title));
            mPaymentTextView.setVisibility(View.GONE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_climber_title));
            getSupportLoaderManager().initLoader(CLIMBER_LOADER, null, this);

            mOnDateCursorAdapter = new OnDateCursorAdapter(this, null);

            mOnDateCursorAdapter.swapCursor(getPaymentCursorByClimber(mCurrentClimberUri));

            mDatesListView.setAdapter(mOnDateCursorAdapter);

        }
    }

    private Cursor getPaymentCursorByClimber(Uri climberUri) {
        String climberId = climberUri.getLastPathSegment();

        String[] projection = new String[]{
                PaymentsEntry._ID,
                PaymentsEntry.COLUMN_CLIMBER_ID,
                PaymentsEntry.COLUMN_CLIMBER_NAME,
                PaymentsEntry.COLUMN_DATE,
                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
                PaymentsEntry.COLUMN_PAYED_TO_ME};

        String selection = PaymentsEntry.COLUMN_CLIMBER_ID + " = ?";
        String[] selectionArgs = {climberId};

        return getContentResolver().query(
                PaymentsEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
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

        ContentValues climberValues = new ContentValues();

        int age = 0;
        climberValues.put(ClimbersEntry.COLUMN_NAME, nameString);
        if (!TextUtils.isEmpty(ageString)) {
            age = Integer.parseInt(ageString);
        }

        climberValues.put(ClimbersEntry.COLUMN_AGE, age);
        climberValues.put(ClimbersEntry.COLUMN_GENDER, mGender);
        climberValues.put(ClimbersEntry.COLUMN_RANK, mRank);
        climberValues.put(ClimbersEntry.COLUMN_TYPE_PAYMENT, mPayment);

        if (mCurrentClimberUri == null) {
            // Insert a new climber into the provider, returning the content URI for the new climber.
            // Insert a new row for climber in the database, returning the ID of that new row.
            Uri newUri = getContentResolver().insert(ClimbersEntry.CONTENT_URI, climberValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_saving_climber_toast), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.saved_climber_toast), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentClimberUri, climberValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.error_updating_climber_toast), Toast.LENGTH_SHORT).show();
            } else {
                updateClimberNameInPaymentsDB(mCurrentClimberUri.getLastPathSegment() ,nameString);
                Toast.makeText(this, getString(R.string.updated_climber_toast), Toast.LENGTH_SHORT).show();
            }
        }
        // Close the Activity
        finish();
    }

    private void updateClimberNameInPaymentsDB(String climberId, String name) {
//        String[] projection = new String[]{
//                PaymentsEntry._ID,
//                PaymentsEntry.COLUMN_CLIMBER_ID,
//                PaymentsEntry.COLUMN_CLIMBER_NAME,
//                PaymentsEntry.COLUMN_DATE,
//                PaymentsEntry.COLUMN_PAYED_TO_GRAN,
//                PaymentsEntry.COLUMN_PAYED_TO_ME};

        String selection = PaymentsEntry.COLUMN_CLIMBER_ID + " = ?";
        String[] selectionArgs = {climberId};

        ContentValues paymentValues = new ContentValues();
        paymentValues.put(PaymentsEntry.COLUMN_CLIMBER_NAME, name);

        getContentResolver().update(
                PaymentsEntry.CONTENT_URI,
                paymentValues,
                selection,
                selectionArgs);
    }

    private void deleteClimber() {
        if (mCurrentClimberUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentClimberUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deleting_climber_toast), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.deleted_climber_toast), Toast.LENGTH_SHORT).show();
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
                    mGenderSpinner.setSelection(GENDER_MALE);
                    break;
                case ClimbersEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(GENDER_FEMALE);
                    break;
                default:
                    mGenderSpinner.setSelection(GENDER_UNKNOWN);
                    break;
            }
            switch (rank) {
                case ClimbersEntry.RANK_THREE:
                    mRankSpinner.setSelection(RANK_THREE);
                    break;
                case ClimbersEntry.RANK_TWO:
                    mRankSpinner.setSelection(RANK_TWO);
                    break;
                case ClimbersEntry.RANK_ONE:
                    mRankSpinner.setSelection(RANK_ONE);
                    break;
                case ClimbersEntry.RANK_KMS:
                    mRankSpinner.setSelection(RANK_KMS);
                    break;
                case ClimbersEntry.RANK_MS:
                    mRankSpinner.setSelection(RANK_MS);
                    break;
                default:
                    mRankSpinner.setSelection(RANK_BR);
                    break;
            }
            switch (payment) {
                case ClimbersEntry.TYPE_PAYMENT_SUBSCRIPTION:
                    mPaymentSpinner.setSelection(TYPE_PAYMENT_SUBSCRIPTION);
                    break;
                case ClimbersEntry.TYPE_PAYMENT_CERTIFICATE:
                    mPaymentSpinner.setSelection(TYPE_PAYMENT_CERTIFICATE);
                    break;
                case ClimbersEntry.TYPE_PAYMENT_SPECIAL:
                    mPaymentSpinner.setSelection(TYPE_PAYMENT_SPECIAL);
                    break;
                default:
                    mPaymentSpinner.setSelection(TYPE_PAYMENT_SINGLE);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mGenderSpinner.setSelection(GENDER_UNKNOWN);
        mAgeEditText.setText("");
        mRankSpinner.setSelection(RANK_BR);
        mPaymentSpinner.setSelection(TYPE_PAYMENT_SINGLE);


    }
}
