package com.cloud.cloudphotos;

import java.io.File;
import java.net.URLEncoder;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.entity.FileEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.cloud.cloudphotos.provider.rackspace.RackspaceHttpClient;
import com.cloud.cloudphotos.provider.rackspace.Setup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class BackgroundService extends Service {

    static boolean isRunning = false;
    private ApplicationConfig config;

    private final String TAG = "CloudPhotos";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.v(TAG, "CloudPhotos service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().endsWith("NEW_PICTURE")) {
            config = new ApplicationConfig(this.getApplicationContext());
            try {
                notifyStarted();
                Uri uri = intent.getData();
                String filePath = getPathFromUri(uri);
                File photo = new File(filePath);
                String fileName = photo.getName();
                runProviders(photo, fileName);
                Log.v(TAG, "CloudPhotos photo detected.");
                Log.v(TAG, "CloudPhotos file path:");
                Log.v(TAG, filePath);
            } catch (Exception e) {
                Log.v(TAG, "CloudPhotos: Error receiving photo.");
            }
        } else {
            Log.v(TAG, "CloudPhotos service created");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.v(TAG, "CloudPhotos destroyed");
        super.onDestroy();
    }

    private void notifyStarted() {
        Log.v(TAG, "CloudPhotos Processing...");
        Integer nId = (new Random()).nextInt(100) + 1;
        Notification.Builder nBuilder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true).setTicker("CloudPhotos Processing...");

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(nId, nBuilder.build());
        nManager.cancel(nId);
    }

    /**
     * Retrieve the path to the given URI
     * 
     * @param contentURI
     * @return
     */
    private String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void runProviders(File file, String fileName) {
        try {
            runRackspace(file, fileName);
        } catch (Exception e) {
        }
    }

    private void runRackspace(File file, String fileName) {
        Log.v("CloudPhotos", "Running Rackspace Upload");
        RackspaceHttpClient clientFactory = new RackspaceHttpClient();
        Setup setup = new com.cloud.cloudphotos.provider.rackspace.Setup();
        Boolean hasRackspace = config.getBoolean(setup.PREFS_KEY_HAS_ACCOUNT, false);
        if (hasRackspace == true) {
            String authToken = config.getString(setup.PREFS_AUTH_TOKEN, "");
            String storageUrl = config.getString(setup.PREFS_URL_STORAGE, "");
            String containerName = config.getString(setup.PREFS_CONTAINER_NAME, "");
            String url = storageUrl + "/" + URLEncoder.encode(containerName) + "/" + URLEncoder.encode(fileName);
            AsyncHttpClient client = clientFactory.getAuthenticatedStorageClient(authToken);
            Log.v("CloudPhotos", "Uploading");

            String mime_type = getMimeTypeFromFilePath(file.getPath());

            FileEntity entity = new FileEntity(file, mime_type);
            client.put(getApplicationContext(), url, entity, mime_type, new AsyncHttpResponseHandler() {
                private Boolean completed = false;

                @Override
                public void onSuccess(int statusCode, Header[] headers, String content) {
                    completed = true;
                    if (statusCode == 201) {
                        Log.v("CloudPhotos", "Rackspace Upload Completed");
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
                    Log.v("CloudPhotos", "Rackspace Error Uploading");
                }

                @Override
                public void onFinish() {
                    if (completed == false) {
                        errorCalling();
                    }
                }
            });

        }
    }

    /**
     * Retrieve the mime type for a file.
     * 
     * @param url
     * @return
     */
    private String getMimeTypeFromFilePath(String url) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        String mime_type = map.getMimeTypeFromExtension(ext);
        return mime_type;
    }
}