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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LocationService extends IntentService 
{
	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	private static String TAG= "getloc";
	int p = 0;
	long time1;
	int radius;
	String placesSearchStr = null;
	TextView txtLong,txtLat;
	String answer = "", restaurantName = null;
	
	Intent intent;
	int counter = 0;
	   
	public LocationService() {
		super("LocationService");
	}

//    @Override
//    public void onCreate() 
//    {
//        super.onCreate();
//        intent = new Intent(BROADCAST_ACTION);      
//    }

    @Override
    public void onStart(Intent intent, int startId) 
    {      
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();        
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        StrictMode.ThreadPolicy policy = new
          		 StrictMode.ThreadPolicy.Builder().permitAll().build();
          		        StrictMode.setThreadPolicy(policy);
		radius = 500;
		restaurantName = null;
    }

    @Override
	protected void onHandleIntent(Intent intent) {
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();        
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        StrictMode.ThreadPolicy policy = new
          		 StrictMode.ThreadPolicy.Builder().permitAll().build();
          		        StrictMode.setThreadPolicy(policy);
	}
    
//    @Override
//    public IBinder onBind(Intent intent) 
//    {
//        return null;
//    }

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
    }

	public class MyLocationListener implements LocationListener
	{
	
	    public void onLocationChanged(final Location loc)
	    {
	        if(isBetterLocation(loc, previousBestLocation)) {
	            loc.getLatitude();
	            loc.getLongitude(); 
	            String lngVal = Double.toString(loc.getLongitude());
	            String latVal = Double.toString(loc.getLatitude());
	            Log.d("lat", latVal);
	            Log.d("long", lngVal);
	            
	            
	            while(radius<=750)
	     		{
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
	        			if(radius == 500 && p<1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 750;
	        				continue;
	        			}
	        			else if(radius == 500 && p>=1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 1000;
	        			}
	        			if(radius==750 && p<1)
	        			{
	        				Log.d(TAG, radius+"m");
	        				radius = 1000;
	        				break;
	        			}else if(radius==750 && p>=1)
	        			{
	        				radius = 1000;
	        			}
	        			
	        			for(int i=0; i<p;i++)
	        			{
	        				String name;
	        				double latitude;
	        				double longitude;
	        				Restaurant restaurant= new Restaurant();
	
	        				JSONObject placeObject= placesArray.getJSONObject(i);
	        				JSONObject loc1= placeObject.getJSONObject("geometry").getJSONObject("location");
	
	        				latitude=Double.valueOf(loc1.getString("lng"));
	        				longitude=Double.valueOf(loc1.getString("lng"));
	        				name=placeObject.getString("name");
	        				restaurantName = name;
	        				answer = answer + name + " " ;
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
	            intent = new Intent();
	            intent.setAction("Response");
	            intent.addCategory(Intent.CATEGORY_DEFAULT);
	            intent.putExtra("flag", "res");
	            intent.putExtra("Restaurant", answer);
	            intent.putExtra("Longitude", lngVal);  
	            intent.putExtra("Longitude", lngVal);   
	            sendBroadcast(intent);
	     		Log.d(TAG, "finished");	     
	     		locationManager.removeUpdates(listener);
	        }
	    }
		
	    public void onProviderDisabled(String provider)
	    {
	        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
	    }
	
	
	    public void onProviderEnabled(String provider)
	    {
	        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
	    }
	
	
	    public void onStatusChanged(String provider, int status, Bundle extras)
	    {
	
	    }
	
	}
}