package com.track.android;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class TrackService extends Service {
	private GeoCoder mSearch = GeoCoder.newInstance();{
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				LatLng loc = result.getLocation();
				TrackData data = new TrackData(result.getAddress(), loc.latitude, 
						loc.longitude, dateString, hour, minute, "", "");
				DatabaseHelper db = new DatabaseHelper(getApplicationContext());
				db.insertData(data);
				db.close();
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
			}
		});
		}
	private LocationManager locationManager;
	public TrackService() {
		Log.d("success", "Service constructors");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private long lastTime = 0;
	private String dateString;
	private int hour;
	private int minute;
	boolean isFirstTask = true;
	Handler handler = new Handler();
	Runnable task = new Runnable() {
		
		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			Log.i("success", currentTime +":" + lastTime);
			if (currentTime - lastTime < 10000){
				return;
			}
			lastTime = currentTime;
			SettingClass settings = new SettingClass(TrackService.this.getApplicationContext());
			Date date = new Date();
			long timeBase = date.getTime();
			Log.d("success", date.toString() + ":loop task running");
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTime(date);
			calendar.set(Calendar.SECOND, 0);
			dateString = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) 
					+ "-" + calendar.get(Calendar.DAY_OF_MONTH);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
			minute = minute / settings.getTimeInterval() * settings.getTimeInterval();
			calendar.set(Calendar.MINUTE, minute);
			calendar.add(Calendar.MINUTE, settings.getTimeInterval());
			Log.d("success", "after calculate:" + (calendar.getTimeInMillis() - timeBase));
			handler.postDelayed(task, calendar.getTimeInMillis() - timeBase);
			if (isFirstTask){
				isFirstTask = false;
				return;
			}
			Location location = locationManager.getLastKnownLocation(
					LocationManager.GPS_PROVIDER);
			if (location != null){
				Log.i("success", "lat:" + location.getLatitude() + 
						":lng:" + location.getLongitude());
				LatLng src = new LatLng(location.getLatitude(), location.getLongitude());
				CoordinateConverter converter = new CoordinateConverter();
				converter.coord(src);
				converter.from(CoordType.GPS);
				
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(
						converter.convert()));
			}
		}
	};
	
	@Override
	public void onCreate() {
		Log.d("success", "Service created");

		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, 
				new LocationListener() {
					
					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {
					}
					
					@Override
					public void onProviderEnabled(String provider) {
					}
					
					@Override
					public void onProviderDisabled(String provider) {
					}
					
					@Override
					public void onLocationChanged(Location location) {
						if (location != null)
							Log.i("success", "lat:" + location.getLatitude() + 
									":lng:" + location.getLongitude());
						
					}
				});
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
		Log.d("success", "Service destroyed");
	}

}
