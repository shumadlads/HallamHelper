package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session_Table;

public class TimetableDetailActivity extends AppCompatActivity {

    private TextView Title;
    private TextView SubTitle;
    private TextView SecondTitle;
    private TextView Content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_detail_activity);
        InitToolBar();
        InitFields();
        FillFields();
    }

    public void InitFields() {
        Title = findViewById(R.id.title_timetable_detail_activity_TextView);
        SubTitle = findViewById(R.id.subtitle_timetable_detail_activity_TextView);
        SecondTitle = findViewById(R.id.secondtitle_timetable_detail_activity_TextView);
        Content = findViewById(R.id.content_timetable_detail_activity_TextView);
    }

    public void FillFields() {
        int id = getIntent().getIntExtra("Id", 0);
        Session session = SQLite.select().from(Session.class).where(Session_Table.SessionId.eq(id)).querySingle();
        if (session != null) {
            session.getModule().load();
            session.getRoom().load();
            session.getRoom().getBuilding().load();
                Title.setText(session.getModule().getModuleNickname());
                SubTitle.setText(session.getModule().getModuleName());
                SecondTitle.setText(session.getType());
                String content =
                        "This Session is located in room " + session.getRoom().getRoomName() +
                                " on floor " + session.getRoom().getFloor() +
                                " of " + session.getRoom().getBuilding().getBuildingName() +
                                " and takes place from " + session.getStartTime() + " to " + session.getEndTime();
                Content.setText(content);
        } else
            finish();
    }

    public void InitToolBar() {
        Toolbar bar = findViewById(R.id.timetable_detail_activity_Toolbar);
            bar.setTitle("");
            setSupportActionBar(bar);
            if (getSupportActionBar() != null)
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
