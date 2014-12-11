package com.example.getloc;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable {
	private String name;
	private double latitude;
	private double longitude;
	private double food_rating;
	private double ambience_rating;
	private double service_rating;
	
	public Restaurant() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getFood_rating() {
		return food_rating;
	}
	public void setFood_rating(double food_rating) {
		this.food_rating = food_rating;
	}
	public double getAmbience_rating() {
		return ambience_rating;
	}
	public void setAmbience_rating(double ambience_rating) {
		this.ambience_rating = ambience_rating;
	}
	public double getService_rating() {
		return service_rating;
	}
	public void setService_rating(double service_rating) {
		this.service_rating = service_rating;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeDouble(ambience_rating);
		dest.writeDouble(food_rating);
		dest.writeDouble(service_rating);
	}
	
	

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
        	Restaurant rest= new Restaurant();
        	rest.name=in.readString();
        	rest.latitude=in.readDouble();
        	rest.longitude=in.readDouble();
        	
        	rest.ambience_rating=in.readDouble();
        	rest.food_rating=in.readDouble();
        	rest.service_rating=in.readDouble();
            return rest;
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
    
    
	
	
}