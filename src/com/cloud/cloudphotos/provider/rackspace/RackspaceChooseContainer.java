package com.cloud.cloudphotos.provider.rackspace;

import android.app.Activity;
import android.os.Bundle;

import com.cloud.cloudphotos.ApplicationConfig;
import com.cloud.cloudphotos.R;

public class RackspaceChooseContainer extends Activity {

    ApplicationConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_photos);
    }

}
