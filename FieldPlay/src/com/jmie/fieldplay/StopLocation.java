package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


import com.jmie.fieldplay.media.FPVideo;

public class StopLocation extends InterestLocation implements Parcelable{
	private double _alert_radius = 100;

	private List<String> binocularPoints = new ArrayList<String>();
	private List<FPVideo> videos = new ArrayList<FPVideo>();
	//hack to get parsable working with inheritence

	
	public StopLocation(double latitude, double longitude, double elevation,
			String name, String description) {
		super(latitude, longitude, elevation, name, description);
		setType("stop_location");
	}
	public StopLocation(Parcel in){
		super(in);
		readFromParcel(in);
	}
	public void setAlertRadius(double radius){
		_alert_radius = radius;
	}
	public double getAlertRadius(){
		return _alert_radius;
	}

	public Iterator<FPVideo> getVideoIterator(){
		return videos.iterator();
	}
	public void addBinocularLocation(String loc){
		binocularPoints.add(loc);
	}
	public void addVideo(FPVideo vid){
		videos.add(vid);
	}

	public Iterator<String> getBinocularPointIterator(){
		return binocularPoints.iterator();
	}
	@Override
	public void writeToParcel(Parcel dest, int flags){
		super.writeToParcel(dest, flags);
		dest.writeDouble(_alert_radius);
		dest.writeStringList(binocularPoints);
		dest.writeTypedList(videos);
	
	}
	@Override
	protected void readFromParcel(Parcel in){
		super.readFromParcel(in);
		_alert_radius = in.readDouble();
		in.readStringList(binocularPoints);
		in.readTypedList(videos, FPVideo.CREATOR);
	
	}
	
	public static final Parcelable.Creator<StopLocation> CREATOR = new Parcelable.Creator<StopLocation>() {
		@Override
		public StopLocation createFromParcel(Parcel in){
			return new StopLocation(in);
		}
		@Override
		public StopLocation[] newArray(int size){
			return new StopLocation[size];
		}
	};
	
}
