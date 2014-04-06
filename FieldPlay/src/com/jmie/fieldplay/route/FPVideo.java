package com.jmie.fieldplay.route;

import android.os.Parcel;
import android.os.Parcelable;

public class FPVideo implements Parcelable{
	private String _name;
	private String _resourceLocation;
	private String _description;
	
	public FPVideo(String name, String description, String path){
		this._name = name;
		this._resourceLocation = path;
		this._description = description;

	}
	
	public FPVideo(Parcel in){
		readFromParcel(in);
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
	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(_name);
		dest.writeString(_resourceLocation);
		dest.writeString(_description);
	}
	private void readFromParcel(Parcel in){
		_name = in.readString();
		_resourceLocation = in.readString();
		_description = in.readString();
	}
	public static final Parcelable.Creator<FPVideo> CREATOR = new Parcelable.Creator<FPVideo>(){
		public FPVideo createFromParcel (Parcel in) {
			return new FPVideo(in);
		}
		public FPVideo[] newArray(int size){
			return new FPVideo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
