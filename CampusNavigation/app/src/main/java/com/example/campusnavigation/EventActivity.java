package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.net.ssl.HttpsURLConnection;

//This is the Event Activity, this is where you come after selecting events.
public class EventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView button;
    String event, time, desc, loc, date; //For the user inputs
    String totalInput;

    EditText eventInput;
    EditText timeInput;
    EditText descInput;
    EditText dateInput;

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //This is the setup for the SQL connection
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //This is the setup of the Locations spinner dropdown menu
        Spinner spinner = (Spinner) findViewById(R.id.locInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //This is all the prep for the input from users for events, time, description, and date.
        eventInput = (EditText) findViewById(R.id.eventInput);
        timeInput = (EditText) findViewById(R.id.timeInput);
        descInput = (EditText) findViewById(R.id.descInput);
        dateInput = (EditText) findViewById(R.id.dateInput);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//This is converting the input into strings
                Spinner spinner = (Spinner) findViewById(R.id.locInput);
                String text = spinner.getSelectedItem().toString();

                event = eventInput.getText().toString();
                time = timeInput.getText().toString();
                desc = descInput.getText().toString();
                date = dateInput.getText().toString();
                loc = text;

                //https://androidexample.com/How_To_Make_HTTP_POST_Request_To_Server_-_Android_Example/index.php?view=article_discription&aid=64&aaid=89
                //https://stackoverflow.com/questions/7537377/how-to-include-a-php-variable-inside-a-mysql-statement
                //https://www.w3schools.com/php/php_mysql_prepared_statements.asp

                URL url = null;
                HttpsURLConnection urlConnection = null;
                String totalInput = "";
                BufferedReader reader = null;
                try {
                    //Encoding string to pass through connection
                    totalInput = URLEncoder.encode("event", "UTF-8")
                            + "=" + URLEncoder.encode(event, "UTF-8");

                    totalInput += "&" + URLEncoder.encode("time", "UTF-8") + "="
                            + URLEncoder.encode(time, "UTF-8");

                    totalInput += "&" + URLEncoder.encode("desc", "UTF-8")
                            + "=" + URLEncoder.encode(desc, "UTF-8");

                    totalInput += "&" + URLEncoder.encode("date", "UTF-8")
                            + "=" + URLEncoder.encode(date, "UTF-8");

                    totalInput +=  "&" + URLEncoder.encode("loc", "UTF-8")
                            + "=" + URLEncoder.encode(loc, "UTF-8");

                    //opening connection to php file
                    url = new URL("https://medusa.mcs.uvawise.edu/~jwe3nv/connect.php");
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    Log.d("SUCCESS", "Made connection. totalInput is: " + totalInput);

                    //passing encoded string to the php file
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                    wr.write(totalInput);
                    wr.flush();
                    Log.d("BREAKPOINT", "PASSED OUTPUT");

                    // I'm not sure why we need this, but we do. Without this code we can't properly
                    //get the data to the php file.
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    text = sb.toString();
                    Log.d("SUCCESS", "text returned is:" + text);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();

                // after all the data has been set, now we will insert into database


                //This is displaying the text that was saved in the previous step
                displayText(event);
                displayText(time);
                displayText(date);
                displayText(loc);
                displayText(desc);

                //This is a debugging tag
                Log.d("SUCCESS", "Properly Saved Event Data");

            }

        });
    }

    private void displayText(String text){//This just displays the input at the bottom of the screen
        Toast.makeText(EventActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//This does something after an item is selected from the spinner
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}