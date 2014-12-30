package com.example.getloc;
 
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
    String check1, check2;
    
    private MyBroadcastReceiver receiver;
      
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(this, LocationService.class);
//    	startService(intent);
//       		i.putExtra("KEY1", "Value to be used by the service");
    	receiver = new MyBroadcastReceiver();
    	IntentFilter intentFilter = new IntentFilter("Response");
    	  intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
    	  registerReceiver(receiver, intentFilter);
    	  
    	  Intent intent = new Intent(this, LocationService.class);
    	  intent.putExtra("flag", "alarm");
    	  PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
    	  Calendar cal = Calendar.getInstance();
          cal.setTimeInMillis(System.currentTimeMillis());
    	  AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	  alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent);
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
   		
   		String flag = intent.getStringExtra("flag");
   		if(flag.equals("alarm"))
   		{
	   		Intent i = new Intent(context, MainActivity.class);
	        context.startService(i);
	        Log.d(TAG, "alarm");
   		}
   		else if(flag.equals("res"))
   		{
	   	   latitude = intent.getStringExtra("Latitude");
	   	   longitude = intent.getStringExtra("Longitude");
	   	   restaurantName = intent.getStringExtra("Restaurant");
	   	   txtLat = (TextView) findViewById(R.id.txtLat);
			   Log.d(TAG, "testing");
	
	   	   if(restaurantName!= "")
	   	   {
	   		   txtLat.setText(restaurantName);
	   	   }
	   	   else
	   	   {
	   		   txtLat.setText("Restaurant not found");
	   	   }
   		}
   	  }
   }
}