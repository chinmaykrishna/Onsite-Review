package com.example.getloc;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

public class LocationServiceStopAlarm extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		Toast.makeText(context, "StopAlarm!", Toast.LENGTH_LONG).show();
		context.stopService(new Intent(context, LocationService.class));
		wl.release();
	}
	
	public void SetAlarm(Context context)
	 {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
	     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	     Intent i = new Intent(context, LocationServiceStartAlarm.class);
	     PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
	     am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY  ,pi);
	  }

	 public void CancelAlarm(Context context)
	 {
	     Intent intent = new Intent(context, LocationServiceStartAlarm.class);
	     PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	     AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	     alarmManager.cancel(sender);
	 }

}
