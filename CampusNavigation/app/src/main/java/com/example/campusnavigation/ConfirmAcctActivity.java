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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

public class ConfirmAcctActivity extends BasicLoginFunctionality {
    private final String CONFIRM_LINK = "https://medusa.mcs.uvawise.edu/~jdl8y/confirmAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_acct);

        setTitle("Confirm Account");

        final Button ConfirmAcctButton = (Button) findViewById(R.id.ConfirmAcctButton);
        final EditText ConfirmEmailField = (EditText) findViewById(R.id.ConfirmAcctEmailBox);
        final EditText ConfirmPasswordField = (EditText) findViewById(R.id.ConfirmAcctPasswordBox);
        final EditText ConfirmCodeField = (EditText) findViewById((R.id.UserConfirmCodeBox));
        final TextView BlankFieldsError = (TextView) findViewById((R.id.BlankFieldsConfirmError));
        final TextView AccountNotFoundError = (TextView) findViewById((R.id.AcctUnfoundConfirmError));
        final TextView IncorrectPasswordError = (TextView) findViewById((R.id.IncorrectPasswordConfirmError));
        final TextView IncorrectConfirmCodeError = (TextView) findViewById((R.id.IncorrectConfirmCodeError));
        final TextView IncorrectEmailPasswordLength = (TextView) findViewById((R.id.IncorrectEmailPasswordConfirmError));

        BlankFieldsError.setVisibility(View.INVISIBLE);
        AccountNotFoundError.setVisibility(View.INVISIBLE);
        IncorrectPasswordError.setVisibility(View.INVISIBLE);
        IncorrectConfirmCodeError.setVisibility(View.INVISIBLE);
        IncorrectEmailPasswordLength.setVisibility(View.INVISIBLE);

        ConfirmAcctButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BlankFieldsError.setVisibility(View.INVISIBLE);
                    AccountNotFoundError.setVisibility(View.INVISIBLE);
                    IncorrectPasswordError.setVisibility(View.INVISIBLE);
                    IncorrectConfirmCodeError.setVisibility(View.INVISIBLE);
                    IncorrectEmailPasswordLength.setVisibility(View.INVISIBLE);

                    final String email = ConfirmEmailField.getText().toString();
                    final String password = ConfirmPasswordField.getText().toString();
                    final String confirmationCode = ConfirmCodeField.getText().toString();
                    boolean inputIsGood = true;

                    //check if any fields are empty
                    if(email.isEmpty() || password.isEmpty() || confirmationCode.isEmpty()){
                        inputIsGood = false;
                        BlankFieldsError.setVisibility(View.VISIBLE);
                    }
                    //check if the input length of the email and password are good
                    else if(!InputLengthIsGood(email, false)||!InputLengthIsGood(password, true)){
                        inputIsGood = false;
                        IncorrectEmailPasswordLength.setVisibility(View.VISIBLE);
                    }
                    //check if the input length of the confirmation code is good
                    else if(confirmationCode.length() != CONFIRM_CODE_LENGTH){
                        inputIsGood = false;
                        IncorrectConfirmCodeError.setVisibility(View.VISIBLE);
                    }
                    //makes sure email and password have the required chars in them that they need to fit our criteria
                    else if(!ContainsReqCharTypes(email, false) || !ContainsReqCharTypes(password, true)){
                        inputIsGood = false;
                        IncorrectEmailPasswordLength.setVisibility(View.VISIBLE);
                    }

                    if(inputIsGood){
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
                                AccountNotFoundError.setVisibility(View.VISIBLE);
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
                                    Log.d("Confirmation","error in finding hashing algorithm");
                                    inputIsGood = false; //stops since the password can't be hashed
                                }
                                if (inputIsGood) {
                                    //attempt to login to an account
                                    final String[] UserAccountInfo = {email, passwordInHex, confirmationCode};

                                    ExecutorService executor = Executors.newSingleThreadExecutor();
                                    final Handler handler = new Handler(Looper.getMainLooper());

                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            //try to login
                                            final String result = SendDataConfirmAcct(UserAccountInfo[0], UserAccountInfo[1], UserAccountInfo[2]);

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //if login was successful
                                                    if (result.contains("success")) {
                                                        //change screen to home page
                                                        startActivity(new Intent(ConfirmAcctActivity.this, LoginActivity.class));
                                                    }
                                                    else if(result.contains("email_not_found")){
                                                        //account doesn't exist, set message
                                                        Log.d("Confirmation","Account doesn't exist");
                                                        AccountNotFoundError.setVisibility(View.VISIBLE);
                                                    }
                                                    else if(result.contains("wrong_password")){
                                                        //wrong password, set message
                                                        Log.d("Confirmation","Incorrect password");
                                                        IncorrectPasswordError.setVisibility(View.VISIBLE);
                                                    }
                                                    else if(result.contains("wrong_code")){
                                                        //wrong confirmation code, set message
                                                        Log.d("Confirmation","Could not confirm account");
                                                        IncorrectConfirmCodeError.setVisibility(View.VISIBLE);
                                                    }
                                                    else{
                                                        //should not fire
                                                        Log.d("Confirmation", "Unexpected return from server");
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        );
    }

    //tries to send data to confirm the user's account
    private String SendDataConfirmAcct(String email, String password, String confirmationCode){
        StringBuilder queryResult = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(CONFIRM_LINK);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(confirmationCode, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            //can't get input stream
            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";

            while((line = reader.readLine()) != null) {
                queryResult.append(line + "\n");
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

        return queryResult.toString();
    }
}

