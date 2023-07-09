package ca.uwaterloo.cs446.journeytogether.user;

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

import ca.uwaterloo.cs446.journeytogether.MainActivity;
import ca.uwaterloo.cs446.journeytogether.R;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etLogEmail;
    private TextInputEditText etLogPassword;
    private Button btnLogin;
    private TextView tvRegisterHere;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogEmail = findViewById(R.id.etLogEmail);
        etLogPassword = findViewById(R.id.etLogPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            loginUser();
        });

        tvRegisterHere.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
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
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
