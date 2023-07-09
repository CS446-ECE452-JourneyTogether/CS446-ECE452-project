package ca.uwaterloo.cs446.journeytogether.user;

import java.io.Serializable;
import java.util.List;

public class Trip implements Serializable {
    private User driver;
    private String origin;
    private String destination;
    private String date;
    private String time;
    private float duration; // Duration in hours
    private int availableSeats;
    private int totalSeats;
    private int cost;
    private List<User> passengers;

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

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
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

