package com.track.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.track.android.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
	int displayWidth;
	private final int DURATION = 500;
	private LinearLayout menu;
	private MapView mapView;
	private BaiduMap map;
	private ArrayList<Overlay> overlays = new ArrayList<Overlay>();
	private AlphaAnimation disappearAlpha;
	private AlphaAnimation appearAlpha;
	private Bitmap bitmap;
	private Paint paint;
	{
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
	}
	private volatile String resultPlaceName = "";
	private GeoCoder mSearch = GeoCoder.newInstance();{
	mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
		
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			resultPlaceName = result.getAddress();
			LatLng loc = result.getLocation();
			Log.i("success", resultPlaceName + "---");
			Log.i("success", resultPlaceName + ":");
			DatePicker datePicker = (DatePicker)menu.findViewById(R.id.datepicker);
			Intent intent = new Intent();
			String date = datePicker.getYear() + "-" 
    				+ (datePicker.getMonth() + 1) + "-"
    				+ datePicker.getDayOfMonth();
			intent.putExtra(TrackData.LATITUDE_STRING, loc.latitude);
			intent.putExtra(TrackData.LONGITUDE_STRING, loc.longitude);
			intent.putExtra(TrackData.DATE_STRING,date);
			intent.putExtra(TrackData.PLACENAME_STRING, resultPlaceName);
			intent.setClass(getBaseContext(), CreateTrackActivity.class);
			startActivityForResult(intent, CreateTrackActivity.RESULT_CODE);
		}
		
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
		}
	});
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), TrackService.class));
        mapView = (MapView)findViewById(R.id.mapview);
        map = mapView.getMap();
        menu = (LinearLayout)findViewById(R.id.menu);
        
        bitmap = BitmapFactory.decodeResource(getResources(), 
    			R.drawable.overlay);
        disappearAlpha = new AlphaAnimation(0.8f, 0);
        appearAlpha = new AlphaAnimation(0, 0.8f);
        disappearAlpha.setDuration(DURATION);
        appearAlpha.setDuration(DURATION);
        map.setOnMapLongClickListener(longClickListener);
        map.setOnMarkerClickListener(markerClickListener);
        map.setOnMarkerDragListener(markerDragListener);
        map.setOnMapClickListener(mapClickListener);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        showOverlays(df.format(date));
    }
    private void showOverlays(String date){
    	new AsyncTask<String, Integer, ArrayList<TrackData>>() {

			@Override
			protected ArrayList<TrackData> doInBackground(String... params) {
		        DatabaseHelper db = new DatabaseHelper(getBaseContext());
		    	ArrayList<TrackData> datas = db.selectData(params[0]);
		    	db.close();
				return datas;
			}
			
			@Override
			protected void onPostExecute(ArrayList<TrackData> datas) {
		    	MapStatusUpdate update;
				if (datas.size() == 0){
					map.clear();
					update = MapStatusUpdateFactory.newLatLng(new LatLng(
							TrackData.DEFAULT_LATITUDE, TrackData.DEFAULT_LONGITUDE));
					map.setMapStatus(update);
					return;
				}
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
		        for (int i = 0; i != datas.size(); ++i){
		        	Bundle bundle = new Bundle();
		        	bundle.putSerializable("data", datas.get(i));
		        	LatLng loc = new LatLng(datas.get(i).getLatitude(), datas.get(i).getLongitude());
		        	builder.include(loc);
		        	Bitmap currentBitmap = bitmap.copy(bitmap.getConfig(), true);
		        	Canvas canvas = new Canvas(currentBitmap);
		        	canvas.drawText(String.valueOf(i + 1), canvas.getWidth()/2, 
		        			canvas.getHeight()/2 + 2, paint);
		        	BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(currentBitmap);
		        	MarkerOptions markerOptions = new MarkerOptions().position(loc)
							.extraInfo(bundle).draggable(true).title("title").icon(bitmapDescriptor);
		        	overlays.add(map.addOverlay(markerOptions));
		        	if (i != 0){
		        		Log.i("success", String.valueOf(i));
		        		PolylineOptions line = new PolylineOptions();
						ArrayList<LatLng> points = new ArrayList<LatLng>();
						points.add(markerOptions.getPosition());
						points.add(((Marker)overlays.get(overlays.size() - 2)).getPosition());
						line.points(points).color(Color.BLUE).width(4);
						map.addOverlay(line);
		        	}
		        }
		        update = MapStatusUpdateFactory.newLatLngBounds(builder.build());
		        map.setMapStatus(update);
			}
		}.execute(date);
    }
    
    public void onSearchButtonClick(View v){
    	if (menu.getVisibility() == View.GONE)
    	{
	    	menu.setVisibility(View.VISIBLE);
	    	menu.startAnimation(appearAlpha);
    	}
    	else 
    	{
    		DatePicker datePicker = (DatePicker)menu.findViewById(R.id.datepicker);
    		String date = datePicker.getYear() + "-" 
    				+ (datePicker.getMonth() + 1) + "-"
    				+ datePicker.getDayOfMonth();
    		Log.i("success", date);
    		map.clear();
    		showOverlays(date);
	    	menu.startAnimation(disappearAlpha);
    		menu.setVisibility(View.GONE);
    	}
    }
    @Override
    protected void onDestroy() {  
        super.onDestroy();
        mapView.onDestroy();  
    }
    @Override
    protected void onResume() {  
        super.onResume();
        mapView.onResume();
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        mapView.onPause();  
    }
    BaiduMap.OnMapLongClickListener longClickListener = new BaiduMap.OnMapLongClickListener(){

		@Override
		public void onMapLongClick(LatLng loc) {
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(loc));
			Log.i("success", "onMapLongClick");
		}
    };
    BaiduMap.OnMapClickListener mapClickListener = new OnMapClickListener() {
		
		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			return false;
		}
		
		@Override
		public void onMapClick(LatLng arg0) {
			map.hideInfoWindow();
		}
	};
    BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener(){

		@Override
		public boolean onMarkerClick(final Marker marker) {
			map.hideInfoWindow();
			TrackData data = (TrackData)marker.getExtraInfo().getSerializable("data");
			View view = View.inflate(getBaseContext(), R.layout.info_window, null);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("data", marker.getExtraInfo().getSerializable("data"));
					intent.setClass(getBaseContext(), TrackActivity.class);
					startActivityForResult(intent, TrackActivity.TRACK_RESULT_CODE);
				}
			});
			InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(), -47);
			((TextView)view.findViewById(R.id.window_time)).setText(
					data.getHour() + ":" + data.getMinute());
			((TextView)view.findViewById(R.id.window_location)).setText(data.getPlaceName());
			map.showInfoWindow(infoWindow);
			return false;
		}
    	
    };
    BaiduMap.OnMarkerDragListener markerDragListener = new BaiduMap.OnMarkerDragListener() {
		
		@Override
		public void onMarkerDragStart(Marker arg0) {
		}
		
		@Override
		public void onMarkerDragEnd(Marker marker) {
			LatLng pos = marker.getPosition();
			TrackData data = (TrackData)marker.getExtraInfo().getSerializable("data");
			data.setLatitude(pos.latitude);
			data.setLongitude(pos.longitude);
			DatabaseHelper db = new DatabaseHelper(getBaseContext());
			db.updateData(data);
			db.close();
			map.clear();
			showOverlays(data.getDate());
		}
		
		@Override
		public void onMarkerDrag(Marker arg0) {
		}
	};
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode != RESULT_OK) {
			Log.e("error","ActivityResult resultCode error");
			return;
		}
		if (requestCode == CreateTrackActivity.RESULT_CODE){
			TrackData tdata = (TrackData)data.getSerializableExtra("data");
			if (tdata == null)
				Log.i("success", "nothing");
			Log.i("success", tdata.toString());
			DatabaseHelper db = new DatabaseHelper(getBaseContext());
			db.insertData(tdata);
			db.close();
			map.clear();
			showOverlays(tdata.getDate());
		}
		else if (requestCode == TrackActivity.TRACK_RESULT_CODE){
			TrackData tdata = (TrackData)data.getSerializableExtra("data");
			Log.i("success", tdata.toString());
			if (data.getIntExtra(TrackActivity.ACTION_STRING, 0) == TrackActivity.ACTION_DELETE_CODE){
				DatabaseHelper db = new DatabaseHelper(getBaseContext());
				db.deleteData(tdata);
				db.close();
				map.clear();
				showOverlays(tdata.getDate());
			}
			else{
				DatabaseHelper db = new DatabaseHelper(getBaseContext());
				db.updateData(tdata);
				db.close();
				map.clear();
				showOverlays(tdata.getDate());
			}
		}
	}
}
