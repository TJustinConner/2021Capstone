//This activity takes in the users email, checks to see if the input email is in the database and confirmed, and if so send the user to
//input a confirmation code and a new password

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

public class ResetPasswordActivity extends BasicLoginFunctionality {

    private final String LINK_RESET_PASSWORD = "https://medusa.mcs.uvawise.edu/~jdl8y/generateConfirmationCode.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        final Button SendEmailButton = (Button) findViewById(R.id.SendEmailResetButton);
        final EditText EmailToReset = (EditText) findViewById(R.id.EmailBoxReset);
        final TextView AccountNotFoundError = (TextView) findViewById(R.id.ResetAcctUnfoundError);
        final TextView AccountUnconfirmedError = (TextView) findViewById(R.id.ResetAcctUnconfirmedError);
        final TextView BlankFieldError = (TextView) findViewById(R.id.ResetBlankFieldError);

        AccountNotFoundError.setVisibility(View.INVISIBLE);
        AccountUnconfirmedError.setVisibility(View.INVISIBLE);
        BlankFieldError.setVisibility(View.INVISIBLE);

        SendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = EmailToReset.getText().toString(); // get email input by user
                boolean inputIsGood = true; //keeps track of when the input validates some part of the criteria for valid input

                AccountNotFoundError.setVisibility(View.INVISIBLE);
                AccountUnconfirmedError.setVisibility(View.INVISIBLE);
                BlankFieldError.setVisibility(View.INVISIBLE);

                //check input to see if it matches our requirements for a valid email
                if(email.isEmpty()){
                    inputIsGood = false;
                    BlankFieldError.setVisibility(View.VISIBLE);
                }
                if(!ContainsReqCharTypes(email, false)){
                    inputIsGood = false;
                    AccountNotFoundError.setVisibility(View.VISIBLE);
                }
                if(!InputLengthIsGood(email, false)){
                    inputIsGood = false;
                    AccountNotFoundError.setVisibility(View.VISIBLE);
                }


                if(inputIsGood){
                    //https://stackoverflow.com/questions/58767733/android-asynctask-api-deprecating-in-android-11-what-are-the-alternatives
                    //can't run network tasks on main thread, so we use an executor to do this task
                    final ExecutorService emailCheckExecutor = Executors.newSingleThreadExecutor();

                    //have to use a callable to return a result and use a future task to store the result
                    FutureTask task = new FutureTask(new Callable<String>() {
                        @Override
                        public String call() throws Exception {

                            final String result = SendDataCheckEmail(email);

                            return result;
                        }
                    });

                    emailCheckExecutor.execute(task); //run the task, try to see if the account exists and is confirmed
                    //if the email exists and is confirmed, the script
                    //sets a new confirmation code and sends that confirmation code to the email

                    StringBuilder resultBuilder = new StringBuilder();

                    try{
                        resultBuilder.append(task.get()); //get the result from the task
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Result: ");

                    if(resultBuilder.toString().contains("Account Does Not Exist")){
                        AccountNotFoundError.setVisibility(View.VISIBLE);
                        Log.d("PasswordResetCheckEmail","Account does not exist");
                    }
                    else if(resultBuilder.toString().contains("Account Not Confirmed")){
                        AccountUnconfirmedError.setVisibility(View.VISIBLE);
                        Log.d("PasswordResetCheckEmail","Account not confirmed");
                    }
                    else if(resultBuilder.toString().contains("Success")){
                        Intent toSetNewPassword = new Intent(ResetPasswordActivity.this, SetNewPasswordActivity.class);
                        toSetNewPassword.putExtra("email", email);
                        startActivity(toSetNewPassword);
                    }
                    else{
                        //this should not happen
                        Log.d("PasswordResetCheckEmail","Unexpected output from server");
                    }
                }
            }
        });
    }

    //sends the data to the server to try and send a reset code to the email
    private String SendDataCheckEmail(String email){
        StringBuilder queryResult = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;
        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(LINK_RESET_PASSWORD);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            Log.d("PasswordResetCheckEmail","Data Written to Server");

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
            Log.d("PasswordResetCheckEmail","Bad URL");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("PasswordResetCheckEmail","Could not encode data");
        }
        catch (java.io.IOException ioException){
            Log.d("PasswordResetCheckEmail","Could not open connection");
        }

        return queryResult.toString(); //true if account was created
    }
}