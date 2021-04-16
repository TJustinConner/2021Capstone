package com.example.campusnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends BasicLoginFunctionality {

    private final String loginLink = "https://medusa.mcs.uvawise.edu/~jdl8y/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Account Log In");

        final Button LoginButton = (Button) findViewById(R.id.CreateAcctButton);
        final EditText EmailLoginField = (EditText) findViewById(R.id.NewAcctEmailBox);
        final EditText PasswordLoginField = (EditText) findViewById(R.id.NewAcctPasswordBox);
        final TextView NewAccountLink = (TextView) findViewById((R.id.newAccountLink));
        final TextView ConfirmAccountLink = (TextView) findViewById((R.id.confirmAccountLink));

        NewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes user to password requirements activity
                Intent ToAccountCreation = new Intent(v.getContext(), AccountCreation.class);
                startActivity(ToAccountCreation);
            }
        });

        ConfirmAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes user to password requirements activity
                Intent ToAccountConfirmation = new Intent(v.getContext(), ConfirmAcctActivity.class);
                startActivity(ToAccountConfirmation);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = EmailLoginField.getText().toString();
                final String password = PasswordLoginField.getText().toString();

                boolean inputIsGood = true;

                //check if any fields are empty
                if (email.isEmpty() || password.isEmpty()) {
                    inputIsGood = false;
                }

                //check to see if input length is in our set bounds
                else if (!InputLengthIsGood(email, password)) {
                    inputIsGood = false;
                }

                //check to see if input length is in our set bounds
                else if (!ContainsReqCharTypes(email, false) || !ContainsReqCharTypes(password, true)){
                    inputIsGood = false;
                }

                //take user to email sent page if all input was good
                if (inputIsGood) {

                    //https://stackoverflow.com/questions/58767733/android-asynctask-api-deprecating-in-android-11-what-are-the-alternatives
                    //can't run network tasks on main thread, so we use an executor to do this task
                    final ExecutorService saltExecutor = Executors.newSingleThreadExecutor();

                    //have to use a callable to return a result and use a future task to store the result
                    FutureTask task = new FutureTask(new Callable<String>() {
                        @Override
                        public String call() throws Exception {

                            final String returnedSalt = GetSalt(email);

                            return returnedSalt;
                        }
                    });

                    saltExecutor.execute(task); //run the task, try to get the salt

                    StringBuilder salt = new StringBuilder();

                    try {
                        salt.append(task.get()); //get the salt from the task
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //check if the salt was returned
                    if(salt.toString().contains("Account Does Not Exist") || salt.toString().isEmpty()){
                        inputIsGood = false;
                    }

                    if (inputIsGood) {
                        //hash password
                        MessageDigest digest = null;
                        String passwordInHex = null;
                        String saltAndPassword = salt.toString().trim() + password.trim();

                        try {
                            digest = MessageDigest.getInstance("SHA-512");
                            byte[] inBytes = digest.digest(saltAndPassword.getBytes());
                            //change from byte array to hex
                            StringBuilder createdHexRep = new StringBuilder();
                            for (int i = 0; i < inBytes.length; i++) {
                                //https://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax
                                createdHexRep.append(String.format("%02X", inBytes[i]));
                            }
                            passwordInHex = createdHexRep.toString();

                        } catch (java.security.NoSuchAlgorithmException e) {
                            Log.d("Login","error in finding hashing algorithm");
                            inputIsGood = false; //stops data from being entered into db
                        }
                        if (inputIsGood) {
                            //attempt to login to an account
                            final String[] UserAccountInfo = {email, passwordInHex};

                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            final Handler handler = new Handler(Looper.getMainLooper());

                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    //try to login
                                    final boolean result = SendDataLogin(UserAccountInfo[0], UserAccountInfo[1]);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //if login was successful
                                            if (result) {
                                                //change screen to home page
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            } else {
                                                //wrong password, set message
                                                Log.d("Login","Could not login");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    //tries to log in the user
    private boolean SendDataLogin(String email, String password){
        Boolean successfulSignIn = false;
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(loginLink);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            //can't get input stream
            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";

            while((line = reader.readLine()) != null) {
                builder.append(line + "\n");
                //break;
            }

            System.out.println(password);

            //see if the server returned that the user was logged in
            if(builder.toString().contains("true")){
                successfulSignIn = true;
            }

            writer.close();
        }
        catch (java.net.MalformedURLException malformedURLException){
            Log.d("Login","Bad url.");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("Login","Could not encode data.");
        }
        catch (java.io.IOException ioException){
            Log.d("Login","Could not open connection");
        }

        return successfulSignIn; //true if account was created
    }
}