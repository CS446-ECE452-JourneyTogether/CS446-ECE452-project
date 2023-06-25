package ca.uwaterloo.cs446.journeytogether;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TripListFragment extends Fragment {

    private ArrayList<Trip> trips = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TripListFragment() {
        // necessarily empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        db.collection("jt_carpool")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> addedUsernames = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String username = documentSnapshot.getString("username");
                        String startLoc = documentSnapshot.getString("startloc");
                        String destLoc = documentSnapshot.getString("destloc");
                        int availSeat = documentSnapshot.getLong("availseat").intValue();
                        if (!addedUsernames.contains(username)) {
                            db.collection("jt_driver")
                                    .whereEqualTo("username", username)
                                    .get()
                                    .addOnSuccessListener(driverQuerySnapshot -> {
                                        // Retrieve the matching document from jt_driver collection
                                        for (QueryDocumentSnapshot driverDocument : driverQuerySnapshot) {
                                            String firstName = driverDocument.getString("firstname");
                                            String lastName = driverDocument.getString("lastname");

                                            trips.add(new Trip(username, new User(username, firstName, lastName), availSeat));
                                            addedUsernames.add(username);
                                        }

                                        // Refresh the adapter to update the UI
                                        tripAdapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
        recyclerView = rootView.findViewById(R.id.tripListRecyclerView);
        tripAdapter = new TripAdapter(trips, getContext());
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
}
