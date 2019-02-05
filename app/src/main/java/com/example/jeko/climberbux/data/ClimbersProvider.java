package com.example.jeko.climberbux.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

public class ClimbersProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ClimbersProvider.class.getSimpleName();

    private ClimbersDbHelper mDbHelper;

    /** URI matcher code for the content URI for the climbers table */
    private static final int CLIMBERS = 100;

    /** URI matcher code for the content URI for a single climber in the climbers table */
    private static final int CLIMBER_ID = 101;

    /** URI matcher code for the content URI for the payments table */
    private static final int PAYMENTS = 200;

    /** URI matcher code for the content URI for a single payment in the payments table */
    private static final int PAYMENT_ID = 201;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ClimbersEntry.CONTENT_AUTHORITY, ClimbersEntry.PATH_CLIMBERS, CLIMBERS);
        sUriMatcher.addURI(ClimbersEntry.CONTENT_AUTHORITY, ClimbersEntry.PATH_CLIMBERS + "/#", CLIMBER_ID);
        sUriMatcher.addURI(PaymentsEntry.CONTENT_AUTHORITY, PaymentsEntry.PATH_PAYMENTS, PAYMENTS);
        sUriMatcher.addURI(PaymentsEntry.CONTENT_AUTHORITY, PaymentsEntry.PATH_PAYMENTS + "/#", PAYMENT_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */
        @Override
    public boolean onCreate() {
        mDbHelper = new ClimbersDbHelper(getContext());
        return true;
    }
    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIMBERS:
                cursor = database.query(ClimbersEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CLIMBER_ID:
                selection = ClimbersEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ClimbersEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PAYMENTS:
                cursor = database.query(PaymentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PAYMENT_ID:
                selection = PaymentsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(PaymentsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIMBERS:
                return ClimbersEntry.CONTENT_LIST_TYPE;
            case CLIMBER_ID:
                return ClimbersEntry.CONTENT_ITEM_TYPE;
            case PAYMENTS:
                return PaymentsEntry.CONTENT_LIST_TYPE;
            case PAYMENT_ID:
                return PaymentsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIMBERS:
                return insertClimber(uri, values);
            case PAYMENTS:
                return insertPayment(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertClimber(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ClimbersEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Climber requires a name");
        }
        // Check that the gender is not null
        Integer gender = values.getAsInteger(ClimbersEntry.COLUMN_GENDER);
        if (gender == null && ClimbersEntry.isUnValidGender(gender)) {
            throw new IllegalArgumentException("Climber requires valid gender");
        }
        // Check that the rank is not null
        Integer rank = values.getAsInteger(ClimbersEntry.COLUMN_RANK);
        if (rank == null && ClimbersEntry.isUnValidRank(rank)) {
            throw new IllegalArgumentException("Climber requires valid rank");
        }
        // Check that the type payment is not null
        Integer payment = values.getAsInteger(ClimbersEntry.COLUMN_TYPE_PAYMENT);
        if (payment == null && ClimbersEntry.isUnValidPayment(payment)) {
            throw new IllegalArgumentException("Climber requires valid type payment");
        }
        // Check that the name is not null
//        Integer payed = values.getAsInteger(ClimbersEntry.COLUMN_PAYED);
//        if (payed == null && payed < 0) {
//            throw new IllegalArgumentException("Climber requires valid payed");
//        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new book with the given values
        long id = database.insert(ClimbersEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertPayment(Uri uri, ContentValues values) {
        // Check that the name is not null
        Long climberId = values.getAsLong(PaymentsEntry.COLUMN_CLIMBER_ID);
        if (climberId == null) {
            throw new IllegalArgumentException("Climber requires valid climbers id");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Insert the new book with the given values
        long id = database.insert(PaymentsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIMBERS:
                rowsDeleted = database.delete(ClimbersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CLIMBER_ID:
                selection = ClimbersEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ClimbersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PAYMENTS:
                rowsDeleted = database.delete(PaymentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PAYMENT_ID:
                selection = PaymentsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PaymentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIMBERS:
                return updateClimber(uri, values, selection, selectionArgs);
            case CLIMBER_ID:
                selection = ClimbersEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateClimber(uri, values, selection, selectionArgs);
            case PAYMENTS:
                return updatePayment(uri, values, selection, selectionArgs);
            case PAYMENT_ID:
                selection = PaymentsEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePayment(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateClimber(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ClimberEntry#COLUMN_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ClimbersEntry.COLUMN_NAME)) {
            String name = values.getAsString(ClimbersEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Climber requires a name");
            }
        }
        // Check that the gender value is not null
        if (values.containsKey(ClimbersEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(ClimbersEntry.COLUMN_GENDER);
            if (gender == null && ClimbersEntry.isUnValidGender(gender)) {
                throw new IllegalArgumentException("Climber requires valid gender");
            }
        }
        // Check that the rank is not null
        if (values.containsKey(ClimbersEntry.COLUMN_RANK)) {
            Integer rank = values.getAsInteger(ClimbersEntry.COLUMN_RANK);
            if (rank == null && ClimbersEntry.isUnValidRank(rank)) {
                throw new IllegalArgumentException("Climber requires valid rank");
            }
        }
        // Check that the type payment is not null
        if (values.containsKey(ClimbersEntry.COLUMN_TYPE_PAYMENT)) {
            Integer payment = values.getAsInteger(ClimbersEntry.COLUMN_TYPE_PAYMENT);
            if (payment == null && ClimbersEntry.isUnValidPayment(payment)) {
                throw new IllegalArgumentException("Climber requires valid type payment");
            }
        }
        // Check that the name is not null
        if (values.containsKey(ClimbersEntry.COLUMN_PAYED)) {
            Integer payed = values.getAsInteger(ClimbersEntry.COLUMN_PAYED);
            if (payed == null && payed < 0) {
                throw new IllegalArgumentException("Climber requires valid payed");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdate = database.update(ClimbersEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdate;
    }

    private int updatePayment(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PaymentEntry#COLUMN_CLIMBER_ID} key is present,
        // check that the name is not null
        if (values.containsKey(PaymentsEntry.COLUMN_CLIMBER_ID)) {
            Long climberId = values.getAsLong(PaymentsEntry.COLUMN_CLIMBER_ID);
            if (climberId == null) {
                throw new IllegalArgumentException("Climber requires valid climbers id");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdate = database.update(PaymentsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdate;
    }
}