package com.example.getloc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ReviewNotifier {
	public void createNotification(Context context)
	{
		 // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(context, MainActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
	 // Build notification
	    // minSdk changed for supporting notification
	    android.app.Notification noti = new android.app.Notification.Builder(context)
	        .setContentTitle("RSP- Review")
	        .setContentText( "Do you want to review the restaurant you are in?")
	        .setContentIntent(pIntent)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setAutoCancel(true)
	        .build();
	    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    notificationManager.notify(0, noti);
	    

	}

}
