package com.example.campusnavigation;

import android.content.Intent;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

public class SetNewPasswordActivity extends BasicLoginFunctionality {
    private final String CHECK_CODE_LINK = "https://medusa.mcs.uvawise.edu/~jdl8y/checkCodeAgainstEmail.php";
    private final String SET_NEW_PASSWORD_LINK = "https://medusa.mcs.uvawise.edu/~jdl8y/setNewPassword.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        final EditText ResetPasswordBox = (EditText) findViewById(R.id.ResetPasswordBox);
        final EditText ConfirmResetPasswordBox = (EditText) findViewById(R.id.ConfirmResetPasswordBox);
        final EditText ResetCodeBox = (EditText) findViewById(R.id.ResetCodeBox);
        final Button SetNewPasswordButton = (Button) findViewById(R.id.SetNewPasswordButton);
        final TextView PasswordReqsLink = (TextView) findViewById((R.id.PasswordReqsLinkSetNew));
        final TextView PasswordMismatchError = (TextView) findViewById(R.id.PasswordMismatchSetNewError);
        final TextView BlankFieldsError = (TextView) findViewById(R.id.BlankFieldSetNewError);
        final TextView ConfirmCodeMismatchError = (TextView) findViewById(R.id.CodeMismatchSetNewError);
        final TextView MissingReqsError = (TextView) findViewById(R.id.PasswordMissingReqsSetNewError);

        StringBuilder holdEmail = new StringBuilder();

        //used to grab the email sent from the ResetPasswordActivity
        Bundle addedContents = getIntent().getExtras();
        if(addedContents != null){
            holdEmail.append(addedContents.getString("email"));
        }
        else{
            holdEmail.append("");
        }

        final String email = holdEmail.toString();

        BlankFieldsError.setVisibility(View.INVISIBLE);
        MissingReqsError.setVisibility(View.INVISIBLE);
        ConfirmCodeMismatchError.setVisibility(View.INVISIBLE);
        PasswordMismatchError.setVisibility(View.INVISIBLE);

        PasswordReqsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes user to password requirements activity
                Intent ToPasswordReqs = new Intent(v.getContext(), PasswordRequirementsPage.class);
                startActivity(ToPasswordReqs);
            }
        });

        SetNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BlankFieldsError.setVisibility(View.INVISIBLE);
                MissingReqsError.setVisibility(View.INVISIBLE);
                ConfirmCodeMismatchError.setVisibility(View.INVISIBLE);
                PasswordMismatchError.setVisibility(View.INVISIBLE);

                final String password = ResetPasswordBox.getText().toString();
                String confirmPassword = ConfirmResetPasswordBox.getText().toString();
                final String confirmCode = ResetCodeBox.getText().toString();
                boolean inputIsGood = true; //keeps track of when the input validates some part of the criteria for valid input

                if(password.isEmpty() || confirmPassword.isEmpty() || confirmCode.isEmpty()){
                    inputIsGood = false;
                    BlankFieldsError.setVisibility(View.VISIBLE);
                    Log.d("SetNewPassword","One of the fields is blank");
                }
                else if(!password.equals(confirmPassword)){
                    inputIsGood = false;
                    PasswordMismatchError.setVisibility(View.VISIBLE);
                    Log.d("SetNewPassword","Password Mismatch");
                }
                else if(!ContainsReqCharTypes(password, true)){
                    inputIsGood = false;
                    MissingReqsError.setVisibility(View.VISIBLE);
                    Log.d("SetNewPassword","Password doesn't contain required char types");
                }
                else if(!InputLengthIsGood(password, true)){
                    inputIsGood = false;
                    MissingReqsError.setVisibility(View.VISIBLE);
                    Log.d("SetNewPassword","Password length is invalid");
                }
                else if(confirmCode.length() != CONFIRM_CODE_LENGTH){
                    inputIsGood = false;
                    ConfirmCodeMismatchError.setVisibility(View.VISIBLE);
                    Log.d("SetNewPassword","Confirm code length is incorrect");
                }

                if(inputIsGood){
                    Log.d("SetNewPassword", "Input password was good");
                    //check if the confirm code is correct for the email stored
                    //https://stackoverflow.com/questions/58767733/android-asynctask-api-deprecating-in-android-11-what-are-the-alternatives
                    //can't run network tasks on main thread, so we use an executor to do this task
                    final ExecutorService emailCheckExecutor = Executors.newSingleThreadExecutor();

                    //have to use a callable to return a result and use a future task to store the result
                    FutureTask task = new FutureTask(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            boolean result = SendDataCheckCode(email, confirmCode);

                            return result;
                        }
                    });

                    emailCheckExecutor.execute(task); //run the task, try to see if the code matches the account

                    boolean isCorrectCode = false;

                    try{
                        isCorrectCode = (Boolean) task.get(); //get the result from the task
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //if the input is valid and the code input was correct, hash the password
                    if(inputIsGood && isCorrectCode){
                        //hash password
                        MessageDigest digest = null;
                        String passwordInHex = null;
                        final String salt = GenerateRandomSalt();
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

                            //set new hashed password and salt to email
                        }
                        catch(java.security.NoSuchAlgorithmException e) {
                            Log.d("SetNewPassword","error in finding hashing algorithm");
                            inputIsGood = false; //stops data from being entered into db
                        }

                        //if the password was set correctly, set the salt and password
                        if(inputIsGood) {
                            //check if the confirm code is correct for the email stored
                            ExecutorService executor = Executors.newSingleThreadExecutor();

                            String finalPasswordInHex = passwordInHex;
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    SendDataSetPassword(email, finalPasswordInHex, salt);
                                }
                            });

                            startActivity(new Intent(SetNewPasswordActivity.this, LoginActivity.class));
                        }
                    }
                    else{
                        ConfirmCodeMismatchError.setVisibility(View.VISIBLE);
                        Log.d("SetNewPassword", "incorrect confirmation code entered");
                    }
                }
            }
        });

    }

    //sends the data to the server so that the server can try to confirm that the code entered matches the account
    private boolean SendDataCheckCode(String email, String code){
        StringBuilder queryResult = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        boolean result = false;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(CHECK_CODE_LINK);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            Log.d("SetNewPassword","Data Written to Server");

            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";

            while((line = reader.readLine()) != null) {
                queryResult.append(line + "\n");
            }
            writer.close();
            conn.disconnect();
            if(queryResult.toString().contains("true")){
                result = true;
            }
        }
        catch (java.net.MalformedURLException malformedURLException){
            Log.d("SetNewPassword","Bad URL");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("SetNewPassword","Could not encode data");
        }
        catch (java.io.IOException ioException){
            Log.d("SetNewPassword","Could not open connection");
        }

        return result; //true if code matches account
    }

    //sends the data to the server so that the server can try to set the new password and salt
    private void SendDataSetPassword(String email, String password, String salt){
        StringBuilder queryResult = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(SET_NEW_PASSWORD_LINK);
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

            Log.d("SetNewPassword","Data Written to Server");

            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";

            while((line = reader.readLine()) != null) {
                queryResult.append(line + "\n");
            }

            writer.close();
            conn.disconnect();

            Log.d("SetNewPassword","Data Written to Server");
        }
        catch (java.net.MalformedURLException malformedURLException){
            Log.d("SetNewPassword","Bad URL");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("SetNewPassword","Could not encode data");
        }
        catch (java.io.IOException ioException){
            Log.d("SetNewPassword","Could not open connection");
        }
    }
}