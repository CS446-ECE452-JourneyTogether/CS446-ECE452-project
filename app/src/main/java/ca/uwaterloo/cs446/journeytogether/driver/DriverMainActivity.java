package ca.uwaterloo.cs446.journeytogether.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.WelcomeActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserMainActivity;
import ca.uwaterloo.cs446.journeytogether.driver.DriverProfileFragment;


public class DriverMainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private BottomNavigationView bnvDriver;

    private DriverTripsFragment tripsDriverFragment;
    private DriverProfileFragment profileDriverFragment;

    private DriverModeFragment modeDriverFragment;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToWelcomePage();
        } else {
            requestForegroundServicePermission();
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

    private void requestForegroundServicePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            startForegroundService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllPermissionGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionGranted = false;
                    break;
                }
            }

            if (!isAllPermissionGranted) {
                bnvDriver.getMenu().findItem(R.id.driver_mode).setEnabled(false);
            } else {
                startForegroundService();
            }
        }
    }

    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, DriverModeService.class);
        startForegroundService(serviceIntent);
    }
}
