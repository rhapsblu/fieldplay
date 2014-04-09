package com.jmie.fieldplay.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public abstract class FPLocation implements Parcelable{
	private LocationType _type  = LocationType.ABSTRACT;
	private double _lat;
	private double _long;
	private double _elevation;
	private String _name;
	private String _description;
	private List<FPPicture> images;

	public FPLocation(){
		_lat = 0;
		_long = 0;
		_elevation = 0;
		_name ="";
		_description = "";
		images= new ArrayList<FPPicture>();
	}

	public FPLocation(double latitude, double longitude, double elevation, 
			String name, String description){
		this._lat = latitude;
		this._long = longitude;
		this._elevation = elevation;
		this._name = name;
		this._description = description;
		images= new ArrayList<FPPicture>();
		
	}
	protected void setType(LocationType type){
		this._type = type;
	}
	public LocationType getType(){
		return _type;
	}
	public FPLocation(Parcel in){
		images= new ArrayList<FPPicture>();
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
		//dest.writeString(_type);
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
		in.readTypedList(images, FPPicture.CREATOR);

	}

	public enum LocationType{
		ABSTRACT(0),
		BINOCULAR_LOCATION(1),
		INTEREST_LOCATION(2),
		STOP_LOCATION(3);
		private int typeNumber;
		private LocationType(int i){
			typeNumber = i;
		}
		public int getTypeNumber(){
			return typeNumber;
		}
		private static final LocationType[] types= {LocationType.ABSTRACT, LocationType.BINOCULAR_LOCATION,
													LocationType.INTEREST_LOCATION, LocationType.STOP_LOCATION};
		public static LocationType getType(int i){
			return types[i];
		}
	}
}
