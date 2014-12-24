package com.track.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TrackService extends Service {
	private LocationClient locationClient;
	public TrackService() {
		Log.d("success", "Service constructors");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	Handler handler = new Handler();
	volatile boolean needRequest = false; 
	Runnable task = new Runnable() {
		
		@Override
		public void run() {
			SettingClass settings = new SettingClass(TrackService.this.getApplicationContext());
			Date date = new Date();
			long timeBase = date.getTime();
			Log.d("success", date.toString() + ":loop task running");
			int ret = locationClient.requestLocation();
			needRequest = true;
			Log.d("success", "return: "+ ret);
			
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTime(date);
			calendar.set(Calendar.SECOND, 0);
			int minute = calendar.get(Calendar.MINUTE);
			calendar.set(Calendar.MINUTE, minute / settings.getTimeInterval()
					* settings.getTimeInterval());
			calendar.add(Calendar.MINUTE, settings.getTimeInterval());
			Log.d("success", "after calculate:" + (calendar.getTimeInMillis() - timeBase));
			handler.postDelayed(task, calendar.getTimeInMillis() - timeBase);
		}
	};
	
	@Override
	public void onCreate() {
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation loc) {
				if (!needRequest)
					return;
				Date date = new Date();
				Calendar calendar = Calendar.getInstance(Locale.CHINA);
				calendar.setTime(date);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
		        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				TrackData data = new TrackData(loc.getAddrStr(), loc.getLatitude(), 
						loc.getLongitude(), df.format(date), hour, minute, 
						null, null);
				Log.i("success", "onReceiveLocation:" + data.toString());
				DatabaseHelper db = new DatabaseHelper(getBaseContext());
				db.insertData(data);
				db.close();
				needRequest = false;
			}
		});
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(false);
		option.setScanSpan(5000);
		locationClient.setLocOption(option);
		locationClient.start();
		Log.d("success", "isStarted:" + locationClient.isStarted());
		Log.d("success", "Service created");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("success", "Service starting");
		handler.postDelayed(task, 1000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy(){
		if (locationClient != null && locationClient.isStarted())
			locationClient.stop();
		Log.d("success", "Service destroyed");
	}

}
