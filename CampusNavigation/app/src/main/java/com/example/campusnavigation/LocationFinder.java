package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFinder extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    int Coord_number = 0;
    //String[] location_number = getResources().getStringArray(R.array.Location_number);//this holds the values in the string array location_number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_finder3);



        Spinner spinner = findViewById(R.id.maps_Spinners);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] loc = getResources().getStringArray(R.array.Location_number);//this holds the values in the string array location_number

        if(parent.getId()==R.id.maps_Spinners) {

            String text = parent.getItemAtPosition(position).toString();
            //  Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
            display(text,loc);
            if(Coord_number != 0) {
                String intToString = String.valueOf(Coord_number);
                Intent sender = new Intent();
                sender.putExtra("coordinate",intToString);
                setResult(Activity.RESULT_OK,sender);
                finish();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //THIS IS SOO CUTE COMPARED TO WHAT IT WAS BEFORE!!!!
    //basicly loc is a string array that lists every location along with the
    //place where they are in the coords.xml file
    //so we can tie the title of the location to the coordant to the place in the Coords datasheet
    //this allows us to use navigation easily
    //so LOC[] holds both the name of the place as well as the coord # cooresponding to the coords file
    public void display(String name,String [] loc){

        for(int iterator = 0; iterator<77;iterator=iterator+2){

            if(loc[iterator].equals(name)){

                Coord_number = Integer.valueOf(loc[iterator+1]);
            }
        }

    }//display
}