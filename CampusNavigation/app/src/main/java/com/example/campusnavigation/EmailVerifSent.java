package com.example.campusnavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EmailVerifSent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verif_sent);

        final TextView acctConfirmLink = (TextView) findViewById(R.id.acctConfirmLink);

        acctConfirmLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sends user to account confirm activity
                Intent ToAccountConfirmation = new Intent(v.getContext(), ConfirmAcctActivity.class);
                startActivity(ToAccountConfirmation);
            }
        });
    }


}