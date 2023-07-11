package ca.uwaterloo.cs446.journeytogether.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import ca.uwaterloo.cs446.journeytogether.WelcomeActivity;
import ca.uwaterloo.cs446.journeytogether.user.UserMainActivity;
import ca.uwaterloo.cs446.journeytogether.R;
import ca.uwaterloo.cs446.journeytogether.user.Trip;
import ca.uwaterloo.cs446.journeytogether.user.User;

public class DriverProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private View rootView;
    private Button btnDriverProfileSignout;

    public DriverProfileFragment(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_driver_profile, container, false);
        btnDriverProfileSignout = rootView.findViewById(R.id.btnDriverProfileSignout);
        btnDriverProfileSignout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), WelcomeActivity.class));
            getActivity().finish();
        });

        return rootView;
    }
}
