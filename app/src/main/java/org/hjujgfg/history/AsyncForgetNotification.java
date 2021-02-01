package org.hjujgfg.history;

import android.os.AsyncTask;

public class AsyncForgetNotification extends AsyncTask<DoNotForgetNotification, Void, Void> {

    private NotificationDatabase db;
    private Runnable callback;

    public AsyncForgetNotification(NotificationDatabase db, Runnable callback) {
        this.db = db;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(DoNotForgetNotification... doNotForgetNotifications) {
        for (DoNotForgetNotification n : doNotForgetNotifications) {
            db.notificationDao().delete(n);
        }
        callback.run();
        return null;
    }
}
