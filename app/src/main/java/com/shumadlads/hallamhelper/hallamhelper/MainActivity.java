package com.shumadlads.hallamhelper.hallamhelper;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.BottomSheetDialogFragment;
import android.widget.ImageView;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Building;
import com.shumadlads.hallamhelper.hallamhelper.Models.Building_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Module_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableDispayModel;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableDisplayAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    FloatingActionButton fab;
    FloatingActionButton fabslack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        toolbar.setTitle("Navigate");
        loadFragment(new Navigate());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        FloatingActionButton fabSlack = findViewById(R.id.fabSlack);
        fabSlack.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        fabSlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FloatingActionButton fab = findViewById(R.id.fab);
            FloatingActionButton fabslack = findViewById(R.id.fabSlack);

            switch (item.getItemId()) {
                case R.id.action_navigate:
                    toolbar.setTitle("Navigate");
                    fragment = new Navigate();
                    loadFragment(fragment);
                    fab.setVisibility(View.INVISIBLE);
                    fabslack.setVisibility(View.INVISIBLE);
                    return true;

                case R.id.action_timetable:
                    toolbar.setTitle("Timetables");
                    fragment = new Timetables();
                    loadFragment(fragment);
                    fab.setVisibility(View.VISIBLE);
                    fabslack.setVisibility(View.INVISIBLE);
                    return true;

                case R.id.action_slack:
                    toolbar.setTitle("Don't Slack!");
                    fragment = new StopSlack();
                    loadFragment(fragment);
                    fab.setVisibility(View.INVISIBLE);
                    fabslack.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
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
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.routedebug:
                intent = new Intent(this, RouteNavigationTestActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
    }

}
