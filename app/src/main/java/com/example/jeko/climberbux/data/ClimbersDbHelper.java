package com.example.jeko.climberbux.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

public class ClimbersDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "accounting.db";

    public ClimbersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES_CLIMBERS =
                "CREATE TABLE " + ClimbersEntry.TABLE_NAME + "(" +
                ClimbersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ClimbersEntry.COLUMN_NAME + " TEXT NOT NULL," +
                ClimbersEntry.COLUMN_GENDER + " INTEGER NOT NULL DEFAULT 0," +
                ClimbersEntry.COLUMN_AGE + " INTEGER," +
                ClimbersEntry.COLUMN_RANK + " INTEGER NOT NULL DEFAULT 0," +
                ClimbersEntry.COLUMN_TYPE_PAYMENT + " INTEGER NOT NULL DEFAULT 0," +
                ClimbersEntry.COLUMN_PAYED + " INTEGER NOT NULL DEFAULT 0," +
                ClimbersEntry.COLUMN_VISITS + " TEXT," +
                ClimbersEntry.COLUMN_PHOTO + " TEXT);";
        // Создание таблицы Climbers
        db.execSQL(SQL_CREATE_ENTRIES_CLIMBERS);

        String SQL_CREATE_ENTRIES_PAYMENTS =
                "CREATE TABLE " + PaymentsEntry.TABLE_NAME + "(" +
                PaymentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PaymentsEntry.COLUMN_CLIMBER_ID + " INTEGER NOT NULL," +
                PaymentsEntry.COLUMN_DATE + " LONG NOT NULL," +
                PaymentsEntry.COLUMN_PAYED + " INTEGER NOT NULL DEFAULT 0);";
        // Создание таблицы Payments
        db.execSQL(SQL_CREATE_ENTRIES_PAYMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Empty yet
    }
}
