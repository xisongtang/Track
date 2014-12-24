package com.track.android;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private Context context;
	ArrayList<Bitmap> imageArray = new ArrayList<Bitmap>();
	ArrayList<Uri> uriArray = new ArrayList<Uri>();
	void addImage(Uri uri){
		uriArray.add(uri);
		Bitmap bitmap;
		ContentResolver resolver = context.getContentResolver();
		ParcelFileDescriptor parcelFileDescriptor;
		try {
			parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			imageArray.add(bitmap);
			parcelFileDescriptor.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("error", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("error", e.toString());
		}
		 
		notifyDataSetChanged();
	}
	public ImageAdapter(Context context, AssetManager assetsmanager){
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageArray.size();
	}

	@Override
	public Object getItem(int n) {
		// TODO Auto-generated method stub
		return imageArray.get(n);
	}

	@Override
	public long getItemId(int n) {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public View getView(int n, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageViewWithUri imageView;
		if (convertView == null){
			imageView = new ImageViewWithUri(context);
			imageView.setDrawingCacheEnabled(true);
			imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 150));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setUri(uriArray.get(n));
			imageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(context, PhotoActivity.class);
					Log.i("success", "id = " + Integer.toString(v.getId()));
					intent.putExtra("photo", ((ImageViewWithUri)v).getUri());
					context.startActivity(intent);
				}
				
			});
		}
		else
		{
			imageView = (ImageViewWithUri) convertView;
		}
		imageView.setImageBitmap(imageArray.get(n));
		return imageView;
	}

}
