package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jmie.fieldplay.media.FPAudio;


public class InterestLocation extends FPLocation{
	private double _content_radius = 0;
	private List<FPAudio> _audioList;


	public InterestLocation(double latitude, double longitude, double elevation,
			String name, String description) {
		super(latitude, longitude, elevation, name, description);
		_audioList = new ArrayList<FPAudio>();
	}
	public void addAudio(FPAudio audio){
		_audioList.add(audio);
	}
	public void setContentRadius(double radius){
		_content_radius = radius;
	}
	public Iterator<FPAudio> getAudioIterator(){
		return _audioList.iterator();
	}
	public double getContentRadius(){
		return _content_radius;
	}
}
