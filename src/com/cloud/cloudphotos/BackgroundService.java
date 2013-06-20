package com.cloud.cloudphotos;

import java.util.Random;

import com.cloud.cloudphotos.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {

    static boolean isRunning = false;
	
    private String TAG = "CloudPhotos";
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        Log.v(TAG, "CloudPhotos service created");
        notifyStarted();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "CloudPhotos service started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
    	isRunning = false;
        Log.v(TAG, "CloudPhotos destroyed");
        super.onDestroy();
    }

    private void notifyStarted() {
        Log.v(TAG, "CloudPhotos sending notification");
        Integer nId = (new Random()).nextInt(100) + 1;
        Notification.Builder nBuilder = new Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setAutoCancel(true)
            .setTicker("CloudPhotos Service Started");

        NotificationManager nManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(nId, nBuilder.build());
        nManager.cancel(nId);
    }
}