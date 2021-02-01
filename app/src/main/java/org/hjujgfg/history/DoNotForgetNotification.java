package org.hjujgfg.history;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;

@Entity(tableName = "notification")
@TypeConverters(LocalDateTimeConverter.class)
public class DoNotForgetNotification {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date")
    public LocalDateTime date;

    public DoNotForgetNotification() {
    }

    public DoNotForgetNotification(String content) {
        this.content = content;
        this.date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
