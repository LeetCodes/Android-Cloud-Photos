package com.cloud.cloudphotos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlitePhotoStorage extends SQLiteOpenHelper {

    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_DATESTAMP = "date";

    private static final String DATABASE_NAME = "photos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_PHOTOS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PATH + " text not null, " + COLUMN_DATESTAMP
            + " text not null);";

    public SqlitePhotoStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        Log.w(SqlitePhotoStorage.class.getName(), "Database Upgrade " + oldV + " to " + newV
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

}