package com.example.campusnavigation;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class AccountCreation extends BasicLoginFunctionality {
    private final String ACCT_CREATION_LINK = "https://medusa.mcs.uvawise.edu/~jdl8y/accountCreation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        setTitle("Account Creation");

        final Button CreateAccountButton = (Button) findViewById(R.id.CreateAcctButton);
        final EditText UsernameField = (EditText) findViewById(R.id.NewAcctEmailBox);
        final EditText PasswordField = (EditText) findViewById(R.id.NewAcctPasswordBox);
        final EditText ConfirmPasswordField = (EditText) findViewById((R.id.ConfirmPasswordNewBox));
        final TextView PasswordReqsLink = (TextView) findViewById((R.id.PasswordReqsLinkCreation));
        final TextView PasswordMismatchText = (TextView) findViewById((R.id.PasswordMismatchCreateError));
        final TextView BlankFieldText = (TextView) findViewById((R.id.BlankFieldsCreateError));
        final TextView MissingRequirementsText = (TextView) findViewById((R.id.MissingRequirementsCreateError));
        final TextView AcctAlreadyExistsText = (TextView) findViewById((R.id.AccountExistsCreateError));
        final TextView AcctNotWhitelistedText = (TextView) findViewById((R.id.UnwhitelistedEmailCreateError));

        PasswordMismatchText.setVisibility(View.INVISIBLE);
        BlankFieldText.setVisibility(View.INVISIBLE);
        MissingRequirementsText.setVisibility(View.INVISIBLE);
        AcctNotWhitelistedText.setVisibility(View.INVISIBLE);
        AcctAlreadyExistsText.setVisibility(View.INVISIBLE);

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
                AcctNotWhitelistedText.setVisibility(View.INVISIBLE);
                AcctAlreadyExistsText.setVisibility(View.INVISIBLE);


                //do action on click
                String email = UsernameField.getText().toString(); //email input from user
                String password = PasswordField.getText().toString(); //password input from user
                String confirmedPassword = ConfirmPasswordField.getText().toString(); //confirmed password input from user
                boolean inputIsGood = true; //keeps track of when the input validates some part of the criteria for valid input

                if(email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()){ //check if any fields are empty
                    //don't send, make user re-enter input
                    BlankFieldText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }
                else if(!password.equals(confirmedPassword)){ //check if passwords match
                    Log.d("AcctCreation","Password Mismatch");
                    PasswordMismatchText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }
                //check to see if input password is within the required password length requirements and has the correct character types in it
                else if(!ContainsReqCharTypes(password, true)||!InputLengthIsGood(password, true)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);
                    //shows refer to password requirements if input length is bad
                    inputIsGood = false;
                }
                //check to see if input password is within the required password length requirements and has the correct character types in it
                else if(!ContainsReqCharTypes(email, false) || !InputLengthIsGood(email, false)){
                    AcctNotWhitelistedText.setVisibility(View.VISIBLE);

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
                    String salt = GenerateRandomSalt();
                    String saltAndPassword = salt.trim() + password.trim();

                    //had to be put into try catch
                    try {
                        digest = MessageDigest.getInstance("SHA-512");
                        //creates a byte rep of the salt and password combination
                        byte[] inBytes = digest.digest(saltAndPassword.getBytes());
                        //hashes each byte
                        StringBuilder createdHexRep = new StringBuilder();
                        //changes the byte array to a string representation of the hashed salt and password
                        for(int i = 0; i < inBytes.length; i++){
                            //https://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax
                            createdHexRep.append(String.format("%02X", inBytes[i]));
                        }
                        passwordInHex = createdHexRep.toString();

                    }
                    catch(java.security.NoSuchAlgorithmException e) {
                        Log.d("AcctCreation","error in finding hashing algorithm");
                        inputIsGood = false; //stops data from being entered into db
                    }


                    if(inputIsGood) {
                        final String[] UserAccountInfo = {email, passwordInHex, salt};

                        //https://stackoverflow.com/questions/58767733/android-asynctask-api-deprecating-in-android-11-what-are-the-alternatives
                        //can't run network tasks on main thread, so we use an executor to do this task
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        final Handler handler = new Handler(Looper.getMainLooper());

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                //try to create the account
                                final String result = SendData(UserAccountInfo[0], UserAccountInfo[1], UserAccountInfo[2]);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //if the account was created
                                        if(result.contains("created")) {
                                            //change screen to confirmation email sent page
                                            Log.d("AcctCreation","Account successfully created");
                                            startActivity(new Intent(AccountCreation.this, EmailVerifSent.class));
                                        }
                                        else if(result.contains("acct_exists")){
                                            //set message account already exists
                                            AcctAlreadyExistsText.setVisibility(View.VISIBLE);
                                            Log.d("AcctCreation","Account already exists");
                                        }
                                        else if(result.contains("not_whitelisted")){
                                            //set message email not whitelisted
                                            AcctNotWhitelistedText.setVisibility(View.VISIBLE);
                                            Log.d("AcctCreation", "Email is not whitelisted");
                                        }
                                        else{
                                            //should not trigger
                                            Log.d("AcctCreation", "Unexpected return from server");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    //sends the data to the server so that the server can try to create the account
    private String SendData(String email, String password, String salt){
        StringBuilder queryResult = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(ACCT_CREATION_LINK);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("salt", "UTF-8") + "=" + URLEncoder.encode(salt, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            Log.d("AcctCreation","Data Written to Server");

            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";

            while((line = reader.readLine()) != null) {
                queryResult.append(line + "\n");
            }
            writer.close();
            conn.disconnect();
        }
        catch (java.net.MalformedURLException malformedURLException){
            Log.d("AcctCreation","Bad URL");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("AcctCreation","Could not encode data");
        }
        catch (java.io.IOException ioException){
            Log.d("AcctCreation","Could not open connection");
        }

        return queryResult.toString();
    }
}
