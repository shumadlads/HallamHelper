package com.shumadlads.hallamhelper.hallamhelper;


import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room_Table;


public class MainActivity extends AppCompatActivity {

    public static final int TIMETABLE_FRAGMENT = 0;
    public static final int MAP_FRAGMENT = 3;
    public static final int NAVIAGTE_FRAGMENT = 1;
    public static final int SLACK_FRAGMENT = 2;

    private SharedPreferences SharedPrefs;
    private SharedPreferences.Editor Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor = SharedPrefs.edit();

        // To change theme just put your theme id.



        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //For night mode theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //For day mode theme
        setContentView(R.layout.main_activity);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            switch (extras.getInt("LoadDefaultFragment")) {
                case TIMETABLE_FRAGMENT:
                    loadFragment(new TimetableFragment());
                    break;
                case MAP_FRAGMENT:
                    Intent intent = new Intent(this, MapActivity.class);
                    int start = extras.getInt("StartId");
                    if (start == -1) {
                        start = 9099; //remove later
                    } else {
                        Room startroom = SQLite.select().from(Room.class).where(Room_Table.RoomId.eq(start)).querySingle();
                        start = Integer.parseInt(startroom.getRoomName());
                    }
                    int target = extras.getInt("TargetId");
                    Room targetroom = SQLite.select().from(Room.class).where(Room_Table.RoomId.eq(target)).querySingle();
                    Room startroom = SQLite.select().from(Room.class).where(Room_Table.RoomId.eq(start)).querySingle();
                    intent.putExtra("RoomTo", Integer.parseInt(targetroom.getRoomName()));
                    intent.putExtra("RoomFrom", start);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    break;
                default:
                    loadFragment(new StopTheSlackFragment());
            }
        } else
            loadFragment(new NavigateFragment());
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.action_navigate:
                    fragment = new NavigateFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.action_timetable:
                    fragment = new TimetableFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.action_slack:
                    fragment = new StopTheSlackFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                Editor.putBoolean(getString(R.string.SP_Toggle), false);
                Editor.commit();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
