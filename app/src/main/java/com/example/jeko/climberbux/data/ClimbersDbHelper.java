package com.example.jeko.climberbux.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jeko.climberbux.data.ClimbersContract.ClimbersEntry;
import com.example.jeko.climberbux.data.ClimbersContract.PaymentsEntry;

public class ClimbersDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "accounting.db";
    private static final String DATABASE_ALTER_TABLE_1 = "ALTER TABLE "
            + ClimbersEntry.TABLE_NAME + " ADD COLUMN " + ClimbersEntry.COLUMN_IS_CHECKED + " INTEGER NOT NULL DEFAULT 0;";
    String SQL_CREATE_ENTRIES_CLIMBERS =
            "CREATE TABLE " + ClimbersEntry.TABLE_NAME + "(" +
                    ClimbersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ClimbersEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ClimbersEntry.COLUMN_GENDER + " INTEGER NOT NULL DEFAULT 0," +
                    ClimbersEntry.COLUMN_AGE + " INTEGER," +
                    ClimbersEntry.COLUMN_RANK + " INTEGER NOT NULL DEFAULT 0," +
                    ClimbersEntry.COLUMN_TYPE_PAYMENT + " INTEGER NOT NULL DEFAULT 0," +
                    ClimbersEntry.COLUMN_PAYED + " INTEGER NOT NULL DEFAULT 0," +
                    ClimbersEntry.COLUMN_VISITS + " INTEGER NOT NULL DEFAULT 0," +
                    ClimbersEntry.COLUMN_PHOTO + " TEXT," +
                    ClimbersEntry.COLUMN_IS_CHECKED + " INTEGER NOT NULL DEFAULT 0);";

    String SQL_CREATE_ENTRIES_PAYMENTS =
            "CREATE TABLE " + PaymentsEntry.TABLE_NAME + "(" +
                    PaymentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PaymentsEntry.COLUMN_CLIMBER_ID + " INTEGER NOT NULL," +
                    PaymentsEntry.COLUMN_CLIMBER_NAME + " TEXT NOT NULL," +
                    PaymentsEntry.COLUMN_DATE + " TEXT NOT NULL," +
                    PaymentsEntry.COLUMN_PAYED_TO_GRAN + " INTEGER NOT NULL DEFAULT 0," +
                    PaymentsEntry.COLUMN_PAYED_TO_ME + " INTEGER NOT NULL DEFAULT 0);";

    public ClimbersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы Climbers
        db.execSQL(SQL_CREATE_ENTRIES_CLIMBERS);

        // Создание таблицы Payments
        db.execSQL(SQL_CREATE_ENTRIES_PAYMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            Log.v("NEW version", "Add: " + DATABASE_ALTER_TABLE_1);
            db.execSQL(DATABASE_ALTER_TABLE_1);
        }
    }
}
