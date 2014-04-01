package com.jmie.fieldplay.media;

import android.os.Parcel;
import android.os.Parcelable;

public class FPAudio implements Comparable<FPAudio>, Parcelable{
	private String _name;
	private String _resourceLocation;
	private int _priority;
	
	public FPAudio(String name, String path, int priority) {
		this._name = name;
		this._resourceLocation = path;
		this._priority = priority;
	}
	public FPAudio(Parcel in){
		readFromParcel(in);
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
	
	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(_name);
		dest.writeString(_resourceLocation);
		dest.writeInt(_priority);
	}
	private void readFromParcel(Parcel in){
		_name = in.readString();
		_resourceLocation = in.readString();
		_priority = in.readInt();
	}
	
	public static final Parcelable.Creator<FPAudio> CREATOR = new Parcelable.Creator<FPAudio>() {
		public FPAudio createFromParcel(Parcel in){
			return new FPAudio(in);
		}
		public FPAudio[] newArray(int size){
			return new FPAudio[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
