package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.common.InAppNotice;
import ca.uwaterloo.cs446.journeytogether.component.DateTimePickerButton;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;

public class PostTripActivity extends AppCompatActivity {

    private TextInputEditText etAvailableSeats;
    private TextInputEditText etCost;
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
        destinationLocationSelector = findViewById(R.id.pickupAddressLocationSelector);

        departureDateTimePicker = findViewById(R.id.departureDateTimePicker);
        arrivalDateTimePicker = findViewById(R.id.arrivalDateTimePicker);
        departureDateTimePicker.setFragmentManager(getSupportFragmentManager());
        arrivalDateTimePicker.setFragmentManager(getSupportFragmentManager());


        originLocationSelector.setActivity(this, 1);
        destinationLocationSelector.setActivity(this, 2);

        etAvailableSeats = findViewById(R.id.ptEtSeats);
        etCost = findViewById(R.id.ptEtCost);

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
        String costStr = etCost.getText().toString().trim();
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
        int cost = Integer.parseInt(costStr);

        CurrentUser.getCurrentUser().thenApply((user) -> {
            Trip trip = new Trip(user, origin, destination, availableSeats, cost, departureTime, arrivalTime);
            Trip.firestore.create(
                    trip,
                    () -> {
                        Toast.makeText(PostTripActivity.this, "Trip posted successfully", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PostTripActivity.this, DriverMainActivity.class);
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
