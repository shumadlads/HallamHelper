package com.shumadlads.hallamhelper.hallamhelper;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewParent;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableRecyclerViewListener;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableRecyclerViewModel;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimeTableRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TimetableFragment extends Fragment implements TimeTableRecyclerViewListener {

    FloatingActionButton fab;
    static int CurrentUser = 1;
    SimpleDateFormat DateFormat;
    RecyclerView TimeTableList;
    TimeTableRecyclerViewAdapter TimeTableAdapter;
    ArrayList<TimeTableRecyclerViewModel> TimeTable;

    public TimetableFragment() {
        // Required empty public constructor
    }

    public static TimetableFragment newInstance(String param1, String param2) {
        TimetableFragment fragment = new TimetableFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_timetables, container, false);
        DateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        Toolbar bar = (Toolbar) view.findViewById(R.id.TopBar);
        bar.setTitle("TimeTable");
        ((AppCompatActivity) getActivity()).setSupportActionBar(bar);

        FloatingActionButton fab = view.findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
                bottomSheet.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        TimeTableList = view.findViewById(R.id.collapsing_toolbar_recycler_view);
        TimeTableList.setAdapter(TimeTableAdapter);
        TimeTableList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        return view;
    }


    public void FillList() {
        User user = SQLite.select().from(User.class).where(User_Table.UserId.eq(CurrentUser)).querySingle();
        TimeTable = new ArrayList<TimeTableRecyclerViewModel>();
        if (user != null) {
            TimeTable = FindUserSessions();
        }
        TimeTableAdapter = new TimeTableRecyclerViewAdapter(getActivity().getApplicationContext(), TimeTable, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timetable_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public ArrayList<TimeTableRecyclerViewModel> FindUserSessions() {
        ArrayList<TimeTableRecyclerViewModel> timeTable = new ArrayList<TimeTableRecyclerViewModel>();
        List<User_Session> user_sessions = SQLite.select().from(User_Session.class).where(User_Session_Table.User.eq(CurrentUser)).queryList();

        for (User_Session user_session : user_sessions) {
            user_session.getSession().load();
            Session session = user_session.getSession();
            session.getModule().load();
            session.getRoom().load();
            timeTable.add(new TimeTableRecyclerViewModel(
                    session.getSessionId(),
                    session.getModule().getModuleNickname(),
                    session.getType(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getRoom().getRoomName()
            ));
        }
        return timeTable;
    }


    @Override
    public void OnSessionClick(int pos) {
        Intent intent = new Intent(getActivity().getApplicationContext(), TimeTableDetailActivity.class);
        intent.putExtra("Id", TimeTable.get(pos).getId());

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}