package ca.uwaterloo.cs446.journeytogether.driver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.app.Service;

import java.lang.*;

import ca.uwaterloo.cs446.journeytogether.R;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class DriverModeService extends Service implements TextToSpeech.OnInitListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_UPDATE_INTERVAL = 20000; // Update interval in milliseconds
    private static final String NOTIFICATION_CHANNEL_ID = "DriverModeChannel";
    private static final int NOTIFICATION_ID = 1;

    private Location currentLocation;

    private TextToSpeech textToSpeech;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private FirebaseFirestore db;

    private String message;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());
        requestLocationPermission();
        return START_STICKY;
    }

    private void requestLocationPermission() {
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(LOCATION_UPDATE_INTERVAL)
                    .setFastestInterval(LOCATION_UPDATE_INTERVAL)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        currentLocation = locationResult.getLastLocation();
                        updateLocation();
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            // 启动前台服务通知
            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    private void updateLocation() {
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyCsFS0c6k07jbJLYwNBXbSiCiwSPMMUjWU").build();
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            LatLng location = new LatLng(latitude, longitude);

            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, location).await();

            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                String tempNum = null;
                String tempRoad = null;
                String tempCity = null;
                for (AddressComponent component : result.addressComponents) {
                    for (AddressComponentType type : component.types) {
                        if ("street_number".equals(type.toString())) {
                            tempNum = component.longName;
                            break;
                        }

                        if ("route".equals(type.toString())) {
                            tempRoad = component.longName;
                            break;
                        }

                        if ("locality".equals(type.toString())) {
                            tempCity = component.longName;
                            break;
                        }
                    }
                }

                if (tempNum != null && tempNum.matches("\\d+") && tempRoad != null && tempCity != null) {
                    String roadNum = tempNum;
                    String roadName = tempRoad;
                    String cityName = tempCity;
                    db.collection("jt_roadcond")
                        .whereEqualTo("street", roadName)
                        .get()
                        .addOnCompleteListener(task -> {
                            String message = null;
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        Long startunitLong = document.getLong("startunit");
                                        Long endunitLong = document.getLong("endunit");
                                        int startunit = startunitLong != null ? startunitLong.intValue() : 0;
                                        int endunit = endunitLong != null ? endunitLong.intValue() : 0;
                                        if (startunit <= Integer.parseInt(roadNum) && endunit >= Integer.parseInt(roadNum)) {
                                            String status = document.getString("status");
                                            message = roadNum + '\n' + roadName + '\n' + cityName + '\n' + status;
                                        }
                                    }
                                } else {
                                    message = roadNum + '\n' + roadName + '\n' + cityName;
                                }
                            }

                            // 发送广播更新消息
                            Intent broadcastIntent = new Intent("LOCATION_UPDATE");
                            broadcastIntent.putExtra("message", message);
                            sendBroadcast(broadcastIntent);
                            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                        });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // TextToSpeech initialization successful
        } else {
            // TextToSpeech initialization failed
            Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, DriverMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Driver Mode")
                .setContentText("Driver mode is running")
                .setSmallIcon(R.drawable.driving)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Driver Mode";
            String description = "Driving Mode";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
