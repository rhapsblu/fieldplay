package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {
	private String _name = "default";
	private String _description = "none";
	private List<FPLocation> locations;
	private double _length;
	private Boundry _boundry;
	private Map<String, FPLocation> nameToLocation = new HashMap<String, FPLocation>();
	private List<MapLayer> mapLayers = new ArrayList<MapLayer>();
	private String storageName;
	
	public Route(){
		locations = new ArrayList<FPLocation>();
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
}
