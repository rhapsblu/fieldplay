package com.jmie.fieldplay.route;

public class RouteData {
	private int _id;
	private String _routeName;
	private String _routeDescription;
	private String _routeFile;
	private int _downloadProgress;
	private int _unzipProgress;
	private long _managerID;
	
	public RouteData(){
		
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
}
