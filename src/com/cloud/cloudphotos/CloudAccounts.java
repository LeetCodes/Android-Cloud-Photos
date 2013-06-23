package com.cloud.cloudphotos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cloud.cloudphotos.provider.rackspace.RackspaceCloudAccount;

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
    }

    private void bindProviders() {
        new RackspaceCloudAccount(this);
    }

}
