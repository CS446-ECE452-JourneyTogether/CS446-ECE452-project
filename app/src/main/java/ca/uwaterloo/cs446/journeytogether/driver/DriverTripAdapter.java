package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.driver.ViewRequestsActivity;

public class DriverTripAdapter extends RecyclerView.Adapter<DriverTripAdapter.DriverTripViewHolder> {

    private ArrayList<Trip> trips;
    private Context context;
    private Geocoder geocoder;

    public DriverTripAdapter(ArrayList<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public DriverTripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_trip_item_layout, parent, false);
        return new DriverTripViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverTripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class DriverTripViewHolder extends RecyclerView.ViewHolder {
        private TextView tripOriginTextView;
        private TextView tripDestinationTextView;
        private TextView tripCostTextView;
        private TextView tripSeatsLeftTextView;
        private Button startViewRequestsButton;
        private Context context;
        private Trip trip;

        public DriverTripViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tripOriginTextView = itemView.findViewById(R.id.tripOriginTextView);
            tripDestinationTextView = itemView.findViewById(R.id.tripDestinationTextView);
            tripCostTextView = itemView.findViewById(R.id.tripCostTextView);
            tripSeatsLeftTextView = itemView.findViewById(R.id.tripSeatsLeftTextView);
            startViewRequestsButton = itemView.findViewById(R.id.startViewRequestsButton);
            this.context = context;
        }

        public void setAllowViewRequests(boolean allow) {
            startViewRequestsButton.setVisibility(allow ? View.VISIBLE : View.GONE);
        }

        public void bind(Trip trip) {
            this.trip = trip;

            tripOriginTextView.setText(trip.getOriginLocation(this.context));
            tripDestinationTextView.setText(trip.getRouteStringRep(this.context));
            tripCostTextView.setText(String.format("$%d/seat", trip.getCost()));
            tripSeatsLeftTextView.setText(String.format("%d/%d seats available", trip.getAvailableSeats(), trip.getTotalSeats()));

            startViewRequestsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewRequestsActivity.class);
                    intent.putExtra("trip", trip);
                    context.startActivity(intent);
                }
            });
        }
    }
}

