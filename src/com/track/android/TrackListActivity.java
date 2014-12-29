package com.track.android;

import java.util.ArrayList;

import com.track.android.ListViewAdapter.OnItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class TrackListActivity extends Activity {
	ListView listView;
	ListViewAdapter listViewAdapter;
	public static final int TRACKLIST_RESULT_CODE = 0xffff; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list);
		Intent intent = getIntent();
		String dateString = intent.getStringExtra("date");
		((TextView)findViewById(R.id.date_text_view)).setText(dateString);
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		ArrayList<TrackData> datas = db.selectData(dateString);
		db.close();
		listView = (ListView)findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter(getBaseContext());
        listViewAdapter.setListener(new OnItemClickListener() {
			
			@Override
			public void onClick(View v, TrackData data) {
				Intent intent = new Intent();
				intent.putExtra("data", data);
				intent.setClass(getBaseContext(), TrackActivity.class);
				startActivityForResult(intent, TrackActivity.TRACK_RESULT_CODE);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
        listView.setAdapter(listViewAdapter);
        listViewAdapter.setDatas(datas);
	}
	public void onSaveButtonClick(View v){
		onBackPressed();
	}
	@Override
	public void onBackPressed() {
		if (listViewAdapter.isEdited()){
			Log.i("success", true + "");
			setResult(RESULT_OK);
		}
		else{
			Log.i("success", false + "");
			setResult(RESULT_CANCELED);
		}
		finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.list_activity_disappear);
	}
}
