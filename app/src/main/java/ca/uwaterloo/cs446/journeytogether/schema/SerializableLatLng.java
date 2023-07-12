package ca.uwaterloo.cs446.journeytogether.schema;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SerializableLatLng implements Serializable {
    public SerializableLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SerializableLatLng(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public double latitude;
    public double longitude;


}
