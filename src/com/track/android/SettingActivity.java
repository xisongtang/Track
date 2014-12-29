package com.track.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingActivity extends Activity {
	private Spinner intervalSpinner;
	private Spinner startSpinner;
	private Spinner endSpinner;
	private Switch isOnSwitch;
    private SettingData settings;
    private boolean isOn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        settings = new SettingData(getApplicationContext());
        int endHour = settings.getEndHour();
        int startHour = settings.getStartHour();
        int interval = settings.getTimeInterval();
        isOn = settings.isOn();
        intervalSpinner = (Spinner)findViewById(R.id.spinner_interval);
        startSpinner = (Spinner)findViewById(R.id.Spinner_start);
        endSpinner = (Spinner)findViewById(R.id.spinner_end);
        isOnSwitch = (Switch)findViewById(R.id.switch1);
        isOnSwitch.setChecked(isOn);
        isOnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isOn = isChecked;
			}
		});
        int intervalPosition;
        switch (interval){
        case SettingData.INTERVAL_TEN:
        	intervalPosition = 0;
        	break;
        case SettingData.INTERVAL_TWENTY:
        	intervalPosition = 1;
        	break;
        case SettingData.INTERVAL_HALFHOUR:
        	intervalPosition = 2;
        	break;
        case SettingData.INTERVAL_HOUR:
        	intervalPosition = 3;
        	break;
        case SettingData.INTERVAL_TWOHOURS:
        	intervalPosition = 4;
        	break;
        default:
        	intervalPosition = 0;
        	break;
        }
        intervalSpinner.setSelection(intervalPosition);
        int startPosition;
        switch (startHour){
        case 5:
        	startPosition = 0;
        	break;
        case 7:
        	startPosition = 1;
        	break;
        case 9:
        	startPosition = 2;
        	break;
        case 11:
        	startPosition = 3;
        	break;
        default:
        	startPosition = 1;
        	break;
        }
        startSpinner.setSelection(startPosition);
        int endPosition;
        switch (endHour){
        case 5:
        	endPosition = 0;
        	break;
        case 7:
        	endPosition = 1;
        	break;
        case 9:
        	endPosition = 2;
        	break;
        case 11:
        	endPosition = 3;
        	break;
        default:
        	endPosition = 2;
        	break;
        }
        endSpinner.setSelection(endPosition);
	}
	
	public void onSaveButtonClick(View v){
		int intervalPosition = intervalSpinner.getSelectedItemPosition();;
		int interval;
        switch (intervalPosition){
        case 0:
        	interval = SettingData.INTERVAL_TEN;
        	break;
        case 1:
        	interval = SettingData.INTERVAL_TWENTY;
        	break;
        case 2:
        	interval = SettingData.INTERVAL_HALFHOUR;
        	break;
        case 3:
        	interval = SettingData.INTERVAL_HOUR;
        	break;
        case 4:
        	interval = SettingData.INTERVAL_TWOHOURS;
        	break;
        default:
        	interval = SettingData.INTERVAL_HALFHOUR;
        	break;
        }
        int startPosition = startSpinner.getSelectedItemPosition();
        int startHour;
        switch (startPosition){
        case 0:
        	startHour = 5;
        	break;
        case 1:
        	startHour = 7;
        	break;
        case 2:
        	startHour = 9;
        	break;
        case 3:
        	startHour = 11;
        	break;
        default:
        	startHour = 7;
        	break;
        }
        int endPosition = endSpinner.getSelectedItemPosition();
        int endHour;
        switch (endPosition){
        case 0:
        	endHour = 18;
        	break;
        case 1:
        	endHour = 20;
        	break;
        case 2:
        	endHour = 22;
        	break;
        case 3:
        	endHour = 24;
        	break;
        default:
        	endHour = 22;
        	break;
        }
        settings.setEndHour(endHour);
        settings.setStartHour(startHour);
        settings.setTimeInterval(interval);
        settings.setOn(isOn);
        settings.save();
        finish();
	}
}
