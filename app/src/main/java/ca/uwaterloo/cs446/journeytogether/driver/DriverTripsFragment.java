package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.uwaterloo.cs446.journeytogether.driver.PostTripActivity;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.user.TripAdapter;
import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;

import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DriverTripsFragment extends Fragment {
    private View rootView;
    private Button btnPost;
    private ArrayList<Trip> trips = new ArrayList<>();
    private RecyclerView recyclerView;
    private DriverTripAdapter driverTripAdapter;
    private InAppNotice inAppNotice;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DriverTripsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_driver_trips, container, false);
        btnPost = rootView.findViewById(R.id.btnPost);
        inAppNotice = new InAppNotice(rootView);

        btnPost.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PostTripActivity.class));
        });

        CurrentUser.getCurrentUser().thenApply((driver) -> {
            Trip.firestore.makeQuery(
                c -> c.whereEqualTo("driver", driver.getId()),
                (arr) -> {
                    this.trips = arr;
                    recyclerView = rootView.findViewById(R.id.driverTripListRecyclerView);
                    driverTripAdapter = new DriverTripAdapter(trips, getContext());
                    recyclerView.setAdapter(driverTripAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                },
                () -> { inAppNotice.onError("Cannot fetch the trips. Please try again later."); }
            );
            return null;
        });

        return rootView;
    }
}
