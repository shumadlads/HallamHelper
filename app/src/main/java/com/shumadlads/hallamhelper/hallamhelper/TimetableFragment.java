package com.shumadlads.hallamhelper.hallamhelper;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.DatePicker;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewAdapter;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewListener;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewModel;

import java.util.ArrayList;
import java.util.List;


public class TimetableFragment extends Fragment implements TimetableRecyclerViewListener {

    private static int CurrentUser = 1;
    private TimetableRecyclerViewAdapter TimeTableAdapter;
    private ArrayList<TimetableRecyclerViewModel> TimeTable;

    public TimetableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FillList();
    }

    @Override
    public void onResume() {
        super.onResume();
        TimeTableAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.timetable_fragment, container, false);
        InitToolBar(view);
        InitRecyclerView(view);
        InitFAB(view);
        return view;
    }

    public void InitToolBar(View view) {
        Toolbar bar = view.findViewById(R.id.timetable_fragment_Toolbar);

        bar.setTitle("TimeTable");
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(bar);

    }

    public void InitRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.timetable_fragment_RecyclerView);

        recyclerView.setAdapter(TimeTableAdapter);
        if (getActivity() != null)
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

    }

    public void InitFAB(View view) {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.timetable_fragment_FloatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSessionBottomSheet bottomSheet = new AddSessionBottomSheet();
                if (getActivity() != null)
                    bottomSheet.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");
            }
        });
    }


    public void FillList() {
        TimeTable = new ArrayList<>();
        User user = SQLite.select().from(User.class).where(User_Table.UserId.eq(CurrentUser)).querySingle();
        if (user != null)
            TimeTable = FindUserSessions();

        if (getActivity() != null)
            TimeTableAdapter = new TimetableRecyclerViewAdapter(getActivity().getApplicationContext(), TimeTable, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timetable_recyclerview_menu, menu);
        MenuItem search = menu.findItem(R.id.action_item_one);
        if (search != null) {
            SearchView searchView = (SearchView) search.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    TimeTableAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_one:
                break;
            case R.id.action_item_two:
                if (getActivity() != null) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                }
                            }, 0, 0, 0);
                    datePickerDialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public ArrayList<TimetableRecyclerViewModel> FindUserSessions() {
        ArrayList<TimetableRecyclerViewModel> timeTable = new ArrayList<>();
        List<User_Session> user_sessions = SQLite.select().from(User_Session.class).where(User_Session_Table.User.eq(CurrentUser)).queryList();

        for (User_Session user_session : user_sessions) {
            user_session.getSession().load();
            Session session = user_session.getSession();
            session.getModule().load();
            session.getRoom().load();
            timeTable.add(new TimetableRecyclerViewModel(
                    session.getSessionId(),
                    session.getModule().getModuleNickname(),
                    session.getModule().getModuleImage(),
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
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity().getApplicationContext(), TimetableDetailActivity.class);
            intent.putExtra("Id", TimeTable.get(pos).getId());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
    }
}