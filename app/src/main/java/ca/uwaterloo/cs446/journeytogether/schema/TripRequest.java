package ca.uwaterloo.cs446.journeytogether.schema;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;

public class TripRequest {

    private String id;
    private Trip trip;
    private User passenger;
    private int seatRequest;
    private boolean sharePhone;
    private String pickupAddr;
    private String comment;

    private static final String COLLECTION_PATH = "jt_triprequests";
    public static final FirestoreCollection<TripRequest> firestore =
            new FirestoreCollection<>(
                    COLLECTION_PATH,
                    (onto, document) -> { onto.update(document); },
                    (value) -> value.asMap(),
                    (onto, id) -> onto.setId(id),
                    () -> new TripRequest()
            );

    public void update(DocumentSnapshot document) {
        try {
            String tripId = (String) document.get("trip");
            Trip.firestore.getValuesById(tripId, (arr) -> {
                if (!arr.isEmpty()) { this.trip = arr.get(0); }
            } );

            String passengerId = (String) document.get("passenger");
            User.firestore.getValuesById(passengerId, (arr) -> {
                if (!arr.isEmpty()) { this.passenger = arr.get(0); }
            } );

            this.seatRequest = Math.toIntExact((long) document.get("seatRequest"));
            this.sharePhone = (boolean) document.get("sharePhone");
            this.pickupAddr = (String) document.get("pickupAddr");
            this.comment = (String) document.get("comment");
        } catch (ClassCastException e) {
            Log.e("E", String.format("Casting error occurred with TripRequest %s: %s", this.id, e.getMessage()));
        }
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("trip", this.trip.getId());
        map.put("passenger", this.passenger.getId());
        map.put("seatRequest", this.seatRequest);
        map.put("sharePhone", this.sharePhone);
        map.put("pickupAddr", this.pickupAddr);
        map.put("comment", this.comment);

        return map;
    }

    private void setId(String id) {
        this.id = id;
    }

    public TripRequest() {}

    public TripRequest(Trip trip, User passenger, int seatRequest, boolean sharePhone, String pickupAddr, String comment) {
        this.trip = trip;
        this.passenger = passenger;
        this.seatRequest = seatRequest;
        this.sharePhone = sharePhone;
        this.pickupAddr = pickupAddr;
        this.comment = comment;
    }

    // Getters and Setters for each property

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public int getSeatRequest() {
        return seatRequest;
    }

    public void setSeatRequest(int seatRequest) {
        this.seatRequest = seatRequest;
    }

    public boolean isSharePhone() {
        return sharePhone;
    }

    public void setSharePhone(boolean sharePhone) {
        this.sharePhone = sharePhone;
    }

    public String getPickupAddr() {
        return pickupAddr;
    }

    public void setPickupAddr(String pickupAddr) {
        this.pickupAddr = pickupAddr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
