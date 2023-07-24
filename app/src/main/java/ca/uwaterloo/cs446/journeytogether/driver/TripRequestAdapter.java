package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.common.AddressRep;
import ca.uwaterloo.cs446.journeytogether.driver.ViewRequestsActivity;
import ca.uwaterloo.cs446.journeytogether.schema.Trip;
import ca.uwaterloo.cs446.journeytogether.schema.TripRequest;
import ca.uwaterloo.cs446.journeytogether.schema.User;

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
        private TextView seatsRequestedTextView, pickupTextView, additionalInfoTextView;
        private TextView statusTextView;
        private TextView phoneNumTextView;
        private ImageView phoneImage;
        private Button acceptButton;
        private Button rejectButton;
        private Context context;
        private TripRequest tripRequest;

        public TripRequestViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            passengerTextView = itemView.findViewById(R.id.passengerTextView);
            seatsRequestedTextView = itemView.findViewById(R.id.seatsRequestedTextView);
            pickupTextView = itemView.findViewById(R.id.pickupTextView);
            additionalInfoTextView = itemView.findViewById(R.id.additionalInfoTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            phoneImage = itemView.findViewById(R.id.imageView4);
            phoneNumTextView = itemView.findViewById(R.id.phoneNumTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            this.context = context;
        }

        private void toViewRequestsActivity() {
            // TODO: update available seats display in ViewRequestsActivity after accepting/rejecting request
            Intent intent = new Intent(context, ViewRequestsActivity.class);
            intent.putExtra("trip", tripRequest.getTrip());
            ((ViewRequestsActivity) context).finish();
            context.startActivity(intent);
        }

        private boolean changeStatus(TripRequest.Status status) {
            // TODO: transaction
            AtomicBoolean success = new AtomicBoolean(true);
            TripRequest.firestore.update(tripRequest.getId(), "status", status,
                () -> {},
                () -> { success.set(false); }
            );
            if (status == TripRequest.Status.REJECTED) {
                if (!success.get()) {
                    Toast.makeText(context, "Failed to reject request", Toast.LENGTH_LONG).show();
                    return false;
                }
                Toast.makeText(context, "Request rejected", Toast.LENGTH_LONG).show();
                return true;
            }
            Trip.firestore.update(
                tripRequest.getTrip().getId(), 
                "availableSeats",
                tripRequest.getTrip().getAvailableSeats() - tripRequest.getSeatRequest(),
                () -> {},
                () -> { success.set(false); }
            );
            ArrayList<String> newPassengers = new ArrayList<>();
            for (User user : tripRequest.getTrip().getPassengers()) {
                newPassengers.add(user.getId());
            }
            newPassengers.add(tripRequest.getPassenger().getId());
            Trip.firestore.update(
                tripRequest.getTrip().getId(),
                "passengers",
                newPassengers,
                () -> {},
                () -> { success.set(false); }
            );

            if (!success.get()) {
                Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show();
                return false;
            }
            // TODO: reject all other requests that need more seats than available
            Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
            return true;
        }

        public void bind(TripRequest tripRequest) {
            this.tripRequest = tripRequest;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (tripRequest.getPassenger() != null) {
                passengerTextView.setText(tripRequest.getPassenger().getDisplayName());
            }
            passengerTextView.setText(tripRequest.getPassenger() != null ? tripRequest.getPassenger().getDisplayName() : "...");
            seatsRequestedTextView.setText(String.format("%d seats requested", tripRequest.getSeatRequest()));
            pickupTextView.setText(String.format("Pickup at: %s", AddressRep.getLocationStringAddress(context, tripRequest.getPickupAddr())));

            if (tripRequest.getStatus() == TripRequest.Status.ACCEPTED && tripRequest.isSharePhone() && tripRequest.getPassenger() != null) {
                phoneNumTextView.setText(tripRequest.getPassenger().getPhoneNum());
            } else {
                phoneNumTextView.setVisibility(View.GONE);
                phoneImage.setVisibility(View.GONE);
            }

            if (tripRequest.getComment() == null || tripRequest.getComment().isEmpty()) {
                itemView.findViewById(R.id.textView9).setVisibility(View.GONE);
                additionalInfoTextView.setVisibility(View.GONE);
            } else {
                additionalInfoTextView.setText(tripRequest.getComment());
            }

            switch (tripRequest.getStatus()) {
                case ACCEPTED:
                    statusTextView.setText("Accepted");
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    break;
                case REJECTED:
                    statusTextView.setText("Rejected");
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    break;
                case PENDING:
                    break;
            }
            

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tripRequest.getSeatRequest() > tripRequest.getTrip().getAvailableSeats()) {
                        Toast.makeText(context, "There is not enough seats to accept this request", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (changeStatus(TripRequest.Status.ACCEPTED))
                        toViewRequestsActivity();
                }
            });
            
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (changeStatus(TripRequest.Status.REJECTED))
                        toViewRequestsActivity();
                }
            });
        }
    }
}

