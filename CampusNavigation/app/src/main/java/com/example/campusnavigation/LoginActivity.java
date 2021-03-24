package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    final ArrayList<Character> ACCEPTED_SPECIAL_CHARS = new ArrayList<Character>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '.', '?', '-', '_'));

    final int MAX_PASSWORD_LENGTH = 32;
    final int MIN_PASSWORD_LENGTH = 12;
    final int MAX_EMAIL_LENGTH = 22;
    private final String link = "";

    private class AttemptLogin extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... acctInfo) {
            String email = (String) acctInfo[0];
            String password = (String) acctInfo[1];

            boolean result = SendData(email, password);

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                //change screen to home page
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                //set message account could not be created
                System.out.println("Could not login");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Account Log In");

        Button LoginButton = (Button) findViewById(R.id.LoginButton);
        final EditText UsernameField = (EditText) findViewById(R.id.UserEmailAddressBox);
        final EditText PasswordField = (EditText) findViewById(R.id.UserPasswordBox);
        TextView NewAccountLink = (TextView) findViewById((R.id.newAccountLink));

        NewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes user to password requirements activity
                Intent ToAccountCreation = new Intent(v.getContext(), AccountCreation.class);
                startActivity(ToAccountCreation);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UsernameField.getText().toString();
                String password = PasswordField.getText().toString();

                boolean inputIsGood = true;

                //check if any fields are empty
                if(email.isEmpty() || password.isEmpty()){
                    inputIsGood = false;
                }

                //check to see if input length is in our set bounds
                else if(!InputLengthIsGood(password, email)){
                    inputIsGood = false;
                }

                //take user to email sent page if all input was good
                if(inputIsGood) {

                    //hash password
                    MessageDigest digest = null;
                    String passwordInHex = null;
                    try {
                        digest = MessageDigest.getInstance("SHA-512");
                        byte[] inBytes = digest.digest(password.getBytes());
                        //change from byte array to hex
                        StringBuilder createdHexRep = new StringBuilder();
                        for (int i = 0; i < inBytes.length; i++) {
                            //https://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax
                            createdHexRep.append(String.format("%02X", inBytes[i]));
                        }
                        passwordInHex = createdHexRep.toString();
                        System.out.println(passwordInHex);

                    } catch (java.security.NoSuchAlgorithmException e) {
                        System.out.println("error in finding hashing algorithm");
                        inputIsGood = false; //stops data from being entered into db
                    }

                    if(inputIsGood){

                    }
                }
            }
        });
    }

    private boolean InputLengthIsGood(String password, String email){
        if(password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH && email.length() <= MAX_EMAIL_LENGTH){
            return true;
        }

        else{
            Log.d("Login","Invalid Input Length");
            return false;
        }
    }

    private boolean SendData(String password, String email){
        Boolean successfulSignIn = false;
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(link);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            System.out.println("outputstreamwriter made");

            writer.write(data); //send the user's data
            writer.flush();

            System.out.println("Data written to server");

            //can't get input stream
            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";

            System.out.println("String builder created");

            while((line = reader.readLine()) != null) {
                builder.append(line + "\n");
                //break;
            }

            String result = builder.toString();

            System.out.println(builder.toString()); //test code

            //see if the server returned that the user was logged in
            if(builder.toString() == "true"){
                successfulSignIn = true;
            }

            writer.close();
        }
        catch (java.net.MalformedURLException malformedURLException){
            System.out.println("Bad url.");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            System.out.println("Could not encode data.");
        }
        catch (java.io.IOException ioException){
            System.out.println("Could not open connection");
        }

        return successfulSignIn; //true if account was created
    }
}