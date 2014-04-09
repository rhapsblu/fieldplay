package com.jmie.fieldplay.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;




public class StopLocation extends InterestLocation {
	private double _alert_radius = 100;

	private List<String> binocularPoints;
	private List<FPVideo> videos;

	
	public StopLocation(double latitude, double longitude, double elevation,
			String name, String description) {
		super(latitude, longitude, elevation, name, description);
		binocularPoints = new ArrayList<String>();
		videos = new ArrayList<FPVideo>();
		setType(LocationType.STOP_LOCATION);
	}
	public StopLocation(Parcel in){
		setType(LocationType.STOP_LOCATION);
		binocularPoints = new ArrayList<String>();
		videos = new ArrayList<FPVideo>();
		readFromParcel(in);
		
	}
	public StopLocation(){
		super();
		setType(LocationType.STOP_LOCATION);
		binocularPoints = new ArrayList<String>();
		videos = new ArrayList<FPVideo>();
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
	public int getBinocPointCount(){
		return binocularPoints.size();
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
		//Log.d("Stop Write", "alert radius: "+ _alert_radius);
		dest.writeDouble(_alert_radius);
		//Log.d("Stop Write", "binocular points: ");
		dest.writeStringList(binocularPoints);	
		//Log.d("Stop Write", "videos: ");
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
