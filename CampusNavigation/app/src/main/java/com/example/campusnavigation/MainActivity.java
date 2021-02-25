package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button AcctCreationButton = (Button) findViewById(R.id.AccountCreationButton);
        Button WorkoutsButton = (Button) findViewById(R.id.WorkoutsButton);
        Button EventsButton = (Button) findViewById(R.id.EventsButton);
        Button SchedulingButton = (Button) findViewById(R.id.SchedulingButton);

        AcctCreationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to account creation activity
                Intent ToAcctCreation = new Intent(v.getContext(), AccountCreation.class);
                startActivity(ToAcctCreation);
            }});
    }
    public void StartWorkout (View s){
        Intent i = new Intent(this, WorkoutCreation.class);
        startActivity(i);
    }
    public void StartEvent(View e){
        Intent i = new Intent(this, EventActivity.class);
        startActivity(i);
    }
    public void StartMap(View k){
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);

    }
}