package com.track.android;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.track.android.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
	private final int IMAGE_CODE = 0;
	private final int CAMERA_CODE = 1;
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
				intent.getStringExtra(TrackData.DATE_STRING), hour, minute, description, imageAdapter.getImagesString());
		Intent result = new Intent();
		result.putExtra("data", data);
		setResult(RESULT_OK, result);
		
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
	public void onAddButtonClick(View v){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, IMAGE_CODE);
	}
	Uri cameraUri;
	public void onPhotoButtonClick(View v){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
		File DCIM = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM);
		File out = new File(DCIM.getAbsolutePath() + "/Camera/" + df.format(
				new Date()) + ".jpg");
		cameraUri = Uri.fromFile(out);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(intent, CAMERA_CODE);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_track);
		FixedGridView gridView = (FixedGridView)findViewById(R.id.photos);
		imageAdapter = new ImageAdapter(this);
		gridView.setAdapter(imageAdapter);
		editButton = (Button)findViewById(R.id.edit_button);
		editText = (EditText)findViewById(R.id.textarea);
		((TextView)findViewById(R.id.date)).setText(intent.getStringExtra(TrackData.DATE_STRING));
		((TextView)findViewById(R.id.location)).setText(intent.getStringExtra(TrackData.PLACENAME_STRING));
		textView = (TextView)findViewById(R.id.text_record);
		addButton = (Button)findViewById(R.id.add_button);
		takePhotoButton = (Button)findViewById(R.id.takephoto_button);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode != RESULT_OK) {
			Log.e("error","ActivityResult resultCode error");
			return;
		}
		if (requestCode == IMAGE_CODE){
			Uri originalUri = data.getData();
			Log.i("success", "Uri: " + originalUri.toString());
			imageAdapter.addImage(originalUri);
		}
		else if (requestCode == CAMERA_CODE){
			imageAdapter.addImage(cameraUri);
		}
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}