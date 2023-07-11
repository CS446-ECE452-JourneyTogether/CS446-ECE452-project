package ca.uwaterloo.cs446.journeytogether;

import android.content.Context;
import android.content.Intent;
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

import ca.uwaterloo.cs446.journeytogether.schema.Trip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Trip> trips;
    private Context context;

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
        private TextView tripDestinationTextView;
        private TextView tripCostTextView;
        private TextView tripSeatsLeftTextView;
        private Button startSendRequestButton;
//        private ImageView iconImageView;
        private Context context;
        private Trip trip;

        public TripViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tripDriverTextView = itemView.findViewById(R.id.tripDriverTextView);
            tripDestinationTextView = itemView.findViewById(R.id.tripDestinationTextView);
            tripCostTextView = itemView.findViewById(R.id.tripCostTextView);
            tripSeatsLeftTextView = itemView.findViewById(R.id.tripSeatsLeftTextView);
            startSendRequestButton = itemView.findViewById(R.id.startSendRequestButton);
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

            if (trip.getDriver() != null) {
                tripDriverTextView.setText(trip.getDriver().getDisplayName());
            }

            tripDestinationTextView.setText("TODO: location needs string rep!");
            tripCostTextView.setText(String.format("$%d", trip.getCost()));
            tripSeatsLeftTextView.setText(String.format("%d/%d seats available", trip.getAvailableSeats(), trip.getTotalSeats()));
//            iconImageView.setImageResource(trip.getIconResId());

            // upon pressing the send button, it takes us to a trip request activity
            startSendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TripRequestActivity.class);
                    intent.putExtra("trip", trip);
                    context.startActivity(intent);
                }
            });
        }
    }
}

