package com.cloud.cloudphotos;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cloud.cloudphotos.helper.SortFiles;

public class ApplicationSettings extends Activity {

    ApplicationConfig config;
    final Context context = this;
    Boolean wifiOnly = true;
    String cachePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);
        config = new ApplicationConfig(getApplicationContext());
        wifiOnly = config.getBoolean("wifionly", true);
        cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CloudPhotos-Cache";
        makeCacheFolder();
        bindEditConnection();
        bindClearCache();
    }

    private void bindClearCache() {
        Button btnCache = (Button) findViewById(R.id.cache_button);
        btnCache.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Clear Cache");
                alert.setMessage("Are you sure you want to clear the cache?\n\nClearing cache will remove small thumbnails stored on your device, but will not delete them from your storage provider.");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        doClearCache();
                    }
                });
                alert.setNegativeButton("Cancel", null);
                alert.setCancelable(false);
                alert.show();
            }
        });
    }

    private void doClearCache() {
        File[] files = SortFiles.getDirectoryList(cachePath);
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private void bindEditConnection() {
        Button btnConnection = (Button) findViewById(R.id.connection_button);
        final TextView connectionText = (TextView) findViewById(R.id.connection_text);
        if (wifiOnly.equals(true)) {
            connectionText.setText("Over WiFi only");
        } else {
            connectionText.setText("Over any connection");
        }
        btnConnection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("When to Upload");
                if (wifiOnly.equals(true)) {
                    alert.setMessage("Currently you upload only on WiFi, click 'Any Connection' to enable upload over any connection type.");
                    alert.setPositiveButton("Any Connection", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            config.setBoolean("wifionly", false);
                            connectionText.setText("Over any connection");
                            wifiOnly = false;
                        }
                    });
                } else {
                    alert.setMessage("Currently you upload over any connection, click 'WiFi Only' to switch to just upload over WiFi.");
                    alert.setPositiveButton("WiFi Only", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            config.setBoolean("wifionly", true);
                            connectionText.setText("Over WiFi only");
                            wifiOnly = true;
                        }
                    });
                }
                alert.setNegativeButton("Cancel", null);
                alert.setCancelable(false);
                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.cloud_photos, menu);
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
        }

        return super.onOptionsItemSelected(item);
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
