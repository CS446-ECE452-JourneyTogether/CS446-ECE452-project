package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import ca.uwaterloo.cs446.journeytogether.R;

public class HomeFragment extends Fragment {
    private View rootView;
    private Button btnSearch;
    public HomeFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btnSearch = rootView.findViewById(R.id.btnHomeSearch);
        btnSearch.setOnClickListener(v -> onSearchButtonClick());

        return  rootView;
    }

    private void onSearchButtonClick() {
        startActivity(new Intent(getActivity(), SearchTripActivity.class));
    }
}
