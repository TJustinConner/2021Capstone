package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.os.Bundle;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class AccountCreation extends AppCompatActivity {

    final ArrayList<Character> ACCEPTED_SPECIAL_CHARS = new ArrayList<Character>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '.', '?', '-', '_'));

    final int MAX_PASSWORD_LENGTH = 32;
    final int MIN_PASSWORD_LENGTH = 12;
    final int MAX_EMAIL_LENGTH = 22;


    private final String link = "https://medusa.mcs.uvawise.edu/~jdl8y/accountCreation.php";

    //used to attempt to create an account, task can't be done on main thread, so it is ran in an AsyncTask
    private class AttemptCreateAccount extends AsyncTask<Object, Void, Boolean> {
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
                //change screen to confirmation email sent page
                startActivity(new Intent(AccountCreation.this, EmailVerifSent.class));
            } else {
                //set message account could not be created
                System.out.println("Account couldn't be created");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        setTitle("Account Creation");

        Button CreateAccountButton = (Button) findViewById(R.id.LoginButton);
        final EditText UsernameField = (EditText) findViewById(R.id.UserEmailAddressBox);
        final EditText PasswordField = (EditText) findViewById(R.id.UserPasswordBox);
        final EditText ConfirmPasswordField = (EditText) findViewById((R.id.UserConfirmPasswordBox));
        TextView PasswordReqsLink = (TextView) findViewById((R.id.PasswordReqsLink));
        final TextView PasswordMismatchText = (TextView) findViewById((R.id.PasswordMismatchError));
        final TextView BlankFieldText = (TextView) findViewById((R.id.BlankFieldsError));
        final TextView MissingRequirementsText = (TextView) findViewById((R.id.MissingRequirementsError));



        PasswordMismatchText.setVisibility(View.INVISIBLE);
        BlankFieldText.setVisibility(View.INVISIBLE);
        MissingRequirementsText.setVisibility(View.INVISIBLE);

        PasswordReqsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes user to password requirements activity
                Intent ToPasswordReqs = new Intent(v.getContext(), PasswordRequirementsPage.class);
                startActivity(ToPasswordReqs);
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PasswordMismatchText.setVisibility(View.INVISIBLE);
                BlankFieldText.setVisibility(View.INVISIBLE);
                MissingRequirementsText.setVisibility(View.INVISIBLE);

                //do action on click
                String email = UsernameField.getText().toString();
                String password = PasswordField.getText().toString();
                String confirmedPassword = ConfirmPasswordField.getText().toString();
                boolean inputIsGood = true;

                //check if any fields are empty
                if(email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()){
                    //don't send, make user re-enter input
                    BlankFieldText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }
                //check if passwords match
                else if(!PasswordSuccessfullyConfirmed(password, confirmedPassword)){
                    PasswordMismatchText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }
                //check to see if input length is in our set bounds
                else if(!InputLengthIsGood(password, email)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);
                    //shows refer to password requirements if email length is bad
                    inputIsGood = false;
                }
                //makes sure email and password have the required chars in them that they need to fit our criteria
                else if(!ContainsReqCharTypes(email, false) || !ContainsReqCharTypes(password, true)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }

                //Here we encode the data and check to see if the account has already been created and if the email is on the whitelist

                //if good, go to email sent to verify account page
                //otherwise stay on page, clear fields

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
                        for(int i = 0; i < inBytes.length; i++){
                            //https://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax
                            createdHexRep.append(String.format("%02X", inBytes[i]));
                        }
                        passwordInHex = createdHexRep.toString();

                    }
                    catch(java.security.NoSuchAlgorithmException e) {
                        System.out.println("error in finding hashing algorithm");
                        inputIsGood = false; //stops data from being entered into db
                    }


                    if(inputIsGood) {
                        //attempt to create account
                        Object[] UserAccountInfo = new Object[2];
                        UserAccountInfo[0] = email;
                        UserAccountInfo[1] = passwordInHex;

                        //establish connection
                        AsyncTask<Object, Void, Boolean> createdAccount = new AttemptCreateAccount().execute(UserAccountInfo);
                    }
                }
            }
        });
    }

    //checks to make sure the password matches the confirmed password
    private boolean PasswordSuccessfullyConfirmed(String password1, String password2) {
        if (password1.equals(password2)) {
            return true;
        }
        else {
            Log.d("AcctCreation","Password Mismatch");
            return false;
        }
    }

    //checks to see if input has the required chars in it, isPassword is true for checking the password, false for the email address
    //password requires 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character
    private boolean ContainsReqCharTypes(String input, boolean isPassword){

        //check the password
        if(isPassword){
            boolean containsLowerAlpha = false;
            boolean containsUpperAlpha = false;
            boolean containsNumber = false;
            boolean containsSpecialChar = false;

            //check for each needed type of char, check each position
            for(int i = 0; i < input.length(); i++){
                if(ACCEPTED_SPECIAL_CHARS.contains(input.charAt(i))){
                    containsSpecialChar = true;
                }
                else if(Character.isLowerCase(input.charAt(i))){
                    containsLowerAlpha = true;
                }
                else if(Character.isUpperCase(input.charAt(i))){
                    containsUpperAlpha = true;
                }
                else if(Character.isDigit(input.charAt(i))){
                    containsNumber = true;
                }
            }

            //if the password meets all char requirements
            if(containsLowerAlpha && containsNumber && containsSpecialChar && containsUpperAlpha){
                return true;
            }
            else{
                Log.d("AcctCreation","Missing Required Char Type(s)");
                return false;
            }

        }

        //check the email
        else{
            if(input.contains("@uvawise.edu")){
                return true;
            }
            else{
                Log.d("AcctCreation","Missing Correct Email Domain");
                return false;
            }
        }
    }

    private boolean InputLengthIsGood(String password, String email){
        if(password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH && email.length() <= MAX_EMAIL_LENGTH){
            return true;
        }

        else{
            Log.d("AcctCreation","Invalid Input Length");
            return false;
        }
    }

    private boolean SendData(String email, String password){
        Boolean accountCreated = false;
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

            //see if the server returned that the account was created
            if(result.contains("true")){
                accountCreated = true;
            }
            else if(result.contains("false")){
                accountCreated = false;
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

        return accountCreated; //true if account was created
    }
}
