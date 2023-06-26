package ca.uwaterloo.cs446.journeytogether;

import static java.time.temporal.ChronoUnit.SECONDS;

import android.os.Parcelable;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Trip implements Serializable {
    private String id;
    private User driver;
    private Set<User> riders;
    private int cost;
    private int totalSeats;

    private LatLng startLoc;
    private LocalDateTime startTime;

    private LatLng destLoc;
    private LocalDateTime expectedDestTime;

    public Trip(String id, User driver, int totalSeats, int cost) {
        this.id = id;
        this.cost = cost;
        this.driver = driver;
        this.totalSeats = totalSeats;
        this.riders = new HashSet<>(); // TODO: placeholder
    }

    public boolean equals(Trip other) {
        return this.id.equals(other.id);
    }

    public Duration expectedDuration() {
        return Duration.between(startTime, expectedDestTime);
    }

    public User getDriver() {
        return this.driver;
    }

    public int totalSeats() { return totalSeats; }
    public int takenSeats() { return riders.size(); }
    public int getCost() { return cost; }
    public int availableSeats() { return totalSeats - riders.size(); }
}
