package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import ca.uwaterloo.cs446.journeytogether.driver.DriverLoginActivity;
import ca.uwaterloo.cs446.journeytogether.schema.User;
import ca.uwaterloo.cs446.journeytogether.user.UserLoginActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserLoginActivity;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etRegEmail;
    private TextInputEditText etRegPassword;
    private TextInputEditText etFirstName; // Added line
    private TextInputEditText etLastName;  // Added line
    private TextInputEditText etPhoneNum;  // Added line
    private Button btnRegister;
    private TextView tvLoginHere;

    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean isDriverRegistration; // Flag to determine if it's driver registration or user registration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the intent extras to determine if it's driver registration or user registration
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isDriverRegistration = extras.getBoolean("isDriverRegistration", false);
        }

        if (isDriverRegistration) {
            setContentView(R.layout.activity_driver_register);
        } else {
            setContentView(R.layout.activity_user_register);
        }

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
            if (isDriverRegistration) {
                startActivity(new Intent(RegisterActivity.this, DriverLoginActivity.class));
            } else {
                startActivity(new Intent(RegisterActivity.this, UserLoginActivity.class));
            }
            finish();
        });
    }

    private void createUser() {
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim(); // Added line
        String lastName = etLastName.getText().toString().trim(); // Added line
        String phoneNum = etPhoneNum.getText().toString().trim(); // Added line

        if (TextUtils.isEmpty(email)) {
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
                                new User(email, firstName,lastName,phoneNum,isDriverRegistration),
                                () -> {
                                    Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                                     if (!isDriverRegistration) {
                                         startActivity(new Intent(RegisterActivity.this, UserLoginActivity.class));
                                     } else {
                                         startActivity(new Intent(RegisterActivity.this, DriverLoginActivity.class));
                                     }
                                },
                                () -> { onError("An error occurred. Please try again later."); }
                        );
                    } else {
                        onError(task.getException().getMessage());
                        Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void onError(String message) {
        Toast.makeText(RegisterActivity.this, String.format("Error: %s", message), Toast.LENGTH_LONG).show();
    }
}
