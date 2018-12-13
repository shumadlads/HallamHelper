package com.shumadlads.hallamhelper.hallamhelper;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.BottomSheetDialogFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FloatingActionButton fab = findViewById(R.id.fab);
            switch (item.getItemId()) {
                case R.id.action_navigate:
                    toolbar.setTitle("Navigate");
                    fragment = new Navigate();
                    loadFragment(fragment);
                    fab.setVisibility(View.INVISIBLE);
                    return true;

                case R.id.action_timetable:
                    toolbar.setTitle("Timetables");
                    fragment = new Timetables();
                    loadFragment(fragment);
                    fab.setVisibility(View.VISIBLE);
                    return true;

                case R.id.action_slack:
                    toolbar.setTitle("Don't Slack!");
                    fragment = new StopSlack();
                    loadFragment(fragment);
                    fab.setVisibility(View.INVISIBLE);
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
        UserName_EditText = (EditText)findViewById(R.id.UserNameEditText);
        Submit_Button = (Button)findViewById(R.id.SubmitUserButton);
        AddNewButton = (Button)findViewById(R.id.AddNewClass) ;
        TimeTable_ListView = (ListView) findViewById(R.id.TimeTable_ListView);
        SetModules();
        AddNewClass();
    }

    public void AddNewClass(){
        AddNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Room room =  SQLite.select().from(Room.class).where(Room_Table.RoomName.eq("9120")).querySingle();
                Module module =  SQLite.select().from(Module.class).where(Module_Table.ModuleName.eq("3D GAMES PROTOTYPING (LONG1 AF-2018/9)")).querySingle();
                Session newSession = new Session();
                newSession.setClassType("IT LABS");
                newSession.setDate("2/01/2019");
                newSession.setStartTime("10:00");
                newSession.setEndTime("12:00");
                newSession.setRoom(room);
                newSession.setModule(module);
                newSession.save();
                FillList();
            }
        });
    }


    public void SetModules(){
        Submit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillList();
            }
        });
    }

    public void FillList(){
        String name = UserName_EditText.getText().toString();
        User user = SQLite.select().from(User.class).where(User_Table.UserName.eq(name)).querySingle();
        ArrayList<TimeTableDispayModel> TimeTable = new ArrayList<TimeTableDispayModel>();
        if(user != null){
            CurrentUser=  user.getUserId();
            TimeTable = FindUserModules();
        }
        TimeTableDisplayAdapter adapter = new TimeTableDisplayAdapter(getApplicationContext(), TimeTable);
        TimeTable_ListView.setAdapter(adapter);
    }



    public  ArrayList<TimeTableDispayModel> FindUserModules(){
        ArrayList<TimeTableDispayModel> timeTable = new ArrayList<TimeTableDispayModel>();
        List<User_Module> userModules = SQLite.select().from(User_Module.class).where(User_Module_Table.User.eq(CurrentUser)).queryList();
        for (User_Module userModule :userModules) {
            userModule.getModule().load();
            Module currentModule = userModule.getModule();
            for (Session sesh :currentModule.getSessions()){
                sesh.getRoom().load();
                Room currentRoom = sesh.getRoom();
                timeTable.add(new TimeTableDispayModel(
                        currentModule.getModuleName(),
                        currentModule.getModuleLeader(),
                        sesh.getClassType(),
                        sesh.getDate(),
                        sesh.getStartTime(),
                        sesh.getEndTime(),
                        currentRoom.getRoomName()
                ));
        }}
       return timeTable;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
