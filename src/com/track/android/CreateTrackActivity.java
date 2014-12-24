package com.track.android;

import com.track.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateTrackActivity extends Activity {
	Button editButton;
	Button addButton;
	Button takePhotoButton;
	EditText editText;
	TextView textView;
	ImageAdapter imageAdapter;
	Intent intent;
	public static final int RESULT_CODE = 0xff;
	public void onSubmitButtonClick(View v){
		String placeName = ((TextView)findViewById(R.id.location)).getText().toString();
		Log.i("success", placeName + "::");
		int hour = ((TimePicker)findViewById(R.id.time)).getCurrentHour();
		int minute = ((TimePicker)findViewById(R.id.time)).getCurrentMinute();
		String description = ((TextView)findViewById(R.id.textarea)).getText().toString();
		TrackData data = new TrackData(placeName, intent.getDoubleExtra(
				TrackData.LATITUDE_STRING, TrackData.DEFAULT_LATITUDE), 
				intent.getDoubleExtra(TrackData.LONGITUDE_STRING, TrackData.DEFAULT_LONGITUDE), 
				intent.getStringExtra(TrackData.DATE_STRING), hour, minute, description, null);
		Intent result = new Intent();
		result.putExtra("data", data);
		setResult(RESULT_OK, result);
		
		finish();
	}
	public void onCancelButtonClick(View v){
		setResult(RESULT_CANCELED);
		finish();
	}
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_track);
		FixedGridView gridView = (FixedGridView)findViewById(R.id.photos);
		imageAdapter = new ImageAdapter(this, getAssets());
		gridView.setAdapter(imageAdapter);
		editButton = (Button)findViewById(R.id.edit_button);
		editText = (EditText)findViewById(R.id.textarea);
		((TextView)findViewById(R.id.date)).setText(intent.getStringExtra(TrackData.DATE_STRING));
		((TextView)findViewById(R.id.location)).setText(intent.getStringExtra(TrackData.PLACENAME_STRING));
		textView = (TextView)findViewById(R.id.text_record);
		addButton = (Button)findViewById(R.id.add_button);
		takePhotoButton = (Button)findViewById(R.id.takephoto_button);
	}
}