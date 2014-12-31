package com.example.getloc;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class AlarmStartService extends IntentService{
	LocationServiceStartAlarm alarm= new LocationServiceStartAlarm();

	public AlarmStartService() {
		super("AlarmStart");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		alarm.SetAlarm(AlarmStartService.this);
	}

	 @Override
	    public int onStartCommand(Intent intent, int flags, int startId){
	         alarm.SetAlarm(AlarmStartService.this);
	         return START_STICKY;
	    }
	
	 
	 @Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		alarm.SetAlarm(AlarmStartService.this);
	}

	@Override
	 public IBinder onBind(Intent intent) 
	 {
	     return null;
	 }
		
	
}
