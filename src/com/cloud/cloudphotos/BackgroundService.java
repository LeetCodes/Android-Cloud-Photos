package com.cloud.cloudphotos;

import java.io.File;
import java.util.Random;

import com.cloud.cloudphotos.R;

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
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null
				&& intent.getAction().endsWith("NEW_PICTURE")) {
			try {
				notifyStarted();
				Uri uri = intent.getData();
				String filePath = getPathFromUri(uri);
				File photo = new File(filePath);
				String fileName = photo.getName();
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
		Notification.Builder nBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setTicker("CloudPhotos Processing...");

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
}