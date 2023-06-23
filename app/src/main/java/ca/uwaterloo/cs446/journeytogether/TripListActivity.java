package ca.uwaterloo.cs446.journeytogether;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripListActivity extends AppCompatActivity {

    private ArrayList<Trip> trips = new ArrayList<>();
    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trip_list);

        trips.add(new Trip("rgt", new User("k323lee", "Kevim", "Lee"), 5));
        trips.add(new Trip("bah", new User("k323lee", "Kevin", "Lhhhhh"), 4));
        trips.add(new Trip("rgeth", new User("k323lee", "Kevid", "Lsssss"), 3));
        trips.add(new Trip("rgeth", new User("k323lee", "Kevid", "Lsssss"), 4));

        recyclerView = findViewById(R.id.tripListRecyclerView);
        tripAdapter = new TripAdapter(trips);
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}