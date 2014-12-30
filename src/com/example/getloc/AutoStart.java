package com.example.getloc;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {
	LocationServiceStartAlarm alarmStart= new LocationServiceStartAlarm();
	LocationServiceStopAlarm alarmStop= new LocationServiceStopAlarm();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
			alarmStart.SetAlarm(context);
			Calendar rightNow = Calendar.getInstance();
			long timenow=rightNow.getTimeInMillis();
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 9); 
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			long nine_am=cal.getTimeInMillis();
			if(timenow<nine_am)
			{
				alarmStop.SetAlarm(context);
			}
			
        }
	}

	}
