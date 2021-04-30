package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

//This activity is used for inheritance only, it should not be accessible by the user in the final app

public abstract class BasicLoginFunctionality extends AppCompatActivity {

    private final String GET_SALT_LINK = "https://medusa.mcs.uvawise.edu/~jdl8y/getSalt.php"; //link for the php file used to retrieve a user's salt
    final int MAX_PASSWORD_LENGTH = 32; //maximum allowed password length
    final int MIN_PASSWORD_LENGTH = 12; //minimum allowed password length
    final int MAX_EMAIL_LENGTH = 32; //maximum allowed length for an input email
    final int CONFIRM_CODE_LENGTH = 6; //required length for a code input into the confirm field
    final String POSSIBLE_SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; //list of characters used to generate a salt
    final ArrayList<Character> ACCEPTED_SPECIAL_CHARS = new ArrayList<Character>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '.', '?', '-', '_')); //special characters allowed in an input password

    //Grabs the salt from the database for the user so we can rehash their password
    protected String GetSalt(String email){
        StringBuilder salt = new StringBuilder();
        URL url = null;
        HttpsURLConnection conn = null;
        String data = null;
        BufferedReader reader = null;

        try {
            //https://www.tutorialspoint.com/android/android_php_mysql.htm
            //create a url object and open a connection to the specified link
            url = new URL(GET_SALT_LINK);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //tries to encode the user's data
            data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(data); //send the user's data
            writer.flush();

            //can't get input stream
            //read returned message from server
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";

            while((line = reader.readLine()) != null) {
                salt.append(line + "\n");
            } //salt will either be empty or contain an error message if no salt was found

            writer.close();
            conn.disconnect();
        }
        catch (java.net.MalformedURLException malformedURLException){
            Log.d("BasicLoginFunctionality","Bad url.");
        }
        catch(java.io.UnsupportedEncodingException unsupportedEncodingException){
            Log.d("BasicLoginFunctionality","Could not encode data.");
        }
        catch (java.io.IOException ioException){
            Log.d("BasicLoginFunctionality","Could not open connection");
        }

        return salt.toString(); //true if account was created
    }

    //checks to see if input has the required chars in it, isPassword is true for checking the password, false for the email address
    //password requires 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character
    protected boolean ContainsReqCharTypes(String input, boolean isPassword){

        //check the password
        if(isPassword){
            boolean containsLowerAlpha = false;
            boolean containsUpperAlpha = false;
            boolean containsNumber = false;
            boolean containsSpecialChar = false;
            boolean containsInvalidChar = false;

            //check for each needed type of char, check each position
            for(int i = 0; i < input.length(); i++){
                if(Character.isLowerCase(input.charAt(i))){
                    containsLowerAlpha = true;
                }
                else if(Character.isUpperCase(input.charAt(i))){
                    containsUpperAlpha = true;
                }
                else if(Character.isDigit(input.charAt(i))){
                    containsNumber = true;
                }
                else if(ACCEPTED_SPECIAL_CHARS.contains(input.charAt(i))){
                    containsSpecialChar = true;
                }
                else{
                    containsInvalidChar = true;
                }
            }

            //if the password meets all char requirements and doesn't contain an invalid char
            if(containsLowerAlpha && containsNumber && containsSpecialChar && containsUpperAlpha && !containsInvalidChar){
                return true;
            }
            else{
                Log.d("BasicLoginFunctionality","Missing Required Char Type(s) or Invalid Char");
                return false;
            }

        }

        //check the email
        else{
            if(input.contains("@uvawise.edu") || input.contains("@mcs.uvawise.edu") || input.contains("@virginia.edu")){
                return true;
            }
            else{
                Log.d("BasicLoginFunctionality","Missing Correct Email Domain");
                return false;
            }
        }
    }

    protected boolean InputLengthIsGood(String input, boolean isPassword){
        if(isPassword) {
            if(input.length() >= MIN_PASSWORD_LENGTH && input.length() <= MAX_PASSWORD_LENGTH) {
                return true;
            }
            else{
                Log.d("BasicLoginFunctionality","Invalid Input Length");
                return false;
            }
        }

        else{
            if(input.length() <= MAX_EMAIL_LENGTH) {
                return true;
            }
            else{
                Log.d("BasicLoginFunctionality","Invalid Input Length");
                return false;
            }
        }
    }

    //generate a random salt value of length 8 to use for the password hash
    protected String GenerateRandomSalt(){
        StringBuilder salt = new StringBuilder();
        Random rand = new Random();
        //generate a hash 8 characters long, with each char from the possibleSaltChars characters
        for(int i = 0; i < 8; i++){
            salt.append(POSSIBLE_SALT_CHARS.charAt(rand.nextInt(POSSIBLE_SALT_CHARS.length())));
        }

        return salt.toString();
    }
}