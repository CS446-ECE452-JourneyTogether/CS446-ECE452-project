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

import android.util.Log;

public class HomeFragment extends Fragment {
    public HomeFragment(FirebaseAuth mAuth) {this.mAuth = mAuth; }
    private View rootView;
    private ArrayList<TripRequest> tripRequest = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestAdapter RequestAdapter;
    private InAppNotice inAppNotice;
    private FirebaseAuth mAuth;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        inAppNotice = new InAppNotice(rootView);
        FirebaseUser user = mAuth.getCurrentUser();

        TripRequest.firestore.makeQuery(
                c -> c.whereEqualTo("passenger",user.getUid()),
                (arr) -> {
                    Log.w("Home", arr.toString());
                    this.tripRequest = arr;
                    recyclerView = rootView.findViewById(R.id.tripReqRecyclerView);
                    RequestAdapter = new RequestAdapter(tripRequest, getContext());
                    recyclerView.setAdapter(RequestAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                },
                () -> { inAppNotice.onError("Cannot fetch the trips. Please try again later."); }
        );
        return rootView;
    }
}
