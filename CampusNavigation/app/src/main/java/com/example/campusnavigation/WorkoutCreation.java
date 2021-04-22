package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    private Spinner DistanceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_creation);

        Log.d("WorkoutCreation", "onCreate initiated");

        //setting title of page
        setTitle("Workout Route Creation");

        //what is needed for spinner
        Log.d("WorkoutCreation","RouteSpinner initiating");
        RouteSpinner = findViewById(R.id.workoutCreationSpinnerRouteType002);
        List<String> RouteOptions = new ArrayList<String>();
        RouteOptions.add("Circuit");
        RouteOptions.add("In and Out");
        RouteOptions.add("One Way");
        ArrayAdapter<String> RouteAdaptor = new ArrayAdapter<String>(WorkoutCreation.this, android.R.layout.simple_list_item_1, RouteOptions);
        RouteSpinner.setAdapter(RouteAdaptor);
        Log.d("WorkoutCreation","RouteSpinner created");

        //spinner drop down menu for the different distance types.
        Log.d("WorkoutCreation","DistanceSpinner initiating");
        DistanceType = findViewById(R.id.spinnerDistanceType002);
        List<String> DistanceOptions = new ArrayList<String>();
        DistanceOptions.add("Mi");
        DistanceOptions.add("Ft");
        //DistanceOptions.add("Steps");
        ArrayAdapter<String> DistanceAdaptor = new ArrayAdapter<String>(WorkoutCreation.this, android.R.layout.simple_list_item_1, DistanceOptions);
        DistanceType.setAdapter(DistanceAdaptor);
        Log.d("WorkoutCreation","DistanceSpinner complete");


        Log.d("WorkoutCreation","onCreate complete");

    }

    //method to disable a button.
    public void disable2 (View v){
        Log.d("WorkoutCreation","disable2 initiated");
        v.setEnabled(false);
        Log.d("WorkoutCreation","disable2 complete");

    }

    /*
    public void disable (View v){
        View myView = findViewById(R.id.button);
        findViewById(R.id.button).setEnabled(false);
        ((Button)findViewById(R.id.button)).setText("new Disable style");

        */
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
    //}

    public void HandleText (View v){
        Log.d ("initiated", "HandleText initiated");
        Toast.makeText(this, "Building your route", Toast.LENGTH_LONG).show();
        launchSettings(v);
        Log.d ("Success", "HandleText");
    }
    public void ErrorFlight_of_Stairs_To_Few (View v){
        Log.d ("initiated", "ErrorFlight_of_Stairs_To_Few initiated");
        Toast.makeText(this, "Negative Numbers for Flight of Stars is not allowed.", Toast.LENGTH_LONG).show();
        launchSettings(v);
        Log.d ("Success", "ErrorFlight_of_Stairs_To_Few");
    }
    public void launchSettings (View s){
        Log.d ("Initiated", "launchSettings");
        Intent i = new Intent(this, WorkoutRoute.class);
        //texting for passing formation from activities
        //i.putExtra("Validation","Good");
        //outputs string of inputted number to next activity

        String message = ((EditText)findViewById(R.id.workoutCreationEditTextNumberSigned003)).getText().toString();
        i.putExtra("Distance",message);

        //outputs string of inputted number to next activity
        String message2 = ((EditText)findViewById(R.id.workoutCreationEditTextNumber002)).getText().toString();
        // if ((int)((EditText)findViewById(R.id.WorkoutCreationEditTextNumber002))) < 0){
        //      ErrorFlight_of_Stairs_To_Few("a");
        // }
        i.putExtra("Flights_of_Stairs",message2);

        //made for testing purposes and not used.
        //@SuppressLint("WrongViewCast") String DistaceTypeString    = ((EditText)findViewById(R.id.spinnerDistanceType)).getText().toString();

        //used to activate another activity an send all previously created output strings made above.
        startActivity(i);
        Log.d ("WorkoutCreation", "HandleText");
        Log.d ("Success", "launchSettings");

    }

}

