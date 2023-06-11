package ca.uwaterloo.ece452.journeytogether;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class Trip {
    private String id;
    private User driver;
    private Set<User> riders;

    private LatLng startLocation;
    private LocalDateTime startTime;

    private LatLng end;
    private LocalDateTime expectedEndTime;

    public boolean equals(Trip other) {
        return this.id.equals(other.id);
    }

    public Duration expectedDuration() {
        return Duration.between(startTime, expectedEndTime);
    }
}
