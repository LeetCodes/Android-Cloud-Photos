package com.cloud.cloudphotos.provider.rackspace;

import java.security.KeyStore;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cloud.cloudphotos.ApplicationConfig;
import com.cloud.cloudphotos.CloudAccounts;
import com.cloud.cloudphotos.CloudPhotos;
import com.cloud.cloudphotos.R;
import com.cloud.cloudphotos.helper.SslFactory;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class RackspaceCloudAccount {

    CloudAccounts activityContext;
    View dialoglayout;
    AlertDialog builderFinal;

    /**
     * Constructor to bind onto the 'Rackspace Cloud Files' button
     * 
     * @param cloudAccounts
     */
    public RackspaceCloudAccount(CloudAccounts cloudAccounts) {
        activityContext = cloudAccounts;
        Button rackspace = (Button) activityContext.findViewById(R.id.rackspace);
        rackspace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                run();
            };
        });
    }

    /**
     * Prompts the user with a simple view to ask for the credentials.
     */
    private void run() {
        ApplicationConfig config = new ApplicationConfig(activityContext);
        final com.cloud.cloudphotos.provider.rackspace.Setup setup = new com.cloud.cloudphotos.provider.rackspace.Setup();
        LayoutInflater inflater = activityContext.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.provider_rackspace_setup_prompt,
                (ViewGroup) activityContext.getCurrentFocus());
        if (config.getBoolean(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_KEY_HAS_ACCOUNT, false) == true) {
            dialoglayout.findViewById(R.id.already_has).setVisibility(View.VISIBLE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setView(dialoglayout);
        builderFinal = builder.show();
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
                TextView etext = (TextView) dialoglayout.findViewById(R.id.error_text);
                etext.setVisibility(View.GONE);
                TextView authFailed = (TextView) dialoglayout.findViewById(R.id.authentication_failed);
                authFailed.setVisibility(View.GONE);
                EditText username = (EditText) dialoglayout.findViewById(R.id.username);
                EditText apikey = (EditText) dialoglayout.findViewById(R.id.api_key);
                Spinner endpoint = (Spinner) dialoglayout.findViewById(R.id.endpoint);
                String uname = username.getText().toString();
                String akey = apikey.getText().toString();
                String epoint = endpoint.getItemAtPosition(endpoint.getSelectedItemPosition()).toString();
                if (setup.areValidCredentials(uname, akey, epoint)) {
                    String url = Setup.getAuthenticationEndpointFromString(epoint);
                    validateRackspace(uname, akey, url);
                } else {
                    etext.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Authentication was a success, evaluate the headers and if valid, store
     * the values.
     * 
     * @param headers
     * @param username
     * @param apikey
     * @param authUrl
     */
    public void authenticationSuccess(Header[] headers, String username, String apikey, String authUrl) {
        String token = "";
        String storageUrl = "";
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase("X-Auth-Token")) {
                token = header.getValue();
            } else if (header.getName().equalsIgnoreCase("X-Storage-Url")) {
                storageUrl = header.getValue();
            }
        }
        if (token.isEmpty() || storageUrl.isEmpty()) {
            authenticationFailed();
        } else {
            builderFinal.cancel();
            Intent intent = new Intent(activityContext, CloudPhotos.class);
            activityContext.startActivity(intent);
            activityContext.overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            Log.v("CloudPhotos", username);
            Log.v("CloudPhotos", apikey);
            Log.v("CloudPhotos", token);
            Log.v("CloudPhotos", storageUrl);
        }
    }

    /**
     * Authentication failed, notify the user.
     */
    public void authenticationFailed() {
        Log.v("CloudPhotos", "Authentication failed");
        TextView tv = (TextView) dialoglayout.findViewById(R.id.authentication_failed);
        tv.setVisibility(View.VISIBLE);
    }

    /**
     * Make a HTTP Request to Rackspace to validate the authentication details
     * 
     * @param username
     * @param apikey
     * @param url
     */
    private void validateRackspace(final String username, final String apikey, final String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SslFactory sf = new SslFactory(trustStore);
            sf.setHostnameVerifier(SslFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (Exception e) {

        }
        client.addHeader("X-Auth-User", username);
        client.addHeader("X-Auth-Key", apikey);
        client.get(url, new AsyncHttpResponseHandler() {
            private Boolean completed = false;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                completed = true;
                if (statusCode == 204) {
                    authenticationSuccess(headers, username, apikey, url);
                } else {
                    authenticationFailed();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                completed = true;
                authenticationFailed();
            }

            @Override
            public void onFinish() {
                if (completed == false) {
                    authenticationFailed();
                }
            }
        });
    }
}