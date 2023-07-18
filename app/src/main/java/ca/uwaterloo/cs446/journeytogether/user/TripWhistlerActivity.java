package ca.uwaterloo.cs446.journeytogether.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ca.uwaterloo.cs446.journeytogether.R;

public class TripWhistlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_whistler);

        setDefaultMethod(); // 调用默认方法
    }

    private void setDefaultMethod() {

    }

    // 其他生命周期方法和类成员...
}
