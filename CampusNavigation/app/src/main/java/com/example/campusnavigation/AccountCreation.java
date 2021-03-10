package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountCreation extends AppCompatActivity {

    final ArrayList<Character> RESTRICTED_CHARS = new ArrayList<Character>(Arrays.asList('\'', ';', '\"', '/', '\\', ' ')); // need to check for --
    final ArrayList<String> RESTRICTED_STRINGS = new ArrayList<String>(Arrays.asList("--"));
    final ArrayList<Character> ACCEPTED_SPECIAL_CHARS = new ArrayList<Character>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '.', '?', '-', '_'));

    final int MAX_PASSWORD_LENGTH = 32;
    final int MIN_PASSWORD_LENGTH = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        setTitle("Account Creation");

        final Button CreateAccountButton = (Button) findViewById(R.id.CreateAccountButton);
        final EditText UsernameField = (EditText) findViewById(R.id.UserEmailAddressBox);
        final EditText PasswordField = (EditText) findViewById(R.id.UserPasswordBox);
        final EditText ConfirmPasswordField = (EditText) findViewById((R.id.UserConfirmPasswordBox));
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
                Intent currentIntent = getIntent(); //grabs the current activity in case it needs to be restarted

                PasswordMismatchText.setVisibility(View.INVISIBLE);
                BlankFieldText.setVisibility(View.INVISIBLE);
                MissingRequirementsText.setVisibility(View.INVISIBLE);

                //do action on click
                String email = UsernameField.getText().toString();
                String password = PasswordField.getText().toString();
                String confirmedPassword = ConfirmPasswordField.getText().toString();
                boolean inputIsGood = true;

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

                //check for bad characters to prevent SQL injection
                else if(!NoRestrictedChars(email) || !NoRestrictedChars(password)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }

                //check to see if password length is in our set bounds
                else if(!PasswordLengthIsGood(password)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }

                //makes sure email and password have the required chars in them that they need to fit our criteria
                else if(!ContainsReqCharTypes(email, false) || !ContainsReqCharTypes(password, true)){
                    MissingRequirementsText.setVisibility(View.VISIBLE);

                    inputIsGood = false;
                }

                //Here we would encode the data and check to see if the account has already been created and if the email is on the whitelist

                //if good, go to email sent to verify account page
                //otherwise stay on page, clear fields

                //take user to email sent page if all input was good
                if(inputIsGood) {
                    Intent ToEmailSent = new Intent(v.getContext(), EmailVerifSent.class);
                    startActivity(ToEmailSent);
                }
            }
        });
    }

    //checks if an input (email, password, whatever is passed) contains a restricted char
    private boolean NoRestrictedChars(String input){
        for(int i = 0; i < input.length(); i++) {
            if(RESTRICTED_CHARS.contains(input.charAt(i))) {
                Log.d("AcctCreation","Prohibited Character(s)");
                return false;
            }
        }

        for(int i = 0; i < RESTRICTED_STRINGS.size(); i++) {
            if(input.contains(RESTRICTED_STRINGS.get(i))){
                Log.d("AcctCreation","Prohibited String(s)");
                return false;
            }
        }

        return true;

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

    private boolean PasswordLengthIsGood(String password){
        if(password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH){
            return true;
        }
        else{
            Log.d("AcctCreation","Invalid Password Length");
            return false;
        }
    }
}