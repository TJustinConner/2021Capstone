package com.example.workoutv01;

//THE FUCK IS THIS HORSE SHIT

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

public class WorkoutCreation extends AppCompatActivity {

    private Spinner RouteSpinner;
    private Spinner DistenceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting title of page
        setTitle("Workout Route Creation");

        //what is needed for spinner
        RouteSpinner = findViewById(R.id.SpinnerRouteType);
        List<String> RouteOptions = new ArrayList<String>();
        RouteOptions.add("In and Out");
        RouteOptions.add("One Way");
        RouteOptions.add("Circuit");
        ArrayAdapter<String> RouteAdaptor = new ArrayAdapter<String>(WorkoutCreation.this, android.R.layout.simple_list_item_1, RouteOptions);
        RouteSpinner.setAdapter(RouteAdaptor);

        //spinner drop down menu for the different distance types.
        DistenceType = findViewById(R.id.spinnerDistenceType);
        List<String> DistanceOptions = new ArrayList<String>();
        DistanceOptions.add("Mi");
        DistanceOptions.add("Ft");
        //DistanceOptions.add("Steps");
        ArrayAdapter<String> DistanceAdaptor = new ArrayAdapter<String>(WorkoutCreation.this, android.R.layout.simple_list_item_1, DistanceOptions);
        DistenceType.setAdapter(DistanceAdaptor);


    }

    //method to disable a button.
    public void disable2 (View v){
        v.setEnabled(false);
        Log.d("success","Button Disabled on workout screen");

    }
    public void disable (View v){
        View myView = findViewById(R.id.button);
        findViewById(R.id.button).setEnabled(false);
        ((Button)findViewById(R.id.button)).setText("new Disable style");


        //disable but change what button says
        /*
        myView.setEnabled(false);
        Button button = (Button) myView;
        button.setText("New Disabled");

         */
        //simple disable for button
        /*
        v.setEnabled(false);
        Button button = (Button) v;
        button.setText("Disabled");
        */
    }

    public void HandleText (View v){
        Log.d ("Initiated", "HandleText");
        Toast.makeText(this, "Building your route", Toast.LENGTH_LONG).show();
        launchSettings(v);
        Log.d ("Success", "HandleText");
    }

    public void launchSettings (View s){
        Log.d ("Initiated", "launchSettings");
        Intent i = new Intent(this, WorkoutRoute.class);
        //texting for passing formation from activities
        //i.putExtra("Validation","Good");

        String message = ((EditText)findViewById(R.id.editTextNumberSigned)).getText().toString();
        i.putExtra("Distance",message);

        String message2 = ((EditText)findViewById(R.id.editTextNumber2)).getText().toString();
        i.putExtra("Flights_of_Stairs",message2);

        //@SuppressLint("WrongViewCast") String DistaceTypeString    = ((EditText)findViewById(R.id.spinnerDistenceType)).getText().toString();

        startActivity(i);
        Log.d ("Success", "launchSettings");
    }


}