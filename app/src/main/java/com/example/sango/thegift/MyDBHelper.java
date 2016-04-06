package com.example.sango.thegift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/4/4.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "thegift.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public static final String TABLE_NAME = "card";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String STATUS_COLUMN = "status";

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME_COLUMN + " TEXT NOT NULL, " +
                        STATUS_COLUMN + " INTEGER NOT NULL)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
