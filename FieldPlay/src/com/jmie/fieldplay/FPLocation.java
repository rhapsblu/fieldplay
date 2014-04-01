package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

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
		dest.writeString(_type);
		dest.writeDouble(_lat);
		dest.writeDouble(_long);
		dest.writeDouble(_elevation);
		dest.writeString(_name);
		dest.writeString(_description);
		dest.writeTypedList(images);
	}
	protected void readFromParcel(Parcel in){
		_type = in.readString();
		_lat = in.readDouble();
		_long = in.readDouble();
		_elevation = in.readDouble();
		_name = in.readString();
		_description = in.readString();
		in.readTypedList(images, FPPicture.CREATOR);
	}
	public static final Parcelable.Creator<FPLocation> CREATOR = new Parcelable.Creator<FPLocation>() {
		public FPLocation createFromParcel(Parcel in){
			String location_type = in.readString();
			FPLocation location = null;
			if(location_type.equals("binocular_location"))
				location = (FPLocation) new BinocularLocation(in);
			else if(location_type.equals("interest_location"))
				location = (FPLocation) new InterestLocation(in);
			else if(location_type.equals("stop_location"))
				location = (FPLocation) new StopLocation(in);
			return location;
		}
		public FPLocation[] newArray(int size){
			return new FPLocation[size];
		}
	};
}
