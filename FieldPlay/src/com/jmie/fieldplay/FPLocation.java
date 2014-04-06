package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.jmie.fieldplay.details.FPPicture;

public abstract class FPLocation implements Parcelable{
	private String _type = "abstract";
	private double _lat;
	private double _long;
	private double _elevation;
	private String _name;
	private String _description;
	private List<FPPicture> images= new ArrayList<FPPicture>();

	
	public FPLocation(double latitude, double longitude, double elevation, 
			String name, String description){
		this._lat = latitude;
		this._long = longitude;
		this._elevation = elevation;
		this._name = name;
		this._description = description;
		
	}
	protected void setType(String type){
		this._type = type;
	}
	public FPLocation(Parcel in){
		readFromParcel(in);
	}
	public double getLatitude() {
		return _lat;
	}
	public double getLongitude(){
		return _long;
	}
	public double getElevation(){
		return _elevation;
	}
	public String getName(){
		return _name;
	}
	public String getDescription(){
		return _description;
	}
	public Iterator<FPPicture> getImageIterator(){
		return images.iterator();
	}
	public List<FPPicture> getImageList(){
		return images;
	}
	public void addImage(FPPicture pic){
		images.add(pic);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags){
//		Log.d("Location Write", "Start: "+ _name);
//		Log.d("Location Write", "type: "+ _type);
		dest.writeString(_type);
//		Log.d("Location Write", "lat: "+ _lat);
		dest.writeDouble(_lat);
//		Log.d("Location Write", "long: "+ _long);
		dest.writeDouble(_long);
//		Log.d("Location Write", "elevation: "+ _elevation);
		dest.writeDouble(_elevation);
//		Log.d("Location Write", "name: "+ _name);
		dest.writeString(_name);
//		Log.d("Location Write", "description: "+ _description);
		dest.writeString(_description);
//		Log.d("Location Write", "images: ");
		dest.writeTypedList(images);
//		Log.d("Location Write", "End: "+ _name);
	}
	protected void readFromParcel(Parcel in){
		//_type = in.readString();
		_lat = in.readDouble();
//		Log.d("FPLocation read", "lat: " + _lat);
		_long = in.readDouble();
//		Log.d("FPLocation read", "long: " + _long);
		_elevation = in.readDouble();
//		Log.d("FPLocation read", "elevation: " + _elevation);
		_name = in.readString();
//		Log.d("FPLocation read", "name: " + _name);
		_description = in.readString();
//		Log.d("FPLocation read", "description: " + _description);
		
		List<FPPicture> images = new ArrayList<FPPicture>();
		in.readTypedList(images, FPPicture.CREATOR);
		this.images = images;
	}
	public static final Parcelable.Creator<FPLocation> CREATOR = new Parcelable.Creator<FPLocation>() {
		public FPLocation createFromParcel(Parcel in){
			String location_type = in.readString();
//			Log.d("FP Location Creator", "read in type string " + location_type);
			FPLocation location = null;
			if(location_type.equals("binocular_location"))
				location = (FPLocation) new BinocularLocation(in);
				
			else if(location_type.equals("interest_location"))
				location = (FPLocation) new InterestLocation(in);
			else if(location_type.equals("stop_location"))
				location = (FPLocation) new StopLocation(in);
			
//			Log.d("FP Location loader", "done loading " + location.getName());
			return location;
		}
		public FPLocation[] newArray(int size){
			return new FPLocation[size];
		}
	};
}
