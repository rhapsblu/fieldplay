package com.jmie.fieldplay.route;


import android.os.Parcel;
import android.os.Parcelable;




public class BinocularLocation extends FPLocation {
	//hack to get parcelable working with inheritence
	public BinocularLocation(double latitude, double longitude, double elevation, 
			String name, String description){
		super(latitude, longitude, elevation,name, description);
		setType(LocationType.BINOCULAR_LOCATION);
	}
	public BinocularLocation(Parcel in){
		//super(in);
		//setType("binocular_location");
		setType(LocationType.BINOCULAR_LOCATION);
		readFromParcel(in);
	}
	public void writeToParcel(Parcel dest, int flags){
		super.writeToParcel(dest, flags);
//		Log.d("Binocular Write", "no write: ");
	}

	public static final Parcelable.Creator<BinocularLocation> CREATOR = new Parcelable.Creator<BinocularLocation>() {
		public BinocularLocation createFromParcel(Parcel in){
			return new BinocularLocation(in);
		}
		public BinocularLocation[] newArray(int size){
			return new BinocularLocation[size];
		}
	};
	protected void readFromParcel(Parcel in){
		super.readFromParcel(in);

	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
