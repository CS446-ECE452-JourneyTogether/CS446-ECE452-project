package ca.uwaterloo.cs446.journeytogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import android.widget.Toast;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uwaterloo.cs446.journeytogether.user.HomeFragment;
import ca.uwaterloo.cs446.journeytogether.user.LoginActivity;
import ca.uwaterloo.cs446.journeytogether.user.ProfileFragment;
import ca.uwaterloo.cs446.journeytogether.user.TripListFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // fragments used for navigation
    TripListFragment tripListFragment;
    ProfileFragment profileFragment;
    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        homeFragment = new HomeFragment();
        tripListFragment = new TripListFragment();
        profileFragment = new ProfileFragment(mAuth);
        setFragment(homeFragment);

        // ..............................

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    Fragment fragment;
                    if (item.getItemId() == R.id.navigation_home) {
                        fragment = homeFragment;
                    }
                    else if (item.getItemId() == R.id.navigation_carpool) {
                        fragment = tripListFragment;
                    }
                    else if (item.getItemId() == R.id.navigation_profile) {
                        fragment = profileFragment;
                    } else {
                        return false;
                    }
                    setFragment(fragment);
                    return true;
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        final boolean DEBUGGING = false;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (DEBUGGING) {
            redirectToDebuggingPage();
        }

        if(currentUser == null){
            redirectToLoginPage();
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentContainerView, fragment)
                .commit();
    }

    private void showMainContent() {
        // Add your code to display the main content of the page
        // For this example, we will display a toast message
        Toast.makeText(MainActivity.this, "Welcome to the main page!", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLoginPage() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void redirectToDebuggingPage() {
//        startActivity(new Intent(MainActivity.this, TripListActivity.class));
        finish();
    }
}