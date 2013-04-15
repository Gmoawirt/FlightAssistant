package com.gmoawirt.flightassistant;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {

	private static NotificationManager notificationManager;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_NOT_RUNNING = 2;
	private static final int NOTIFICATION_ID = 1;

	public static void updateNotification(FlightManager fm, int status) {

		Intent resultIntent = new Intent(fm, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(fm);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(fm);
		mBuilder.setContentIntent(resultPendingIntent);

		switch (status) {
		case STATUS_RUNNING:

			mBuilder.setSmallIcon(R.drawable.mini_plane).setContentTitle("FlightAssistant").setContentText("Background service running.");
			notificationManager = (NotificationManager) fm.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			break;
			
		case STATUS_NOT_RUNNING:
			
			mBuilder.setSmallIcon(android.R.drawable.stat_notify_error).setContentTitle("FlightAssistant").setContentText("Background service NOT running.");
			notificationManager = (NotificationManager) fm.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			break;

		default:
			break;
		}

	}

	public static void cancelNotification(FlightManager fm) {
		notificationManager = (NotificationManager) fm.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

}
