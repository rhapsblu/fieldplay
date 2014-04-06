package com.jmie.fieldplay.details;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class FPPicture implements Parcelable{
	private String _name;
	private String _description;
	private String _resource;
	public FPPicture(String name, String description, String resource){
		this._name = name;
		this._description = description;
		this._resource = resource;
	}
	public FPPicture(Parcel in){
		readFromParcel(in);
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d("Picture Write", "Start: "+ _name);
		dest.writeString(_name);
		dest.writeString(_description);
		dest.writeString(_resource);
		Log.d("Picture Write", "End: "+ _name);
		
	}
	private void readFromParcel(Parcel in){
		Log.d("Picture Read", "Start: "+ _name);
		_name = in.readString();
		_description = in.readString();
		_resource = in.readString();
		Log.d("Picture read", "End: "+ _name);
	}
	public static final Parcelable.Creator<FPPicture> CREATOR = new Parcelable.Creator<FPPicture>() {
		public FPPicture createFromParcel (Parcel in){
			return new FPPicture(in);
		}
		public FPPicture[] newArray(int size) {
			return new FPPicture[size];
		}
	};
}
