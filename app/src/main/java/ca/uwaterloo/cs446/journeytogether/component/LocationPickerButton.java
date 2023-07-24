package ca.uwaterloo.cs446.journeytogether.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.model.LatLng;

import ca.uwaterloo.cs446.journeytogether.LocationPickerActivity;
import ca.uwaterloo.cs446.journeytogether.common.AddressRep;

public class LocationPickerButton extends AppCompatButton {

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
            this.setText(AddressRep.getLocationStringAddress(getContext(), selectedLocation));
        }
    }
}
