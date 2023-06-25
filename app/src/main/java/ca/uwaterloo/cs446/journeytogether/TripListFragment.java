package ca.uwaterloo.cs446.journeytogether;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripListFragment extends Fragment {

    private ArrayList<Trip> trips = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;

    public TripListFragment() {
        // necessarily empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        trips.add(new Trip("rgt", new User("lala135", "Kevim", "Lee"), 5));
        trips.add(new Trip("bah", new User("lala123", "Kevin", "Lhhhhh"), 4));
        trips.add(new Trip("rgeth", new User("45hebsdf", "Kevid", "Lsssss"), 3));
        trips.add(new Trip("4y4y4", new User("fghretyt", "Kevid", "Lsssss"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));
        trips.add(new Trip("etett", new User("bingchilling", "John", "Cena"), 4));

        recyclerView = rootView.findViewById(R.id.tripListRecyclerView);
        tripAdapter = new TripAdapter(trips, getContext());
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }
}
