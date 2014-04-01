package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable{
	private String _name = "default";
	private String _description = "none";
	private List<FPLocation> locations;
	private double _length;
	private Boundry _boundry = new Boundry();
	private Map<String, FPLocation> nameToLocation = new HashMap<String, FPLocation>();
	private List<MapLayer> mapLayers = new ArrayList<MapLayer>();
	private String storageName;
	
	public Route(){
		locations = new ArrayList<FPLocation>();
	}
	public Route(Parcel in){
		readFromParcel(in);
	}
	public String getName(){
		return _name;
	}

	public void setName(String name){
		this._name = name;
	}
	public void setStorageName(String storageName){
		this.storageName = storageName;
	}
	public String getStorageName(){
		return this.storageName;
	}
	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}
	public void addMapLayer(MapLayer mapLayer){
		mapLayers.add(mapLayer);
	}
	public List<MapLayer> getMapLayers(){
		return mapLayers;
	}
	public void addLocation(FPLocation loc){
		locations.add(loc);
		nameToLocation.put(loc.getName(), loc);
	}
	public FPLocation getLocationByName(String name){
		return nameToLocation.get(name);
	}
	public double getLength() {
		return _length;
	}
	public void setLength(double length) {
		this._length = length;
	}
	public Boundry getBoundry(){
		return _boundry;
	}
	public void setBoundry(Boundry boundry) {
		this._boundry = boundry;
	}
	public class Boundry{
		public long _lat1;
		public long _lat2;
		public long _long1;
		public long _long2;	
	}
	public List<FPLocation> getLocationList(){
		return locations;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(_name);
		dest.writeString(_description);
		dest.writeTypedList(locations);
		dest.writeDouble(_length);
		dest.writeLong(_boundry._lat1);
		dest.writeLong(_boundry._lat2);
		dest.writeLong(_boundry._long1);
		dest.writeLong(_boundry._long2);
		dest.writeInt(nameToLocation.size());
		for(String key: nameToLocation.keySet()){
			FPLocation location = nameToLocation.get(key);
			dest.writeString(key);
			dest.writeParcelable(location, flags);
		}
		dest.writeTypedList(mapLayers);
		dest.writeString(storageName);
		
		
	}
	private void readFromParcel(Parcel in){
		_name = in.readString();
		_description = in.readString();
		in.readTypedList(locations, FPLocation.CREATOR);
		_length = in.readDouble();
		_boundry._lat1 = in.readLong();
		_boundry._lat2 = in.readLong();
		_boundry._long1 = in.readLong();
		_boundry._long2 = in.readLong();
		final int N = in.readInt();
		for(int i = 0; i<N; i++){
			String key = in.readString();
			FPLocation location = FPLocation.CREATOR.createFromParcel(in); 
			nameToLocation.put(key, location);
		}
		in.readTypedList(mapLayers, MapLayer.CREATOR);
		storageName = in.readString();
	}
	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
		public Route createFromParcel(Parcel in){
			return new Route(in);
		}
		public Route[] newArray(int size){
			return new Route[size];
		}
	};
}
