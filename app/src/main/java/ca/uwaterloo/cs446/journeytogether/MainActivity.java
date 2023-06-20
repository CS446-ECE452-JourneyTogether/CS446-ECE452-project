package ca.uwaterloo.cs446.journeytogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button btnSignout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.navigation_home) {
                            // Handle home item selection
                            return true;
                        }
                        if (item.getItemId() == R.id.navigation_dashboard) {
                            // Handle dashboard item selection
                            return true;
                        }
                        if (item.getItemId() == R.id.navigation_notifications) {
                            // Handle notifications item selection
                            return true;
                        }
                        return false;
                    }
                });

        btnSignout = findViewById(R.id.btnSignout);
        mAuth = FirebaseAuth.getInstance();

        btnSignout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            redirectToLoginPage();
        }
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
}