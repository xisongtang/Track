package com.track.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Environment;
import android.util.Log;

public class PhotoGetter {
	ArrayList<File> photos = new ArrayList<File>();
	Comparator<File> comparator = new Comparator<File>() {
		
		@Override
		public int compare(File f1, File f2) {
			if (f1.lastModified() > f2.lastModified())
				return 1;
			else if (f1.lastModified() == f2.lastModified())
				return 0;
			else
				return -1;
		}
	};
	public PhotoGetter() {
		Queue<File> queue = new LinkedList<File>();
		File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		queue.add(file);
		while (!queue.isEmpty()){
			file = queue.remove();
			String filename = file.getName();
			if (filename.startsWith("."))
				continue;
			File files[] = file.listFiles();
			if (files != null)
				queue.addAll(Arrays.asList(files));
			String[] strs = filename.split("\\.");
			if (strs.length < 2)
				continue;
			String ext = strs[1];
			if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg"))
				photos.add(file);
		}
		Collections.sort(photos, comparator);
	}
	public ArrayList<File> getPhotosDuringDates(Date start, Date end){
		int startIndex = -1, endIndex = -1;
		for (int i = 0; i != photos.size(); ++i){
			File file = photos.get(i);
			Date date = new Date(file.lastModified());
			Log.i("success", date.toString());
			if (file.lastModified() > start.getTime()){
				startIndex = i;
				break;
			}
		}
		for (int i = photos.size() - 1; i >= 0; --i){
			File file = photos.get(i);
			if (file.lastModified() < end.getTime()){
				endIndex = i;
				break;
			}
		}
		ArrayList<File> result = new ArrayList<File>();
		if (startIndex == -1)
			return result;
		for (int i = startIndex; i <= endIndex; ++i){
			result.add(photos.get(i));
		}
		return result;
	}
}
