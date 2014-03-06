package com.jmie.fieldplay.media;

public class FPAudio implements Comparable<FPAudio>{
	private String _name;
	private String _resourceLocation;
	private int _priority;
	
	public FPAudio(String name, String path, int priority) {
		this._name = name;
		this._resourceLocation = path;
		this._priority = priority;
	}
	
	public String getName(){
		return _name;
	}
	public String getFilePath(){
		return _resourceLocation;
	}
	public int getPriority(){
		return _priority;
	}

	@Override
	public int compareTo(FPAudio another) {
		
		return this.getPriority()-another.getPriority();
	}
}
