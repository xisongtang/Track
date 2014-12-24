package com.track.android;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
			
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ImageView imageView = (ImageView)findViewById(R.id.imageview);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("photo");
        Log.i("success", "" + (uri == null));
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
			// TODO Auto-generated catch block
			Log.e("error", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("error", e.toString());
		}
	}
}
