package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.WelcomeActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserMainActivity;

public class DriverMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private BottomNavigationView bnvDriver;

    private DriverTripsFragment tripsDriverFragment;
    private DriverProfileFragment profileDriverFragment;

    private DriverModeFragment modeDriverFragment;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToWelcomePage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        // Create instances of the fragments
        mAuth = FirebaseAuth.getInstance();
        tripsDriverFragment = new DriverTripsFragment();
        profileDriverFragment = new DriverProfileFragment(mAuth);
        modeDriverFragment = new DriverModeFragment();

        // Set the initial fragment
        setFragment(tripsDriverFragment);

        // Handle navigation item selection
        bnvDriver = findViewById(R.id.bnvDriver);
        bnvDriver.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.driver_menu_trips) {
                setFragment(tripsDriverFragment);
                return true;
            } else if (item.getItemId() == R.id.driver_mode) {
                setFragment(modeDriverFragment);
                return true;
            } else if (item.getItemId() == R.id.driver_menu_profile) {
                setFragment(profileDriverFragment);
                return true;
            } else {
                return false;
            }
        });

    }

    private void redirectToWelcomePage() {
        startActivity(new Intent(DriverMainActivity.this, WelcomeActivity.class));
        finish();
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fcvDriver, fragment)
                .commit();
    }
}
