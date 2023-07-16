package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.uwaterloo.cs446.journeytogether.driver.TripRequestAdapter;
import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;
import ca.uwaterloo.cs446.journeytogether.component.LocationPickerButton;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;
import ca.uwaterloo.cs446.journeytogether.schema.User;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity {

    private Trip selectedTrip;
    private ArrayList<TripRequest> tripRequests;
    private FrameLayout selectedTripDisplay;
    private DriverTripAdapter.DriverTripViewHolder selectedTripViewHolder;

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

        setContentView(R.layout.activity_view_requests);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // display the selected trip at the top
        selectedTripDisplay = findViewById(R.id.selectedTripDisplay);
        View selectedTripView = LayoutInflater.from(this).inflate(R.layout.driver_trip_item_layout, selectedTripDisplay, false);
        selectedTripDisplay.addView(selectedTripView);
        selectedTripViewHolder = new DriverTripAdapter.DriverTripViewHolder(selectedTripView, this);
        selectedTripViewHolder.setAllowViewRequests(false);
        selectedTripViewHolder.bind(selectedTrip);

        // get the requests for the selected trip  // TODO: filter by trip after enough testing
        TripRequest.firestore.makeQuery(
                c -> c,//.whereEqualTo("trip", selectedTrip.getId()),
                (arr) -> {
                    this.tripRequests = arr;
                    RecyclerView requestsRecyclerView = findViewById(R.id.requestListRecyclerView);
                    requestsRecyclerView.setAdapter(new TripRequestAdapter(tripRequests, this));
                    requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                },
                () -> { }
        );
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
