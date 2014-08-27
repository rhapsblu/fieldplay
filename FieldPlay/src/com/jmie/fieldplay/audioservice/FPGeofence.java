/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmie.fieldplay.audioservice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.StopLocation;
import com.jmie.fieldplay.route.FPLocation.LocationType;

/**
 * A Geofence object, defined by its center (latitude and longitude position) and radius.
 */
public class FPGeofence implements Parcelable {
    // Instance variables
	private String contentId;
	private String alertId;

    private InterestLocation location;
    private long mExpirationDuration;
    private int mTransitionType;
    private double alertRadius;
    

    /**
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center. The value is not checked for validity.
     * @param longitude Longitude of the Geofence's center. The value is not checked for validity.
     * @param radius Radius of the geofence circle. The value is not checked for validity
     * @param expiration Geofence expiration duration in milliseconds The value is not checked for
     * validity.
     * @param transition Type of Geofence transition. The value is not checked for validity.
     */
    public FPGeofence(
            String contentId,
            InterestLocation location,
            long expiration,
            int transition) {
        // Set the instance fields from the constructor
    	this.location = location;
        // An identifier for the geofence
        this.contentId = contentId;
        this.alertId = "";

        // Expiration time in milliseconds
        this.mExpirationDuration = expiration;

        // Transition type
        this.mTransitionType = transition;
        this.alertRadius = 0;

    }
    public FPGeofence(
            String contentId,
            StopLocation location,
            long expiration,
            int transition) {
        // Set the instance fields from the constructor
    	this.location = location;
        // An identifier for the geofence
        this.contentId = contentId;
        this.alertId = "!"+contentId;

        // Expiration time in milliseconds
        this.mExpirationDuration = expiration;

        // Transition type
        this.mTransitionType = transition;
        this.alertRadius = location.getAlertRadius();

    }
    public FPGeofence (Parcel in){
    	readFromParcel(in);
    }
    // Instance field getters

    /**
     * Get the geofence ID
     * @return A SimpleGeofence ID
     */
    public String getContentId() {
        return contentId;
    }
    public String getAlertId(){
    	return alertId;
    }
    /**
     * Get the geofence expiration duration
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    /**
     * Get the geofence transition type
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return mTransitionType;
    }
    public InterestLocation getInterestLocation(){
    	return location;
    }
//    public FPLocation getLocation{
//    	return location;
//    }
    /**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toContentGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                       .setRequestId(getContentId())
                       .setTransitionTypes(mTransitionType)
                       .setCircularRegion(
                               location.getLatitude(),
                               location.getLongitude(),
                               (float)location.getContentRadius())
                       .setExpirationDuration(mExpirationDuration)
                       .build();
    }
    public Geofence toAlertGeofence() {
    	if(this.alertRadius==0) return null;
    	else{
	        return new Geofence.Builder()
	                       .setRequestId(getAlertId())
	                       .setTransitionTypes(mTransitionType)
	                       .setCircularRegion(
	                               location.getLatitude(),
	                               location.getLongitude(),
	                               (float)this.alertRadius)
	                       .setExpirationDuration(mExpirationDuration)
	                       .build();
    	}
    }
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(contentId);
		dest.writeString(alertId);
		dest.writeInt(location.getType().getTypeNumber());
		dest.writeParcelable(location, flags);
		dest.writeLong(mExpirationDuration);
		dest.writeInt(mTransitionType);
		dest.writeDouble(alertRadius);
	}
	protected void readFromParcel(Parcel in){
		contentId = in.readString();
		alertId = in.readString();
		LocationType type = LocationType.getType(in.readInt());
		switch(type){
			case INTEREST_LOCATION:
				location = (InterestLocation)in.readParcelable(InterestLocation.class.getClassLoader());
				break;
			case STOP_LOCATION:
				location = (InterestLocation)in.readParcelable(StopLocation.class.getClassLoader());
				break;
			case ABSTRACT://uh-oh's
			default:
		}
		mExpirationDuration = in.readLong();
		mTransitionType = in.readInt();
		alertRadius = in.readDouble();
	}
	
	public static final Parcelable.Creator<FPGeofence> CREATOR = new Parcelable.Creator<FPGeofence>() {
		public FPGeofence createFromParcel (Parcel in){
			return new FPGeofence(in);
		}
		public FPGeofence[] newArray(int size) {
			return new FPGeofence[size];
		}
	};
}
