package com.jmie.fieldplay.route;

import android.os.Parcel;
import android.os.Parcelable;


public class Reference implements Parcelable{
	private int id;
	private String text;
	private String link;
	
	public Reference(Parcel in){
		readFromParcel(in);
	}
	public Reference(int id, String text, String link){
		this.id = id;
		this.text = text;
		this.link = link;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(text);
		dest.writeString(link);
	}
	private void readFromParcel (Parcel in) {
		id = in.readInt();
		text = in.readString();
		link = in.readString();
	}
	
	public static final Parcelable.Creator<Reference> CREATOR = new Parcelable.Creator<Reference>() {
		public Reference createFromParcel(Parcel in){
			return new Reference(in);
		}
		public Reference[] newArray(int size) {
			return new Reference[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
