package com.example.campusnavigation;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class BulletinBoardGUI extends AppCompatActivity implements AdapterView.OnItemSelectedListener{




        private String location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        if(extras != null )

        {
        location = extras.getString("location");
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_board_g_u_i);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("DEBUG", "Java sucks dick.");
        //This is the setup of the Locations spinner dropdown menu
        Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }
    protected static String eventSearch(String location) throws IOException {//query the database with the location that was given
        URL url = null;
        HttpsURLConnection urlConnection = null;;
        BufferedReader reader = null;



        String input = location;
        String output = "";

        //prepare the data for the query by encoding
        input = URLEncoder.encode("location", "UTF-8")
                + "=" + URLEncoder.encode(input, "UTF-8");

        //set the url to the php file in the student account, make the connection
        url = new URL("https://medusa.mcs.uvawise.edu/~jwe3nv/search.php");
        urlConnection = (HttpsURLConnection) url.openConnection();
        Log.d("SUCCESS", "Made connection. totalInput is: " + input);

        //make the actual connection, pass the input data into the writer, and run the php
        urlConnection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
        wr.write(input);
        wr.flush();
        Log.d("BREAKPOINT", "PASSED OUTPUT");

        //this is taking the echo/returned stuff from the database and saving it in text
        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        output = sb.toString();
        Log.d("SUCCESS", "The SQL QUERY RETURNED:" + output);

        output.trim();
        String outputS[] = output.split("\n");
        String temp[];
        Integer int1, int2;
        output = "";
        boolean flag = false;

        for (int i = 0; i < outputS.length; i += 1){
            if (outputS[i].contains("id")){//finds the id line
                i += 1;
                if(outputS[i].indexOf('(') != -1){//finds a " in the string
                    Log.d("DEBUG", "found the first quote");
                    int1 = outputS[i].indexOf('(');//saves the first pos
                    int2 = outputS[i].indexOf(')');//saves the second pos
                    output += "ID: " + outputS[i].substring(int1+1, int2) + "\n";
                }
            }
            else if (outputS[i].contains("name")){//found the name field
                Log.d("DEBUG", "Found the name string.");
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "NAME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("Time")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "TIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("eTime")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "ETIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("Date")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "DATE: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("recur")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "RECUR: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";;
            }
            else if (outputS[i].contains("location")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "LOCATION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("description")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "DESCRIPTION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
            else if (outputS[i].contains("image")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                output += "BLOB: " + outputS[i].substring(int1 +3, outputS[i].length() - 1) + "\n";
            }
        }
        Log.d("SUCCESS", "The trimmed output is: " + output);
        return output;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            Log.d("DEBUG", "Results of test are: " + eventSearch(parent.getItemAtPosition(position).toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
