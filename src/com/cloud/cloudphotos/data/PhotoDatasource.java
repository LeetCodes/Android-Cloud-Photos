package com.cloud.cloudphotos.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PhotoDatasource {

    private final SQLiteDatabase database;
    private final SqlitePhotoStorage dbHelper;
    private final String[] allColumns = { SqlitePhotoStorage.COLUMN_ID, SqlitePhotoStorage.COLUMN_PATH,
            SqlitePhotoStorage.COLUMN_DATESTAMP };

    public PhotoDatasource(Context context) {
        dbHelper = new SqlitePhotoStorage(context);
        database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        // database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Photo createPhoto(String photo, String datestamp) {
        ContentValues values = new ContentValues();
        values.put(SqlitePhotoStorage.COLUMN_PATH, photo);
        values.put(SqlitePhotoStorage.COLUMN_DATESTAMP, datestamp);
        long insertId = database.insert(SqlitePhotoStorage.TABLE_PHOTOS, null, values);
        Cursor cursor = database.query(SqlitePhotoStorage.TABLE_PHOTOS, allColumns, SqlitePhotoStorage.COLUMN_ID
                + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Photo newPhoto = cursorToPhoto(cursor);
        cursor.close();
        return newPhoto;
    }

    public void deletePhotoModel(Photo photo) {
        long id = photo.getId();
        System.out.println("Photo id " + id + " deleted");
        database.delete(SqlitePhotoStorage.TABLE_PHOTOS, SqlitePhotoStorage.COLUMN_ID + " = " + id, null);
    }

    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<Photo>();

        Cursor cursor = database.query(SqlitePhotoStorage.TABLE_PHOTOS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo photo = cursorToPhoto(cursor);
            photos.add(photo);
            cursor.moveToNext();
        }

        cursor.close();
        return photos;
    }

    private Photo cursorToPhoto(Cursor cursor) {
        Photo photoModel = new Photo();
        photoModel.setId(cursor.getLong(0));
        photoModel.setPath(cursor.getString(1));
        photoModel.setDatestamp(cursor.getString(2));
        return photoModel;
    }
}