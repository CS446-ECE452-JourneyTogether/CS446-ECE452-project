package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.schema.User;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View rootView;
    private EditText etLastName, etFirstName;
    private Button btnUpdate, btnSignout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        etLastName = rootView.findViewById(R.id.profileEtLastName);
        etFirstName = rootView.findViewById(R.id.profileEtFirstName);
        btnUpdate = rootView.findViewById(R.id.profileBtnUpdate);
        btnSignout = rootView.findViewById(R.id.profileBtnSignout);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = firebaseUser.getDisplayName(); // Retrieve the display name
        if(displayName != null && !displayName.isEmpty()) {
            String[] nameParts = displayName.split(" ");

            String firstName = "";
            String lastName = "";

            if (nameParts.length > 0) {
                firstName = nameParts[0]; // Retrieve the first name

                if (nameParts.length > 1) {
                    lastName = nameParts[nameParts.length - 1]; // Retrieve the last name
                }
            }
            etFirstName.setText(firstName);
            etLastName.setText(lastName);
        } else {
            etFirstName.setText("");
            etLastName.setText("");
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();

            String displayName = firstName + " " + lastName;

            CollectionReference usersCollection = db.collection("jt_driver");

            User.firestore.makeQuery(
                    c -> c.whereEqualTo("email", user.getEmail()).limit(1),
                    arr -> {
                        if (!arr.isEmpty()) {
                            User u = arr.get(0);
                            u.setName(firstName, lastName);
                            User.firestore.syncById(u.getId(), () -> {}, () -> {});
                        }
                    },
                    () -> {}
            );

            usersCollection.whereEqualTo("id", user.getEmail())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String documentId = documentSnapshot.getId();

                            DocumentReference documentRef = db.collection("jt_driver").document(documentId);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("FirstName", firstName);
                            updates.put("LastName", lastName);

                            documentRef.update(updates);
                        }
                    });

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_LONG).show();
                            etFirstName.setText(firstName);
                            etLastName.setText(lastName);
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}

