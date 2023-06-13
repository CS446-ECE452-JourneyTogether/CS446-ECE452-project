package ca.uwaterloo.ece452.journeytogether;

import android.location.Location;

public class LatLng {
    // This class encodes information about a location on Earth
    // It is an immutable type to avoid accidental mutation

    private double latitude;
    private double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float distanceTo(LatLng end) {
        float[] results = new float[3];
        Location.distanceBetween(this.latitude, this.longitude, end.latitude, end.longitude, results);
        return results[0];
    }
}