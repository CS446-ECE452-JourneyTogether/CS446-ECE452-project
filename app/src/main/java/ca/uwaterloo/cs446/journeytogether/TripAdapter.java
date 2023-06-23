package ca.uwaterloo.cs446.journeytogether;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Trip> trips;

    public TripAdapter(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item_layout, parent, false);
        return new TripViewHolder(view);
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
        private TextView tripDestinationTextView;
        private TextView tripCostTextView;
        private TextView tripSeatsLeftTextView;
//        private ImageView iconImageView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripDriverTextView = itemView.findViewById(R.id.tripDriverTextView);
            tripDestinationTextView = itemView.findViewById(R.id.tripDestinationTextView);
            tripCostTextView = itemView.findViewById(R.id.tripCostTextView);
            tripSeatsLeftTextView = itemView.findViewById(R.id.tripSeatsLeftTextView);
//            iconImageView = itemView.findViewById(R.id.iconImageView);
        }

        public void bind(Trip trip) {
            tripDriverTextView.setText(trip.getDriver().getDisplayName());
            tripDestinationTextView.setText("Placeholder location name");
            tripCostTextView.setText("Placeholder location name");
            tripSeatsLeftTextView.setText(String.format("%d/%d seats available", trip.availableSeats(), trip.totalSeats()));
//            iconImageView.setImageResource(trip.getIconResId());
        }
    }
}

