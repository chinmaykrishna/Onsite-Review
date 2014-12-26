package com.example.getloc;
 
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
 
public class MainActivity extends Activity {
 
	private static String TAG= "MainActivity";
	String restaurantName = null;
	String answer = "You are near ";
	String notFound = "Restaurant not found";
	long time1;
	String latitude = null, longitude = null;
    TextView txtLong,txtLat;
    
    
    private MyBroadcastReceiver receiver;
      
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LocationService.class);
    	startService(intent);
//       		i.putExtra("KEY1", "Value to be used by the service");
    	receiver = new MyBroadcastReceiver();
    	IntentFilter intentFilter = new IntentFilter("Response");
    	  intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
    	  registerReceiver(receiver, intentFilter);
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
      
     @Override
     protected void onResume() {
       super.onResume();
       IntentFilter intentFilter = new IntentFilter("Response");
 	  intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
       registerReceiver(receiver, intentFilter);
     }
     @Override
     protected void onPause() {
       super.onPause();
       unregisterReceiver(receiver);
     }
     public class MyBroadcastReceiver extends BroadcastReceiver {

   	  @Override
   	  public void onReceive(Context context, Intent intent) {
   	   latitude = intent.getStringExtra("Latitude");
   	   longitude = intent.getStringExtra("Longitude");
   	   restaurantName = intent.getStringExtra("Restaurant");
   	   txtLat = (TextView) findViewById(R.id.txtLat);
   	   if(restaurantName!= null)
   	   {
   		   Log.d(TAG, restaurantName);
   		   txtLat.setText(restaurantName);
   	   }
   	   else
   	   {
   		   txtLat.setText("Restaurant not found");
   	   }
   	  }
   }
}