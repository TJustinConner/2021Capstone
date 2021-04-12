package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

//This is the Event Activity, this is where you come after selecting events.
public class EventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView button;
    private String event, time, desc, loc, date, blob, recurrence; //For the user inputs
    private String eTime;
    private String totalInput;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView dateDisplay;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView timeDisplay;

    private TimePickerDialog.OnTimeSetListener meTimeSetListener;
    private TextView eTimeDisplay;

    private EditText eventInput;
    private EditText descInput;
    private TextView eventImageButton;
    private TextView exportCalendarButton;
    Uri path;

    @Override
    //this runs on start of the app
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SUCCESS", "made it into onCreate");
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

        Spinner spunner = (Spinner) findViewById(R.id.recInput);
        ArrayAdapter<CharSequence> edapter = ArrayAdapter.createFromResource(this, R.array.Recurrence, android.R.layout.simple_spinner_item);
        edapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spunner.setAdapter(edapter);
        spunner.setOnItemSelectedListener(this);

        //This is all the prep for the input from users for events, time, description, and date.
        eventInput = (EditText) findViewById(R.id.eventInput);
        descInput = (EditText) findViewById(R.id.descInput);
        dateDisplay = (TextView) findViewById(R.id.datePicker);
        timeDisplay = (TextView) findViewById(R.id.timePicker);
        eTimeDisplay = (TextView) findViewById(R.id.eTimePicker);
        eventImageButton = (TextView) findViewById(R.id.eventImageSelector);
        exportCalendarButton = (TextView) findViewById(R.id.exportCalendarEvent);
        final TextView eventError = (TextView) findViewById(R.id.SCE);
        eventError.setVisibility(View.INVISIBLE);

        //This handles everything that happens when the user clicks the submit button at the bottom of the screen
        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//This is converting the input into strings
                Spinner spinner = (Spinner) findViewById(R.id.locInput);
                String text = spinner.getSelectedItem().toString();

                Spinner spunner = (Spinner) findViewById(R.id.recInput);
                String text2 = spunner.getSelectedItem().toString();

                event = eventInput.getText().toString();
                time = timeDisplay.getText().toString();
                eTime = eTimeDisplay.getText().toString();
                desc = descInput.getText().toString();
                date = dateDisplay.getText().toString();
                loc = text;
                recurrence = text2;

                //convert pdf to blob
                Path pdfPath = Paths.get(path.toString());
                byte[] pdfByteArray = new byte[0];
                try {
                    pdfByteArray = Files.readAllBytes(pdfPath);
                    Files.write(pdfPath, pdfByteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                blob = pdfByteArray.toString();
                Log.d("SUCCESS", "The pdfPathS (before encode) is: " + blob);
                Log.d("SUCCESS", "Retrieved strings");


                //https://androidexample.com/How_To_Make_HTTP_POST_Request_To_Server_-_Android_Example/index.php?view=article_discription&aid=64&aaid=89
                //https://stackoverflow.com/questions/7537377/how-to-include-a-php-variable-inside-a-mysql-statement
                //https://www.w3schools.com/php/php_mysql_prepared_statements.asp
                String testCheck = sanitize(event, time, date);
                displayText(testCheck);

                if (testCheck.compareTo("SAFE")==0){
                    Log.d("DEBUGGING", testCheck);

                    eventError.setVisibility(View.INVISIBLE);

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

                        totalInput += "&" + URLEncoder.encode("eTime", "UTF-8") + "="
                                + URLEncoder.encode(time, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("desc", "UTF-8")
                                + "=" + URLEncoder.encode(desc, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("date", "UTF-8")
                                + "=" + URLEncoder.encode(date, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("loc", "UTF-8")
                                + "=" + URLEncoder.encode(loc, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("blob", "UTF-8")
                                + "=" + URLEncoder.encode(blob, "UTF-8");

<<<<<<< HEAD
=======
                        totalInput += "&" + URLEncoder.encode("recur", "UTF-8")
                                + "=" + URLEncoder.encode(recurrence, "UTF-8");

>>>>>>> main
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
                        while ((line = reader.readLine()) != null) {
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
                    displayText(eTime);
                    displayText(date);
                    displayText(loc);
                    displayText(desc);

<<<<<<< HEAD
=======
                    try {
                        BulletinBoard.eventSearch(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
>>>>>>> main
                }
                else{//this prints error messages to the screen for the user and gives a debugging
                    //message to console/terminal/whatever its called
                    Log.d("FAILED", "user input is bad: " + testCheck);
                    if (testCheck.equals("SCE")){
                        Log.d("FAILED", "Event error.");
                        eventError.setVisibility(View.VISIBLE);
                    }
                }

<<<<<<< HEAD
                Intent toMain = new Intent(v.getContext(), MainActivity.class);
                startActivity(toMain);
=======
                //Intent toMain = new Intent(v.getContext(), MainActivity.class);
                //startActivity(toMain);
>>>>>>> main
            }

        });

        //This handles what happens when the user clicks on the Date Setting Widget
        //This video was used as reference https://www.youtube.com/watch?v=hwe1abDO2Ag
        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(EventActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog, mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //This is the saving/setting of the actual date
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //months start with jan = 0, so this fixes that issue
                month = month + 1;
                Log.d("SUCCESS", "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                dateDisplay.setText(date);

            }
        };

        //this creates the object used to get time input from the user and then saves it to a string
        timeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this, mTimeSetListener, hourOfDay, minute, false);
                //display the widget with a white background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                timePickerDialog.show();
            }
        });

        //this sets the time properly
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeS = hourOfDay + ":" + minute;
                Log.d("DEBUGGING", "The current time is: " + timeS);
                timeDisplay.setText(timeS);
            }
        };

        //this creates the object used to get end time input from the user and then saves it to a string
        eTimeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this, meTimeSetListener, hourOfDay, minute, false);
                //display the widget with a white background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                timePickerDialog.show();
            }
        });

        //this sets the end time properly
        meTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeS = hourOfDay + ":" + minute;
                Log.d("DEBUGGING", "The current time is: " + timeS);
                eTimeDisplay.setText(timeS);
            }
        };

        //this is supposed to get the pdf from the user
        eventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://stackoverflow.com/questions/2227209/how-to-get-the-images-from-device-in-android-java-application
                Intent pdfPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pdfPickerIntent.setType("application/pdf");
                pdfPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pdfPickerIntent, "Select PDF"), 1);

            }
        });

        exportCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the inputs
                Spinner spinner = (Spinner) findViewById(R.id.locInput);
                String text = spinner.getSelectedItem().toString();

                Spinner spunner = (Spinner) findViewById(R.id.recInput);
                String text2 = spunner.getSelectedItem().toString();

                event = eventInput.getText().toString();
                time = timeDisplay.getText().toString();
                eTime = eTimeDisplay.getText().toString();
                desc = descInput.getText().toString();
                date = dateDisplay.getText().toString();
                loc = text;
                recurrence = text2;

                addToCalendar(event, loc, date, time, eTime, desc, recurrence);


            }
        });

    }

    //This function if required for the pdf and flier stuff. This is just returning the proper path
    //and submitting a debugging message
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                path = result.getData();
                Log.d("SUCCESS", "The 'path' saved is: " + path);
            }
        }
    }

    //this is mainly for debugging, it prints the parameter to the screen in a little gray box
    //However this can/is used for just displaying text to the user for a brief period of time.
    private void displayText(String text){//This just displays the input at the bottom of the screen
        Toast.makeText(EventActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    //This does something after an item is selected from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}//default function, has no purpose, but is required for syntax

    private String sanitize(String event, String time, String date){//sanitize user input for special characters
        //checks the given user input for any of the ArrayList of characters. There has to be separate ArrayLists
        //because Date can have '/', but event and time can't, and etc. You could probably refactor this
        //to something cleaner but this is sufficient for now.
        final ArrayList<Character> SC_ARRAY_E = new ArrayList<Character>(Arrays.<Character>asList('!', '"', '#', '$', '%', '^', '&', '*', '(', ')', '+', '=', '-', '_', ',', '<',
        '.', '>', '/', '?', ':', ';', '|', '[', '{', '}', ']', '`', '~', '@', (char) ('[' + 1)));

        for (int i = 0; i < event.length(); i+=1){
            if (SC_ARRAY_E.contains(event.charAt(i))){
                return "SCE";
            }
        }
        return "SAFE";
    }

    public void addToCalendar(String title, String location, String date, String sTime, String eTime, String description, String recurrence){
        //This is to add events a user has created or selected from a bulletin board to their personal calendar
        //The title, location, date, time(start then end time), and description are required for the import.
        //Adding the possibility of inviting other people (perhaps through user accounts or emails) to the events is a good future

        //setting intents
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        Log.d("DEBUG", "The recurrence is: " + recurrence);
        //modify below if you need to add/change calendar inputs
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra(CalendarContract.Events.ALL_DAY, "false");
        intent.putExtra(CalendarContract.Events.DTSTART, sTime);
        intent.putExtra(CalendarContract.Events.DTEND, eTime);
        if (recurrence.equals("Monthly")){
            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=MONTHLY;COUNT=12");
        }
        if (recurrence.equals("Weekly")){
            Log.d("DEBUG", "in weekly add");
            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=52");
        }
        //intent.putExtra(Intent.EXTRA_EMAIL, value "insert email");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        else{
            displayText("There is no app to support this action");
        }
        displayText("Successfully saved event to personal calendar.");
        Log.d("SUCCESS", "Calendar event 'created' check phone calendar");
    }


}