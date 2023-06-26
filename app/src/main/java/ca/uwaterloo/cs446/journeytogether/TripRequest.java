package ca.uwaterloo.cs446.journeytogether;

public class TripRequest {
    private Trip trip;
    private int seats;

    private boolean phoneNumberShared;
    private String phoneNumber;
    // let's just have it as a string for now... Addresses are hard to verify/normalize without using some map API
    private String pickupAddress;
    private String additionalInfo;

    // constructor with phone number shared
    public TripRequest(Trip trip, int seats, String phoneNumber, String pickupAddress, String additionalInfo) {
        this.trip = trip;
        this.seats = seats;
        this.phoneNumberShared = true;
        this.phoneNumber = phoneNumber;
        this.pickupAddress = pickupAddress;
        this.additionalInfo = additionalInfo;
    }

    // constructor without phone number shared
    public TripRequest(Trip trip, int seats, String pickupAddress, String additionalInfo) {
        this.trip = trip;
        this.seats = seats;
        this.phoneNumberShared = false;
        this.pickupAddress = pickupAddress;
        this.additionalInfo = additionalInfo;
    }

    public boolean phoneNumberShared() {
        return this.phoneNumberShared;
    }
}
