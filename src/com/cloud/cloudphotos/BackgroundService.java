package com.cloud.cloudphotos;

import java.util.Random;

import com.cloud.cloudphotos.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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
        final Integer notificationId = (new Random()).nextInt(100) + 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CloudPhotos Service Started")
                .setContentText("Listening listening for events...");
        Intent resultIntent = new Intent(this, CloudPhotos.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(CloudPhotos.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // ID allows interaction later down the line,
        mNotificationManager.notify(notificationId, mBuilder.build());
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                mNotificationManager.cancel(notificationId);
            }
        }, 1000);
    }
}