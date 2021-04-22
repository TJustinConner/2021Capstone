package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WorkoutRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_route);

        //setting title of page
        setTitle("Route View");

        //getting information from precious activity
        Intent i = getIntent();

        /*//testing to pass things between activities
        String Verification = i.getStringExtra("Validation");
        //casting information from above into textfield for testing.
        ((TextView)findViewById(R.id.textView4)).setText(Verification);
         */
        String DistanceAmount = i.getStringExtra("Distance");
        ((TextView)findViewById(R.id.workoutRoutetextView)).setText(DistanceAmount);


        //textView6  Flights_of_Stairs

        String StairAmount = i.getStringExtra("Flights_of_Stairs");
        ((TextView)findViewById(R.id.textView2)).setText(StairAmount);
    }

}