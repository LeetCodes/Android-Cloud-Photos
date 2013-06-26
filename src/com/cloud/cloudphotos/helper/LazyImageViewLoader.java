package com.cloud.cloudphotos.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LazyImageViewLoader extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewRef;
    private final File fileRef;

    public LazyImageViewLoader(ImageView imageView, File file) {
        imageViewRef = new WeakReference<ImageView>(imageView);
        fileRef = file;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        InputStream in = null;
        Bitmap bm = null;
        try {
            in = new BufferedInputStream(new FileInputStream(fileRef));
            bm = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return bm;

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewRef != null && bitmap != null) {
            final ImageView imageView = imageViewRef.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}