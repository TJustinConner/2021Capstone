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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class AccountCreation extends BasicLoginFunctionality {

    final String possibleSaltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final String link = "https://medusa.mcs.uvawise.edu/~jdl8y/accountCreation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        setTitle("Account Creation");

        final Button CreateAccountButton = (Button) findViewById(R.id.CreateAcctButton);
        final EditText UsernameField = (EditText) findViewById(R.id.NewAcctEmailBox);
        final EditText PasswordField = (EditText) findViewById(R.id.NewAcctPasswordBox);
        final EditText ConfirmPasswordField = (EditText) findViewById((R.id.ConfirmPasswordNewBox));
        final TextView PasswordReqsLink = (TextView) findViewById((R.id.PasswordReqsLink));
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
                else if(!InputLengthIsGood(email, password)){
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
                                final boolean result = SendData(UserAccountInfo[0], UserAccountInfo[1], UserAccountInfo[2]);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //if the account was created
                                        if (result) {
                                            //change screen to confirmation email sent page
                                            startActivity(new Intent(AccountCreation.this, EmailVerifSent.class));
                                        } else {
                                            //set message account could not be created
                                            Log.d("AcctCreation","Account Couldn't Be Created");
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

    //sends the data to the server so that the server can try to create the account
    private boolean SendData(String email, String password, String salt){
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
            data += "&" + URLEncoder.encode("salt", "UTF-8") + "=" + URLEncoder.encode(salt, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            Log.d("AcctCreation","Data Written to Server");

            //can't get input stream
            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";

            while((line = reader.readLine()) != null) {
                builder.append(line + "\n");
                //break;
            }

            System.out.println("output:");
            System.out.println(toString());

            String result = builder.toString();

            //see if the server returned that the account was created
            if(result.contains("true")){
                accountCreated = true;
                Log.d("AcctCreation","Account Successfully Created");
            }
            else if(result.contains("false")){
                accountCreated = false;
            }

            writer.close();
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

        return accountCreated; //true if account was created
    }

    //generate a random salt value of length 8 to use for the password hash
    private String GenerateRandomSalt(){
        StringBuilder salt = new StringBuilder();
        Random rand = new Random();
        //generate a hash 8 characters long, with each char from the possibleSaltChars characters
        for(int i = 0; i < 8; i++){
            salt.append(possibleSaltChars.charAt(rand.nextInt(possibleSaltChars.length())));
        }

        return salt.toString();
    }
}
