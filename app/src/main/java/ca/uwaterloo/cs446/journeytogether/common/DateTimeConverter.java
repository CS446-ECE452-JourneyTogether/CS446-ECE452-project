package ca.uwaterloo.cs446.journeytogether.common;

import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeConverter {
    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        long epochSeconds = localDateTime.toEpochSecond(ZoneOffset.UTC);
        int nanoseconds = localDateTime.getNano();
        return new Timestamp(epochSeconds, nanoseconds);
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        long epochSeconds = timestamp.getSeconds();
        int nanoseconds = timestamp.getNanoseconds();
        return LocalDateTime.ofEpochSecond(epochSeconds, nanoseconds, ZoneOffset.UTC);
    }
}
