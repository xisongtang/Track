package com.track.android;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.util.Log;

public class TrackApplication extends Application {

	public TrackApplication() {
		Log.d("success", "Application constructor");
	}
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("success", "Application onCreate");
		SDKInitializer.initialize(getApplicationContext()); 
	}
	
}
