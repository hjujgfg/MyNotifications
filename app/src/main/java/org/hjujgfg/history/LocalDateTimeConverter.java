package org.hjujgfg.history;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeConverter {
    @TypeConverter
    public static LocalDateTime toDate(Long dateLong){
        return dateLong == null ? null: Instant.ofEpochMilli(dateLong)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @TypeConverter
    public static Long fromDate(LocalDateTime date){
        return date == null ? null : date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
