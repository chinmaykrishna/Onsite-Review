package com.example.getloc;

import android.app.AlarmManager;

public class Constants 
{
	//Amazon Credentials Starts
	public static final String ACCOUNT_ID = "659879211996";
    public static final String IDENTITY_POOL_ID = "us-east-1:8565615e-4f88-436b-8f42-418081d8253b";
    public static final String UNAUTH_ROLE_ARN= "arn:aws:iam::659879211996:role/Cognito_DbAccessorUnauth_DefaultRole";
	public static final String AUTH_ROLE_ARN="arn:aws:iam::659879211996:role/Cognito_DbAccessorAuth_DefaultRole";
    //Amazon Credentails Ends
    public static int MAX_DISTANCE= 100;
	public static long MAX_TIME=AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	
	// You will generally want passive location updates to occur less frequently
	  // than active updates. You need to balance location freshness with battery life.
	  // The location update distance for passive updates.
	  public static int PASSIVE_MAX_DISTANCE = MAX_DISTANCE;
	  // The location update time for passive updates
	  public static long PASSIVE_MAX_TIME = MAX_TIME;
	  // Use the GPS (fine location provider) when the Activity is visible?
	  public static boolean USE_GPS_WHEN_ACTIVITY_VISIBLE = true;
	  //When the user exits via the back button, do you want to disable
	  // passive background updates.
	  public static boolean DISABLE_PASSIVE_LOCATION_WHEN_USER_EXIT = false;
	  
	  public static String EXTRA_KEY_LOCATION = "location";
	  public static String EXTRA_KEY_RADIUS ="radius";
	  
	  public static int DEFAULT_RADIUS= 19312; //19312 meters = 12 miles.Originally it was set to 100000 meters.
}