package ca.uwaterloo.cs446.journeytogether.user;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;
import android.util.Log;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.GeoUtils;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;

public class SearchedTripsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TripAdapter adapter;
    private ArrayList<Trip> searchedTrips = new ArrayList<>();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_trips);

        // Retrieve the latitude and longitude from the intent
        double userOriginLatitude = getIntent().getDoubleExtra("originLatitude", 0.0);
        double userOriginLongitude = getIntent().getDoubleExtra("originLongitude", 0.0);
        double userDestinationLatitude = getIntent().getDoubleExtra("destinationLatitude", 0.0);
        double userDestinationLongitude = getIntent().getDoubleExtra("destinationLongitude", 0.0);
        String formattedDateTime = getIntent().getStringExtra("departureTime");
        Log.w("TIME", formattedDateTime);
        LocalDateTime departureDateTime = LocalDateTime.parse(formattedDateTime);

        Trip.firestore.makeQuery(
                c -> c,
                (arr) -> {
                    // Define the maximum allowable difference in kilometers
                    double maxAllowableDifference = 20.0;

                    arr.removeIf(trip ->{
                        Duration duration = Duration.between(departureDateTime,trip.getDepartureTime());
                        return duration.toHours() > 24;
                    });
                    // Filter trips whose origin or destination is not within the threshold
                    arr.removeIf(trip -> {
                        double originDistance = GeoUtils.calculateDistance(trip.getOrigin().latitude, trip.getOrigin().longitude, userOriginLatitude, userOriginLongitude);
                        double destinationDistance = GeoUtils.calculateDistance(trip.getDestination().latitude, trip.getDestination().longitude, userDestinationLatitude, userDestinationLongitude);

                        // Check if either the origin or destination is not within the threshold
                        return Math.abs(originDistance) > maxAllowableDifference ||
                                Math.abs(destinationDistance) > maxAllowableDifference;
                    });

                    // Create a priority queue to store the trips based on their distance cost
                    PriorityQueue<Trip> tripQueue = new PriorityQueue<>((t1, t2) -> {
                        double originDistance1 = GeoUtils.calculateDistance(t1.getOrigin().latitude, t1.getOrigin().longitude, userOriginLatitude, userOriginLongitude);
                        double destinationDistance1 = GeoUtils.calculateDistance(t1.getDestination().latitude, t1.getDestination().longitude, userDestinationLatitude, userDestinationLongitude);
                        double distanceCost1 = originDistance1 + destinationDistance1;

                        double originDistance2 = GeoUtils.calculateDistance(t2.getOrigin().latitude, t2.getOrigin().longitude, userOriginLatitude, userOriginLongitude);
                        double destinationDistance2 = GeoUtils.calculateDistance(t2.getDestination().latitude, t2.getDestination().longitude, userDestinationLatitude, userDestinationLongitude);
                        double distanceCost2 = originDistance2 + destinationDistance2;

                        return Double.compare(distanceCost1, distanceCost2);
                    });

                    // Add the filtered trips to the priority queue
                    tripQueue.addAll(arr);

                    // Retrieve the sorted trips from the priority queue
                    ArrayList<Trip> sortedTrips = new ArrayList<>();
                    while (!tripQueue.isEmpty()) {
                        sortedTrips.add(tripQueue.poll());
                    }

                    // Set up the RecyclerView adapter with the sorted trips
                    this.searchedTrips = sortedTrips;
                    recyclerView = findViewById(R.id.searchedTripListRecyclerView);
                    adapter = new TripAdapter(this.searchedTrips, this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                },
                () -> { Toast.makeText(SearchedTripsActivity.this, "Failed to get trips in search activity. Please try again later.", Toast.LENGTH_LONG).show();; }
        );
    }
}

