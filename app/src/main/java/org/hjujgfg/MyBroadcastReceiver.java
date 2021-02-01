package org.hjujgfg;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "MyBroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Shit happened" + intent, Toast.LENGTH_LONG).show();
        Log.i(TAG, "onReceive: " + intent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = intent.getIntExtra(Notification.EXTRA_NOTIFICATION_ID, 1);
        Log.i("IDS", String.format(Locale.getDefault(),
                "Cancelling, got from intent: %d",
                notificationId));
        notificationManager.cancel(notificationId);
        context.stopService(new Intent(context, MyBackGroundService.class));
    }
}
