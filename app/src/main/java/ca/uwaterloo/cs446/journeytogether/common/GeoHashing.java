package ca.uwaterloo.cs446.journeytogether.common;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.schema.SerializableLatLng;

public class GeoHashing {

    static final public String GEOHASH_FORMAT =  "geohash_%s";
    static final public String LAT_FORMAT =  "lat_%s";
    static final public String LNG_FORMAT =  "lng_%s";

    // add the hash and other relevant location info into the map
    public static void hash(String fieldName, SerializableLatLng latlng, Map<String, Object> map) {
        double lat = latlng.latitude;
        double lng = latlng.longitude;
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(lat, lng));

        map.put(String.format(GEOHASH_FORMAT,   fieldName), geohash);
        map.put(String.format(LAT_FORMAT,       fieldName), lat);
        map.put(String.format(LNG_FORMAT,       fieldName), lng);

        return;
    }

    public static LatLng unhash(String fieldName, DocumentSnapshot doc) {
        double lat = (double) doc.get(String.format(LAT_FORMAT, fieldName));
        double lng = (double) doc.get(String.format(LNG_FORMAT, fieldName));

        return new LatLng(lat, lng);
    }

    public static LatLng unhashMap(String fieldName, DocumentSnapshot doc) {
        HashMap<String, Object> geohash = (HashMap<String, Object>) doc.get(fieldName);
        double lat = (double) geohash.get("latitude");
        double lng = (double) geohash.get("longitude");

        return new LatLng(lat, lng);
    }
}
