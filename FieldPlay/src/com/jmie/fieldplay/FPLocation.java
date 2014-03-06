package com.jmie.fieldplay;

public abstract class FPLocation {
	private double _lat;
	private double _long;
	private double _elevation;
	private String _name;
	private String _description;
	
	
	public FPLocation(double latitude, double longitude, double elevation, 
			String name, String description){
		this._lat = latitude;
		this._long = longitude;
		this._elevation = elevation;
		this._name = name;
		this._description = description;
		
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
}
