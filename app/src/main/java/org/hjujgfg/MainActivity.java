package org.hjujgfg;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import org.hjujgfg.history.AsyncNotificationInserter;
import org.hjujgfg.history.AsyncNotificationRetriever;
import org.hjujgfg.history.DoNotForgetNotification;
import org.hjujgfg.history.NotificationDatabase;
import org.hjujgfg.view.HistoricalNotificationView;

import java.util.Collection;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements HistoryLoadedHandler {

    private static final Random RANDOM = new Random();
    private static final String CHANNEL_ID = "TestChannel";
    public static final String NOTIFICATION_ID_KEY = "notification_id";
    public static final String NOTIFICATION_CONTENT_KEY = "notification_content";
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
        startNotification(message);
    }

    public void cancel(View view) {
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.prefs_file), MODE_PRIVATE
        );
        stopService(new Intent(getApplicationContext(), NotificationService.class));
        prefs.edit().putInt(NOTIFICATION_ID_KEY, -1).apply();
    }

    public void startNotification(CharSequence message) {
        if (message == null || message.toString().isEmpty()) {
            Toast t = Toast.makeText(
                    this, getString(R.string.nothing_entered), Toast.LENGTH_SHORT
            );
            t.show();
            return;
        }
        stopService(new Intent(getApplicationContext(), NotificationService.class));
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra(NOTIFICATION_CONTENT_KEY, message.toString());
        startForegroundService(intent);
        TextView textView = findViewById(R.id.messageArea);
        textView.setText("");
        new AsyncNotificationInserter(db)
                .execute(new DoNotForgetNotification(message.toString()));
        triggerReloadHistory();
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
