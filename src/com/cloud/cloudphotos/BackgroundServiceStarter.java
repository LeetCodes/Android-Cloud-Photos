package com.cloud.cloudphotos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackgroundServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	String iName = intent.getAction();
        if ("android.intent.action.BOOT_COMPLETED".equals(iName)) {
            Intent i = new Intent(context, BackgroundService.class);
            context.startService(i);
        }
        else if (
            "android.hardware.action.NEW_PICTURE".equals(iName) ||
            "com.android.camera.NEW_PICTURE".equals(iName)
        ) {
        	if (intent.getData() != null) {
        		Intent i = new Intent(context, BackgroundService.class);
            	i.setData(intent.getData());
            	i.setAction(iName);
            	context.startService(i);
        	}
        }
    }
}