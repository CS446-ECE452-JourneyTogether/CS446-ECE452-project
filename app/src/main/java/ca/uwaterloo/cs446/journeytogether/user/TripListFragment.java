package ca.uwaterloo.cs446.journeytogether.user;

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

import ca.uwaterloo.cs446.journeytogether.R;

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

//        db.collection("jt_trips")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        trips.clear();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            // Convert Firestore document to Trip object
//                            Trip trip = document.toObject(Trip.class);
//                            trips.add(trip);
//                        }
//                        tripAdapter.notifyDataSetChanged();
//                    } else {
//                        Log.d("Fetching trips", "Error getting trips: ", task.getException());
//                    }
//                });
//        recyclerView = rootView.findViewById(R.id.tripListRecyclerView);
//        tripAdapter = new TripAdapter(trips, getContext());
//        recyclerView.setAdapter(tripAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
}
