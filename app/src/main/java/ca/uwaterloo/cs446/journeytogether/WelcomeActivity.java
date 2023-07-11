package ca.uwaterloo.cs446.journeytogether;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ca.uwaterloo.cs446.journeytogether.driver.DriverLoginActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserLoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnUserLogin = findViewById(R.id.btnUserLogin);
        Button btnDriverLogin = findViewById(R.id.btnDriverLogin);

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, UserLoginActivity.class));
                finish();
            }
        });

        btnDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, DriverLoginActivity.class));
                finish();
            }
        });
    }
}
