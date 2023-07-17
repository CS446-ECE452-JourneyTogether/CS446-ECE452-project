package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.TripViewHolder> {

    private ArrayList<TripRequest> tripRequests;
    private Context context;
    private Geocoder geocoder;

    public RequestAdapter(ArrayList<TripRequest> tripRequest, Context context) {
        this.tripRequests = tripRequest;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.triprequest_item_layout, parent, false);
        return new TripViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripRequest tripr = tripRequests.get(position);
        holder.bind(tripr);
    }

    @Override
    public int getItemCount() {
        return tripRequests.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        private TextView tripDriverTextView;
        private TextView tripDestinationTextView;
        private TextView tripCostTextView;
        private ImageView StatusImageView;
//        private ImageView iconImageView;
        private Context context;
        private TripRequest tripRequest;

        public TripViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tripDriverTextView = itemView.findViewById(R.id.tripReqDriverTextView);
            tripDestinationTextView = itemView.findViewById(R.id.tripReqDestinationTextView);
            tripCostTextView = itemView.findViewById(R.id.tripReqCostTextView);
            StatusImageView = itemView.findViewById(R.id.StatusImageView);
            this.context = context;
        }

        public void bind(TripRequest tripRequest) {
            this.tripRequest = tripRequest;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference driverCollection = db.collection("jt_user");
            CollectionReference tripCollection = db.collection("jt_trips");

            Trip trip = tripRequest.getTrip();
            if (trip.getDriver() != null) {
                tripDriverTextView.setText(trip.getDriver().getDisplayName());
            }

            tripDestinationTextView.setText(trip.getRouteStringRep(this.context));
            tripCostTextView.setText(String.format("$%d/seat", trip.getCost()));

        }
    }
}

