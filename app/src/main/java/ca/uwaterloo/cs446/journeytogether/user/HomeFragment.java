package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;
import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;

import android.util.Log;

public class  HomeFragment extends Fragment {

    public HomeFragment() { }
    private View rootView;
    private ArrayList<TripRequest> tripRequest = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button btnSearch, btnCollectedTrips;
    private RequestAdapter requestAdapter;
    private InAppNotice inAppNotice;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        inAppNotice = new InAppNotice(rootView);
        btnSearch = rootView.findViewById(R.id.btnHomeSearch);
        btnCollectedTrips = rootView.findViewById(R.id.btnCollectedTrips);
        btnSearch.setOnClickListener(v -> onSearchButtonClick());
        btnCollectedTrips.setOnClickListener(v -> onBtnCollectedTripsClick());

        CurrentUser.getCurrentUser().thenApply((user) -> {
            TripRequest.firestore.makeQuery(
                    c -> c.whereEqualTo("passenger", user.getId()),
                    (arr) -> {
                        this.tripRequest = arr;
                        recyclerView = rootView.findViewById(R.id.tripReqRecyclerView);
                        requestAdapter = new RequestAdapter(tripRequest, getContext());
                        recyclerView.setAdapter(requestAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    },
                    () -> { inAppNotice.onError("Cannot fetch trip requests. Please try again later."); }
            );
            return null;
        });

        return rootView;
    }

    private void onSearchButtonClick() {
        startActivity(new Intent(getActivity(), SearchTripActivity.class));
    }

    private void onBtnCollectedTripsClick() {
        startActivity(new Intent(getActivity(), CollectedTripsActivity.class));
    }
}
