package org.hjujgfg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "TestChannel";
    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void show(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        Intent cancelIntent = new Intent();
        cancelIntent.setAction(getString(R.string.CANCEL_ACTION));
        cancelIntent.putExtra(Notification.EXTRA_NOTIFICATION_ID, 1);
        PendingIntent cancelPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, 0);

        TextView textView = findViewById(R.id.messageArea);
        CharSequence message = textView.getText();
        notificationManager.cancel(1);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Do not forget, man!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                        R.drawable.ic_launcher_background,
                        getString(R.string.cancel),
                        cancelPendingIntent);

        // notificationId is a unique int for each notification that you must define
        startService(new Intent(getApplicationContext(), MyBackGroundService.class));
        notificationManager.notify(1, builder.build());
    }

    public void cancel(View view) {
        notificationManager.cancel(1);
    }

    public void stopService(View view) {
        stopService(new Intent(this, MyBackGroundService.class));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
