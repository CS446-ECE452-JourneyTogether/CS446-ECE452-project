package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
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
import java.util.concurrent.CompletableFuture;

import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.User;

public class PostTripActivity extends AppCompatActivity {

    private TextInputEditText etOrigin, etDestination, etAvailableSeats, etDate, etTime;
    private LocationPickerButton originLocationSelector, destinationLocationSelector;
    private InAppNotice inAppNotice;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trip);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        originLocationSelector = findViewById(R.id.originLocationSelector);
        destinationLocationSelector = findViewById(R.id.destinationLocationSelector);

        originLocationSelector.setActivity(this, 1);
        destinationLocationSelector.setActivity(this, 2);

        etOrigin = findViewById(R.id.ptEtOrigin);
        etDestination = findViewById(R.id.ptEtDestination);
        etAvailableSeats = findViewById(R.id.ptEtSeats);
        etDate = findViewById(R.id.ptEtDate);
        etTime = findViewById(R.id.ptEtTime);

        Button postButton = findViewById(R.id.ptBtnPost);
        postButton.setOnClickListener(view -> handleFormInputs());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        originLocationSelector.onActivityResult(requestCode, resultCode, data);
        destinationLocationSelector.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFormInputs() {

        LatLng origin = originLocationSelector.getSelectedLocation();
        LatLng destination = destinationLocationSelector.getSelectedLocation();

        if (origin == null || destination == null) {
            Toast.makeText(PostTripActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String availableSeatsStr = etAvailableSeats.getText().toString().trim();
        String dateText = etDate.getText().toString().trim();
        String timeText = etTime.getText().toString().trim();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US);
        String dateStr = null;
        String timeStr = null;

        if (
                availableSeatsStr.isEmpty() || dateText.isEmpty() || timeText.isEmpty() ||
                dateText.isEmpty() || timeText.isEmpty()
        ) {
            Toast.makeText(PostTripActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateText, dateFormatter);
            dateStr = date.format(dateFormatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(PostTripActivity.this, "Error parsing date or time", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try {
            LocalTime time = LocalTime.parse(timeText, timeFormatter);
            timeStr = time.format(timeFormatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(PostTripActivity.this, "Time is formatted incorrectly", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        int availableSeats = Integer.parseInt(availableSeatsStr);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (dateStr == null || timeStr == null) {
            return;
        }

        CompletableFuture<User> futureUser = new CompletableFuture<>();
        User.firestore.makeQuery(
                v -> v.whereEqualTo("email", currentUser.getEmail()),
                arr -> {
                    if (arr.isEmpty()) {
                        futureUser.completeExceptionally(new Exception("Query failed"));
                        return;
                    }
                    futureUser.complete(arr.get(0));
                },
                () -> {
                    futureUser.completeExceptionally(new Exception("Query failed"));
                }
        );

        futureUser.thenApply((user) -> {
            Trip trip = new Trip(user, origin, destination, availableSeats, "test", "test");

//              Add the trip to Firestore collection
            Trip.firestore.create(
                    trip,
                    () -> {
                        Toast.makeText(PostTripActivity.this, "Trip posted successfully", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PostTripActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    },
                    () -> {
                        Toast.makeText(PostTripActivity.this, "Failed to post trip. Please try again later.", Toast.LENGTH_LONG).show();
                    }
            );

            return user;
        }).exceptionally(exception -> {
            Toast.makeText(PostTripActivity.this, "Failed to post trip. Please try again later.", Toast.LENGTH_LONG).show();
            return null;
        });

    }}
