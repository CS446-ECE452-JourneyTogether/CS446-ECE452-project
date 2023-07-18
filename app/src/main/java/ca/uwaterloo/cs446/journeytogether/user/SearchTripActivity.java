package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.component.DateTimePickerButton;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.driver.PostTripActivity;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;

public class SearchTripActivity extends AppCompatActivity {

    private LocationPickerButton originLocationPicker;
    private LocationPickerButton destinationLocationPicker;
    private DateTimePickerButton departureDateTimePicker;
    private Button searchButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        db = FirebaseFirestore.getInstance();

        originLocationPicker = findViewById(R.id.lsSearchOrigin);
        destinationLocationPicker = findViewById(R.id.lsSearchDestination);
        departureDateTimePicker = findViewById(R.id.tpSearchDeparture);
        searchButton = findViewById(R.id.btnSearchTrip);

        // Set listeners for location picker buttons
        originLocationPicker.setActivity(this, 3);
        destinationLocationPicker.setActivity(this, 4);

        departureDateTimePicker.setFragmentManager(getSupportFragmentManager());
        // Set listener for search button
        searchButton.setOnClickListener(v -> onSearchButtonClick());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        originLocationPicker.onActivityResult(requestCode, resultCode, data);
        destinationLocationPicker.onActivityResult(requestCode, resultCode, data);
    }

    private void onSearchButtonClick() {
        LatLng origin = originLocationPicker.getSelectedLocation();
        LatLng destination = destinationLocationPicker.getSelectedLocation();
        LocalDateTime departureDateTime = departureDateTimePicker.getDateTime();

        if (origin == null || destination == null) {
            Toast.makeText(this, "Please select origin and destination locations", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint userStartLocation = new GeoPoint(origin.latitude, origin.longitude);
        GeoPoint userEndLocation = new GeoPoint(destination.latitude, destination.longitude);
        Trip.firestore.makeQuery(
                c -> c,
                (arr) -> {
                    List<Trip> trips = arr;
                    Collections.sort(trips, (t1, t2) -> {
                        double originDistance1 = calculateDistance(userStartLocation, t1.getOrigin());
                        double destinationDistance1 = calculateDistance(userEndLocation, t1.getDestination());
                        double distanceCost1 = originDistance1 + destinationDistance1;

                        double originDistance2 = calculateDistance(userStartLocation, t2.getOrigin());
                        double destinationDistance2 = calculateDistance(userEndLocation, t2.getDestination());
                        double distanceCost2 = originDistance2 + destinationDistance2;

                        return Double.compare(distanceCost1, distanceCost2);
                    });

                    // Process the sorted trip list
                    for (Trip trip : trips) {
                        Log.d("trip111", trip.getOrigin().toString());
                    }

                },
                () -> { Toast.makeText(SearchTripActivity.this, "Failed to get trips in search activity. Please try again later.", Toast.LENGTH_LONG).show();; }
        );

//        db.collection("jt_trips_test")
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    List<Trip> trips = new ArrayList<>();
//                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
//                        Trip trip = documentSnapshot.toObject(Trip.class);
//                        trips.add(trip);
////                        Log.d("database_trips", trip.toString());
//                    }
//
//                    // Sort trips by ascending distance cost using custom Comparator
//                    Collections.sort(trips, (t1, t2) -> {
//                        double originDistance1 = calculateDistance(userStartLocation, t1.getOrigin());
//                        double destinationDistance1 = calculateDistance(userEndLocation, t1.getDestination());
//                        double distanceCost1 = originDistance1 + destinationDistance1;
//
//                        double originDistance2 = calculateDistance(userStartLocation, t2.getOrigin());
//                        double destinationDistance2 = calculateDistance(userEndLocation, t2.getDestination());
//                        double distanceCost2 = originDistance2 + destinationDistance2;
//
//                        return Double.compare(distanceCost1, distanceCost2);
//                    });
//
//                    // Process the sorted trip list
//                    for (Trip trip : trips) {
//                        System.out.println(trip.toString());
////                        Log.d("trip", trip.toString());
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Error querying trips
//                });

//         Query trips based on the selected origin, destination, and departureDateTime
//        Query searchQuery = db.collection("trips_test")
//                .whereNearTo("origin", new GeoPoint(origin.latitude, origin.longitude))
//                .whereNearTo("destination", new GeoPoint(destination.latitude, destination.longitude));
//
//        if (departureDateTime != null) {
//            searchQuery = searchQuery.whereGreaterThan("departureTime", departureDateTime);
//        }
//
//        searchQuery.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<Trip> matchingTrips = new ArrayList<>();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Trip trip = document.toObject(Trip.class);
//                    matchingTrips.add(trip);
//                }
//
//                // Handle the matchingTrips list (e.g., display in a list view or on the map)
//            } else {
//                Toast.makeText(this, "Failed to perform search. Please try again later.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private double calculateDistance(GeoPoint point1, GeoPoint point2) {
        double earthRadius = 6371; // Radius of the Earth in kilometers

        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance;
    }

}

