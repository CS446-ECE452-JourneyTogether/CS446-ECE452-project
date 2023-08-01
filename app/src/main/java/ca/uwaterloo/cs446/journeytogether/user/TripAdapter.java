package ca.uwaterloo.cs446.journeytogether.user;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ca.uwaterloo.cs446.journeytogether.common.CurrentUser;

import java.util.ArrayList;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;

import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;
import ca.uwaterloo.cs446.journeytogether.schema.User;
import ca.uwaterloo.cs446.journeytogether.user.TripRequestActivity;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Trip> trips;
    private Context context;
    private Geocoder geocoder;

    public TripAdapter(ArrayList<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item_layout, parent, false);
        return new TripViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView tripDriverTextView;
        private TextView tripOriginTextView;
        private TextView tripDestinationTextView;
        private TextView tripCostTextView;
        private TextView tripSeatsLeftTextView;
        private TextView tripTimeTexView;
        private Button startSendRequestButton;
//        private ImageView iconImageView;
        private ImageButton collectTripButton;
        private boolean isCollected = false;
        private Context context;
        private Trip trip;

        public TripViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            tripDriverTextView = itemView.findViewById(R.id.tripDriverTextView);
            tripOriginTextView = itemView.findViewById(R.id.tripOriginTextView);
            tripDestinationTextView = itemView.findViewById(R.id.tripDestinationTextView);
            tripCostTextView = itemView.findViewById(R.id.tripCostTextView);
            tripSeatsLeftTextView = itemView.findViewById(R.id.tripSeatsLeftTextView);
            tripTimeTexView = itemView.findViewById(R.id.tripTimeTextView);

            startSendRequestButton = itemView.findViewById(R.id.startSendRequestButton);
            collectTripButton = itemView.findViewById(R.id.collectTripButton);
            collectTripButton.setOnClickListener(v -> onCollectTripButtonClick());

            this.context = context;
//            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        // this function enables or disables the Send request button
        public void setAllowSendRequest(boolean allow) {
            startSendRequestButton.setVisibility(allow ? View.VISIBLE : View.GONE);
        }

        public void bind(Trip trip) {
            this.trip = trip;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference driverCollection = db.collection("jt_driver");
            updateToggleButtonAppearance();

            CurrentUser.getCurrentUser().thenAccept(currentUser -> {
                initToggleButtonAppearance(currentUser);
            });

            if (trip.getDriver() != null) {
                tripDriverTextView.setText(trip.getDriver().getDisplayName());
            }

            tripOriginTextView.setText(trip.getOriginLocation(this.context));
            tripDestinationTextView.setText(trip.getDestinationLocation(this.context));
            tripCostTextView.setText(String.format("$%d/seat", trip.getCost()));
            tripSeatsLeftTextView.setText(String.format("%d/%d seats available", trip.getAvailableSeats(), trip.getTotalSeats()));
            tripTimeTexView.setText(String.format("%s -> %s" , trip.getDepartureTime().toString(),trip.getArrivalTime().toString()));
//            iconImageView.setImageResource(trip.getIconResId());

            // upon pressing the send button, it takes us to a trip request activity
            startSendRequestButton.setOnClickListener(v -> {
                CurrentUser.getCurrentUser().thenApply((currentUser) -> {
                    TripRequest.firestore.makeQuery(
                        c -> c.whereEqualTo("trip", trip.getId()).whereEqualTo("passenger", currentUser.getId()),
                        (arr) -> {
                            if (arr.size() > 0) {
                                Toast.makeText(context, "You have already sent a request for this trip", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(context, TripRequestActivity.class);
                                intent.putExtra("trip", trip);
                                context.startActivity(intent);
                            }
                        },
                        () -> { });
                    return currentUser;
                });
            });
        }

        private void initToggleButtonAppearance(User currentUser) {
            if (currentUser.getCollectedTrips() == null) {
                currentUser.setCollectedTrips(new ArrayList<>());
            }
            if (!currentUser.getCollectedTrips().contains(trip.getId())) {
                isCollected = false;
            } else {
                isCollected = true;
            }
            Log.d("CollectTest", currentUser.getCollectedTrips().toString());
            updateToggleButtonAppearance();
        }


        private void updateToggleButtonAppearance() {
            if (isCollected) {
                collectTripButton.setImageResource(R.drawable.ic_star_filled);
            } else {
                collectTripButton.setImageResource(R.drawable.ic_star_outline);
            }
        }

        // Handle click event of the ImageButton
        public void onCollectTripButtonClick() {
            isCollected = !isCollected;
            updateToggleButtonAppearance();
            Log.d("CollectTest", trip.getId());

            CurrentUser.getCurrentUser().thenAccept(currentUser -> {
                // Update the collectedTrips list of the current user based on the isCollected flag
                if (currentUser.getCollectedTrips() == null) {
                    currentUser.setCollectedTrips(new ArrayList<>());
                }
                if (!currentUser.getCollectedTrips().contains(trip.getId())) {
                    currentUser.getCollectedTrips().add(trip.getId());
                    Log.d("CollectTest", currentUser.getCollectedTrips().toString());
                } else {
                    currentUser.getCollectedTrips().remove(trip.getId());
                }
                Log.d("CollectTest", currentUser.asMap().toString());

                // Save the updated user data to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("jt_user")
                        .whereEqualTo("email", currentUser.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        // The document ID can be used to get the document path
                                        String path = document.getReference().getPath();
                                        db.document(path).update("collectedTrips", currentUser.getCollectedTrips());
                                    }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            });
        }
    }
}

