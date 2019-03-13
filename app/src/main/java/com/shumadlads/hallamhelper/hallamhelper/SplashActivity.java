package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    boolean isLoggedin = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(isLoggedin == false)

        {Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();}
            else
        {Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();}
        }
        }




