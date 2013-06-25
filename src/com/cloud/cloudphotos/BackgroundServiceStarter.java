package com.cloud.cloudphotos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class BackgroundServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String iName = intent.getAction();
        Log.i("CloudPhotos", "Intent received: " + iName);
        if ("android.intent.action.BOOT_COMPLETED".equals(iName)) {
            Intent i = new Intent(context, BackgroundService.class);
            context.startService(i);
        } else if ("android.hardware.action.NEW_PICTURE".equals(iName)
                || "com.android.camera.NEW_PICTURE".equals(iName)) {
            if (intent.getData() != null) {
                Intent i = new Intent(context, BackgroundService.class);
                i.setData(intent.getData());
                i.setAction(iName);
                context.startService(i);
            }
        } else if (iName.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                Log.i("CloudPhotos", "Wifi connected");
                Intent i = new Intent(context, BackgroundService.class);
                i.setData(intent.getData());
                i.setAction(iName);
                context.startService(i);
            }
        } else if (iName.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            @SuppressWarnings("deprecation")
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && !networkInfo.isConnected()) {
                // Wifi is disconnected
            }
        } else if (iName.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
            if (connected) {
                // start your service here
                Log.i("CloudPhotos", "Wifi connected");
                Intent i = new Intent(context, BackgroundService.class);
                i.setData(intent.getData());
                i.setAction(iName);
                context.startService(i);
            }
        }
    }
}