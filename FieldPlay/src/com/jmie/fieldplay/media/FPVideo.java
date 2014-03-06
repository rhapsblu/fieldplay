package com.jmie.fieldplay.media;

public class FPVideo {
	private String _name;
	private String _resourceLocation;
	private String _description;
	
	public FPVideo(String name, String description, String path){
		this._name = name;
		this._resourceLocation = path;
		this._description = description;

	}
	
	public String getName(){
		return _name;
	}
	public String getFilePath(){
		return _resourceLocation;
	}
	public String getDescription(){
		return this._description;
	}
}
