package com.jmie.fieldplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.jmie.fieldplay.media.FPAudio;


public class InterestLocation extends FPLocation{
	private double _content_radius = 0;
	private List<FPAudio> _audioList = new ArrayList<FPAudio>();



	public InterestLocation(double latitude, double longitude, double elevation,
			String name, String description) {
		super(latitude, longitude, elevation, name, description);
		_audioList = new ArrayList<FPAudio>();
		setType("interest_location");
	}
	public InterestLocation(Parcel in){
		super(in);
		readFromParcel(in);
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags){
		super.writeToParcel(dest, flags);
		dest.writeDouble(_content_radius);
		dest.writeTypedList(_audioList);
	}
	@Override
	protected void readFromParcel(Parcel in){
		super.readFromParcel(in);
		_content_radius = in.readDouble();
		in.readTypedList(_audioList, FPAudio.CREATOR);
	}
	public static final Parcelable.Creator<InterestLocation> CREATOR = new Parcelable.Creator<InterestLocation>() {
		public InterestLocation createFromParcel(Parcel in){
			return new InterestLocation(in);
		}
		public InterestLocation[] newArray(int size){
			return new InterestLocation[size];
		}
	};
}
