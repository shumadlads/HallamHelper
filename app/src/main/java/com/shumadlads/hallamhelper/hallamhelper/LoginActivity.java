package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences SharedPrefs;
    private SharedPreferences.Editor Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText username = (EditText) findViewById(R.id.input_email);
        final EditText password = (EditText) findViewById(R.id.input_password);
        final Button button = (Button) findViewById(R.id.btn_signup);
        final TextView txt = (TextView) findViewById(R.id.error);
        final CheckBox checkbox = findViewById(R.id.stayloggedin);
        txt.setVisibility(View.GONE);

        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor = SharedPrefs.edit();

        boolean loggedIn = SharedPrefs.getBoolean(getString(R.string.SP_Toggle), false);
        int UserId = SharedPrefs.getInt(getString(R.string.SP_UserId), -1);

        if (loggedIn && UserId != -1) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User user = SQLite.select().from(User.class).where(User_Table.UserName.eq(username.getText().toString())).and(User_Table.Password.eq(password.getText().toString())).querySingle();

                if (user != null) {
                    Editor.putInt(getString(R.string.SP_UserId), user.getUserId());
                    Editor.putBoolean(getString(R.string.SP_Toggle), checkbox.isChecked());

                    Editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    //correct password
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    txt.setVisibility(View.VISIBLE);
                }


                //wrong password


            }
        });

    }
}
