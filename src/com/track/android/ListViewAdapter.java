package com.track.android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private ArrayList<TrackData> datas = new ArrayList<TrackData>();
	private ArrayList<Integer> indexs = new ArrayList<Integer>();
	private Context context;
	private OnItemClickListener listener;
	private Bitmap bitmap;
	private boolean isEdited = false;
	private Paint paint;
	{
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
	}
	public void setDatas(ArrayList<TrackData> datas){
		this.datas = datas;
		indexs.clear();
		this.notifyDataSetChanged();
		for (int i = 0; i != datas.size(); ++i){
			indexs.add(i);
		}
	}
	
	public ListViewAdapter(Context context) {
		this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), 
    			R.drawable.overlay);
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		TextView timeTextView;
		TextView locationTextView;
		final TrackData data = (TrackData)getItem(position);
		if (convertView == null){
        	view = View.inflate(context, R.layout.listview_layout, null);
		}
		else{
			view = convertView;
		}
		Bitmap currentBitmap = bitmap.copy(bitmap.getConfig(), true);
    	Canvas canvas = new Canvas(currentBitmap);
    	canvas.drawText(String.valueOf(indexs.get(position) + 1), canvas.getWidth()/2, 
    			canvas.getHeight()/2 + 2, paint);
    	ImageView titleImageView = (ImageView)view.findViewById(R.id.image_view_title);
    	titleImageView.setImageBitmap(currentBitmap);
		timeTextView = (TextView)view.findViewById(R.id.time);
    	locationTextView = (TextView)view.findViewById(R.id.location);
    	ImageButton garbageImageButton = (ImageButton)view.findViewById(
    			R.id.image_button_garbage);
    	timeTextView.setText(data.getHour() + ":" + data.getMinute());
    	locationTextView.setText(data.getPlaceName());
    	garbageImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatabaseHelper db = new DatabaseHelper(context);
				db.deleteData(data);
				db.close();
				if (position > datas.size() - 1)
					return;
				datas.remove(position);
				indexs.remove(position);
				ListViewAdapter.this.notifyDataSetChanged();
				setEdited(true);
			}
		});
    	view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClick(v, datas.get(position));
			}
		});
		return view;
	}

	public boolean isEdited() {
		return isEdited;
	}

	public void setEdited(boolean isEdit) {
		this.isEdited = isEdit;
	}
	
	public OnItemClickListener getListener() {
		return listener;
	}

	public void setListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	interface OnItemClickListener{
		public void onClick(View v, TrackData data);
	}
}
