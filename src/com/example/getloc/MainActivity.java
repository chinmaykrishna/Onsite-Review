package com.example.getloc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
 
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getloc.Constants;
import com.example.getloc.Restaurant;
 
public class MainActivity extends Activity {
 
    // locations objects
	int enter, stay, flag=0;
	//    LocationClient mLocationClient;
	//    Location mCurrentLocation;
	//    LocationRequest mLocationRequest;
//    private static final int TWO_MINUTES = 1000 * 60 * 2;
//    private int p;
//	private static String TAG= "Service";
//	private int radius = 10;
//	private String placesSearchStr;
	String restaurantName;
	String answer = "You are near ";
	long time1;
     
    TextView txtLong,txtLat;
    
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StrictMode.ThreadPolicy policy = new
//       		 StrictMode.ThreadPolicy.Builder().permitAll().build();
//       		        StrictMode.setThreadPolicy(policy);
        Intent intent = new Intent(this, Service.class);
    	startService(intent);
//       		i.putExtra("KEY1", "Value to be used by the service");
         
        txtLong = (TextView) findViewById(R.id.txtLong);
        txtLat = (TextView) findViewById(R.id.txtLat);
 
/*
        mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000 * 3600);
        mLocationRequest.setFastestInterval(1000 * 600);*/
 
         
    }
     @Override
        protected void onStart() {
            super.onStart();
            //mLocationClient.connect();
        }
     @Override
        protected void onStop() {
            super.onStop();
            //mLocationClient.disconnect();
        }
      
    /*@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }
 
    // GooglePlayServicesClient.ConnectionCallbacks 
    @Override
    public void onConnected(Bundle arg0) {
         
        /*if(mLocationClient != null)
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
	        			Log.d("radius", radius+"");
	        			if(radius == 10 && p<1)
	        			{
	        				Log.d("rad1", radius+"");
	        				radius = 15;
	        				continue;
	        			}
	        			else if(radius == 10 && p>=1)
	        			{
	        				Log.d("rad1", radius+"");
	        				radius = 20;
	        			}
	        			if(radius==15 && p<1)
	        			{
	        				Log.d("rad2", radius+"");
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
	        				Log.d("NAME", name);
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
        		Log.d("result", answer);*/
        //}
    /*}
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();
     
    }
 
    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed.", Toast.LENGTH_SHORT).show();
        mCurrentLocation = mLocationClient.getLastLocation();
        //txtLat.setText(mCurrentLocation.getLatitude()+"");
 
        //txtLong.setText(mCurrentLocation.getLongitude()+"");
    }
    
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }*/
}