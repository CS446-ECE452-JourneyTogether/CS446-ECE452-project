package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ca.uwaterloo.cs446.journeytogether.RegisterActivity;

public class UserRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the RegisterActivity and pass extras to indicate user registration
        Intent intent = new Intent(UserRegistrationActivity.this, RegisterActivity.class);
        intent.putExtra("isDriverRegistration", false);
        startActivity(intent);
        finish();
    }
}
