package ca.uwaterloo.cs446.journeytogether;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.util.Log;
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

import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;

public class TripRequestAdapter extends RecyclerView.Adapter<TripRequestAdapter.TripRequestViewHolder> {

    private ArrayList<TripRequest> tripRequests;
    private Context context;
    private Geocoder geocoder;

    public TripRequestAdapter(ArrayList<TripRequest> tripRequests, Context context) {
        this.tripRequests = tripRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public TripRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_layout, parent, false);
        return new TripRequestViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TripRequestViewHolder holder, int position) {
        TripRequest tripRequest = tripRequests.get(position);
        holder.bind(tripRequest);
    }

    @Override
    public int getItemCount() {
        return tripRequests.size();
    }

    public static class TripRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView passengerTextView;
        private TextView seatsRequestedTextView;
        private Button acceptButton;
        private Button rejectButton;
        private Context context;
        private TripRequest tripRequest;

        public TripRequestViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            passengerTextView = itemView.findViewById(R.id.passengerTextView);
            seatsRequestedTextView = itemView.findViewById(R.id.seatsRequestedTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            this.context = context;
        }

        public void bind(TripRequest tripRequest) {
            this.tripRequest = tripRequest;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (tripRequest.getPassenger() != null) {
                passengerTextView.setText(tripRequest.getPassenger().getDisplayName());
            }
            seatsRequestedTextView.setText(String.format("%d seats requested", tripRequest.getSeatRequest()));
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: accept the request
                }
            });
            
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: reject the request
                }
            });
        }
    }
}

