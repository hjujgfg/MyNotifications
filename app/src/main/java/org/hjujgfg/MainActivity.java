package org.hjujgfg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import org.hjujgfg.history.AsyncNotificationInserter;
import org.hjujgfg.history.AsyncNotificationRetriever;
import org.hjujgfg.history.DoNotForgetNotification;
import org.hjujgfg.history.NotificationDatabase;
import org.hjujgfg.view.HistoricalNotificationView;

import java.util.Collection;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.app.PendingIntent.getBroadcast;

public class MainActivity extends AppCompatActivity implements HistoryLoadedHandler {

    private static final Random RANDOM = new Random();
    private static final String CHANNEL_ID = "TestChannel";
    public static final String NOTIFICATION_ID_KEY = "notification_id";
    NotificationManagerCompat notificationManager;
    NotificationDatabase db;
    LinearLayout historyHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);
        db = Room.databaseBuilder(
                getApplicationContext(), NotificationDatabase.class, "do-not-forget-db"
        ).build();
        historyHolder = findViewById(R.id.history_view);
        triggerReloadHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void show(View view) {
        TextView textView = findViewById(R.id.messageArea);
        CharSequence message = textView.getText();
        createNotification(message.toString());
        textView.setText("");
    }

    public void cancel(View view) {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.prefs_file), MODE_PRIVATE
        );
        notificationManager.cancelAll();
        prefs.edit().putInt(NOTIFICATION_ID_KEY, -1).apply();
    }

    public void stopService(View view) {
        stopService(new Intent(this, MyBackGroundService.class));
    }

    public void createNotification(String message) {
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
            notificationManager.cancel(prev);
        }

        int id = RANDOM.nextInt(Integer.MAX_VALUE);

        Intent cancelIntent = new Intent();
        cancelIntent.setAction(getString(R.string.CANCEL_ACTION));
        cancelIntent.putExtra(Notification.EXTRA_NOTIFICATION_ID, id);
        PendingIntent cancelPendingIntent = getBroadcast(
                getApplicationContext(), 0, cancelIntent, FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.dnf_no_bg_white_foreground)
                .setContentTitle("Do not forget, man!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(
                        R.mipmap.dnf_logo,
                        getString(R.string.forget),
                        cancelPendingIntent
                );

        // notificationId is a unique int for each notification that you must define
        startService(new Intent(getApplicationContext(), MyBackGroundService.class));
        notificationManager.notify(id, builder.build());
        new AsyncNotificationInserter(db)
                .execute(new DoNotForgetNotification(message));
        triggerReloadHistory();
        prefs.edit().putInt(NOTIFICATION_ID_KEY, id).apply();
        Log.i("IDS", "Saving id is: " + id);
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

    public void triggerReloadHistory() {
        new AsyncNotificationRetriever(db, this).execute();
    }

    @Override
    public void handle(Collection<DoNotForgetNotification> notifications) {
        runOnUiThread(() -> {
            historyHolder.removeAllViews();
            notifications.forEach(n -> new HistoricalNotificationView(
                    this, historyHolder, n)
            );
        });
    }

    public NotificationDatabase getDb() {
        return db;
    }
}
