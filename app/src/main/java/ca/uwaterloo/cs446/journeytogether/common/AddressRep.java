package ca.uwaterloo.cs446.journeytogether.common;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

import ca.uwaterloo.cs446.journeytogether.schema.SerializableLatLng;

public class AddressRep {
    public static Address getLocationAddress(Context context, LatLng location) {
        Geocoder g = new Geocoder(context, Locale.getDefault());

        try {
            return g.getFromLocation(location.latitude, location.longitude, 1).get(0);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getLocationStringAddress(Context context, LatLng location) {
        Address address = getLocationAddress(context, location);

        if (address == null) {
            Log.w("W", "IO Exception in Geocoder. Resorting to latlng representation.");
            return String.format("%s, %s", location.latitude, location.longitude);
        }

        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            Log.d("D",address.getAddressLine(i));
        }

        String thoroughfare = address.getThoroughfare();
        if (thoroughfare != null) {
            String feature = address.getFeatureName();
            feature = (feature == null || feature.equals(thoroughfare)) ? "" : String.format("%s ", feature);

            String locality = address.getLocality();
            locality = (locality == null) ? "" : String.format(", %s", locality);

            return String.format("%s%s%s", feature, thoroughfare, locality);
        }

        String sublocality = address.getSubLocality();
        if (sublocality != null) {
            return sublocality;
        }

        String locality = address.getLocality();
        if (locality != null) {
            return locality;
        }

        String subAdminArea = address.getSubAdminArea();
        if (subAdminArea != null) {
            return subAdminArea;
        }

        String adminArea = address.getAdminArea();
        if (adminArea != null) {
            return adminArea;
        }

        return address.toString();
    }
}
