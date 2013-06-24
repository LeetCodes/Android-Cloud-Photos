package com.cloud.cloudphotos.provider.rackspace;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cloud.cloudphotos.ApplicationConfig;
import com.cloud.cloudphotos.R;
import com.cloud.cloudphotos.helper.ArrayListHashMapSort;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RackspaceChooseContainer extends Activity {

    ApplicationConfig config;
    String username;
    String apikey;
    String token;
    String endpoint;
    String storageUrl;
    ListView list;
    ProgressDialog dialog;
    AlertDialog builderFinal;
    final Context activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_rackspace_choose_container);
        Bundle extras = getIntent().getExtras();
        config = new ApplicationConfig(getApplicationContext());
        if (extras != null) {
            username = extras.getString("username");
            apikey = extras.getString("apikey");
            token = extras.getString("token");
            endpoint = extras.getString("endpoint");
            storageUrl = extras.getString("storageUrl");
            if (username.isEmpty() || apikey.isEmpty() || token.isEmpty() || storageUrl.isEmpty() || endpoint.isEmpty()) {
                back();
            } else {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Retrieving Containers...");
                dialog.setCancelable(false);
                dialog.show();
                getContainerList();
            }
        } else {
            back();
        }
    }

    private void getContainerList() {

        RackspaceHttpClient httpClient = new RackspaceHttpClient();
        AsyncHttpClient client = httpClient.getAuthenticatedStorageClient(token);
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(storageUrl, params, new AsyncHttpResponseHandler() {
            private Boolean completed = false;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                completed = true;
                if (statusCode == 200) {
                    processResult(content);
                } else {
                    errorRetrieving();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                completed = true;
                errorRetrieving();
            }

            @Override
            public void onFinish() {
                if (completed == false) {
                    errorRetrieving();
                }
            }
        });
    }

    private void processResult(String content) {
        // Log.v("CloudPhotos", content);
        ArrayList<HashMap<String, String>> containerList = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray array = new JSONArray(content);
            Integer countContainers = array.length();
            for (Integer i = 0; i < countContainers; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject container = array.getJSONObject(i);
                Integer count = container.getInt("count");
                String name = container.getString("name");
                Integer bytes = container.getInt("bytes");
                map.put("name", name);
                map.put("count", String.valueOf(count));
                map.put("bytes", String.valueOf(bytes));
                map.put("type", "container");
                containerList.add(map);
            }
        } catch (Exception e) {
            Log.v("CloudPhotos", "Exception");
            Log.v("CloudPhotos", e.getMessage());
            Log.v("CloudPhotos", e.getStackTrace().toString());
            errorRetrieving();
        }
        Collections.sort(containerList, new ArrayListHashMapSort("name"));
        HashMap<String, String> mapNew = new HashMap<String, String>();
        mapNew.put("name", "Add New Container");
        mapNew.put("count", "");
        mapNew.put("bytes", "");
        mapNew.put("type", "add");
        containerList.add(0, mapNew);

        ContainerListAdapter adapter = new ContainerListAdapter(this, containerList);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView listViewType = (TextView) view.findViewById(R.id.typeVal);
                if (listViewType.getText().toString().equals("add")) {
                    promptCreateContainer();
                } else {
                    TextView containerName = (TextView) view.findViewById(R.id.name);
                    Log.v("CloudPhotos", "Selected : " + containerName.getText().toString());
                    confirmContainerSelection(containerName.getText().toString());
                }
            }
        });
        dialog.dismiss();
    }

    public void promptCreateContainer() {
        final Dialog newDialog = new Dialog(activityContext);
        newDialog.setTitle("New Container");
        newDialog.setContentView(R.layout.provider_rackspace_setup_new_container);
        final TextView text = (TextView) newDialog.findViewById(R.id.containerName);
        final TextView errorText = (TextView) newDialog.findViewById(R.id.error_text);

        Button save = (Button) newDialog.findViewById(R.id.save);
        Button cancel = (Button) newDialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newDialog.dismiss();
            }
        });
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String containerName = text.getText().toString();
                if (containerName.isEmpty()) {
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    // errorText.setVisibility(View.GONE);
                    createContainer(containerName, newDialog);
                }
            }
        });

        newDialog.show();
    }

    private void createContainer(final String containerName, final Dialog newDialog) {
        newDialog.hide();
        RackspaceHttpClient clientFactory = new RackspaceHttpClient();
        AsyncHttpClient client = clientFactory.getAuthenticatedStorageClient(token);
        @SuppressWarnings("deprecation")
        String createUrl = storageUrl + "/" + URLEncoder.encode(containerName);
        client.put(createUrl, new AsyncHttpResponseHandler() {
            private Boolean completed = false;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                completed = true;
                if (statusCode == 201) {
                    newDialog.dismiss();
                    Log.v("CloudPhotos", "Container Created - Refresh");
                    confirmContainerSelection(containerName);
                } else {
                    errorCalling();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                completed = true;
                errorCalling();
            }

            private void errorCalling() {
                newDialog.show();
                TextView errorText = (TextView) newDialog.findViewById(R.id.error_text);
                errorText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                if (completed == false) {
                    errorCalling();
                }
            }
        });
    }

    private void confirmContainerSelection(final String name) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Container Selection");
        alert.setMessage("Are you sure you want to use the following container for uploads?\n\n" + name);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                config.setString(Setup.PREFS_USER_USERNAME, username);
                config.setString(Setup.PREFS_USER_APIKEY, apikey);
                config.setString(Setup.PREFS_AUTH_TOKEN, token);
                config.setString(Setup.PREFS_URL_ENDPOINT, endpoint);
                config.setString(Setup.PREFS_URL_STORAGE, storageUrl);
                config.setString(Setup.PREFS_CONTAINER_NAME, name);
                config.setBoolean(Setup.PREFS_KEY_HAS_ACCOUNT, true);
                confirmSaved();
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.setCancelable(false);
        alert.show();
    }

    private void confirmSaved() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Settings Saved");
        alert.setMessage("Your settings have been saved.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                back();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    private void errorRetrieving() {
        dialog.dismiss();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error retrieving containers");
        alert.setMessage("Unable to retrieve your container list. Please try again.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                back();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    private void back() {
        this.finish();
    }
}
