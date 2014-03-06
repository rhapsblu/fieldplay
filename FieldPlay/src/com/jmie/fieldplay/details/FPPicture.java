package com.jmie.fieldplay.details;

public class FPPicture {
	private String _name;
	private String _description;
	private String _resource;
	public FPPicture(String name, String description, String resource){
		this._name = name;
		this._description = description;
		this._resource = resource;
	}
	public String getName() {
		return _name;
	}

	public String getDescription() {
		return _description;
	}

	public String getResource() {
		return _resource;
	}



}
