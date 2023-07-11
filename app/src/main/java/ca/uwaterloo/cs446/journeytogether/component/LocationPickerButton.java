package ca.uwaterloo.cs446.journeytogether.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ca.uwaterloo.cs446.journeytogether.LocationPickerActivity;

public class LocationPickerButton extends AppCompatButton {

    private static final int REQUEST_LOCATION_PICKER = 1;

    private LatLng selectedLocation;
    private Activity activity;
    private int requestCode;
    private Geocoder geocoder;

    public LocationPickerButton(Context context) {
        super(context);
        initialize();
    }

    public LocationPickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LocationPickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationPickerActivity.class);
                activity.startActivityForResult(intent, requestCode);
            }
        });
    }

    public void setActivity(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public LatLng getSelectedLocation() {
        return selectedLocation;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.requestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedLocation = data.getParcelableExtra("selectedLocation");
                updateButtonLabel();
            }
        }
    }

    private void updateButtonLabel() {
        if (selectedLocation == null) {
            this.setText("Select location");
        } else {
            this.setText(addressOf(selectedLocation));
        }
    }

    private String addressOf(LatLng latlng) {
        List<Address> addresses;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            Log.w("W", "IO Exception in Geocoder. Resorting to latlng representation.");
            return String.format("%s, %s", latlng.latitude, latlng.longitude);
        }

        String address = addresses.get(0).getAddressLine(0);
        return address;
    }
}
