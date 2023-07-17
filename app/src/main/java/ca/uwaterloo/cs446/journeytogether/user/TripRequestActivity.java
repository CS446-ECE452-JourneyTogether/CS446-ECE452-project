package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;

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


    private LocationPickerButton pickupAddressLocationSelector;
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
        additionalInfoEditText = findViewById(R.id.additionalInfoEditText);
        sendRequestButton = findViewById(R.id.sendRequestButton);
        pickupAddressLocationSelector = findViewById(R.id.pickupAddressLocationSelector);

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

        pickupAddressLocationSelector.setActivity(this, 1);

        sendRequestButton.setOnClickListener(view -> {
            // TODO: put this in a separate function
            boolean sharePhone = sharePhoneNumberCheckbox.isChecked();
            int seatRequest = seatsSeekBar.getProgress();
            LatLng pickupAddress = pickupAddressLocationSelector.getSelectedLocation();
            String comment = additionalInfoEditText.getText().toString().trim();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            CurrentUser.getCurrentUser().thenApply((user) -> {
                // Create a Trip object with the retrieved details
                TripRequest tripRequest = new TripRequest(this.selectedTrip, user, seatRequest, sharePhone, pickupAddress, comment);

                TripRequest.firestore.create(
                    tripRequest,
                    () -> {
                        Toast.makeText(TripRequestActivity.this, "Trip request posted successfully", Toast.LENGTH_LONG).show();

                        Intent returnIntent = new Intent(TripRequestActivity.this, UserMainActivity.class);
                        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(returnIntent);
                    },
                    () -> {
                        Toast.makeText(TripRequestActivity.this, "Failed to post trip request. Please try again later.", Toast.LENGTH_LONG).show();
                    });

                return user;
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickupAddressLocationSelector.onActivityResult(requestCode, resultCode, data);
    }

    private void updateSeekBarProgress(int progress) {
        seekBarInfoTextView.setText(String.format("%d", progress));
    }
}
