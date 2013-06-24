package com.cloud.cloudphotos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cloud.cloudphotos.provider.rackspace.RackspaceCloudAccount;
import com.cloud.cloudphotos.provider.rackspace.Setup;

public class CloudAccounts extends Activity {

    ApplicationConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_accounts);
        config = new ApplicationConfig(getApplicationContext());
        Button btn = (Button) findViewById(R.id.back);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);

            };
        });

        bindProviders();
        bindClearAll();
    }

    private void bindProviders() {
        new RackspaceCloudAccount(this);
    }

    private void bindClearAll() {
        Button clearAll = (Button) findViewById(R.id.clear_all);
        clearAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                runClearAll();
            }
        });
    }

    private void runClearAll() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Clear All Settings");
        alert.setMessage("Are you sure you want to clear all settings? This will disable all further uploads");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                config.unsetBoolean(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_KEY_HAS_ACCOUNT);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_AUTH_TOKEN);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_ENDPOINT);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_STORAGE);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_APIKEY);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_USERNAME);
                config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_CONTAINER_NAME);
            }
        });
        alert.setNegativeButton("No", null);
        alert.setCancelable(false);
        alert.show();
    }

    @Override
    public void onResume() {
        Log.v("CloudPhotos", String.valueOf(config.getBoolean(Setup.PREFS_KEY_HAS_ACCOUNT, false)));
        super.onResume();
    }

}
