package com.track.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SettingData {
	private Context context;
	private static final String TIMEINTERVAL_KEY_STRING = "timeInterval";
	private static final String STARTHOUR_KEY_STRING = "startHour";
	private static final String ENDHOUR_KEY_STRING = "endHour";
	private static final String ISON_KEY_STRING = "ison";
	
	private int timeInterval;
	private int startHour;
	private int endHour;
	private boolean isOn;
	
	public static final int INTERVAL_MINUTE = 1;
	public static final int INTERVAL_TEN = 10;
	public static final int INTERVAL_TWENTY = 20;
	public static final int INTERVAL_HALFHOUR = 30;
	public static final int INTERVAL_HOUR = 60;
	public static final int INTERVAL_TWOHOURS = 120;
	private static String SETTING_FILENAME = "config.ini";
	
	public SettingData(Context context) {
		this.context = context;
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING_FILENAME, Activity.MODE_PRIVATE);
		setTimeInterval(sharedPreferences.getInt(TIMEINTERVAL_KEY_STRING, INTERVAL_MINUTE));
		setStartHour(sharedPreferences.getInt(STARTHOUR_KEY_STRING, 0));
		setEndHour(sharedPreferences.getInt(ENDHOUR_KEY_STRING, 24));
		setOn(sharedPreferences.getBoolean(ISON_KEY_STRING, true));
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	
	public void save(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING_FILENAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(TIMEINTERVAL_KEY_STRING, timeInterval);
		editor.putInt(STARTHOUR_KEY_STRING, startHour);
		editor.putInt(ENDHOUR_KEY_STRING, endHour);
		editor.putBoolean(ISON_KEY_STRING, isOn);
		editor.commit();
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
}
