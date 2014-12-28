package com.track.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "tracks.db";
	private final static String TB_NAME = "tracks";
	private final static int DB_VERSION = 1;
	
	DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE tracks ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "latitude DOUBLE NOT NULL,"
				+ "longitude DOUBLE NOT NULL,"
				+ "place_name VARCHAR(100) NOT NULL,"
				+ "date DATE NOT NULL,"
				+ "hour INTEGER NOT NULL,"
				+ "minute INTEGER NOT NULL, "
				+ "description VARCHAR(100),"
				+ "photos VARCHAR(500)"
				+ ");");
		Log.i("success", "in database ctor");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
	
	public void insertData(TrackData data){
		ContentValues values = new ContentValues();
		values.put(TrackData.PLACENAME_STRING, data.getPlaceName());
		values.put(TrackData.LONGITUDE_STRING, data.getLongitude());
		values.put(TrackData.LATITUDE_STRING, data.getLatitude());
		values.put(TrackData.HOUR_STRING, data.getHour());
		values.put(TrackData.MINUTE_STRING, data.getMinute());
		values.put(TrackData.DATE_STRING, data.getDate());
		values.put(TrackData.DESCRIPTION_STRING, data.getDescription());
		values.put(TrackData.PHOTOS_STRING, data.getPhotos());
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TB_NAME, null, values);
		db.close();
	}
	
	public ArrayList<TrackData> selectData(String date){
		SQLiteDatabase db = getWritableDatabase();
		String[] columns = {"*"};
		String[] params = {date};
		Cursor cursor = db.query(TB_NAME, columns, TrackData.DATE_STRING + "=?", params, null, 
				null, TrackData.HOUR_STRING + ", " + TrackData.MINUTE_STRING);
		ArrayList<TrackData> datas = new ArrayList<TrackData>();
		cursor.moveToFirst();

		Log.i("success", "in selecting data:" + date);
		while (!cursor.isAfterLast()){
			datas.add(new TrackData(cursor.getInt(0), cursor.getString(3), cursor.getDouble(1), 
					cursor.getDouble(2), cursor.getString(4), cursor.getInt(5),
					cursor.getInt(6), cursor.getString(7), cursor.getString(8)));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return datas;
	}
	public ArrayList<TrackData> selectData(String date, int hour, int minute){
		SQLiteDatabase db = getWritableDatabase();
		String[] columns = {"*"};
		String[] params = {date, String.valueOf(hour), String.valueOf(minute)};
		Cursor cursor = db.query(TB_NAME, columns, TrackData.DATE_STRING + "=? AND "
				+ TrackData.HOUR_STRING + "=? AND" + TrackData.MINUTE_STRING
				+ "=?", params, null, null,	null);
		ArrayList<TrackData> datas = new ArrayList<TrackData>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			datas.add(new TrackData(cursor.getInt(0), cursor.getString(3), cursor.getDouble(1), 
					cursor.getDouble(2), cursor.getString(4), cursor.getInt(5),
					cursor.getInt(6), cursor.getString(7), cursor.getString(8)));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return datas;
	}
	
	public void deleteData(int id){
		String strs[] = new String[]{
			String.valueOf(id)
		};
		getWritableDatabase().delete(TB_NAME, "_id = ?", strs);
	}
	
	public void deleteData(TrackData data){
		this.deleteData(data.getId());
	}
	
	public void updateData(TrackData data){
		ContentValues values = new ContentValues();
		values.put(TrackData.PLACENAME_STRING, data.getPlaceName());
		values.put(TrackData.LONGITUDE_STRING, data.getLongitude());
		values.put(TrackData.LATITUDE_STRING, data.getLatitude());
		values.put(TrackData.HOUR_STRING, data.getHour());
		values.put(TrackData.MINUTE_STRING, data.getMinute());
		values.put(TrackData.DATE_STRING, data.getDate());
		values.put(TrackData.DESCRIPTION_STRING, data.getDescription());
		Log.i("success", "onUpdateData:" + data.getPhotos());
		values.put(TrackData.PHOTOS_STRING, data.getPhotos());
		SQLiteDatabase db = getWritableDatabase();
		String[] strs = new String[]{
			String.valueOf(data.getId())	
		};
		db.update(TB_NAME, values, "_id = ?", strs);
		db.close();
	}
	
	public TrackData selectLatestData(String date, int hour, int minute){
		ArrayList<TrackData> datas = selectData(date);
		for (int i = datas.size() - 1; i >= 0; --i){
			TrackData cur = datas.get(i);
			if (cur.getHour() < hour ||(cur.getHour() == hour && cur.getMinute() < minute))
				return cur;
		}
		return null;
	}
}
