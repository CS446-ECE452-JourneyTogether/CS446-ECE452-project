package ca.uwaterloo.cs446.journeytogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.sql.Connection;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    UserSession userSession = UserSession.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is already logged in
        boolean isLoggedIn = userSession.isLoggedIn();

        if (isLoggedIn) {
            // User is already logged in, show main content
            showMainContent();
        } else {
            // User is not logged in, redirect to login page
            redirectToLoginPage();
        }
    }

    private void showMainContent() {
        // Add your code to display the main content of the page
        // For this example, we will display a toast message

        Toast.makeText(MainActivity.this, "Welcome to the main page!", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLoginPage() {
        // Add your code to redirect the user to the login page
        // For this example, we will start a new LoginActivity

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}