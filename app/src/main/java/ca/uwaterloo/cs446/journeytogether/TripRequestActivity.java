package ca.uwaterloo.cs446.journeytogether;

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

import androidx.appcompat.app.AppCompatActivity;

public class TripRequestActivity extends AppCompatActivity {

    private Trip selectedTrip;
    private FrameLayout selectedTripDisplay;
    private TripAdapter.TripViewHolder selectedTripViewHolder;

    // components
    private TextView seekBarInfoTextView;
    private SeekBar seatsSeekBar;
    private CheckBox sharePhoneNumberCheckbox;


    private EditText pickupAddressEditText;
    private EditText additionalInfoEditText;
    private Button sendRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("trip");

        setContentView(R.layout.activity_trip_request);

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

        // configuring components
        seekBarInfoTextView.setText(String.format("%d", seatsSeekBar.getProgress()));
        seatsSeekBar.setMax(selectedTrip.availableSeats());
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
    }

    private void updateSeekBarProgress(int progress) {
        seekBarInfoTextView.setText(String.format("%d", progress));
    }
}
