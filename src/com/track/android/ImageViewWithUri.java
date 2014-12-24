package com.track.android;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewWithUri extends ImageView {
	private Uri uri;
	public ImageViewWithUri(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ImageViewWithUri(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ImageViewWithUri(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public void setUri(Uri uri){
		this.uri = uri;
	}
	public Uri getUri(){
		return uri;
	}
}
