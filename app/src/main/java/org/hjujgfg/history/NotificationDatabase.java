package org.hjujgfg.history;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DoNotForgetNotification.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();

}
