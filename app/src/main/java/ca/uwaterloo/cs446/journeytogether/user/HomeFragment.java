package ca.uwaterloo.cs446.journeytogether.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;

public class HomeFragment extends Fragment {
    public HomeFragment() {}

    private View rootView;
    private ArrayList<TripRequest> tripRequest = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestAdapter RequestAdapter;
    private InAppNotice inAppNotice;
    private FirebaseAuth mAuth;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        inAppNotice = new InAppNotice(rootView);
        FirebaseUser user = mAuth.getCurrentUser();

        // placeholder: currently it make query about everything
        TripRequest.firestore.makeQuery(
                c -> c.whereEqualTo("Passenger",user.getUid()),
                (arr) -> {
                    this.tripRequest = arr;
                    recyclerView = rootView.findViewById(R.id.tripListRecyclerView);
                    RequestAdapter = new RequestAdapter(tripRequest, getContext());
                    recyclerView.setAdapter(RequestAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                },
                () -> { inAppNotice.onError("Cannot fetch the trips. Please try again later."); }
        );
        return rootView;
    }
}
