package com.example.workoutv01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_justin_branch);

        //setting title of page
        setTitle("Home");
    }
    public void StartWorkout (View s){
        Intent i = new Intent(this, WorkoutCreation.class);
        startActivity(i);
    }
}