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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
 
	private static String TAG= "MainActivity";
	String restaurantName = null;
	String answer = "You are near ";
	String notFound = "Restaurant not found";
	long time1;
	String latitude = null, longitude = null, accuracy = null;
    TextView txtLong,txtLat;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String FIRST = "firstrun"; 
    public static final String CHECK1 = "restaurant1"; 
    public static final String TIME = "time";
    public static final String REVIEW = "review";
    SharedPreferences shared;
//    Button btn;
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
    	  Editor editor = shared.edit();
    	  Intent intent = new Intent(this, LocationService.class);
    	  intent.putExtra("flag", "alarm");
    	  PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
    	  AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	  if (!shared.contains(FIRST))
          {
        	  Calendar cal = Calendar.getInstance();
              cal.setTimeInMillis(System.currentTimeMillis());
        	  alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1*60*1000, pintent);
    		  editor.putString(FIRST, "no");
          }
    	  txtLat = (TextView) findViewById(R.id.txtLat);
		  txtLat.setText(shared.getString(CHECK1, "Searching..."));
    	  if(shared.getBoolean(REVIEW, false))
    	  {
    		  txtLat = (TextView) findViewById(R.id.txtLat);
    		  txtLat.setText("You are ready to review "+shared.getString(CHECK1, ""));
//    		  btn = (Button) findViewById(R.id.button1);
//    		  btn.setEnabled(false);
    		  Log.d(TAG, "inside");
    		  editor.putBoolean(REVIEW, false);
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
   		shared = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
   		String flag = intent.getStringExtra("flag");

   		if(flag.equals("alarm"))
   		{
   			Calendar cal = Calendar.getInstance();
   			int hour = cal.get(Calendar.HOUR_OF_DAY);
   			if(0<=hour && hour<7)
   			{
   				Intent intent2 = new Intent(context, LocationService.class);
   	    	  intent.putExtra("flag", "alarm");
   	    	  PendingIntent pintent = PendingIntent.getService(context, 0, intent2, 0);
   	    	  AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
   	    	  alarm.cancel(pintent);
   	    	cal.set(Calendar.HOUR_OF_DAY, 7);
   	    	cal.set(Calendar.MINUTE, 00);
   	    	cal.set(Calendar.SECOND, 00);
   	    	alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1*60*1000, pintent);
   			}
   				
   			
	   		Intent i = new Intent(context, MainActivity.class);
	        context.startService(i);
	        Log.d(TAG, "alarm");
   		}
   		else if(flag.equals("res"))
   		{
	   	   latitude = intent.getStringExtra("Latitude");
	   	   longitude = intent.getStringExtra("Longitude");
	   	   accuracy = intent.getStringExtra("Accuracy");
	   	   restaurantName = intent.getStringExtra("Restaurant");
	   	   txtLat = (TextView) findViewById(R.id.txtLat);
	   	   if(!restaurantName.equals(""));
	   	   {
		   	   Log.d(TAG,restaurantName);
		   	   txtLat.setText(restaurantName);
	   	   }
//	   	   txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
//	   	   txtAccuracy.setText(accuracy);
//	   	   btn.setEnabled(true);
	   	
//	   	btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            	Toast.makeText(getApplicationContext(), "Review has been sent for verification",
//            			   Toast.LENGTH_LONG).show();
//            }
//        });
//	   	   if(restaurantName!= null)
//	   	   {
//	   		   Editor editor = shared.edit();
//	   		   txtLat.setText(restaurantName);
//	   		   if(restaurantName.equals(shared.getString(CHECK1, "")))
//   			   {
//   			   		Log.d(TAG, "review ready");
//   			   		if((System.currentTimeMillis() - shared.getLong(TIME, 0))  <2*60*1000)
//   			   		{
//   			   			txtLat.setText("You are ready to review "+ shared.getString(CHECK1, ""));Log.d(TAG, "festing");
//   			   			editor.putBoolean(REVIEW, true);
//   			   		}
//   			   		else
//   			   		{
//   			   			
//   			   			editor.putBoolean(REVIEW, false);
//   			   		}
//   			   }
//	   		
//		      editor.putString(CHECK1, restaurantName);
//		      editor.putLong(TIME, System.currentTimeMillis());
//			  editor.commit();
//			  
//
//	   	   }
//	   	   else
//	   	   {
//	   		   float timeLeft  = (System.currentTimeMillis() - shared.getLong(TIME, 0))/6000;
//	   		   int minutesLeft = (int)timeLeft;
//	   		   txtLat.setText("Searching in "+minutesLeft+" minutes");Log.d(TAG, "resting");
//	   	   }
	   	
   		}
   	  }
   }
}