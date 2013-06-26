package com.cloud.cloudphotos;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.cloud.cloudphotos.data.Photo;
import com.cloud.cloudphotos.data.PhotoDatasource;
import com.cloud.cloudphotos.helper.BitmapCacheHelper;
import com.cloud.cloudphotos.helper.NetworkConnection;
import com.cloud.cloudphotos.provider.rackspace.RackspaceHttpClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class BackgroundService extends Service {

    static boolean isRunning = false;
    private ApplicationConfig config;
    private Boolean uploaderRunning = false;
    PhotoDatasource datasource;
    Context activityContext = this;
    Integer numberUploaded = 0;
    String cachePath;

    private final String TAG = "CloudPhotos";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.i(TAG, "CloudPhotos service created");
        makeCacheFolder();
    }

    private void makeCacheFolder() {
        cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CloudPhotos-Cache";
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            config = new ApplicationConfig(this.getApplicationContext());

            if (intent.getAction().endsWith("NEW_PICTURE")) {
                Uri uri = intent.getData();
                String filePath = getPathFromUri(uri);
                File photo = new File(filePath);
                storePhoto(photo, filePath);
                Log.i(TAG, "CloudPhotos photo detected.");
                Log.i(TAG, "CloudPhotos file path:");
                Log.i(TAG, filePath);
            } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                // Wifi state has changed to connected.
                evaluateCanRun();
            } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                // Wifi has been disconnected.
                evaluateCanRun();
            } else if (intent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                // Connection change.
                evaluateCanRun();
            }
        } else {
            Log.i(TAG, "CloudPhotos service created");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void storePhoto(File photo, String filePath) {
        PhotoDatasource datasource = new PhotoDatasource(this);
        datasource.open();
        String datestamp = getDatestamp();
        Photo added = datasource.createPhoto(filePath, datestamp);
        Log.i("CloudPhotos", "Added photo id " + added.getId());
        evaluateCanRun();
    }

    private void notifyNumberUploaded(Integer num) {
        if (num.equals(0)) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CloudPhotos - Uploads").setContentText("Your photos have been uploaded.")
                .setNumber(num);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Integer mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void evaluateCanRun() {
        Boolean wifiOnly = config.getBoolean("wifionly", true);
        if (wifiOnly) {
            // check if connected to wifi.
            Boolean isWifiConnected = NetworkConnection.isWiFiNetworkConnectionAvailable(activityContext);
            if (!isWifiConnected) {
                return; // Dont proceed, as it's wifi only.
            }
        } else {
            Boolean hasAvailableConnection = NetworkConnection.isNetworkConnectionAvailable(activityContext);
            if (!hasAvailableConnection) {
                return; // Dont proceed, as there's no network connection
                        // available.
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                datasource = new PhotoDatasource(activityContext);
                datasource.open();
                List<Photo> photos = datasource.getAllPhotos();
                for (Photo photo : photos) {
                    try {
                        File file = new File(photo.getPath());
                        String name = file.getName();
                        if (file.exists()) {
                            uploadPhoto(file, name, photo);
                        } else {
                            datasource.deletePhotoModel(photo);
                        }
                        break;
                    } catch (Exception e) {

                    }
                }
            }
        }, 4000);

    }

    public void uploadPhoto(File photo, String fileName, Photo model) {
        try {
            notifyStarted();
            runProviders(photo, fileName, model);
        } catch (Exception e) {
            Log.i(TAG, "CloudPhotos: Error processing providers for photo: " + model.getId());
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.i(TAG, "CloudPhotos destroyed");
        super.onDestroy();
    }

    private void notifyStarted() {
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

    private void cacheFile(File file, String fileName) {
        try {
            File fileCheck = new File(cachePath, fileName);
            if (fileCheck.exists()) {
                return;
            } else {
                generateCached(file, cachePath, fileName);
            }
        } catch (Exception e) {

        }
        return;
    }

    private void generateCached(File file, String cachePath, String fileName) {
        try {
            BitmapCacheHelper helper = new BitmapCacheHelper(file, cachePath, fileName);
            helper.execute(200, 200, 60);
        } catch (Exception e) {

        }
    }

    /**
     * Run the individual providers.
     * 
     * @param file
     * @param fileName
     * @param model
     */
    private void runProviders(File file, String fileName, Photo model) {
        try {
            runRackspace(file, fileName, model);
        } catch (Exception e) {
        }
    }

    /**
     * Runs a singular upload to Rackspace
     * 
     * @param file
     * @param fileName
     * @param model
     */
    private void runRackspace(final File file, final String fileName, final Photo model) {
        if (uploaderRunning == true) {
            return;
        }
        uploaderRunning = true;
        Log.i("CloudPhotos", "Rackspace Upload Starting");
        RackspaceHttpClient clientFactory = new RackspaceHttpClient();
        Boolean hasRackspace = config.getBoolean(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_KEY_HAS_ACCOUNT,
                false);
        if (hasRackspace == true) {
            final String authUrl = config.getString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_ENDPOINT,
                    "");
            final String userName = config.getString(
                    com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_USERNAME, "");
            final String apiKey = config
                    .getString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_APIKEY, "");
            final String authToken = config.getString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_AUTH_TOKEN,
                    "");
            final String storageUrl = config.getString(
                    com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_STORAGE, "");
            final String containerName = config.getString(
                    com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_CONTAINER_NAME, "");
            String url = storageUrl + "/" + URLEncoder.encode(containerName) + "/" + URLEncoder.encode(fileName);
            AsyncHttpClient client = clientFactory.getAuthenticatedStorageClient(authToken);

            String mime_type = getMimeTypeFromFilePath(file.getPath());

            FileEntity entity = new FileEntity(file, mime_type);
            client.put(getApplicationContext(), url, entity, mime_type, new AsyncHttpResponseHandler() {
                private Boolean completed = false;

                @Override
                public void onSuccess(int statusCode, Header[] headers, String content) {
                    completed = true;
                    if (statusCode == 201) {
                        uploaderRunning = false;
                        numberUploaded++;
                        notifyNumberUploaded(numberUploaded);
                        datasource.deletePhotoModel(model);
                        Log.i("CloudPhotos", "Rackspace Upload Completed");
                        cacheFile(file, fileName);
                        evaluateCanRun();
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
                    uploaderRunning = false;
                    Log.i("CloudPhotos", "Rackspace Upload Error - Reauthenticating");
                    reauthenticateRackspace(userName, apiKey, authUrl);
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

    private void reauthenticateRackspace(final String username, final String apikey, final String url) {
        RackspaceHttpClient httpClient = new RackspaceHttpClient();
        AsyncHttpClient client = httpClient.getAuthenticationClient(username, apikey);
        client.get(url, new AsyncHttpResponseHandler() {
            private Boolean completed = false;

            @Override
            public void onSuccess(int statusCode, Header[] headers, String content) {
                completed = true;
                if (statusCode == 204) {
                    rackspaceAuthenticationSuccess(headers, username, apikey, url);
                } else {
                    rackspaceAuthenticationFailed();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                completed = true;
                rackspaceAuthenticationFailed();
            }

            @Override
            public void onFinish() {
                if (completed == false) {
                    rackspaceAuthenticationFailed();
                }
            }
        });
    }

    public void rackspaceAuthenticationFailed() {
        Log.i("CloudPhotos", "Rackspace Reauthentication failed.");
        clearRackspaceValues();
    }

    private void clearRackspaceValues() {
        config = new ApplicationConfig(this.getApplicationContext());
        config.unsetString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_AUTH_TOKEN);
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
    public void rackspaceAuthenticationSuccess(Header[] headers, String username, String apikey, String authUrl) {
        clearRackspaceValues();
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
            rackspaceAuthenticationFailed();
        } else {
            config = new ApplicationConfig(this.getApplicationContext());
            config.setBoolean(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_KEY_HAS_ACCOUNT, true);
            config.setString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_AUTH_TOKEN, token);
            config.setString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_ENDPOINT, authUrl);
            config.setString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_URL_STORAGE, storageUrl);
            config.setString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_APIKEY, apikey);
            config.setString(com.cloud.cloudphotos.provider.rackspace.Setup.PREFS_USER_USERNAME, username);
            evaluateCanRun();
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

    private String getDatestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
}