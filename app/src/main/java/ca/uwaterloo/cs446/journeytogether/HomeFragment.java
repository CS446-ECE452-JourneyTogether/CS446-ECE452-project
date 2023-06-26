package ca.uwaterloo.cs446.journeytogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private View rootView;
    private Button btnPost;


    public HomeFragment() {
        // necessarily empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btnPost = rootView.findViewById(R.id.btnPost);

        btnPost.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PostTripActivity.class));
        });

        return rootView;
    }
}
