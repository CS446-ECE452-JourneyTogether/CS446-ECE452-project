package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ca.uwaterloo.cs446.journeytogether.LoginActivity;

public class UserLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the LoginActivity and pass extras to indicate passenger login
        Intent intent = new Intent(UserLoginActivity.this, LoginActivity.class);
        intent.putExtra("isDriverLogin", false);
        startActivity(intent);
        finish();
    }
}
