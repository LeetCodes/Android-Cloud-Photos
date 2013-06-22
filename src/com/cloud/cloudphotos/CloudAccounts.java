package com.cloud.cloudphotos;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

        providerRackspace();
    }

    private void providerRackspace() {
        Button rackspace = (Button) findViewById(R.id.rackspace);
        rackspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                runRackspace();
            };
        });
    }

    private void runRackspace() {
        ApplicationConfig config = new ApplicationConfig(getApplicationContext());
        final Setup setup = new com.cloud.cloudphotos.provider.rackspace.Setup();
        LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.provider_rackspace_setup_prompt,
                (ViewGroup) getCurrentFocus());
        if (config.getBoolean(setup.PREFS_KEY_HAS_ACCOUNT, false) == true) {
            dialoglayout.findViewById(R.id.already_has).setVisibility(View.VISIBLE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);
        final AlertDialog builderFinal = builder.show();
        Button cancel = (Button) dialoglayout.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                builderFinal.cancel();
            }
        });
        Button save = (Button) dialoglayout.findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) dialoglayout.findViewById(R.id.username);
                EditText apikey = (EditText) dialoglayout.findViewById(R.id.api_key);
                Spinner endpoint = (Spinner) dialoglayout.findViewById(R.id.endpoint);
                String uname = username.getText().toString();
                String akey = apikey.getText().toString();
                String epoint = endpoint.getItemAtPosition(endpoint.getSelectedItemPosition()).toString();
                if (setup.areValidCredentials(uname, akey, epoint)) {
                    // todo.
                } else {
                    TextView etext = (TextView) dialoglayout.findViewById(R.id.error_text);
                    etext.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
