package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.input_email);
        final EditText password = (EditText) findViewById(R.id.input_password);
        final Button button = (Button) findViewById(R.id.btn_signup);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    //correct password
                }
                //wrong password


            }
        });

    }
    }
