package com.track.android;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	private ArrayList<Uri> uris;
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener(){
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float deltaX = e1.getX() - e2.getX();
			float deltaY = e1.getY() - e2.getY();
			if (Math.abs(deltaY) > Math.abs(deltaX))
				return false;
			if (deltaX < -imageView.getWidth() / 3){
				onRightButtonClick(imageView);
				return true;
			}
			else if (deltaX > imageView.getWidth() / 3){
				onLeftButtonClick(imageView);
				return true;
			}
			
			return false;
		}
	};
	private GestureDetector detector;
	int index;
	ImageView imageView;
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        imageView = (ImageView)findViewById(R.id.imageview);
        Intent intent = getIntent();
        uris = (ArrayList<Uri>)intent.getSerializableExtra("photos");
        index = intent.getIntExtra("index", 0);
        updateView();
        detector = new GestureDetector(getBaseContext(), gestureListener);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
	public void onLeftButtonClick(View v){
		index = (index - 1 < 0)? uris.size() - 1:index - 1;
		Intent intent = getIntent();
		intent.putExtra("index", index);
		intent.setClass(PhotoActivity.this, PhotoActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
	}
	public void onRightButtonClick(View v){
		index = (index + 1 == uris.size()) ? 0 : index + 1;
		Intent intent = getIntent();
		intent.putExtra("index", index);
		intent.setClass(PhotoActivity.this, PhotoActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
	private void updateView(){
		Uri uri = uris.get(index);
        Bitmap bitmap;
		ContentResolver resolver = getContentResolver();
		ParcelFileDescriptor parcelFileDescriptor;
		try {
			parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			imageView.setImageBitmap(bitmap);
			parcelFileDescriptor.close();
		} catch (FileNotFoundException e) {
			Log.e("error", e.toString());
		} catch (IOException e) {
			Log.e("error", e.toString());
		}
	}
}
