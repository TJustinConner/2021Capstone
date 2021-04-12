package com.example.campusnavigation;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class eventSearch{//This function will be used to pull data for the bulletin boards
    private static eventSearch instance;
    URL url = null;
    HttpsURLConnection urlConnection = null;
    String totalInput = "";
    BufferedReader reader = null;
    String text;

    public static eventSearch getInstance(){
        return instance;
    }
    public void eSearch() throws MalformedURLException {
        //opening connection to php file
        url = new URL("https://medusa.mcs.uvawise.edu/~jwe3nv/connect.php");

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        Log.d("SUCCESS", "Made connection. totalInput is: " + totalInput);

        //passing encoded string to the php file
        urlConnection.setDoOutput(true);
        OutputStreamWriter wr = null;
        try {
            wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(totalInput);
            wr.flush();
            Log.d("BREAKPOINT", "PASSED OUTPUT");

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // I'm not sure why we need this, but we do. Without this code we can't properly
        //get the data to the php file.
        StringBuilder sb = new StringBuilder();
        String line = null;
        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            sb.append(line + "\n");
        }
        text = sb.toString();
        Log.d("SUCCESS", "text returned is:" + text);
        urlConnection.disconnect();
    }

}



