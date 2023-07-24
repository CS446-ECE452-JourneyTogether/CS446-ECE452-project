package ca.uwaterloo.cs446.journeytogether.user;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.provider.ContactsContract;
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
import ca.uwaterloo.cs446.journeytogether.common.AddressRep;
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
        private TextView tripReqDepartureTextView;
        private TextView tripReqDestinationTextView;
        private TextView tripReqCostTextView;
        private TextView tripReqSeatsLeftTextView;
        private TextView tripReqPhoneNumTextView;
        private TextView tripReqTimeTextView;
        private ImageView StatusImageView;
        private TextView tripReqPickUpAddr;
        private TextView tripReqComment;
//        private ImageView iconImageView;
        private Context context;
        private ImageView PhoneNumImage;
        private ImageView CommentImage;
        private TripRequest tripRequest;

        public TripReqViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tripReqDriverTextView = itemView.findViewById(R.id.tripReqDriverTextView);
            tripReqDepartureTextView = itemView.findViewById(R.id.tripReqOriginTextView);
            tripReqDestinationTextView = itemView.findViewById(R.id.tripReqDestinationTextView);
            tripReqCostTextView = itemView.findViewById(R.id.tripReqCostTextView);
            StatusImageView = itemView.findViewById(R.id.StatusImageView);
            tripReqSeatsLeftTextView = itemView.findViewById(R.id.tripReqSeatsLeftTextView);
            tripReqPhoneNumTextView = itemView.findViewById(R.id.tripReqPhoneNumTextView);
            PhoneNumImage=itemView.findViewById(R.id.imageView6);
            tripReqTimeTextView=itemView.findViewById(R.id.tripReqTimeTextView);
            tripReqPickUpAddr=itemView.findViewById(R.id.tripReqPickupAddTextView);
            tripReqComment=itemView.findViewById(R.id.tripReqCommentTextView);
            CommentImage=itemView.findViewById(R.id.imageView9);
            this.context = context;
        }

        public void bind(TripRequest tripRequest) {
            this.tripRequest = tripRequest;
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (tripRequest.getTrip() != null) {
                tripReqDriverTextView.setText(tripRequest.getTrip().getDriver().getDisplayName());
                tripReqDepartureTextView.setText(tripRequest.getTrip().getOriginLocation(this.context));
                tripReqDestinationTextView.setText(tripRequest.getTrip().getDestinationLocation(this.context));
                tripReqTimeTextView.setText(String.format("%s -> %s" , tripRequest.getTrip().getDepartureTime().toString(),tripRequest.getTrip().getArrivalTime().toString()));
                tripReqCostTextView.setText(String.format("$%d/seat", tripRequest.getTrip().getCost()));
                tripReqPickUpAddr.setText(String.format("Pickup at: %s", AddressRep.getLocationStringAddress(context, tripRequest.getPickupAddr())));
                if (tripRequest.getComment().isEmpty()){
                    CommentImage.setVisibility(View.GONE);
                }
                tripReqComment.setText(tripRequest.getComment());
                if (tripRequest.getStatus().toString().equals("ACCEPTED")) {
                    tripReqPhoneNumTextView.setText(tripRequest.getTrip().getDriver().getPhoneNum());
                    PhoneNumImage.setImageResource(R.drawable.phone);
                } else {
                    tripReqPhoneNumTextView.setText("");
                    PhoneNumImage.setVisibility(View.INVISIBLE);
                }
            }

            tripReqSeatsLeftTextView.setText(String.format(String.format("%d seats requested", tripRequest.getSeatRequest())));

            // Log.w("Status", tripRequest.getStatus().toString());

            switch (this.tripRequest.getStatus()) {
                case PENDING:
                    StatusImageView.setImageResource(R.drawable.pending);
                    break;
                case ACCEPTED:
                    StatusImageView.setImageResource(R.drawable.accept);
                    break;
                case REJECTED:
                    StatusImageView.setImageResource(R.drawable.reject);
                    break;
            }
        }
    }
}

