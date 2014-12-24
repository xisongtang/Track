package com.track.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	public BootReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("success", "start up broadcast received");
		Intent serviceIntent = new Intent(context, TrackService.class);
		context.startService(serviceIntent);
	}

}
