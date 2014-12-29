package com.track.android;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.track.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class TrackActivity extends Activity {
	private Button editButton;
	private Button addButton;
	private Button takePhotoButton;
	private EditText editText;
	private TextView textView;
	private TextView locationTextView;
	private TextView timeTextView;
	private EditText editLocationText;
	private TimePicker editTimePicker;
	private ImageAdapter imageAdapter;
	private final int IMAGE_CODE = 0;
	private final int CAMERA_CODE = 1;
	private boolean isChanged = false;
	public final static int TRACK_RESULT_CODE = 0xfff;
	public final static int ACTION_DELETE_CODE = -1;
	public final static int ACTION_NOTHING_CODE = 0;
	public final static int ACTION_CHANGE_CODE = 1;
	public final static String ACTION_STRING = "action";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//get the data from the mainActivity
		Intent intent = getIntent();
		TrackData data = (TrackData)intent.getSerializableExtra("data");
		
		setContentView(R.layout.activity_track);
		
		//initialize the private variable
		FixedGridView gridView = (FixedGridView)findViewById(R.id.photos);
		textView = (TextView)findViewById(R.id.text_record);
		addButton = (Button)findViewById(R.id.add_button);
		takePhotoButton = (Button)findViewById(R.id.takephoto_button);
		imageAdapter = new ImageAdapter(this);
		String photos = data.getPhotos();
		imageAdapter.addImages(photos);
		editButton = (Button)findViewById(R.id.edit_button);
		editText = (EditText)findViewById(R.id.textarea);
		locationTextView = (TextView)findViewById(R.id.location);
		editLocationText = (EditText)findViewById(R.id.location_edit);
		timeTextView = (TextView)findViewById(R.id.time);
		editTimePicker = (TimePicker)findViewById(R.id.time_edit);
		
		gridView.setAdapter(imageAdapter);
		Log.i("success", data.getDescription() + ":");
		((TextView)findViewById(R.id.date)).setText(data.getDate());
		timeTextView.setText(data.getHour() + ":" + data.getMinute());
		locationTextView.setText(data.getPlaceName());
		textView.setText(data.getDescription());

	}
	public static final int DIALOG_ID = 0;
	
	public void onDeleteButtonClick(View v){
		new AlertDialog.Builder(this).setTitle("确认删除吗？") 
	    .setIcon(android.R.drawable.ic_dialog_info) 
	    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	        @Override 
	        public void onClick(DialogInterface dialog, int which) {
	        	Intent intent = new Intent(getIntent());
	        	intent.putExtra(ACTION_STRING, ACTION_DELETE_CODE);
	        	setResult(RESULT_OK, intent);
	        	TrackActivity.this.finish();
	        }
	    })
	    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
	        @Override 
	        public void onClick(DialogInterface dialog, int which) {
	        } 
	    }).show(); 
	}
	
	public void onEditButtonClick(View v){
		if (textView.getVisibility() == View.VISIBLE)
		{
			isChanged = true;
			CharSequence str = textView.getText();
			editText.setText(str);
			textView.setVisibility(View.GONE);
			editText.setVisibility(View.VISIBLE);
			addButton.setVisibility(View.VISIBLE);
			takePhotoButton.setVisibility(View.VISIBLE);
			locationTextView.setVisibility(View.GONE);
			timeTextView.setVisibility(View.GONE);
			editLocationText.setVisibility(View.VISIBLE);
			editTimePicker.setVisibility(View.VISIBLE);
			editLocationText.setText(locationTextView.getText());
			String timeString = timeTextView.getText().toString();
			String strs[] = timeString.split(":");
			int hour = Integer.valueOf(strs[0]);
			int minute = Integer.valueOf(strs[1]);
			editTimePicker.setCurrentHour(hour);
			editTimePicker.setCurrentMinute(minute);
			imageAdapter.setEdit(true);
			editButton.setText("完成");
		}
		else
		{
			CharSequence str = editText.getText();
			textView.setText(str);
			editText.setVisibility(View.GONE);
			textView.setVisibility(View.VISIBLE);
			addButton.setVisibility(View.GONE);
			takePhotoButton.setVisibility(View.GONE);
			locationTextView.setVisibility(View.VISIBLE);
			timeTextView.setVisibility(View.VISIBLE);
			editLocationText.setVisibility(View.GONE);
			editTimePicker.setVisibility(View.GONE);
			locationTextView.setText(editLocationText.getText());
			int hour = editTimePicker.getCurrentHour();
			int minute = editTimePicker.getCurrentMinute();
			timeTextView.setText(hour + ":" + minute);
			imageAdapter.setEdit(false);
			editButton.setText("编辑");
		}
			
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
		if (isChanged){
			Intent intent = getIntent();
			TrackData data = (TrackData)intent.getSerializableExtra("data");
			data.setPlaceName(editLocationText.getText().toString());
			String time = ((TextView)findViewById(
					R.id.time)).getText().toString();
			String[] strs = time.split(":");
			int hour;
			int minute;
			if (strs.length < 2){
				Toast.makeText(getBaseContext(), "时间格式为hh:mm,时间修改失败", 
						Toast.LENGTH_SHORT).show();
				hour = data.getHour();
				minute = data.getMinute();
			}
			hour = Integer.valueOf(strs[0]);
			minute = Integer.valueOf(strs[1]);
			data.setHour(hour);
			data.setMinute(minute);
			data.setDescription(textView.getText().toString());
			data.setPhotos(imageAdapter.getImagesString());
			Log.i("success", data.toString());
			intent.putExtra("data", data);
			intent.putExtra(TrackActivity.ACTION_STRING, TrackActivity.ACTION_CHANGE_CODE);
			setResult(RESULT_OK, intent);
		}
		else{
			setResult(RESULT_CANCELED);
		}
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}