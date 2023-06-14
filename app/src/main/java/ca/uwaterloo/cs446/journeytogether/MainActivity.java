package ca.uwaterloo.cs446.journeytogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection connection = dbConnection.establishConnection();
        // Use the 'connection' object for database operations

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}