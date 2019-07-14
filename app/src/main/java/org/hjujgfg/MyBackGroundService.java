package org.hjujgfg;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MyBackGroundService extends Service {

    BroadcastReceiver br;


    @Override
    public void onCreate() {
        super.onCreate();
        br = new MyBroadcastReceiver();
        registerReceiver(br, new IntentFilter(getString(R.string.CANCEL_ACTION)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(br);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
