package org.hjujgfg;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "MyBroadCastReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Shit happened" + intent, Toast.LENGTH_LONG);
        Log.i(TAG, "onReceive: " + intent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        int notificationId = intent.getIntExtra(Notification.EXTRA_NOTIFICATION_ID, 1);
        notificationManager.cancel(notificationId);
        context.stopService(new Intent(context, MyBackGroundService.class));
    }
}
