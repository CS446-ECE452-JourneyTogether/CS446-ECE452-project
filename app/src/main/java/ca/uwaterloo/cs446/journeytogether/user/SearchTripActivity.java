package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.component.DateTimePickerButton;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;

public class SearchTripActivity extends AppCompatActivity {

    private LocationPickerButton originLocationPicker;
    private LocationPickerButton destinationLocationPicker;
    private DateTimePickerButton departureDateTimePicker;
    private Button searchButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        db = FirebaseFirestore.getInstance();

        originLocationPicker = findViewById(R.id.lsSearchOrigin);
        destinationLocationPicker = findViewById(R.id.lsSearchDestination);
        departureDateTimePicker = findViewById(R.id.tpSearchDeparture);
        searchButton = findViewById(R.id.btnSearchTrip);

        // Set listeners for location picker buttons
        originLocationPicker.setActivity(this, 3);
        destinationLocationPicker.setActivity(this, 4);

        departureDateTimePicker.setFragmentManager(getSupportFragmentManager());
        // Set listener for search button
        searchButton.setOnClickListener(v -> onSearchButtonClick());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        originLocationPicker.onActivityResult(requestCode, resultCode, data);
        destinationLocationPicker.onActivityResult(requestCode, resultCode, data);
    }

    private void onSearchButtonClick() {
        LatLng origin = originLocationPicker.getSelectedLocation();
        LatLng destination = destinationLocationPicker.getSelectedLocation();
        LocalDateTime departureDateTime = departureDateTimePicker.getDateTime();

        if (origin == null || destination == null) {
            Toast.makeText(this, "Please select origin and destination locations", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass latitude and longitude of the GeoPoint objects to the new activity
        Intent intent = new Intent(SearchTripActivity.this, SearchedTripsActivity.class);
        intent.putExtra("originLatitude", origin.latitude);
        intent.putExtra("originLongitude", origin.longitude);
        intent.putExtra("destinationLatitude", destination.latitude);
        intent.putExtra("destinationLongitude", destination.longitude);
        startActivity(intent);
    }
}

