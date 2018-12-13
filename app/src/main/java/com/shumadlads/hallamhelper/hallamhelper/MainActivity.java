package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
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

    public int CurrentUser;
    public Button Submit_Button;
    public EditText UserName_EditText;
    public ListView TimeTable_ListView;
    public Button AddNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
