package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button LoginButton = (Button) findViewById(R.id.accountCreationButton);
        Button WorkoutsButton = (Button) findViewById(R.id.workoutsButton);
        Button eventsButton = (Button) findViewById(R.id.eventsButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button eventSearchButton = (Button) findViewById(R.id.event_search_button_main);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to account creation activity
                Intent ToLoginActivity = new Intent(v.getContext(), LoginActivity.class);
                startActivity(ToLoginActivity);
            }});

        WorkoutsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to workouts
                Log.d("SUCCESS", "Going to event search");
                Intent workouts = new Intent(v.getContext(), WorkoutCreation.class);
                startActivity(workouts);
            }});

        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to maps
                Log.d("SUCCESS", "Going to event search");
                Intent maps = new Intent(v.getContext(), MapsActivity.class);
                startActivity(maps);
            }});

        eventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to event creation
                Log.d("SUCCESS", "Going to events");
                Intent eventCreation = new Intent(v.getContext(), EventActivity.class);
                startActivity(eventCreation);
            }});

        eventSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to event search
                Log.d("SUCCESS", "Going to event search");
                Intent eventSearch = new Intent(v.getContext(), BulletinBoardGUI.class);
                startActivity(eventSearch);
            }});

/*
        eventSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //takes user to event search
                Log.d("SUCCESS", "Going to AR Base Event");
                Intent eventSearch = new Intent(v.getContext(), SharedCameraActivity.class);
                startActivity(eventSearch);
            }});
*/
    }
}