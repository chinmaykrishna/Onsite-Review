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
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
	private static String TAG= "getloc";
	private Location location;
	private Context context;
	int enter, stay, flag=0;
	LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    final int TWO_MINUTES = 1000 * 60 * 2;
    int p = 0;
    long time1;
	int radius = 1000;
	String placesSearchStr = null;
	TextView txtLong,txtLat;
	String answer, restaurantName;
	

	public Service() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
	    
 
		Log.d(TAG, "test");
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
		if(mLocationClient != null)
            mLocationClient.requestLocationUpdates(mLocationRequest,  this);
        
        
 
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
         
        if(mLocationClient != null){
            // get location
            mCurrentLocation = mLocationClient.getLastLocation();
                try{
					if(flag==0)
                		time1 = System.currentTimeMillis();
                	flag = 1;
                    // set TextView(s) 
                    //txtLat.setText(mCurrentLocation.getLatitude()+"");
                    //txtLong.setText(mCurrentLocation.getLongitude()+"");
                     
                }catch(NullPointerException npe){
                     
                    Toast.makeText(this, "Failed to Connect", Toast.LENGTH_SHORT).show();
 
                    // switch on location service intent
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                while(radius<=15)
        		{
	                String lngVal= String.valueOf(mCurrentLocation.getLongitude());
	        		String latVal= String.valueOf(mCurrentLocation.getLatitude());
	
	        		ArrayList<Restaurant> restaurant_list= new ArrayList<Restaurant>();
	        		
	        		try{
	        			placesSearchStr= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
	
	        				    "location="+URLEncoder.encode(latVal,"UTF-8")+","+URLEncoder.encode(lngVal,"UTF-8")
	        				    +"&radius="+URLEncoder.encode(String.valueOf(radius),"UTF-8") +
	        				    "&types="+URLEncoder.encode("restaurant","UTF-8")+
	        				    "&key="+URLEncoder.encode("AIzaSyCtO2knQl51Fpmq0XlxA7LTnuJZTZw6CEE","UTF-8");
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
	        			Log.d(TAG, radius+"");
	        			if(radius == 10 && p<1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 15;
	        				continue;
	        			}
	        			else if(radius == 10 && p>=1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 20;
	        			}
	        			if(radius==15 && p<1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 20;
	        				txtLat.setText("No restaurants found");
	        				mLocationClient = null;
	        				break;
	        			}else if(radius==15 && p>=1)
	        			{
	        				radius = 20;
	        				mLocationClient = null;
	        			}
	        			
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
	        				restaurantName = name;
	        				answer = answer + name;
	        				Log.d(TAG, name);
	        				restaurant.setName(name);
	        				restaurant.setLatitude(latitude);
	        				restaurant.setLongitude(longitude);
	        				restaurant_list.add(restaurant);
	        			}
	        			
	        		} catch (JSONException e) {
	        			e.printStackTrace();
	        		}
	        		
	        	}
                txtLat.setText(answer);
        		Log.d(TAG, answer);
        }
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}