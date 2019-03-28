package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences SharedPrefs;
    private SharedPreferences.Editor Editor;
    User CurrentUser;

    EditText Username, Password;
    Switch KeepMeLoggedIn;

    TextWatcher textchange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ShowSubmitButton();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor = SharedPrefs.edit();

        InitToolBar();
        SetupUserCard();
        SetupAppCard();
        SetupNavCard();
        SetupTimeCard();
    }

    public void SetupUserCard() {
        int UserID = SharedPrefs.getInt(getString(R.string.SP_UserId), -1);
        CurrentUser = SQLite.select().from(User.class).where(User_Table.UserId.eq(UserID)).querySingle();

        Username = findViewById(R.id.username_settings_activity_TextInputEditText);
        Password = findViewById(R.id.password_settings_activity_TextInputEditText);
        KeepMeLoggedIn = findViewById(R.id.UserLogIn_settings_activity_Switch);

        Username.setText(CurrentUser.getUserName());
        Username.addTextChangedListener(textchange);
        Password.setText(CurrentUser.getPassword());
        Password.addTextChangedListener(textchange);
        KeepMeLoggedIn.setChecked(SharedPrefs.getBoolean(getString(R.string.SP_Toggle), false));
        KeepMeLoggedIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ShowSubmitButton();
            }
        });
    }

    public void SetupAppCard() {

        Spinner spinner = (Spinner) findViewById(R.id.Colourblind_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colourblind, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
     //   Switch colorblind = findViewById(R.id.Colorblind_Spinner);
     //   colorblind.setChecked(SharedPrefs.getBoolean(getString(R.string.SP_ColorBlindMode), false));
     //   colorblind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      //      @Override
       //     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       //         Editor.putBoolean(getString(R.string.SP_ColorBlindMode), isChecked);
         //       Editor.commit();
        //    }
       // });
    }

    public class SpinnerActivity extends SettingsActivity implements AdapterView.OnItemSelectedListener {
    //...

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            //shared preferences set
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public void SetupNavCard() {
        Switch lift = findViewById(R.id.lift_settings_activity_switch);
        lift.setChecked(SharedPrefs.getBoolean(getString(R.string.SP_UseLift), false));
        lift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Editor.putBoolean(getString(R.string.SP_UseLift), isChecked);
                Editor.commit();
            }
        });

        Switch room = findViewById(R.id.room_settings_activity_switch);
        room.setChecked(SharedPrefs.getBoolean(getString(R.string.SP_DisplayRoom), false));
        room.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Editor.putBoolean(getString(R.string.SP_DisplayRoom), isChecked);
                Editor.commit();
            }
        });

        Switch floor = findViewById(R.id.floor_settings_activity_switch);
        floor.setChecked(SharedPrefs.getBoolean(getString(R.string.Sp_DisplayFloor), false));
        floor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Editor.putBoolean(getString(R.string.Sp_DisplayFloor), isChecked);
                Editor.commit();
            }
        });
    }

    public void SetupTimeCard() {
        Button button = findViewById(R.id.reset_settings_activity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User_Session> sessions = SQLite.select().from(User_Session.class).where(User_Session_Table.User.eq(CurrentUser.getUserId())).queryList();
                for (User_Session u : sessions) {
                    u.delete();
                }
            }
        });
    }

    public void ShowSubmitButton() {
        Button button = findViewById(R.id.acceptchanges_settings_activity_button);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.setUserName(Username.getText().toString());
                CurrentUser.setPassword(Password.getText().toString());
                CurrentUser.save();

                Editor.putBoolean(getString(R.string.SP_Toggle), KeepMeLoggedIn.isChecked());
                Editor.commit();
                v.setVisibility(View.GONE);
            }
        });
    }

    public void InitToolBar() {
        Toolbar bar = findViewById(R.id.settings_activity_toolbar);
        bar.setTitle("Settings");
        setSupportActionBar(bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
