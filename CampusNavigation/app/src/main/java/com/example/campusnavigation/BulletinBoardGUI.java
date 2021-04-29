package com.example.campusnavigation;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.StrictMode;
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

    private RemotePDFViewPager remotePDFViewPager;
    private PDFPagerAdapter pdfPagerAdapter;
    private static List<String> urlList = new ArrayList<>();
    private LinearLayout pdfLayout;

    private String location;
    private TextView eventClearText;
    private static List<List<String>> outputArrayList = new ArrayList<List<String>>();
    private static int position = 0;
    private static String eventInformation;
    private static boolean flipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlList.clear();
        outputArrayList.clear();
        location = getIntent().getStringExtra("location");
        Log.d("TEST", "kill me");

        setContentView(R.layout.activity_bulletin_board_g_u_i);
        pdfLayout = findViewById(R.id.pdf_layout);

        eventClearText = findViewById(R.id.clearText);
        eventClearText.setVisibility(View.INVISIBLE);

        Button nextPage = findViewById(R.id.nextPDF);
        Button prevPage = findViewById(R.id.prevPDF);
        Button flipButton = findViewById(R.id.flipPDF);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


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
        });

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
        });

        flipButton.setOnClickListener(new View.OnClickListener(){//Function to handle "FLIP" button
            @Override
            public void onClick(View v) {//Display event information when pressed
                flipStuff();
            }
        });

        try {
            eventSearch(location);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < urlList.size();i++){
            Log.d("URL", urlList.get(i));
        }

        Log.d("POS", "The position is: " + position);
        remotePDFViewPager = new RemotePDFViewPager(BulletinBoardGUI.this, urlList.get(position), BulletinBoardGUI.this);

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
                }
            }
            else if (outputS[i].contains("name")){//found the name field
                Log.d("DEBUG", "Found the name string.");
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("NAME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("Time")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("TIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("eTime")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("ETIME: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("Date")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("DATE: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("recur")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("RECUR: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("location")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("LOCATION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("description")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("DESCRIPTION: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
            else if (outputS[i].contains("path")){
                i +=1;
                int1 = outputS[i].indexOf(')');
                row.add("url: " + outputS[i].substring(int1 +3, outputS[i].length() - 1));
                outputArrayList.add(row);
                row = new ArrayList<String>();
                urlList.add(outputS[i].substring(int1 +3, outputS[i].length() - 1));
            }
        }
        Log.d("SUCCESS", "The trimmed output is: " + output);

        for (int x = 0; x < outputArrayList.size(); x +=1){
            for (int y = 0; y < outputArrayList.get(x).size(); y +=1){
                Log.d("DEBUG", outputArrayList.get(x).get(y));
            }
        }
        Log.d("SUCCESS", "The search has returned");
        return output;
    }

    @Override
    public void onSuccess(String url, String destinationPath) {//The download was successful
        pdfPagerAdapter = new PDFPagerAdapter(BulletinBoardGUI.this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(pdfPagerAdapter);
        updateLayout();
        Log.d("RUN", "onSuccess");
    }

    private void updateLayout(){
        pdfLayout.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Log.d("RUN", "updateLayout");
    }
    @Override
    public void onFailure(Exception e) {
        Log.d("FAIL", "Did not download file");
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        Log.d("PROG", "Progress Update");
    }

    public void flipStuff(){
        eventInformation = "";
        for (int i = 0; i < 8; i++){//
            eventInformation += outputArrayList.get(position).get(i) + "\n";
        }

        eventClearText.setText(eventInformation);
        if(eventClearText.getVisibility() == View.INVISIBLE) {
            eventClearText.setVisibility(View.VISIBLE);
            pdfLayout.setVisibility(View.INVISIBLE);
            flipped = true;
        }

        else{
            pdfLayout.setVisibility(View.VISIBLE);
            eventClearText.setVisibility(View.INVISIBLE);
            flipped = false;
        }
    }

}