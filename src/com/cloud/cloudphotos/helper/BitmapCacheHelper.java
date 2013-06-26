package com.cloud.cloudphotos.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = true;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            InputStream in = null;
            Bitmap bm = null;
            Bitmap resized = null;
            try {
                in = new BufferedInputStream(new FileInputStream(fileRef));
                bm = BitmapFactory.decodeStream(in);
                resized = getResizedBitmap(bm, params[0], params[1]);
                File fileNew = new File(cachePath, fileName);
                FileOutputStream fOutStream = new FileOutputStream(fileNew);
                resized.compress(Bitmap.CompressFormat.JPEG, params[2], fOutStream);
                fOutStream.flush();
                fOutStream.close();
            } catch (FileNotFoundException e) {

            } finally {
                resized.recycle();
                bm.recycle();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    protected void onPostExecute(String string) {

    }
}