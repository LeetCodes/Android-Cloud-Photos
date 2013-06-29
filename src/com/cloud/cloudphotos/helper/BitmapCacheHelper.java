package com.cloud.cloudphotos.helper;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

public class BitmapCacheHelper extends AsyncTask<Integer, Void, String> {
    private final File fileRef;
    private final String cachePath;
    private final String fileName;

    public BitmapCacheHelper(File file, String pathToCache, String nameOfFile) {
        fileRef = file;
        cachePath = pathToCache;
        fileName = nameOfFile;
    }

    @Override
    protected String doInBackground(Integer... params) {
        try {
            Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fileRef.getPath()), params[1],
                    params[0]);
            File fileNew = new File(cachePath, fileName);
            FileOutputStream fOutStream = new FileOutputStream(fileNew);
            resized.compress(Bitmap.CompressFormat.JPEG, params[2], fOutStream);
            fOutStream.flush();
            fOutStream.close();
            resized.recycle();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {

    }
}