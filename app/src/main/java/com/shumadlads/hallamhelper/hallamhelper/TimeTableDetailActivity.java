package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session_Table;

public class TimeTableDetailActivity extends AppCompatActivity {

    TextView Title;
    TextView SubTitle;
    TextView SecondTitle;
    TextView Content;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetabledetail);

        InitToolBar();
        InitFields();
        FillFields();


    }

    public void InitFields() {
        Title = (TextView) findViewById(R.id.Title_TV);
        SubTitle = (TextView) findViewById(R.id.SubTitle_TV);
        SecondTitle = (TextView) findViewById(R.id.SecondTitle_TV);
        Content = (TextView) findViewById(R.id.MainContent_TV);
    }

    public void FillFields() {
        int id = getIntent().getIntExtra("Id", 0);
        Session currentsession = SQLite.select().from(Session.class).where(Session_Table.SessionId.eq(id)).querySingle();
        if (currentsession == null)
            return; //handle later
        currentsession.getModule().load();
        currentsession.getRoom().load();
        currentsession.getRoom().getBuilding().load();
        Title.setText(currentsession.getModule().getModuleNickname());
        SubTitle.setText(currentsession.getModule().getModuleName());
        SecondTitle.setText(currentsession.getType());
        String content =
                "This Session is located in room " + currentsession.getRoom().getRoomName() +
                " on floor " + currentsession.getRoom().getFloor() +
                " of " + currentsession.getRoom().getBuilding().getBuildingName() +
                " and takes place from " + currentsession.getStartTime() + " to " +currentsession.getEndTime();
        Content.setText(content);


    }

    public void InitToolBar() {
        Toolbar bar = (Toolbar) findViewById(R.id.DetailToolbar_TB);
        bar.setTitle("");
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
