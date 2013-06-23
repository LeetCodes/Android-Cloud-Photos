package com.cloud.cloudphotos.provider.rackspace;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cloud.cloudphotos.ApplicationConfig;
import com.cloud.cloudphotos.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RackspaceChooseContainer extends Activity {

    ApplicationConfig config;
    String username;
    String apikey;
    String token;
    String storageUrl;
    ListView list;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_rackspace_choose_container);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            apikey = extras.getString("apikey");
            token = extras.getString("token");
            storageUrl = extras.getString("storageUrl");
            if (username.isEmpty() || apikey.isEmpty() || token.isEmpty() || storageUrl.isEmpty()) {
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
                containerList.add(map);
            }
        } catch (Exception e) {
            Log.v("CloudPhotos", "Exception");
            Log.v("CloudPhotos", e.getMessage());
            Log.v("CloudPhotos", e.getStackTrace().toString());
            errorRetrieving();
        }

        ContainerListAdapter adapter = new ContainerListAdapter(this, containerList);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

            }
        });
        dialog.dismiss();
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
