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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LocationService extends Service 
{
       public static final String BROADCAST_ACTION = "Hello World";
       private static final int TWO_MINUTES = 1000 * 60 * 2;
       public LocationManager locationManager;
       public MyLocationListener listener;
       public Location previousBestLocation = null;
       private static String TAG= "getloc";
       int p = 0;
       long time1;
	   int radius = 10;
	   String placesSearchStr = null;
	   TextView txtLong,txtLat;
	   String answer = "You are near", restaurantName;

       Intent intent;
       int counter = 0;
       

    @Override
    public void onCreate() 
    {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);      
    }

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
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



/** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }



@Override
    public void onDestroy() {       
       // handler.removeCallbacks(sendUpdatesToUI);     
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);        
    }   

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

public class MyLocationListener implements LocationListener
{

    public void onLocationChanged(final Location loc)
    {
        Log.i("**************************************", "Location changed");
        if(isBetterLocation(loc, previousBestLocation)) {
            loc.getLatitude();
            loc.getLongitude(); 
            String lngVal = Double.toString(loc.getLongitude());
            String latVal = Double.toString(loc.getLatitude());
            Log.d("lat", latVal);
            Log.d("long", lngVal);
            
            
            while(radius<=15)
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
	        				break;
	        			}else if(radius==15 && p>=1)
	        			{
	        				radius = 20;
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
	     		Log.d(TAG, "finished");
	            
	            intent.putExtra("Latitude", loc.getLatitude());
	            intent.putExtra("Longitude", loc.getLongitude());     
	            intent.putExtra("Provider", loc.getProvider());                 
	            sendBroadcast(intent);
	     		onDestroy();
	
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