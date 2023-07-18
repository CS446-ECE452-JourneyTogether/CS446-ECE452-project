package ca.uwaterloo.cs446.journeytogether.schema;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.common.AddressRep;
import ca.uwaterloo.cs446.journeytogether.common.DateTimeConverter;
import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;

public class Trip implements Serializable {

    private String id;
    private User driver;
    private GeoPoint origin;
    private GeoPoint destination;

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

    private static final String COLLECTION_PATH = "jt_trips_test";
    public static final FirestoreCollection<Trip> firestore =
            new FirestoreCollection<>(
                    COLLECTION_PATH,
                    (onto, document) -> {
                        onto.update(document);
                    },
                    (value) -> value.asMap(),
                    (onto, id) -> {
                        onto.setId(id);
                    },
                    () -> new Trip()
            );

    public void update(DocumentSnapshot document) {
        try {
            String driverId = document.getString("driver");

            if (driverId != null) {
                User.firestore.getValuesById(driverId,
                        (arr) -> {
                            if (!arr.isEmpty()) {
                                this.driver = arr.get(0);
                            }
                        },
                        () -> {
                        }
                );
            }

            this.origin = document.getGeoPoint("origin");
            this.destination = document.getGeoPoint("destination");

            this.departureTime = DateTimeConverter.toLocalDateTime(document.getTimestamp("departureTime"));
            this.arrivalTime = DateTimeConverter.toLocalDateTime(document.getTimestamp("arrivalTime"));

            this.availableSeats = document.getLong("availableSeats").intValue();
            this.totalSeats = document.getLong("totalSeats").intValue();
            this.cost = document.getLong("cost").intValue();

            List<String> passengerId = (List<String>) document.get("passengers");
            this.passengers = new ArrayList<>();

            if (passengerId != null) {
                for (String id : passengerId) {
                    User.firestore.getValuesById(id,
                            (arr) -> {
                                if (!arr.isEmpty()) {
                                    this.passengers.add(arr.get(0));
                                }
                            },
                            () -> {
                            }
                    );
                }
            }

        } catch (Exception e) {
            Log.e("E", String.format("Error occurred with Trip %s: %s", this.id, e.getMessage()));
        }

    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        if (this.driver != null) {
            map.put("driver", this.driver.getId());
        }

        map.put("origin", this.origin);
        map.put("destination", this.destination);

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

    public String getId() {
        return this.id;
    }

    public Trip() {
    }

    public Trip(User driver, LatLng origin, LatLng destination, int availableSeats, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.driver = driver;
        this.origin = new GeoPoint(origin.latitude, origin.longitude);
        this.destination = new GeoPoint(destination.latitude, destination.longitude);
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.cost = 100;
        this.passengers = new ArrayList<>();
    }

    public Address getOriginAddress(Context context) {
        return AddressRep.getLocationAddress(context, new LatLng(origin.getLatitude(), origin.getLongitude()));
    }

    public Address getDestinationAddress(Context context) {
        return AddressRep.getLocationAddress(context, new LatLng(destination.getLatitude(), destination.getLongitude()));
    }

    public String getRouteStringRep(Context context) {
        return String.format(
                "%s → %s",
                AddressRep.getLocationStringAddress(context, new LatLng(origin.getLatitude(), origin.getLongitude())),
                AddressRep.getLocationStringAddress(context, new LatLng(destination.getLatitude(), destination.getLongitude())));
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public GeoPoint getOrigin() {
        return new GeoPoint(origin.getLatitude(), origin.getLongitude());
    }

    public void setOrigin(LatLng origin) {
        this.origin = new GeoPoint(origin.latitude, origin.longitude);
    }

    public GeoPoint getDestination() {
        return new GeoPoint(destination.getLatitude(), destination.getLongitude());
    }

    public void setDestination(LatLng destination) {
        this.destination = new GeoPoint(destination.latitude, destination.longitude);
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
