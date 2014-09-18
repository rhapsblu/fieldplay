package com.jmie.fieldplay.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jmie.fieldplay.map.MapLayer;
import com.jmie.fieldplay.route.FPLocation.LocationType;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Route implements Parcelable{
	//private String _name = "default";
	//private String _description = "none";
	private List<FPLocation> locations = new ArrayList<FPLocation>();
	private double _length;
	private Boundry _boundry = new Boundry();
	private Map<String, Integer> nameToLocation = new HashMap<String, Integer>();
	private List<MapLayer> mapLayers = new ArrayList<MapLayer>();
	//private String storageName;
	private RouteData routeData;
	private List<Reference> references = new ArrayList<Reference>();
	
	public Route(){
		locations = new ArrayList<FPLocation>();
	}
	public Route(Parcel in){
		readFromParcel(in);
	}
	public String getName(){
		return routeData.get_routeName();
	}

//	public void setName(String name){
//		this._name = name;
//	}
//	public void setStorageName(String storageName){
//		this.storageName = storageName;
//	}
//	public String getStorageName(){
//		return this.storageName;
//	}
	public String getDescription() {
		return routeData.get_routeDescription();
	}

//	public void setDescription(String description) {
//		this._description = description;
//	}
	public void addMapLayer(MapLayer mapLayer){
		mapLayers.add(mapLayer);
	}
	public void addReference(Reference ref){
		references.add(ref);
	}
	public List<MapLayer> getMapLayers(){
		return mapLayers;
	}
	public List<Reference> getReferences(){
		return references;
	}
	public void addLocation(FPLocation loc){
		locations.add(loc);
		nameToLocation.put(loc.getName(), locations.size()-1);
	}
	public FPLocation getLocationByName(String name){
		return locations.get(nameToLocation.get(name));
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
		//Log.d("Route Write", "Start: "+ _name);
		//dest.writeString(_name);
		//dest.writeString(_description);
		dest.writeInt(locations.size());
		for(FPLocation loc: locations){
			
			dest.writeInt(loc.getType().getTypeNumber());
			dest.writeParcelable(loc, flags);
		}
		//dest.writeTypedList(locations);
		dest.writeDouble(_length);
		dest.writeLong(_boundry._lat1);
		dest.writeLong(_boundry._lat2);
		dest.writeLong(_boundry._long1);
		dest.writeLong(_boundry._long2);
		dest.writeInt(nameToLocation.size());
		for(String key: nameToLocation.keySet()){
			Integer location = nameToLocation.get(key);
			dest.writeString(key);
			dest.writeInt(location);
		}
		dest.writeTypedList(mapLayers);
		dest.writeParcelable(routeData, flags);
		dest.writeTypedList(references);
		//dest.writeString(storageName);
		//Log.d("Route Write", "End: "+_name);
		
		
	}
	private void readFromParcel(Parcel in){
		//_name = in.readString();
		//Log.d("Route read ", "name: "+ _name);
		//_description = in.readString();
		//Log.d("Route read ", "description: "+ _description);
		int locationCount = in.readInt();
		for(int i=0; i<locationCount; i++){
			LocationType type = LocationType.getType(in.readInt());
			switch(type){
			case INTEREST_LOCATION:
				locations.add((FPLocation)in.readParcelable(InterestLocation.class.getClassLoader()));
				break;
			case BINOCULAR_LOCATION:
				locations.add((FPLocation)in.readParcelable(BinocularLocation.class.getClassLoader()));
				break;
			case STOP_LOCATION:
				locations.add((FPLocation)in.readParcelable(StopLocation.class.getClassLoader()));
				break;
			case ABSTRACT://uh-oh's
			default:
			}
			
				
		}
		//in.readTypedList(locations, FPLocation.CREATOR);
		_length = in.readDouble();
		Log.d("Route read ", "length: "+ _length);
		_boundry._lat1 = in.readLong();
		Log.d("Route read ", "boundry1: "+ _boundry._lat1);
		_boundry._lat2 = in.readLong();
		Log.d("Route read ", "boundry2: "+ _boundry._lat2);
		_boundry._long1 = in.readLong();
		Log.d("Route read ", "boundry3: "+ _boundry._long1);
		_boundry._long2 = in.readLong();
		Log.d("Route read ", "boundry4: "+ _boundry._long2);
		final int N = in.readInt();
		Log.d("Route read ", "map size: "+ N);
		for(int i = 0; i<N; i++){
			String key = in.readString();
			//Log.d("Route read ", "map key: "+ key);
			Integer location = in.readInt();
			//Log.d("Route read ", "key map: " + location);
			nameToLocation.put(key, location);
			
		}
		in.readTypedList(mapLayers, MapLayer.CREATOR);
		routeData = in.readParcelable(RouteData.class.getClassLoader());
		in.readTypedList(references, Reference.CREATOR);
		//Log.d("Route read ", "layers read: ");
		//storageName = in.readString();
		//Log.d("Route read ", "storageName: "+ storageName);
	}
	public RouteData getRouteData() {
		return routeData;
	}
	public void setRouteData(RouteData routeData) {
		this.routeData = routeData;
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
