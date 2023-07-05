package ca.uwaterloo.cs446.journeytogether;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverMainActivity extends AppCompatActivity {

    private BottomNavigationView bnvDriver;

    private DriverTripsFragment tripsDriverFragment;
    private DriverProfileFragment profileDriverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        // Create instances of the fragments
        tripsDriverFragment = new DriverTripsFragment();
        profileDriverFragment = new DriverProfileFragment();

        // Set the initial fragment
        setFragment(tripsDriverFragment);

        // Handle navigation item selection
        bnvDriver = findViewById(R.id.bnvDriver);
        bnvDriver.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.driver_menu_trips) {
                setFragment(tripsDriverFragment);
                return true;
            } else if (item.getItemId() == R.id.driver_menu_profile) {
                setFragment(profileDriverFragment);
                return true;
            } else {
                return false;
            }
        });

    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fcvDriver, fragment)
                .commit();
    }
}
