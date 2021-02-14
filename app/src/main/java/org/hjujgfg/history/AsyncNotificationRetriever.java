package org.hjujgfg.history;

import android.os.AsyncTask;

import org.hjujgfg.HistoryLoadedHandler;

import java.util.List;

public class AsyncNotificationRetriever extends AsyncTask<Void, Void, Void> {
    private NotificationDatabase db;
    private HistoryLoadedHandler resultCallback;

    public AsyncNotificationRetriever(NotificationDatabase db,
                                      HistoryLoadedHandler handler) {
        this.db = db;
        this.resultCallback = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<DoNotForgetNotification> all = db.notificationDao().getAll();
        resultCallback.handle(all);
        return null;
    }
}
