package ca.uwaterloo.cs446.journeytogether.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;

public class CollectedTripsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TripAdapter collectedTripAdapter;
    private ArrayList<Trip> collectedTrips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_trips);

        recyclerView = findViewById(R.id.collectedTripListRecyclerView);
        collectedTripAdapter = new TripAdapter(collectedTrips, this);
        recyclerView.setAdapter(collectedTripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query Firestore to get the list of collected trips for the current user
        // Replace "currentUser.getEmail()" with the actual method to get the current user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("jt_user")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Handle case where no user is found with the given email
                        return;
                    }

                    // Assuming a single user document is found (by user email)
                    DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                    List<String> collectedTripIds = (List<String>) userDocument.get("collectedTrips");
                    Log.d("CollectTests", collectedTripIds.toString());

                    if (collectedTripIds == null || collectedTripIds.isEmpty()) {
                        // Handle case where the user has no collected trips
                        return;
                    }

                    Trip.firestore.makeQuery(
                            c -> c,
                            (arr) -> {
                                arr.removeIf(trip -> !collectedTripIds.contains(trip.getId()));
                                collectedTrips = arr;
                                recyclerView = findViewById(R.id.collectedTripListRecyclerView);
                                collectedTripAdapter = new TripAdapter(collectedTrips, this);
                                recyclerView.setAdapter(collectedTripAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            },
                            () -> { Toast.makeText(CollectedTripsActivity.this, "Failed to get trips in search activity. Please try again later.", Toast.LENGTH_LONG).show();; }

                    );
                });

                    // Query Firestore to get the details of collected trips based on their IDs
//                    db.collection("jt_trips")
//                            .whereIn("id", collectedTripIds)
//                            .get()
//                            .addOnSuccessListener(querySnapshot -> {
//                                for (QueryDocumentSnapshot tripDocument : querySnapshot) {
//                                    Trip trip = tripDocument.toObject(Trip.class);
//                                    Log.d("CollectTests", trip.toString());
//                                    collectedTrips.add(trip);
//                                }
//
//                                recyclerView = findViewById(R.id.collectedTripListRecyclerView);
//                                collectedTripAdapter = new TripAdapter(collectedTrips, this);
//                                recyclerView.setAdapter(collectedTripAdapter);
//                                recyclerView.setLayoutManager(new LinearLayoutManager(this));
//                            })
//                            .addOnFailureListener(e -> {
//                                // Handle failure in querying collected trips
//                            });
//                })
    }
}

