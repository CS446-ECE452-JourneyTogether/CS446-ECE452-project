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
import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.PolygonUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.graphics.PointF;

public class DriverModeService extends Service implements TextToSpeech.OnInitListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_UPDATE_INTERVAL = 20000; // Update interval in milliseconds
    private static final String NOTIFICATION_CHANNEL_ID = "DriverModeChannel";
    private static final int NOTIFICATION_ID = 1;

    private Location currentLocation;

    private TextToSpeech textToSpeech;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallbackBroadcast;
    private LocationCallback locationCallbackTextToSpeech;

    private FirebaseFirestore db;

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

            LocationRequest locationRequestTextToSpeech = LocationRequest.create()
                    .setInterval(LOCATION_UPDATE_INTERVAL)
                    .setFastestInterval(LOCATION_UPDATE_INTERVAL)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationRequest locationRequestBroadcast = LocationRequest.create()
                    .setInterval(5000) // Update interval in milliseconds
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallbackTextToSpeech = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        currentLocation = locationResult.getLastLocation();
                        updateLocationForSpeech();
                    }
                }
            };

            locationCallbackBroadcast = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        currentLocation = locationResult.getLastLocation();
                        updateLocationForBroadcast();
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequestTextToSpeech, locationCallbackTextToSpeech, Looper.getMainLooper());
            fusedLocationClient.requestLocationUpdates(locationRequestBroadcast, locationCallbackBroadcast, Looper.getMainLooper());

            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    private void updateLocationForBroadcast() {
        try {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();

            Intent broadcastIntent = new Intent("LOCATION_UPDATE");
            broadcastIntent.putExtra("longitude", longitude);
            broadcastIntent.putExtra("latitude", latitude);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocationForSpeech() {
        try {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();

            db.collection("jt_polygon").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            String message = "Driver Mode";
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                String status = documentSnapshot.getString("status");
                                List<GeoPoint> boundarySnapshot = (List<GeoPoint>) documentSnapshot.get("boundary");

                                PointF[] polygon = parseBoundaryCoordinates(boundarySnapshot);
                                PointF point = new PointF((float) latitude, (float) longitude);

                                boolean isPointInPolygon = PolygonUtils.isPointInPolygon(point, polygon);

                                if (isPointInPolygon) {
                                    message = status;
                                    break;
                                }

                            }

                            if(!message.equals("Driver Mode")) {
                                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });

            Intent broadcastIntent = new Intent("LOCATION_UPDATE");
            broadcastIntent.putExtra("longitude", longitude);
            broadcastIntent.putExtra("latitude", latitude);
            sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PointF[] parseBoundaryCoordinates(List<GeoPoint> boundarySnapshot) {

        List<PointF> points = new ArrayList<>();

        for (GeoPoint geoPoint : boundarySnapshot) {
            float latitude = (float) geoPoint.getLatitude();
            float longitude = (float) geoPoint.getLongitude();
            points.add(new PointF(longitude, latitude));
        }

        return points.toArray(new PointF[0]);
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
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallbackTextToSpeech);
            fusedLocationClient.removeLocationUpdates(locationCallbackBroadcast);
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
