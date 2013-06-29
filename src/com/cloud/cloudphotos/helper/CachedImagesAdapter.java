package com.cloud.cloudphotos.helper;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cloud.cloudphotos.CloudPhotos;
import com.cloud.cloudphotos.R;

public class CachedImagesAdapter extends BaseAdapter {

    File[] files;
    Context mContext;
    String cachePath;
    CachedImagesAdapter adapter = this;
    CloudPhotos cloudPhotosContext;

    // Constructor
    public CachedImagesAdapter(Context c, File[] fileList, String cacheDir, CloudPhotos thisContext) {
        mContext = c;
        files = fileList;
        cachePath = cacheDir;
        cloudPhotosContext = thisContext;
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public File getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setMinimumWidth(200);
        imageView.setMinimumHeight(200);
        Drawable placeholder = mContext.getResources().getDrawable(R.drawable.ic_launcher);
        imageView.setImageDrawable(placeholder);
        final File item = getItem(position);
        LazyImageViewLoader task = new LazyImageViewLoader(imageView, item);
        task.execute(1);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CloudPhotos", "Clicked : " + item.getName());
            }

        });
        imageView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete Photo");
                alert.setMessage("Delete this cached thumbnail?\n\nThis will not delete it from your storage provider.");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File f = new File(cachePath, item.getName());
                        Boolean deleted = f.delete();
                        if (deleted) {
                            cloudPhotosContext.listFiles();
                        }
                    }
                });
                alert.setNegativeButton("Cancel", null);
                alert.setCancelable(false);
                alert.show();
                return true;
            }

        });
        return imageView;
    }

}