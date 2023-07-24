package ca.uwaterloo.cs446.journeytogether.user;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.WelcomeActivity;



public class UserProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View rootView;
    private TextView etLastName, etFirstName, etEmail, etPhoneNumber;
    private Button btnUpdate, btnSignout, btnLoginToDriver, btnFirstName, btnLastName, btnPhoneNumber;
    private ImageView imgProfile;
    private Uri imagePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<String> mGetContent;
    public UserProfileFragment(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ActivityResultLauncher
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(),
                                        uri
                                );
                                imgProfile.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    private void updateField(FirebaseUser firebaseUser, String fieldName, String dialogTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(dialogTitle);
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textInput = input.getText().toString();
                String userEmail = firebaseUser.getEmail();
                Map<String,Object> map = new HashMap<>();
                map.put(fieldName, textInput);
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("jt_user")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        // The document ID can be used to get the document path
                                        String path = document.getReference().getPath();
                                        if (fieldName.equals("firstName")) {
                                            etFirstName.setText(textInput);
                                        } else if (fieldName.equals("lastName")) {
                                            etLastName.setText(textInput);
                                        } else if (fieldName.equals("phoneNum")) {
                                            etPhoneNumber.setText(textInput);
                                        }
                                        db.document(path).update(map);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        etLastName = rootView.findViewById(R.id.profileEtLastName);
        etFirstName = rootView.findViewById(R.id.profileEtFirstName);
        etEmail = rootView.findViewById(R.id.profileEtEmail);
        etPhoneNumber = rootView.findViewById(R.id.profileEtPhoneNumber);
        //btnUpdate = rootView.findViewById(R.id.profileBtnUpdate);
        btnSignout = rootView.findViewById(R.id.profileBtnSignout);
        btnFirstName = rootView.findViewById(R.id.profileEtFirstNameBtn);
        btnLastName = rootView.findViewById(R.id.profileEtLastNameBtn);
        btnPhoneNumber = rootView.findViewById(R.id.profileEtPhoneNumberBtn);
        imgProfile = rootView.findViewById(R.id.profileEtimage);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            if (userEmail != null && !userEmail.isEmpty()) {
                db.collection("jt_user")
                        .whereEqualTo("email", userEmail)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if (queryDocumentSnapshots != null) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        // Fetch the names from Firestore
                                        String firstName = document.getString("firstName");
                                        String lastName = document.getString("lastName");
                                        String email = document.getString("email");
                                        String phoneNumber = document.getString("phoneNum");
                                        // Update EditText fields with the names from Firestore
                                        if (firstName != null) {
                                            etFirstName.setText(firstName);
                                        }
                                        if (lastName != null) {
                                            etLastName.setText(lastName);
                                        }
                                        if(email != null) {
                                            etEmail.setText(email);
                                        }
                                        if(phoneNumber != null){
                                            etPhoneNumber.setText(phoneNumber);
                                        }
                                    }

                                }
                            }
                        });
            }
        }

        btnSignout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), WelcomeActivity.class));
            getActivity().finish();
        });
        btnFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateField(firebaseUser, "firstName", "Enter new First Name");
            }
        });

        btnLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateField(firebaseUser, "lastName", "Enter new Last Name");
            }
        });

        btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateField(firebaseUser, "phoneNum", "Enter new Phone Number");
            }
        });


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        return rootView;
    }
}

