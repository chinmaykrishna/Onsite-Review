package com.example.getloc;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.getloc.Constants;
import com.example.getloc.Restaurant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class Service extends IntentService implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
LocationListener{
	private int p;
	private static String TAG= "Service";
	private Location location;
	private int radius;
	private String placesSearchStr;
	private Context context;

	public Service() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		int enter, stay;
	    LocationClient mLocationClient;
	    Location mCurrentLocation;
	    LocationRequest mLocationRequest;
	    final int TWO_MINUTES = 1000 * 60 * 2;
	    int p = 0;
		String TAG= "Service";
		int radius = 1000;
		String placesSearchStr = null;
		
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000 * 3600);
		mLocationRequest.setFastestInterval(1000 * 600);
		Bundle extras = intent.getExtras();
		if (intent.hasExtra(Constants.EXTRA_KEY_LOCATION)) {
			location = (Location)(extras.get(Constants.EXTRA_KEY_LOCATION));
			radius = extras.getInt(Constants.EXTRA_KEY_RADIUS, Constants.DEFAULT_RADIUS);
		}


		String lngVal= String.valueOf(location.getLongitude());
		String latVal= String.valueOf(location.getLatitude());

		ArrayList<Restaurant> restaurant_list= new ArrayList<Restaurant>();

		try{
			placesSearchStr= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +

				    "location="+URLEncoder.encode(latVal,"UTF-8")+","+URLEncoder.encode(lngVal,"UTF-8")
				    +"&radius="+URLEncoder.encode(String.valueOf(radius),"UTF-8") +
				    "&types="+URLEncoder.encode("restaurant","UTF-8")+
				    "&key="+URLEncoder.encode("AIzaSyBCbz-B-bwqNsDqda0EYOnub-SCu0GF-BA","UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		StringBuilder placesBuilder= new StringBuilder();
		HttpClient placesClient=new DefaultHttpClient();
		try {
			HttpGet placesGet = new HttpGet(placesSearchStr);
			Log.d(TAG, placesSearchStr);
			HttpResponse placesResponse = placesClient.execute(placesGet);
			StatusLine placesSS= placesResponse.getStatusLine();
			if(placesSS.getStatusCode()==200)
			{
				HttpEntity placesEntity= placesResponse.getEntity();
				InputStream placesContent = placesEntity.getContent();
				InputStreamReader placesInput = new InputStreamReader(placesContent);
				BufferedReader placesReader = new BufferedReader(placesInput);
				String lineIn;
				while ((lineIn = placesReader.readLine()) != null) {
					
					placesBuilder.append(lineIn);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String result= placesBuilder.toString();
		try {
			JSONObject resultObject = new JSONObject(result);
			JSONArray placesArray = resultObject.getJSONArray("results");

			p=placesArray.length();
			Log.d(TAG, String.valueOf(placesArray.length()));

			for(int i=0; i<p;i++)
			{
				String name;
				double latitude;
				double longitude;
				Restaurant restaurant= new Restaurant();

				JSONObject placeObject= placesArray.getJSONObject(i);
				JSONObject loc= placeObject.getJSONObject("geometry").getJSONObject("location");

				latitude=Double.valueOf(loc.getString("lng"));
				longitude=Double.valueOf(loc.getString("lng"));
				name=placeObject.getString("name");
				Log.d("NAME", name);
				restaurant.setName(name);
				restaurant.setLatitude(latitude);
				restaurant.setLongitude(longitude);
				restaurant_list.add(restaurant);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent numberIntent= new Intent("NUMBER");
		Bundle restBundle=new Bundle();
		restBundle.putInt("REST_NUM", p);
		restBundle.putParcelableArrayList("REST_LIST", restaurant_list);
		numberIntent.putExtras(restBundle);
		sendBroadcast(numberIntent);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}