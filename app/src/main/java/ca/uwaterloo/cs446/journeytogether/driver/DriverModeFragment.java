package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ca.uwaterloo.cs446.journeytogether.R;

public class DriverModeFragment extends Fragment implements OnMapReadyCallback {

    private ImageButton buttonStop;
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_mode, container, false);

        mapView = view.findViewById(R.id.mapView);

        buttonStop = view.findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDriverModeService();
            }
        });

        startDriverModeService();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    private void startDriverModeService() {
        Intent serviceIntent = new Intent(requireContext(), DriverModeService.class);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }


    private void stopDriverModeService() {
        Intent serviceIntent = new Intent(requireContext(), DriverModeService.class);
        requireContext().stopService(serviceIntent);

        DriverTripsFragment fragmentTrips = new DriverTripsFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fcvDriver, fragmentTrips);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bnvDriver);
        bottomNavigationView.setSelectedItemId(R.id.driver_menu_trips);
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LOCATION_UPDATE")) {
                double longitude = intent.getDoubleExtra("longitude", 0.0);
                double latitude = intent.getDoubleExtra("latitude", 0.0);

                if (googleMap != null) {

                    LatLng newLocation = new LatLng(latitude, longitude);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));

                    BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.bluedot);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(newLocation)
                            .title("New Location")
                            .icon(customIcon);
                    googleMap.clear();
                    googleMap.addMarker(markerOptions);
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("LOCATION_UPDATE");
        requireActivity().registerReceiver(locationReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(locationReceiver);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(43.4643, -80.5204);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Waterloo");
        googleMap.addMarker(markerOptions);
    }
}

