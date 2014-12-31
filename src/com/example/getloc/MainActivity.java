package com.example.getloc;
 
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String FIRST = "firstrun"; 
    public static final String CHECK1 = "restaurant1"; 
    public static final String TIME = "time";
    public static final String REVIEW = "review";
    SharedPreferences shared;
    
    private MyBroadcastReceiver receiver;
      
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
    	receiver = new MyBroadcastReceiver();
    	
    	IntentFilter intentFilter = new IntentFilter("Response");
    	  intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
    	  registerReceiver(receiver, intentFilter);
    	  shared = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    	  Log.d(TAG, "before edit");
    	  Editor editor = shared.edit();
    	  Log.d(TAG, "after edit");
    	  if (!shared.contains(FIRST))
          {
    		  Intent intent = new Intent(this, LocationService.class);
        	  intent.putExtra("flag", "alarm");
        	  PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        	  Calendar cal = Calendar.getInstance();
              cal.setTimeInMillis(System.currentTimeMillis());
        	  AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        	  alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent);
    		  Log.d(TAG, "inside");
    		  editor.putString(FIRST, "no");
          }
    	  if(shared.getBoolean(REVIEW, false))
    	  {
    		  txtLat = (TextView) findViewById(R.id.txtLat);
    		  txtLat.setText("You are ready to review "+shared.getString(CHECK1, ""));
    	  }
    	  editor.commit();
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
			   
	
	   	   if(restaurantName!= null)
	   	   {
	   		   Editor editor = shared.edit();
	   		   txtLat.setText(restaurantName);
	   		   if(restaurantName.equals(shared.getString(CHECK1, "")))
	   			   {
	   			   		if((System.currentTimeMillis() - shared.getLong(TIME, 0))  <12*1000)
	   			   		{
	   			   			txtLat.setText("You are ready to review "+ restaurantName);Log.d(TAG, "festing");
	   			   			editor.putBoolean(REVIEW, true);
	   			   		}
	   			   		else
	   			   		{
	   			   			editor.putBoolean(REVIEW, false);
	   			   		}
	   			   }
		 	  
		      editor.putString(CHECK1, restaurantName);
		      editor.putLong(TIME, System.currentTimeMillis());
			  editor.commit();

	   	   }
	   	   else
	   	   {
	   		   txtLat.setText("Restaurant not found");Log.d(TAG, "resting");
	   	   }
   		}
   	  }
   }
}