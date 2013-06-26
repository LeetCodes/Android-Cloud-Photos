package com.cloud.cloudphotos;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.cloud.cloudphotos.helper.CachedImagesAdapter;
import com.cloud.cloudphotos.helper.SortFiles;

public class CloudPhotos extends Activity {

    GridView gridView;
    String cachePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_photos);
        cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CloudPhotos-Cache";
        makeCacheFolder();
        gridView = (GridView) findViewById(R.id.grid_view);
        listFiles();
    }

    private void listFiles() {

        File[] files = SortFiles.getDirectoryList(cachePath);
        CachedImagesAdapter adapter = new CachedImagesAdapter(this, files);
        gridView.setAdapter(adapter);
        Log.i("CloudPhotos", String.valueOf(files.length) + " files");
        gridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getVisibility() != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
            }

        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cloud_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_accounts:
            Intent intent = new Intent(this, CloudAccounts.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            return true;
        case R.id.action_settings:
            Intent settings = new Intent(this, ApplicationSettings.class);
            startActivity(settings);
            overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            return true;
        case R.id.action_refresh:
            listFiles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        listFiles();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
        super.onBackPressed();
    }

    private void makeCacheFolder() {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
