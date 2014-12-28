package com.track.android;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class TrackService extends Service {
	private GeoCoder mSearch = GeoCoder.newInstance();{
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null)
					return;
				LatLng loc = result.getLocation();
				if (loc == null)
					return;
				TrackData data = new TrackData(result.getAddress(), loc.latitude, 
						loc.longitude, dateString, hour, minute, "", "");
				PhotoGetter photoGetter = new PhotoGetter();
				DatabaseHelper db = new DatabaseHelper(getApplicationContext());
				TrackData last = db.selectLatestData(dateString, hour, minute);
				//计算时间间隔的开始时间
				Date lastDate;
				if (last == null){
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
					try {
						lastDate = df.parse(dateString);
					} catch (ParseException e) {
						lastDate = new Date();
						e.printStackTrace();
					}					
				}
				else{
					lastDate = last.getDateType();
				}
				//计算时间间隔的结束时间
				Date currentDate = data.getDateType();
				ArrayList<File> files = (ArrayList<File>) photoGetter.getPhotosDuringDates(lastDate, currentDate);
				String photoString = "";
				for (File file:files){
					photoString += file.toURI() + ";";
				}
				data.setPhotos(photoString);
				Log.i("success", photoString + "...");
				db.insertData(data);
				db.close();
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
			}
		});
		}
	private LocationListener locationListener = new LocationListener() {
		
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
				Log.i("success", "in call-back function::lat:" + location.getLatitude() + 
						":lng:" + location.getLongitude());
			else
				Log.i("success", "in call-back function::null");
		}
	};
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
			//Log.i("success", currentTime +":" + lastTime);
			if (currentTime - lastTime < 1000){
				return;
			}
			lastTime = currentTime;
			SettingData settings = new SettingData(TrackService.this.getApplicationContext());
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
			if (!settings.isOn())
				return;
			if (hour < settings.getStartHour() || hour >= settings.getEndHour())
				return;
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(false);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = locationManager.getBestProvider(criteria, true);
			locationManager.requestSingleUpdate(provider, locationListener,
					Looper.myLooper());
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
			else
				Log.e("success", "null location");
		}
	};
	
	@Override
	public void onCreate() {
		Log.d("success", "Service created");
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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
