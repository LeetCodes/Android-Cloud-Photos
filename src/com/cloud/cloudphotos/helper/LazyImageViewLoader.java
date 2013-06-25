package com.cloud.cloudphotos.helper;

import java.io.File;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LazyImageViewLoader extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewRef;
    private final int data = 0;
    private final File fileRef;

    public LazyImageViewLoader(ImageView imageView, File file) {
        imageViewRef = new WeakReference<ImageView>(imageView);
        fileRef = file;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = true;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.outHeight = 200;
        opts.outWidth = 200;
        Bitmap bitmap = BitmapFactory.decodeFile(fileRef.getPath(), opts);
        return bitmap;
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