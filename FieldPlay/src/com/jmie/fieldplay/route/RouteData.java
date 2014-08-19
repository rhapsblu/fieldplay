package com.jmie.fieldplay.route;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteData implements Parcelable{
	private int _id;
	private String _routeName;
	private String _routeDescription;
	private String _routeFile;
	private int _downloadProgress;
	private int _unzipProgress;
	private long _managerID;
	
	public RouteData(){
		
	}
	public RouteData(Parcel in){
		readFromParcel(in);
	}
	public RouteData(int id, String routeName, String routeDescription, String routeFile, int downloadProgress, long managerID, int unzipProgress){
		set_id(id);
		set_routeName(routeName);
		set_routeFile(routeFile);
		set_downloadProgress(downloadProgress);
		set_unzipProgress(unzipProgress);
		set_managerID(managerID);
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String get_routeName() {
		return _routeName;
	}
	public void set_routeName(String _routeName) {
		this._routeName = _routeName;
	}
	public String get_routeDescription() {
		return _routeDescription;
	}
	public void set_routeDescription(String _routeDescription) {
		this._routeDescription = _routeDescription;
	}
	public String get_routeFile() {
		return _routeFile;
	}
	public void set_routeFile(String _routeFile) {
		this._routeFile = _routeFile;
	}
	public int get_downloadProgress() {
		return _downloadProgress;
	}
	public void set_downloadProgress(int _downloadProgress) {
		this._downloadProgress = _downloadProgress;
	}
	public int get_unzipProgress() {
		return _unzipProgress;
	}
	public void set_unzipProgress(int _unzipProgress) {
		this._unzipProgress = _unzipProgress;
	}
	public Route getRoute(){
		return null;
	}
	public long get_managerID() {
		return _managerID;
	}
	public void set_managerID(long _managerID) {
		this._managerID = _managerID;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(_id);
		dest.writeString(_routeName);
		dest.writeString(_routeDescription);
		dest.writeString(_routeFile);
		dest.writeInt(_downloadProgress);
		dest.writeInt(_unzipProgress);
		dest.writeLong(_managerID);	
	}
	
	private void readFromParcel(Parcel in){
		_id = in.readInt();
		_routeName = in.readString();
		_routeDescription = in.readString();
		_routeFile = in.readString();
		_downloadProgress = in.readInt();
		_unzipProgress = in.readInt();
		_managerID = in.readLong();
	}
	@Override
	public String toString(){
		return _routeName;
	}
	public static final Parcelable.Creator<RouteData> CREATOR = new Parcelable.Creator<RouteData>() {
		public RouteData createFromParcel(Parcel in){
			return new RouteData(in);
		}
		public RouteData[] newArray(int size){
			return new RouteData[size];
		}
	};
}
