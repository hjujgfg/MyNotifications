package org.hjujgfg.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("select * from notification order by date desc")
    List<DoNotForgetNotification> getAll();

    @Insert
    void insertAll(DoNotForgetNotification... users);

    @Delete
    void delete(DoNotForgetNotification user);
}
