package ca.uwaterloo.cs446.journeytogether.schema;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;

public class Trip implements Serializable {

    private String id;
    private User driver;
    private String origin;
    private String destination;
    private String date;
    private String time;
    private double duration; // Duration in hours
    private int availableSeats;
    private int totalSeats;
    private int cost;
    private List<User> passengers;

    private static final String COLLECTION_PATH = "jt_trips";
    public static final FirestoreCollection<Trip> firestore =
            new FirestoreCollection<>(
                    COLLECTION_PATH,
                    (onto, document) -> { onto.update(document); },
                    (value) -> value.asMap(),
                    (onto, id) -> onto.setId(id),
                    () -> new Trip()
            );

    public void update(DocumentSnapshot document) {
        try {
            String driverId = (String) document.get("driver");
            User.firestore.getValuesById(driverId, (arr) -> {
                if (!arr.isEmpty()) { this.driver = arr.get(0); }
            } );

            this.origin = (String) document.get("origin");
            this.destination = (String) document.get("destination");
            this.date = (String) document.get("date");
            this.time = (String) document.get("time");
            this.duration = (double) document.get("duration");
            this.availableSeats = Math.toIntExact((long) document.get("availableSeats"));
            this.totalSeats = Math.toIntExact((long) document.get("totalSeats"));
            this.cost = Math.toIntExact((long) document.get("cost"));

            Log.d("D", String.format("%s", this.cost));

            List<String> passengerId = (List<String>) document.get("passengers");
            this.passengers = new ArrayList<User>();

            for (String id : passengerId) {
                User.firestore.getValuesById(id, (arr) -> {
                    if (!arr.isEmpty()) { this.passengers.add(arr.get(0)); }
                });
            }
        } catch (ClassCastException e) {
            Log.e("E", String.format("Casting error occurred with Trip %s: %s", this.id, e.getMessage()));
        }

    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver", this.driver.getId());
        map.put("origin", this.origin);
        map.put("destination", this.destination);
        map.put("date", this.date);
        map.put("time", this.time);
        map.put("duration", this.duration);
        map.put("availableSeats", this.availableSeats);
        map.put("totalSeats", this.totalSeats);
        map.put("cost", this.cost);

        ArrayList<String> passengerId = new ArrayList<>();
        for (User p : passengers) {
            passengerId.add(p.getId());
        }

        map.put("passengers", passengerId);

        return map;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getId() { return this.id; }

    public Trip() {}

    public Trip(User driver, String origin, String destination, int availableSeats, String date, String time) {
        this.driver = driver;
        this.origin = origin;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats;
        this.date = date;
        this.time = time;
        this.duration = 2.0f;
        this.cost = 100;
        this.passengers = null;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }
}

