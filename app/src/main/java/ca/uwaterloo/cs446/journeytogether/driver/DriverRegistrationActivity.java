package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ca.uwaterloo.cs446.journeytogether.RegisterActivity;

public class DriverRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the RegisterActivity and pass extras to indicate driver registration
        Intent intent = new Intent(DriverRegistrationActivity.this, RegisterActivity.class);
        intent.putExtra("isDriverRegistration", true);
        startActivity(intent);
        finish();
    }
}

