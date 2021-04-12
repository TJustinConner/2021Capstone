package com.example.campusnavigation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class BulletinBoard {
    private String locationOnCampus;// this is where the specific bulletin board is located on campus

    protected void create(String location) throws IOException {//this create function takes a location string as a parameter
        locationOnCampus = location;

        eventSearch(location);

    }

    protected static void eventSearch(String location) throws IOException {//query the database with the location that was given
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
                    output += outputS[i].substring(int1, int2);
                }
                output+="a";
            }
            else if (outputS[i].contains("name")){
                output+="a";
            }
            else if (outputS[i].contains("Time")){
                output+="a";
            }
            else if (outputS[i].contains("eTime")){
                output+="a";
            }
            else if (outputS[i].contains("Date")){
                output+="a";
            }
            else if (outputS[i].contains("recur")){
                output+="a";
            }
            else if (outputS[i].contains("location")){
                output+="a";
            }
            else if (outputS[i].contains("description")){
                output+="a";
            }
            else if (outputS[i].contains("image")){
                output+="a";
            }
        }
        Log.d("SUCCESS", "The trimmed output is: " + output);
    }
}
