package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jmie.fieldplay.details.FPPicture;
import com.jmie.fieldplay.media.FPVideo;

public class StopLocation extends InterestLocation {
	private double _alert_radius = 100;
	private List<FPPicture> images= new ArrayList<FPPicture>();
	private List<String> binocularPoints = new ArrayList<String>();
	private List<FPVideo> videos = new ArrayList<FPVideo>();
	
	public StopLocation(double latitude, double longitude, double elevation,
			String name, String description) {
		super(latitude, longitude, elevation, name, description);
	}
	public void setAlertRadius(double radius){
		_alert_radius = radius;
	}
	public double getAlertRadius(){
		return _alert_radius;
	}
	public Iterator<FPPicture> getImageIterator(){
		return images.iterator();
	}
	public List<FPPicture> getImageList(){
		return images;
	}
	public Iterator<FPVideo> getVideoIterator(){
		return videos.iterator();
	}
	public void addBinocularLocation(String loc){
		binocularPoints.add(loc);
	}
	public void addVideo(FPVideo vid){
		videos.add(vid);
	}
	public void addImage(FPPicture pic){
		images.add(pic);
	}
	public Iterator<String> getBinocularPointIterator(){
		return binocularPoints.iterator();
	}
}
