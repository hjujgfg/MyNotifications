package org.hjujgfg.history;

import android.os.AsyncTask;

public class AsyncNotificationInserter extends AsyncTask<DoNotForgetNotification, Void, Void> {

    private NotificationDatabase db;

    public AsyncNotificationInserter(NotificationDatabase db) {
        this.db = db;
    }

    @Override
    protected Void doInBackground(DoNotForgetNotification... doNotForgetNotifications) {
        db.notificationDao().insertAll(doNotForgetNotifications);
        return null;
    }
}
