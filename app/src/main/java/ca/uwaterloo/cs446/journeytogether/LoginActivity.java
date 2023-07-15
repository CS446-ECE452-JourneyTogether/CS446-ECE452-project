package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.uwaterloo.cs446.journeytogether.driver.DriverMainActivity;
import ca.uwaterloo.cs446.journeytogether.driver.DriverRegistrationActivity;
import ca.uwaterloo.cs446.journeytogether.driver.PostTripActivity;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.User;
import ca.uwaterloo.cs446.journeytogether.user.UserMainActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserRegistrationActivity;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etLogEmail;
    private TextInputEditText etLogPassword;
    private Button btnLogin;
    private TextView tvRegisterHere;

    private FirebaseAuth mAuth;

    private boolean isDriverLogin; // Flag to determine if it's driver login or passenger login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the intent extras to determine if it's driver login or passenger login
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isDriverLogin = extras.getBoolean("isDriverLogin", false);
        }
        if (isDriverLogin) {
            setContentView(R.layout.activity_driver_login);
        } else {
            setContentView(R.layout.activity_user_login);
        }

        etLogEmail = findViewById(R.id.etLogEmail);
        etLogPassword = findViewById(R.id.etLogPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            loginUser();
        });

        tvRegisterHere.setOnClickListener(v -> {
            if (isDriverLogin) {
                startActivity(new Intent(LoginActivity.this, DriverRegistrationActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, UserRegistrationActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = etLogEmail.getText().toString().trim();
        String password = etLogPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etLogEmail.setError("Email is empty");
            etLogEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etLogPassword.setError("Password is empty");
            etLogPassword.requestFocus();
        } else {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        boolean isValidRiderType = true;

                        // make sure driver is using driver login, rider is using rider login
                        User.getUserByEmail(email).thenApply((user) -> {

                            if (user.getIsDriver() != isDriverLogin) {
                                Toast.makeText(LoginActivity.this,
                                        String.format("You cannot login here. Please use the %s login screen instead.", isDriverLogin ? "passenger" : "driver"),
                                        Toast.LENGTH_LONG).show();
                                return user;
                            }

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            if (isDriverLogin) {
                                startActivity(new Intent(LoginActivity.this, DriverMainActivity.class));
                            } else {
                                startActivity(new Intent(LoginActivity.this, UserMainActivity.class));
                            }
                            finish();

                            return user;
                        }).exceptionally(exception -> {
                            Toast.makeText(LoginActivity.this, "Failed to log in. Please try again later.", Toast.LENGTH_LONG).show();
                            return null;
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}