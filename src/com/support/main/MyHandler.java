package com.support.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.appolissupport.R;
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.support.fragments.DetailsFragment;
import com.support.utilities.ImageLoader;

public class MyHandler extends NotificationsHandler  {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    static public LoginActivity mainActivity;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");

        sendNotification(nhMessage);
}

    private void sendNotification(String msg) {
        if (!MainActivity.isInForeground()) {
            mNotificationManager = (NotificationManager)
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                    new Intent(ctx, LoginActivity.class), 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ctx)
                            .setSmallIcon(R.drawable.logo_support)
                            .setContentTitle("SlideKick Support")
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))

                            .setContentText(msg);


            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else if (MainActivity.FragPageTitle.matches(".*\\d+.*")) {
            DetailsFragment df = new DetailsFragment();
            df.refresh(MainActivity.getCustomAppContext(), DetailsFragment.caseNumber, false);
        }
        else {
            Toast.makeText(ctx,msg,Toast.LENGTH_LONG).show();
        }
    }
}
