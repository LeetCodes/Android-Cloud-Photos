package com.cloud.cloudphotos.helper;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cloud.cloudphotos.R;

public class CachedImagesAdapter extends BaseAdapter {

    File[] files;
    Context mContext;

    // Constructor
    public CachedImagesAdapter(Context c, File[] fileList) {
        mContext = c;
        files = fileList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
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
        return imageView;
    }

}