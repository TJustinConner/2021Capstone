package com.example.campusnavigation;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

//Shoutout to a lovely Doha Kash who provided me with the article that actually implemented a good embedded PDF Viewer
//https://levelup.gitconnected.com/open-pdf-files-in-android-without-webviews-or-intents-3cc960752cca
public class BulletinBoardGUI extends AppCompatActivity implements DownloadFile.Listener{

    private RemotePDFViewPager remotePDFViewPager;//used for pdf display
    private PDFPagerAdapter pdfPagerAdapter;//used for pdf display
    private static List<String> urlList = new ArrayList<>();//list for urls
    private LinearLayout pdfLayout;//layout for pdf display

    private String location;//location that is passed
    private TextView eventClearText;//used for display of "plain text" event information
    private static List<List<String>> outputArrayList = new ArrayList<List<String>>();//this list stores the parsed event information
    private static int position = 0;//this holds the position in the url/outputArrayList for event information
    private static String eventInformation;//this is the string used to set the text for eventClearText
    private static boolean flipped = false;//this is just used to keep track of the pdf view vs text view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this clears the static lists just in case
        urlList.clear();
        outputArrayList.clear();

        //This sets the location as passed from maps
        location = getIntent().getStringExtra("location");

        //This is the declarations of the view/button display stuff from xml
        setContentView(R.layout.activity_bulletin_board_g_u_i);
        pdfLayout = findViewById(R.id.pdf_layout);
        eventClearText = findViewById(R.id.clearText);
        eventClearText.setVisibility(View.INVISIBLE);

        Button nextPage = findViewById(R.id.nextPDF);
        Button prevPage = findViewById(R.id.prevPDF);
        Button flipButton = findViewById(R.id.flipPDF);
        Button exportButton = findViewById(R.id.export);

        //This stuff is 'required' for the http connection stuff
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            eventSearch(location);
        } catch (IOException e) {
            e.printStackTrace();
        }//end of IOException catch

        for (int i = 0; i < urlList.size();i++){
            Log.d("URL", urlList.get(i));
        }//end of i for loop

        if (urlList.isEmpty()){
            Log.d("Display", "The url list is empty, there are no events");
            remotePDFViewPager = new RemotePDFViewPager(BulletinBoardGUI.this, "https://medusa.mcs.uvawise.edu/~jwe3nv/events/empty.pdf", BulletinBoardGUI.this);
            nextPage.setVisibility(View.INVISIBLE);
            prevPage.setVisibility(View.INVISIBLE);
            flipButton.setVisibility(View.INVISIBLE);
            exportButton.setVisibility(View.INVISIBLE);

        }//end of if
        else{//If stuff is actually returned then put the button listeners and stuff on the screen
            Log.d("POS", "The position is: " + position);
            remotePDFViewPager = new RemotePDFViewPager(BulletinBoardGUI.this, urlList.get(position), BulletinBoardGUI.this);

            nextPage.setOnClickListener(new View.OnClickListener() {//Initialize a clicker for the next page button
                @Override
                public void onClick(View v) {//do things once clicked
                    Log.d("DEBUG", "Hit the next button");
                    if (position < urlList.size()-1) {//move to the right one
                        position += 1;
                    }
                    else{//if already the farthest to the right, go back to the start
                        position = 0;
                    }
                    Log.d("POS", "The position is: " + position);
                    pdfLayout.removeAllViews();
                    remotePDFViewPager = new RemotePDFViewPager (BulletinBoardGUI.this, urlList.get(position), BulletinBoardGUI.this);
                    if (flipped){
                        flipStuff();
                    }
                }
            });//end of prevPage onClickListener

            prevPage.setOnClickListener(new View.OnClickListener() {//Initialize a clicker for the prev page button
                @Override
                public void onClick(View v) {//do things once clicked
                    Log.d("DEBUG", "Hit the prev button");
                    if (position > 0) {//move to the left one
                        position -= 1;
                    }
                    else{//if we are at the start and move to the left, go to the far right
                        position = urlList.size()-1;
                    }
                    Log.d("POS", "The position is: " + position);
                    pdfLayout.removeAllViews();
                    remotePDFViewPager = new RemotePDFViewPager (BulletinBoardGUI.this, urlList.get(position), BulletinBoardGUI.this);
                    if (flipped){
                        flipStuff();
                    }
                }
            });//end of prevPage onClickListener

            flipButton.setOnClickListener(new View.OnClickListener(){//Function to handle "FLIP" button
                @Override
                public void onClick(View v) {//Display event information when pressed
                    flipStuff();
                }
            });//end of flipButton onClickListener

            exportButton.setOnClickListener(new View.OnClickListener() {//Function for when button is pressed
                @Override
                public void onClick(View v) {//do stuff when pressed
                    exportToCalendar("title", location, "5/1/2021", "5:00pm", "6:00pm", "This is an event", "Weekly");
                }
            });
        }//end of else




    }//end of onCreate
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
        }//end of while loop
        output = sb.toString();
        Log.d("SUCCESS", "The SQL QUERY RETURNED:" + output);

        output.trim();
        String outputS[] = output.split("\n");
        Integer int1, int2;
        output = "";
        boolean flag = false;

        List<String> row = new ArrayList<String>();
        for (int i = 0; i < outputS.length; i += 1){
            if (outputS[i].contains("id")){//finds the id line
                i += 1;
                if(outputS[i].indexOf('(') != -1){//finds a " in the string
                    Log.d("DEBUG", "found the first quote");
                    int1 = outputS[i].indexOf('(');//saves the first pos
                    int2 = outputS[i].indexOf(')');//saves the second pos
                    output += "ID: " + outputS[i].substring(int1+1, int2) + "\n";
                    row.add("ID: " + outputS[i].substring(int1+1, int2));
                }//end of ( if
            }//end of id if
            else if (outputS[i].contains("name")){//found the name field
                Log.d("DEBUG", "Found the name string.");
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("NAME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("Time")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("TIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("eTime")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("ETIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("Date")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("DATE: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("recur")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("RECUR: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("location")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("LOCATION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("description")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("DESCRIPTION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
            else if (outputS[i].contains("path")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("url: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
                outputArrayList.add(row);
                row = new ArrayList<String>();
                urlList.add(outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }//end of else if
        }//end of i for loop
        Log.d("SUCCESS", "The trimmed output is: " + output);

        for (int x = 0; x < outputArrayList.size(); x +=1){
            for (int y = 0; y < outputArrayList.get(x).size(); y +=1){
                Log.d("DEBUG", outputArrayList.get(x).get(y));
            }//end of y for loop
        }//end of x for loop
        Log.d("SUCCESS", "The search has returned");
        return output;
    }//end of eventSearch

    @Override
    public void onSuccess(String url, String destinationPath) {//The download was successful
        pdfPagerAdapter = new PDFPagerAdapter(BulletinBoardGUI.this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(pdfPagerAdapter);
        updateLayout();
        Log.d("RUN", "onSuccess");
    }//end of onSuccess

    private void updateLayout(){//updates layout when called
        pdfLayout.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Log.d("RUN", "updateLayout");
    }//end of updateLayout

    @Override
    public void onFailure(Exception e) {//does stuff when the download fails
        Log.d("FAIL", "Did not download file");
    }//end of onFailure

    @Override
    public void onProgressUpdate(int progress, int total) {//does stuff while downloading is occuring
        Log.d("PROG", "Progress Update");
    }//end of onProgressUpdate

    public void flipStuff(){//This function is used to swap between pdf view and plain text view
        eventInformation = "";
        for (int i = 0; i < 8; i++){//
            eventInformation += outputArrayList.get(position).get(i) + "\n";
        }//end of for loop

        eventClearText.setText(eventInformation);
        if(eventClearText.getVisibility() == View.INVISIBLE) {
            eventClearText.setVisibility(View.VISIBLE);
            pdfLayout.setVisibility(View.INVISIBLE);
            flipped = true;
        }//end of if
        else{
            pdfLayout.setVisibility(View.VISIBLE);
            eventClearText.setVisibility(View.INVISIBLE);
            flipped = false;
        }//end of else
    }//end of flip stuff

    private void exportToCalendar(String title, String location, String date, String sTime, String eTime, String description, String recurrence){
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
        }//end of if/else
    }//end of calendar export

}//end of class