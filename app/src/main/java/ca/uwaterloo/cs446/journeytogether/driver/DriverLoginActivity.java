package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ca.uwaterloo.cs446.journeytogether.LoginActivity;

public class DriverLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the LoginActivity and pass extras to indicate driver login
        Intent intent = new Intent(DriverLoginActivity.this, LoginActivity.class);
        intent.putExtra("isDriverLogin", true);
        startActivity(intent);
        finish();
    }
}
