package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

//This is the Event Activity, this is where you come after selecting events.
public class EventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String event, time, eTime, desc, loc, date, recurrence; //For the user inputs
    private String totalInput;//This is for the total encoded input that is passed through the http
    //connection

    //This is setting up views for the date select
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView dateDisplay;

    //This is setting up views for the time select
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView timeDisplay;

    //This is setting up views for the eTime select
    private TimePickerDialog.OnTimeSetListener meTimeSetListener;
    private TextView eTimeDisplay;

    //these are the displays of the set information
    private EditText eventInput;
    private EditText descInput;
    private TextView eventImageButton;
    private TextView exportCalendarButton;
    private String path, serverPath, FILE_NAME;

    //This is used to ask for permission in newer versions of android. In some older versions the permissions in the Manifest are enough,
    //but in newer versions you must ask for permission, which this array just holds those permissions. Add future required permissions here
    //instead of the Manifest
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {//Do things when an instance of this class is created
        //this is asking for the permissions required.
        int permission = ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EventActivity.this, PERMISSIONS_STORAGE, 1);
        }
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

        //This is the setup of the Recurrence spinner dropdown menu
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


                serverPath = "https://medusa.mcs.uvawise.edu/~jwe3nv/events/" + FILE_NAME;

                //https://androidexample.com/How_To_Make_HTTP_POST_Request_To_Server_-_Android_Example/index.php?view=article_discription&aid=64&aaid=89
                //https://stackoverflow.com/questions/7537377/how-to-include-a-php-variable-inside-a-mysql-statement
                //https://www.w3schools.com/php/php_mysql_prepared_statements.asp
                String testCheck = sanitize(event, time, date);
                //displayText(testCheck);

                if (testCheck.compareTo("SAFE") == 0) {//If the user input has been run through sanitize, and pass, do this
                    Log.d("DEBUGGING", testCheck);

                    eventError.setVisibility(View.INVISIBLE);

                    URL url = null;
                    HttpsURLConnection urlConnection = null;
                    String totalInput = "";
                    BufferedReader reader = null;
                    try {//This is trying to upload the data into the SQL server
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

                        totalInput += "&" + URLEncoder.encode("path", "UTF-8")
                                + "=" + URLEncoder.encode(serverPath, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("recur", "UTF-8")
                                + "=" + URLEncoder.encode(recurrence, "UTF-8");

                        totalInput += "&" + URLEncoder.encode("filename", "UTF-8")
                                + "=" + URLEncoder.encode(FILE_NAME, "UTF-8");

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
                        }//end of while loop
                        text = sb.toString();
                        Log.d("SUCCESS", "text returned is:" + text);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }//end of IOException catch
                    urlConnection.disconnect();

                    // after all the data has been set, now we will insert into database

                    //This is displaying the text that was saved in the previous step
                    /*displayText(event);
                    displayText(time);
                    displayText(eTime);
                    displayText(date);
                    displayText(loc);
                    displayText(desc);*/
                    uploadFile();

                } else {//this prints error messages to the screen for the user and gives a debugging
                    //message to console/terminal/whatever its called
                    Log.d("FAILED", "user input is bad: " + testCheck);
                    if (testCheck.equals("SCE")) {
                        Log.d("FAILED", "Event error.");
                        eventError.setVisibility(View.VISIBLE);
                    }//end of if statement
                }//end of else
            }

        });//end of submit button clicker

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
        });//end of dateDisplay button

        //This is the saving/setting of the actual date
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //months start with jan = 0, so this fixes that issue
                month = month + 1;
                Log.d("SUCCESS", "onDateSet: mm/dd/yyyy: " + month + "/" + day + "/" + year);

                String date = month + "-" + day + "-" + year;
                dateDisplay.setText(date);

            }
        };//end of dateSetListener

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
        });//end of timeDisplay button

        //this sets the time properly
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeS = hourOfDay + ":" + minute;
                Log.d("DEBUGGING", "The current time is: " + timeS);
                timeDisplay.setText(timeS);
            }
        };//end of timeSetListener

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
        });//end of endTimeDisplay button

        //this sets the end time properly
        meTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeS = hourOfDay + ":" + minute;
                Log.d("DEBUGGING", "The current time is: " + timeS);
                eTimeDisplay.setText(timeS);
            }
        };//end of endTimeListener

        //this is supposed to get the pdf from the user
        eventImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display popup
                TextView view = new TextView(EventActivity.this);
                onButtonShowPopupWindowClick(view);
                //wait for a few seconds

                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    public void run(){
                        //https://stackoverflow.com/questions/2227209/how-to-get-the-images-from-device-in-android-java-application
                        Intent pdfPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pdfPickerIntent.setType("application/pdf");
                        pdfPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(pdfPickerIntent, "Select PDF"), 1);
                    }
                }, 3000);

            }
        });//end of eventImage button

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
        });//end of exportCalendar button

    }//end of onCreate function

    //This function if required for the pdf and flier stuff. This is just returning the proper path
    //and submitting a debugging message
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.d("DEBUG", "Made it to onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String[] x = result.getData().getPath().split(":");
                String fileName = x[x.length -1];
                Log.d("DEBUG", "The file name is: " + fileName);
                path = "/storage/emulated/0/";
                if (result.getData().toString().contains("download")){
                    path += "Download/";
                }//end of if
                else{
                    path += "Documents/";
                }//end of else
                path += fileName;
                FILE_NAME = fileName;
            }//end of requestCode if
        }//end of RESULT_OK if
    }//end of onActivityResult function

    //this is mainly for debugging, it prints the parameter to the screen in a little gray box
    //However this can/is used for just displaying text to the user for a brief period of time.
    private void displayText(String text) {//This just displays the input at the bottom of the screen
        Toast.makeText(EventActivity.this, text, Toast.LENGTH_SHORT).show();
    }//end of displayText function

    //This does something after an item is selected from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }//end of onItemSelected function

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }//default function, has no purpose, but is required for syntax

    private String sanitize(String event, String time, String date) {//sanitize user input for special characters
        //checks the given user input for any of the ArrayList of characters. There has to be separate ArrayLists
        //because Date can have '/', but event and time can't, and etc. You could probably refactor this
        //to something cleaner but this is sufficient for now.
        final ArrayList<Character> SC_ARRAY_E = new ArrayList<Character>(Arrays.<Character>asList('!', '"', '#', '$', '%', '^', '&', '*', '(', ')', '+', '=',
                '-', '_', ',', '<', '.', '>', '/', '?', ':', ';', '|', '[', '{', '}', ']', '`', '~', '@', (char) ('[' + 1)));

        for (int i = 0; i < event.length(); i += 1) {
            if (SC_ARRAY_E.contains(event.charAt(i))) {
                return "SCE";
            }//end of if
        }//end of i for loop
        return "SAFE";
    }//end of sanitize function

    public void addToCalendar(String title, String location, String date, String sTime, String eTime, String description, String recurrence) {
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
        if (recurrence.equals("Monthly")) {
            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=MONTHLY;COUNT=12");
        }//end of monthly if
        if (recurrence.equals("Weekly")) {
            Log.d("DEBUG", "in weekly add");
            intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=52");
        }//end of Weekly if
        //intent.putExtra(Intent.EXTRA_EMAIL, value "insert email");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            displayText("There is no app to support this action");
        }//end of if/else
        displayText("Successfully saved event to personal calendar.");
        Log.d("SUCCESS", "Calendar event 'created' check phone calendar");
    }//end of addToCalendar function

    private void uploadFile(){//source for this function is https://stackoverflow.com/questions/25398200/uploading-file-in-php-server-from-android-device
        try {//try to create and upload file
            String sourceFileUri = path;
            Log.d("DEBUG", "File path is: " + sourceFileUri);

            HttpsURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);//creates a copy of the file from the given path

            if (sourceFile.isFile()) {//If the file is a file, upload it
                try {//try to upload
                    String upLoadServerUri = "https://medusa.mcs.uvawise.edu/~jwe3nv/connect2.php";

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");//Sets method to post
                    conn.setRequestProperty("Connection", "Keep-Alive");//keep connection alive
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");//how to encrypt
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);//something about a boundary
                    conn.setRequestProperty("file", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());//new output stream

                    dos.writeBytes(twoHyphens + boundary + lineEnd);//write bytes to the output stream
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);//write more bytes

                    dos.writeBytes(lineEnd);//end writing

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);
                    }//end of bytesRead while

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn
                            .getResponseMessage();

                    if (serverResponseCode == 200) {
                        Log.d("UploadFile", "We successfully uploaded the file.");
                        //Toast messageText = null;
                        //messageText.setText(msg);
                        //Toast.makeText(ctx, "File Upload Complete.",
                        //Toast.LENGTH_SHORT).show();
                        //recursiveDelete(mDirectory1);
                    }//end of serverResponseCode if

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }//end of Exception catch
                // dialog.dismiss();

            } // End else block
            else {
                Log.d("UF", "Unable to find source file.");
            }//end of else
        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }//end of Exception else
        finish();
        //Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        //startActivity(intent);
    }//end of uploadFile function

    public void onButtonShowPopupWindowClick(View view){//when this is called, display a popup window
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });//end of touchListener
    }//end of onButtonShowPopupWindowClick function
}//end of EventActivity class