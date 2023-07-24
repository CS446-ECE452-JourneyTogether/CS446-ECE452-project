package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.util.Log;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.TripReqViewHolder> {

    private ArrayList<TripRequest> tripRequests;
    private Context context;
    private Geocoder geocoder;

    public RequestAdapter(ArrayList<TripRequest> tripRequest, Context context) {
        this.tripRequests = tripRequest;
        this.context = context;
    }

    @NonNull
    @Override
    public TripReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.triprequest_item_layout, parent, false);
        return new TripReqViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TripReqViewHolder holder, int position) {
        TripRequest tripRequest = tripRequests.get(position);
        holder.bind(tripRequest);
    }

    @Override
    public int getItemCount() {
        return tripRequests.size();
    }


    public static class TripReqViewHolder extends RecyclerView.ViewHolder {
        private TextView tripReqDriverTextView;
        private TextView tripReqDestinationTextView;
        private TextView tripReqCostTextView;
        private  TextView tripReqSeatsLeftTextView;
        private ImageView StatusImageView;
//        private ImageView iconImageView;
        private Context context;
        private TripRequest tripRequest;

        public TripReqViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tripReqDriverTextView = itemView.findViewById(R.id.tripReqDriverTextView);
            tripReqDestinationTextView = itemView.findViewById(R.id.tripReqDestinationTextView);
            tripReqCostTextView = itemView.findViewById(R.id.tripReqCostTextView);
            StatusImageView = itemView.findViewById(R.id.StatusImageView);
            tripReqSeatsLeftTextView = itemView.findViewById(R.id.tripReqSeatsLeftTextView);
            this.context = context;
        }

        public void bind(TripRequest tripRequest) {
            this.tripRequest = tripRequest;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (tripRequest.getTrip() != null) {
                tripReqDriverTextView.setText(tripRequest.getTrip().getDriver().getDisplayName());
                tripReqDestinationTextView.setText(tripRequest.getTrip().getRouteStringRep(this.context));
                tripReqCostTextView.setText(String.format("$%d/seat", tripRequest.getTrip().getCost()));
            }

            tripReqSeatsLeftTextView.setText(String.format(String.format("%d seats requested", tripRequest.getSeatRequest())));

            Log.w("Status", tripRequest.getStatus().toString());

            TripRequest.Status status = this.tripRequest.getStatus();
            if (status.toString().equals("PENDING")) {
                Log.w("Status",status.toString());
                StatusImageView.setImageResource(R.drawable.pending);
            } else if (status.toString().equals("ACCEPTED")) {
                Log.w("Status",status.toString());
                StatusImageView.setImageResource(R.drawable.accept);
            } else if (status.toString().equals("REJECTED")) {
                StatusImageView.setImageResource(R.drawable.reject);
            }
        }
    }
}

