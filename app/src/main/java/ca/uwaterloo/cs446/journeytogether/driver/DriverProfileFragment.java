package ca.uwaterloo.cs446.journeytogether.driver;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.WelcomeActivity;

public class DriverProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private View rootView;
    private TextView etFirstName,etLastName, etEmail, etPhoneNumber, etDriverL, etCarMake;
    private Button btnUpdate, btnSignout, btnLoginToDriver, btnFirstName, btnLastName, btnPhoneNumber;
    private Button btnDriverL, btnCarMake;
    private ImageView imgProfile;
    private Uri imagePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<String> mGetContent;
    public DriverProfileFragment(FirebaseAuth mAuth) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        etLastName = rootView.findViewById(R.id.profileEtLastName);
        etFirstName = rootView.findViewById(R.id.profileEtFirstName);
        etEmail = rootView.findViewById(R.id.profileEtEmail);
        etPhoneNumber = rootView.findViewById(R.id.profileEtPhoneNumber);
        etDriverL = rootView.findViewById(R.id.profileEtDriverL);
        etCarMake = rootView.findViewById(R.id.profileEtCarMake);
        btnSignout = rootView.findViewById(R.id.profileBtnSignout);
        btnFirstName = rootView.findViewById(R.id.profileEtFirstNameBtn);
        btnLastName = rootView.findViewById(R.id.profileEtLastNameBtn);
        btnPhoneNumber = rootView.findViewById(R.id.profileEtPhoneNumberBtn);
        btnDriverL = rootView.findViewById(R.id.profileEtDriverLBtn);
        btnCarMake = rootView.findViewById(R.id.profileEtCarMakeBtn);

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
                                        String driverL = document.getString("driverLicense");
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
                                        if(driverL != null){
                                            etDriverL.setText(driverL);
                                        }else{

                                            etDriverL.setText("Please update driver License");
                                        }
                                    }

                                }
                            }
                        });
            }
        }


        btnCarMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Vehicle Information Displaying...");

                // Inflate the layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.car_brands, null);
                final TextView carBrand = dialogLayout.findViewById(R.id.carBrandLabel);
                final TextView carColor = dialogLayout.findViewById(R.id.carColorLabel);
                final TextView carYear = dialogLayout.findViewById(R.id.carYearLabel);
                final TextView insurance = dialogLayout.findViewById(R.id.insuranceInput);
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
                                                String CarBrandStr = document.getString("carBrand");
                                                String CarColorStr = document.getString("carColor");
                                                String CarYearStr = document.getString("carYear");
                                                String Insurance = document.getString("insurance");
                                                if(CarBrandStr == null){
                                                    carBrand.setText("Update Brand");
                                                }else{
                                                    carBrand.setText(CarBrandStr);
                                                }
                                                if(CarColorStr == null){
                                                    carColor.setText("Update Color");
                                                }else{
                                                    carColor.setText(CarColorStr);
                                                }
                                                if(CarYearStr == null){
                                                    carYear.setText("Update Year");
                                                }else{
                                                    carYear.setText(CarYearStr);
                                                }
                                                if(Insurance == null){
                                                    insurance.setText("Enter Insurance");
                                                }else{
                                                    insurance.setText(Insurance);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }

                // do the check null for brand here




                Spinner spinnerBrand = dialogLayout.findViewById(R.id.carBrandSpinner);
                Spinner spinnerColor = dialogLayout.findViewById(R.id.carColorSpinner);
                Spinner spinnerYear = dialogLayout.findViewById(R.id.carYearSpinner);
                final EditText insuranceInput = dialogLayout.findViewById(R.id.insuranceInput);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.car_brands, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBrand.setAdapter(adapter);
                //handel the brand spinner

                ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(getContext(),
                        R.array.car_colors, android.R.layout.simple_spinner_item); // Change this to your actual array resource for colors
                adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerColor.setAdapter(adapterColor);

                ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(getContext(),
                        R.array.car_years, android.R.layout.simple_spinner_item); // Change this to your actual array resource for years
                adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerYear.setAdapter(adapterYear);

                spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCarBrand = parent.getItemAtPosition(position).toString();
                        carBrand.setText(selectedCarBrand);
                        Log.d("TAG", "Selected car brand: " + selectedCarBrand);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // This method is invoked when the spinner selection is cleared.
                        // You might want to handle this case too, depending on your app's behavior.
                    }
                });
                spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCarColor = parent.getItemAtPosition(position).toString();
                        carColor.setText(selectedCarColor);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // You might want to handle this case too, depending on your app's behavior.
                    }
                });
                spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCarYear = parent.getItemAtPosition(position).toString();
                        carYear.setText(selectedCarYear);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // You might want to handle this case too, depending on your app's behavior.
                    }
                });


                builder.setView(dialogLayout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String carColorStr = carColor.getText().toString();
                        String insuranceNum = insuranceInput.getText().toString();
                        String carBrandStr =carBrand.getText().toString();
                        String carYearStr =carYear.getText().toString();
                        /*final TextView carBrand = dialogLayout.findViewById(R.id.carBrandLabel);
                        String carBrandValue = carBrand.getText().toString();*/

                        // You may want to add additional validation here, similar to isValidLicense

                        String userEmail = firebaseUser.getEmail();
                        Map<String, Object> map = new HashMap<>();
                        map.put("carColor", carColorStr);
                        map.put("insurance", insuranceNum);
                        map.put("carBrand", carBrandStr);
                        map.put("carYear", carYearStr);
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
                                                // assuming you have an EditText etInsurance for the insurance number
                                                //etInsurance.setText(insuranceNum);
                                                db.document(path).update(map);
                                                Toast.makeText(getContext(), "Car Information Updated", Toast.LENGTH_SHORT).show();
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
        });

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

        btnDriverL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateField(firebaseUser, "driverLicense", "Enter new Driver License");
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

    private boolean isValidLicense(String driverL) {
        Pattern pattern = Pattern.compile("[A-Za-z]{1}\\d{4}-\\d{5}-\\d{5}");
        // example: A1234-12345-12345
        Matcher matcher = pattern.matcher(driverL);
        return matcher.matches();
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
                                        } else if (fieldName.equals("driverLicense")) {
                                            if (isValidLicense(textInput)) {
                                                etDriverL.setText(textInput);
                                            } else {
                                                Toast.makeText(getContext(), "Invalid Driver License", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
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

}

