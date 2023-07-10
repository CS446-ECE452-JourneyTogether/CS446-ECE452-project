package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import ca.uwaterloo.cs446.journeytogether.R;

public class DriverTripsFragment extends Fragment {
    private View rootView;
    private Button btnPost;

    public DriverTripsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_driver_trips, container, false);
        btnPost = rootView.findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DriverProfileFragment.PostTripActivity.class));
        });

        return rootView;
    }
}
