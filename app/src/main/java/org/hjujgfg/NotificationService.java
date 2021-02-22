package org.hjujgfg;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import org.hjujgfg.history.AsyncNotificationInserter;
import org.hjujgfg.history.DoNotForgetNotification;
import org.hjujgfg.history.NotificationDatabase;

import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.app.PendingIntent.getBroadcast;
import static org.hjujgfg.MainActivity.NOTIFICATION_CONTENT_KEY;

public class NotificationService extends Service {

    private static final Random RANDOM = new Random();
    private static final String CHANNEL_ID = "TestChannel";
    public static final String NOTIFICATION_ID_KEY = "notification_id";

    BroadcastReceiver br;
    NotificationDatabase db;
    boolean notificationHasBeenCreated = false;


    @Override
    public void onCreate() {
        super.onCreate();
        br = new MyBroadcastReceiver();
        registerReceiver(br, new IntentFilter(getString(R.string.CANCEL_ACTION)));
        db = Room.databaseBuilder(
                getApplicationContext(), NotificationDatabase.class, "do-not-forget-db"
        ).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!notificationHasBeenCreated) {
            startForeground(1, createNotification(intent.getStringExtra(NOTIFICATION_CONTENT_KEY)));
            notificationHasBeenCreated = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(br);
        db.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Notification createNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = getActivity(
                getApplicationContext(), 0, intent, 0
        );
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.prefs_file), MODE_PRIVATE
        );
        int prev = prefs.getInt(NOTIFICATION_ID_KEY, -1);
        Log.i("IDS", "Found id is: " + prev);
        if (prev > 0) {
            //notificationManager.cancel(prev);
        }

        int id = RANDOM.nextInt(Integer.MAX_VALUE);

        Intent cancelIntent = new Intent();
        cancelIntent.setAction(getString(R.string.CANCEL_ACTION));
        cancelIntent.putExtra(Notification.EXTRA_NOTIFICATION_ID, id);
        PendingIntent cancelPendingIntent = getBroadcast(
                this, 0, cancelIntent, FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.dnf_cutout_light_round)
                .setContentTitle("Do not forget, man!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                        R.mipmap.dnf_cutout_light_round,
                        getString(R.string.forget),
                        cancelPendingIntent
                );

        // notificationId is a unique int for each notification that you must define
        startForegroundService(new Intent(getApplicationContext(), NotificationService.class));

        //notificationManager.notify(id, builder.build());
        prefs.edit().putInt(NOTIFICATION_ID_KEY, id).apply();
        Log.i("IDS", "Saving id is: " + id);
        return builder.build();
    }
}
