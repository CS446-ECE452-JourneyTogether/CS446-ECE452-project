package ca.uwaterloo.cs446.journeytogether.driver;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.RegisterActivity;
import ca.uwaterloo.cs446.journeytogether.schema.User;
import ca.uwaterloo.cs446.journeytogether.user.UserLoginActivity;

public class DriverRegistrationActivity extends AppCompatActivity {
    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etFirstName; // Added line
    private TextInputEditText etLastName;  // Added line
    private TextInputEditText etPhoneNum,etDriverL;  // Added line
    private Button btnRegister;
    private TextView tvLoginHere;

    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);
        etDriverL = findViewById(R.id.etRegDriverL);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etFirstName = findViewById(R.id.etFirstName); // Added line
        etLastName = findViewById(R.id.etLastName); // Added line
        etPhoneNum = findViewById(R.id.etPhoneNum); // Added line
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(v -> createUser());

        tvLoginHere.setOnClickListener(v -> {
            // Redirect to the appropriate login activity based on the registration type
            startActivity(new Intent(DriverRegistrationActivity.this, DriverLoginActivity.class));
        });
    }

    private void createUser() {
        String driverL = etDriverL.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim(); // Added line
        String lastName = etLastName.getText().toString().trim(); // Added line
        String phoneNum = etPhoneNum.getText().toString().trim(); // Added line
        if (!isValidLicense(driverL)) {
            etDriverL.setError("Invalid driver's license");
            etDriverL.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email is empty");
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password is empty");
            etRegPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User.firestore.create(
                                new User(driverL,email, firstName,lastName,phoneNum,true),
                                () -> {
                                    Toast.makeText(DriverRegistrationActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(DriverRegistrationActivity.this, DriverLoginActivity.class));
                                },
                                () -> { onError("An error occurred. Please try again later."); }
                        );
                    } else {
                        onError(task.getException().getMessage());
                        Toast.makeText(DriverRegistrationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private boolean isValidLicense(String driverL) {
        Pattern pattern = Pattern.compile("[A-Za-z]{1}\\d{4}-\\d{5}-\\d{5}");
        Matcher matcher = pattern.matcher(driverL);
        return matcher.matches();
    }

    private void onError(String message) {
        Toast.makeText(DriverRegistrationActivity.this, String.format("Error: %s", message), Toast.LENGTH_LONG).show();
    }
}



