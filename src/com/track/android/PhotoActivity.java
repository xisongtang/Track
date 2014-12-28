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
import android.view.View;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	ArrayList<Uri> uris;
	int index;
	ImageView imageView;
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
			
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageView = (ImageView)findViewById(R.id.imageview);
        Intent intent = getIntent();
        
        uris = (ArrayList<Uri>)intent.getSerializableExtra("photos");
        index = intent.getIntExtra("index", 0);
        updateView();
	}
	public void onLeftButtonClick(View v){
		index = (index - 1 < 0)? uris.size() - 1:index - 1;
		updateView();
	}
	public void onRightButtonClick(View v){
		index = (index + 1 == uris.size()) ? 0 : index + 1;
		updateView();
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
