package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Module_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableDispayModel;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableDisplayAdapter;

import java.util.ArrayList;
import java.util.List;


public class Timetables extends Fragment {

    FloatingActionButton fab;
    static int CurrentUser = 1;

    ListView TimeTableList;
    TimeTableDisplayAdapter TimeTableAdapter;
    ArrayList<TimeTableDispayModel> TimeTable;
    ImageView SessionImage;
    TextView SessionText;

    public Timetables() {
        // Required empty public constructor
    }

    public static Timetables newInstance(String param1, String param2) {
        Timetables fragment = new Timetables();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FillList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetables, container, false);
        SessionImage = (ImageView)view.findViewById(R.id.AddSession_ImageView);
        SessionText = (TextView) view.findViewById(R.id.AddSession_TextView);
        TimeTableList = (ListView) view.findViewById(R.id.TimeTable_ListView);
        if(TimeTable == null || TimeTable.isEmpty()){
            TimeTableList.setVisibility(View.INVISIBLE);
            SessionText.setVisibility(View.VISIBLE);
            SessionImage.setVisibility(View.VISIBLE);
        }
        else{
            TimeTableList.setVisibility(View.VISIBLE);
            SessionText.setVisibility(View.INVISIBLE);
            SessionImage.setVisibility(View.INVISIBLE);
        }
        TimeTableList.setAdapter(TimeTableAdapter);
        return view;
    }


    public void FillList() {
        User user = SQLite.select().from(User.class).where(User_Table.UserId.eq(CurrentUser)).querySingle();
        TimeTable = new ArrayList<TimeTableDispayModel>();
        if (user != null) {
            TimeTable = FindUserModules();
        }
        TimeTableAdapter = new TimeTableDisplayAdapter(getActivity().getApplicationContext(), TimeTable);
    }


    public ArrayList<TimeTableDispayModel> FindUserModules() {
        ArrayList<TimeTableDispayModel> timeTable = new ArrayList<TimeTableDispayModel>();
        List<User_Module> userModules = SQLite.select().from(User_Module.class).where(User_Module_Table.User.eq(CurrentUser)).queryList();
        for (User_Module userModule : userModules) {
            userModule.getModule().load();
            Module currentModule = userModule.getModule();
            for (Session sesh : currentModule.getSessions()) {
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
            }
        }
        return timeTable;
    }


}