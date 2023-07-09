package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import ca.uwaterloo.cs446.journeytogether.MainActivity;
import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.user.Trip;
import ca.uwaterloo.cs446.journeytogether.user.User;

public class PostTripActivity extends AppCompatActivity {

    private TextInputEditText etOrigin, etDestination, etAvailableSeats, etDate, etTime;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trip);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etOrigin = findViewById(R.id.ptEtOrigin);
        etDestination = findViewById(R.id.ptEtDestination);
        etAvailableSeats = findViewById(R.id.ptEtSeats);
        etDate = findViewById(R.id.ptEtDate);
        etTime = findViewById(R.id.ptEtTime);

        Button postButton = findViewById(R.id.ptBtnPost);

        postButton.setOnClickListener(view -> {
            String origin = etOrigin.getText().toString().trim();
            String destination = etDestination.getText().toString().trim();
            String availableSeatsStr = etAvailableSeats.getText().toString().trim();
            String dateText = etDate.getText().toString().trim();
            String timeText = etTime.getText().toString().trim();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US);
            String dateStr = null;
            String timeStr = null;

            if (origin.isEmpty() || destination.isEmpty() ||
                    availableSeatsStr.isEmpty() || dateText.isEmpty() || timeText.isEmpty() ||
                    dateText.isEmpty() || timeText.isEmpty()) {
                Toast.makeText(PostTripActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                LocalDate date = LocalDate.parse(dateText, dateFormatter);
                LocalTime time = LocalTime.parse(timeText, timeFormatter);
                dateStr = date.format(dateFormatter);
                timeStr = time.format(timeFormatter);
            } catch (DateTimeParseException e) {
                Toast.makeText(PostTripActivity.this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            int availableSeats = Integer.parseInt(availableSeatsStr);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            User user = new User(currentUser.getEmail());

            if (dateStr != null && timeStr != null) {
                // Create a Trip object with the retrieved details
                Trip trip = new Trip(user, origin, destination, availableSeats, dateStr, timeStr);

//              Add the trip to Firestore collection
                CollectionReference tripsCollection = db.collection("jt_trips");
                tripsCollection.add(trip)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(PostTripActivity.this, "Trip posted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostTripActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PostTripActivity.this, "Failed to post trip: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            }
        });
    }
}
