package com.track.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TrackData implements Serializable{
	private static final long serialVersionUID = 5348856828228763168L;
	public final static String LONGITUDE_STRING = "longitude";
	public final static String LATITUDE_STRING = "latitude";
	public final static String PLACENAME_STRING = "place_name";
	public final static String DATE_STRING = "date";
	public final static String HOUR_STRING = "hour";
	public final static String MINUTE_STRING = "minute";
	public final static String DESCRIPTION_STRING = "description";
	public final static String PHOTOS_STRING = "photos";
	public final static double DEFAULT_LONGITUDE = 116.46;
	public final static double DEFAULT_LATITUDE = 39.92;
	private int id;
	private String place_name;
	private double longitude;
	private double latitude;
	private String date;
	private int hour;
	private int minute;
	private String description = null;
	private ArrayList<String> photos = new ArrayList<String>();
	TrackData(int id, String place_name, double latitude, double longitude, 
			String date, int hour, int minute){
		this.setId(id);
		this.setPlaceName(place_name);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.setDate(date);
		this.setHour(hour);
		this.setMinute(minute);
	}
	TrackData(String place_name, double latitude, double longitude, 
			String date, int hour, int minute, String description, String photos){
		this.setId(-1);
		this.setPlaceName(place_name);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.setDate(date);
		this.setHour(hour);
		this.setMinute(minute);
		this.setDescription(description);
		this.setPhotos(photos);
	}
	
	TrackData(int id, String place_name, double latitude, double longitude, 
			String date, int hour, int minute, String description, String photos){
		this.setId(id);
		this.setPlaceName(place_name);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
		this.setDate(date);
		this.setHour(hour);
		this.setMinute(minute);
		this.setDescription(description);
		this.setPhotos(photos);
	}
	public String getPlaceName() {
		return place_name;
	}
	public void setPlaceName(String place_name) {
		this.place_name = place_name;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void addPhoto(String photo){
		photos.add(photo);
	}
	public void removePhoto(String photo){
		photos.remove(photo);
	}
	public void setPhotos(String str){
		if (str == null)
			return;
		String[] strs = str.split(";");
		photos.clear();
		for (int i = 0; i != strs.length; ++i)
			if (strs[i].length() != 0){
				photos.add(strs[i]);
			}
	}
	public String getPhotos(){
		String ret = "";
		for (int i = 0; i < photos.size() - 1; ++i){
			ret += photos.get(i) + ";";
		}
		if (photos.size() > 0)
			ret += photos.get(photos.size() - 1);
		return ret;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	@Override
	public String toString() {
		return  "id:" + getId()
				+ "name:" + getPlaceName() + ";latitude:" + getLatitude() + ";longitude:"
				+ getLongitude() + ";date:" + getDate() + ";hour:" + getHour() + ";minute:"
				+ getMinute() + ";description:" + getDescription() + ";photos:" + getPhotos();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getDateType(){
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		String[] strs = date.split("-");
		int year = Integer.valueOf(strs[0]);
		int month = Integer.valueOf(strs[1]);
		int day = Integer.valueOf(strs[2]);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	
	@Override
	public boolean equals(Object o){
		TrackData data = (TrackData)o;
		return id == data.id;
 	}
}
