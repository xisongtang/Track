package com.track.android;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{
	private Context context;
	private boolean isEdit = false;
	private ArrayList<Bitmap> imageArray = new ArrayList<Bitmap>();
	private ArrayList<Uri> uriArray = new ArrayList<Uri>();
	public void addImage(Uri uri){
		ContentResolver resolver = context.getContentResolver();
		addImage(uri, resolver);
		notifyDataSetChanged();
	}
	private void addImage(Uri uri, ContentResolver resolver){
		Bitmap bitmap;
		ParcelFileDescriptor parcelFileDescriptor;
		try {
			parcelFileDescriptor = resolver.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			imageArray.add(bitmap);
			uriArray.add(uri);
			parcelFileDescriptor.close();
		} catch (FileNotFoundException e) {
			Log.e("error", e.toString());
		} catch (IOException e) {
			Log.e("error", e.toString());
		}
	}
	
	public void addImages(String string){
		ContentResolver resolver = context.getContentResolver();
		String[] strs = string.split(";");
		Log.i("success",string);
		for (int i = 0; i != strs.length; ++i){
			Uri uri = Uri.parse(strs[i]);
			addImage(uri, resolver);
		}
		notifyDataSetChanged();
	}
	public void removeImage(Uri uri){
		for (int i = 0; i != uriArray.size(); ++i){
			if (uri.equals(uriArray.get(i))){
				uriArray.remove(i);
				imageArray.remove(i);
				break;
			}
		}
	}
	public String getImagesString(){
		String string = "";
		for (Uri uri : uriArray){
			string += uri.toString() + ";";
		}
		return string;
	}
	public ImageAdapter(Context context){
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return imageArray.size();
	}

	@Override
	public Object getItem(int n) {
		return imageArray.get(n);
	}

	@Override
	public long getItemId(int n) {
		return n;
	}

	@Override
	public View getView(final int n, View convertView, ViewGroup parent) {
		View view;
		ImageView imageView;
		ImageButton button;
		if (convertView == null){
			view = View.inflate(context, R.layout.imageview_in_gridview, null);
			imageView = (ImageView)view.findViewById(R.id.image_view);
			
			imageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, PhotoActivity.class);
					Log.i("success", "id = " + n);
					intent.putExtra("photos", uriArray);
					intent.putExtra("index", n);
					context.startActivity(intent);
				}	
			});
			button = (ImageButton)view.findViewById(R.id.delete_image_button);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					uriArray.remove(n);
					imageArray.remove(n);
					ImageAdapter.this.notifyDataSetChanged();
				}
			});
		}
		else
		{
			view = convertView;
			imageView = (ImageView)view.findViewById(R.id.image_view);
			button = (ImageButton)view.findViewById(R.id.delete_image_button);
		}
		
		if (isEdit){
			Log.i("success", "edit = true");
			button.setVisibility(View.VISIBLE);
		}
		else
			button.setVisibility(View.GONE);
		imageView.setImageBitmap(imageArray.get(n));
		return view;
	}
	public boolean isEdit() {
		return isEdit;
	}
	
	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
		this.notifyDataSetChanged();
	}

}
