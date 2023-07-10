package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;
import ca.uwaterloo.cs446.journeytogether.schema.User;

public class TripRequestActivity extends AppCompatActivity {

    private Trip selectedTrip;
    private FrameLayout selectedTripDisplay;
    private TripAdapter.TripViewHolder selectedTripViewHolder;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 12f;

    // components
    private TextView seekBarInfoTextView;
    private SeekBar seatsSeekBar;
    private CheckBox sharePhoneNumberCheckbox;


    private EditText pickupAddressEditText;
    private MapView pickupAddressMapView;
    private EditText additionalInfoEditText;
    private Button sendRequestButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("trip");

        setContentView(R.layout.activity_trip_request);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // display the selected trip at the top
        selectedTripDisplay = findViewById(R.id.selectedTripDisplay);
        View selectedTripView = LayoutInflater.from(this).inflate(R.layout.trip_item_layout, selectedTripDisplay, false);
        selectedTripDisplay.addView(selectedTripView);
        selectedTripViewHolder = new TripAdapter.TripViewHolder(selectedTripView, this);
        selectedTripViewHolder.setAllowSendRequest(false); // don't display a button that let's user send request. This is just for display
        selectedTripViewHolder.bind(selectedTrip);

        // components
        seekBarInfoTextView = findViewById(R.id.seekBarInfoTextView);
        seatsSeekBar = findViewById(R.id.seatsSeekBar);
        sharePhoneNumberCheckbox = findViewById(R.id.sharePhoneNumberCheckbox);
        pickupAddressEditText = findViewById(R.id.pickupAddressEditText);
        additionalInfoEditText = findViewById(R.id.additionalInfoEditText);
        sendRequestButton = findViewById(R.id.sendRequestButton);
        pickupAddressMapView = findViewById(R.id.pickupAddressMapView);
        pickupAddressMapView.onCreate(savedInstanceState);
        pickupAddressMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(TripRequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }

                setCamera(googleMap);
            }
        });

        // configuring components
        seekBarInfoTextView.setText(String.format("%d", seatsSeekBar.getProgress()));
        seatsSeekBar.setMax(selectedTrip.getAvailableSeats());
        seatsSeekBar.setMin(1);
        seatsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBarProgress(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sendRequestButton.setOnClickListener(view -> {
            // TODO: put this in a separate function
            boolean sharePhone = sharePhoneNumberCheckbox.isChecked();
            int seatRequest = seatsSeekBar.getProgress();
            String pickupAddr = pickupAddressEditText.getText().toString().trim();
            String comment = additionalInfoEditText.getText().toString().trim();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            User passenger = new User(currentUser.getEmail());

            // Create a Trip object with the retrieved details
            TripRequest tripRequest = new TripRequest(this.selectedTrip, passenger, seatRequest, sharePhone, pickupAddr, comment);

            CollectionReference tripRequestCollection = db.collection("jt_carpoolrequest");
            tripRequestCollection.add(tripRequest)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(TripRequestActivity.this, "Trip requested successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(TripRequestActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TripRequestActivity.this, "Failed to request trip: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void updateSeekBarProgress(int progress) {
        seekBarInfoTextView.setText(String.format("%d", progress));
    }

    // the default location shown on map selection
    private void setCamera(GoogleMap googleMap) {

        // this is the location of Waterloo... It is the default if we can't get the user's location
//        LatLng latlng = new LatLng(43.4305658, -80.6407781);

        // Inside onMapReady()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Move the camera to the user's location
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pickupAddressMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pickupAddressMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pickupAddressMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        pickupAddressMapView.onLowMemory();
    }
}
