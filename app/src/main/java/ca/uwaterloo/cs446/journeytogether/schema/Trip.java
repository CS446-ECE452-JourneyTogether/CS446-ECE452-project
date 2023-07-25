package ca.uwaterloo.cs446.journeytogether.schema;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.common.AddressRep;
import ca.uwaterloo.cs446.journeytogether.common.DateTimeConverter;
import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;
import ca.uwaterloo.cs446.journeytogether.common.GeoHashing;

public class Trip implements Serializable {

    private String id;
    private User driver;
    private SerializableLatLng origin;
    private SerializableLatLng destination;

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    private LocalDateTime departureTime;

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    private LocalDateTime arrivalTime;
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

            if (driverId != null) {
                User.firestore.getValuesById(driverId,
                        (arr) -> {
                            if (!arr.isEmpty()) { this.driver = arr.get(0); }
                        },
                        () -> {}
                );
            }

            this.origin = new SerializableLatLng(GeoHashing.unhash("origin", document));
            this.destination = new SerializableLatLng(GeoHashing.unhash("destination", document));

            this.departureTime = DateTimeConverter.toLocalDateTime((Timestamp) document.get("departureTime"));
            this.arrivalTime = DateTimeConverter.toLocalDateTime((Timestamp) document.get("arrivalTime"));

            this.availableSeats = Math.toIntExact((long) document.get("availableSeats"));
            this.totalSeats = Math.toIntExact((long) document.get("totalSeats"));
            this.cost = Math.toIntExact((long) document.get("cost"));

            List<String> passengerId = (List<String>) document.get("passengers");
            this.passengers = new ArrayList<User>();

            if (passengerId != null) {
                for (String id : passengerId) {
                    User.firestore.getValuesById(id,
                            (arr) -> {
                                if (!arr.isEmpty()) { this.passengers.add(arr.get(0)); }
                            },
                            () -> {}
                    );
                }
            }

        } catch (ClassCastException e) {
            Log.e("E", String.format("Casting error occurred with Trip %s: %s", this.id, e.getMessage()));
        }

    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.driver != null) {
            map.put("driver", this.driver.getId());
        }

        GeoHashing.hash("origin", this.origin, map);
        GeoHashing.hash("destination", this.destination, map);

        map.put("departureTime", DateTimeConverter.toTimestamp(departureTime));
        map.put("arrivalTime", DateTimeConverter.toTimestamp(arrivalTime));
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

    public Trip(User driver, LatLng origin, LatLng destination, int availableSeats, int cost, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.driver = driver;
        this.origin = new SerializableLatLng(origin);
        this.destination = new SerializableLatLng(destination);
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.cost = cost;
        this.passengers = new ArrayList<>();
    }

    public Address getOriginAddress(Context context) {
        return AddressRep.getLocationAddress(context, this.origin.toLatLng());
    }

    public Address getDestinationAddress(Context context) {
        return AddressRep.getLocationAddress(context, this.destination.toLatLng());
    }

    public String getRouteStringRep(Context context) {
        return String.format(
                "%s → %s",
                AddressRep.getLocationStringAddress(context, origin.toLatLng()),
                AddressRep.getLocationStringAddress(context, destination.toLatLng()));
    }

    public String getOriginLocation(Context context) {
        return AddressRep.getLocationStringAddress(context, origin.toLatLng());
    }

    public String getDestinationLocation(Context context) {
        return AddressRep.getLocationStringAddress(context, destination.toLatLng());
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public LatLng getOrigin() {
        return origin.toLatLng();
    }

    public void setOrigin(LatLng origin) {
        this.origin = new SerializableLatLng(origin);
    }

    public LatLng getDestination() {
        return destination.toLatLng();
    }

    public void setDestination(LatLng destination) {
        this.destination = new SerializableLatLng(destination);
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
