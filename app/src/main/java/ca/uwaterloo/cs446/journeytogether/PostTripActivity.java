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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;
import ca.uwaterloo.cs446.journeytogether.component.DateTimePickerButton;
import ca.uwaterloo.cs446.journeytogether.component.DateTimePickerPopup;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.User;

public class PostTripActivity extends AppCompatActivity {

    private TextInputEditText etAvailableSeats;
    private DateTimePickerButton departureDateTimePicker, arrivalDateTimePicker;
    private LocationPickerButton originLocationSelector, destinationLocationSelector;
    private InAppNotice inAppNotice;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trip);

        db = FirebaseFirestore.getInstance();

        originLocationSelector = findViewById(R.id.originLocationSelector);
        destinationLocationSelector = findViewById(R.id.destinationLocationSelector);

        departureDateTimePicker = findViewById(R.id.departureDateTimePicker);
        arrivalDateTimePicker = findViewById(R.id.arrivalDateTimePicker);

        originLocationSelector.setActivity(this, 1);
        destinationLocationSelector.setActivity(this, 2);

        etAvailableSeats = findViewById(R.id.ptEtSeats);

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

        String availableSeatsStr = etAvailableSeats.getText().toString().trim();
        LocalDateTime departureTime = departureDateTimePicker.getDateTime();
        LocalDateTime arrivalTime = arrivalDateTimePicker.getDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US);
        String dateStr = null;
        String timeStr = null;

        if (
                origin == null || destination == null || availableSeatsStr.isEmpty() ||
                departureTime == null || arrivalTime == null
        ) {
            Toast.makeText(PostTripActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        int availableSeats = Integer.parseInt(availableSeatsStr);

        CurrentUser.getCurrentUser().thenApply((user) -> {
            Trip trip = new Trip(user, origin, destination, availableSeats, departureTime, arrivalTime);

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
