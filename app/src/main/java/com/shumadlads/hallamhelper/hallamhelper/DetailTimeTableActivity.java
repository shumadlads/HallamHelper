package com.shumadlads.hallamhelper.hallamhelper;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session_Table;

public class DetailTimeTableActivity extends AppCompatActivity {

    private TextView Title;
    private TextView SubTitle;
    private TextView SecondTitle;
    private TextView Content;
    private int Id;
    private int PreviousId;

    public static final int TIMETABLE_FRAGMENT = 0;
    public static final int MAP_FRAGMENT = 3;
    // public static final int NAVIAGTE_FRAGMENT = 1;
    // public static final int SLACK_FRAGMENT = 2;
    private static int CurrentUser = 1;
    private Session CurrentSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_detail_activity);
        Id = getIntent().getIntExtra("Id", 0);
        PreviousId = getIntent().getIntExtra("PreviousId", -1);
        InitToolBar();
        InitFields();
        InitButtons();
    }

    public void InitFields() {
        Title = findViewById(R.id.title_timetable_detail_activity_TextView);
        SubTitle = findViewById(R.id.subtitle_timetable_detail_activity_TextView);
        SecondTitle = findViewById(R.id.secondtitle_timetable_detail_activity_TextView);
        Content = findViewById(R.id.content_timetable_detail_activity_TextView);
        ImageView imageview = findViewById(R.id.timetable_detail_activity_ImageView);
        CurrentSession = SQLite.select().from(Session.class).where(Session_Table.SessionId.eq(Id)).querySingle();
        if (CurrentSession != null) {
            CurrentSession.getModule().load();
            CurrentSession.getRoom().load();
            CurrentSession.getRoom().getBuilding().load();
            if (CurrentSession.getRoom().getBuilding().getBuildingImage() != null) {
                byte[] imageData = CurrentSession.getRoom().getBuilding().getBuildingImage().getBlob();
                Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageview.setImageBitmap(image);
            }
            Title.setText(CurrentSession.getModule().getModuleNickname());
            SubTitle.setText(CurrentSession.getModule().getModuleName());
            SecondTitle.setText(CurrentSession.getType());
            String content =
                    "This Session is located in room " + CurrentSession.getRoom().getRoomName() +
                            " on floor " + CurrentSession.getRoom().getFloor() +
                            " of " + CurrentSession.getRoom().getBuilding().getBuildingName() +
                            " and takes place from " + CurrentSession.getStartTime() + " to " + CurrentSession.getEndTime();
            Content.setText(content);
        } else
            finish();
    }

    public void InitButtons() {
        Button remove = findViewById(R.id.remove_timetable_detail_activity_Button);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remove();
            }
        });
        Button route = findViewById(R.id.route_timetable_detail_activity_Button);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Route();
            }
        });
    }

    public void Remove() {
        User_Session session = SQLite.select().from(User_Session.class).where(User_Session_Table.Session.eq(Id)).and(User_Session_Table.User.eq(CurrentUser)).querySingle();
        session.delete();
        CurrentSession.delete();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("LoadDefaultFragment", TIMETABLE_FRAGMENT);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void Route() {
        User_Session session = SQLite.select().from(User_Session.class).where(User_Session_Table.Session.eq(Id)).and(User_Session_Table.User.eq(CurrentUser)).querySingle();
        session.getSession().load();
        session.getSession().getRoom().load();
        session.getSession().getRoom().getNode().load();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("LoadDefaultFragment", MAP_FRAGMENT);
        if (PreviousId != -1) {
            User_Session prevsession = SQLite.select().from(User_Session.class).where(User_Session_Table.Session.eq(PreviousId)).and(User_Session_Table.User.eq(CurrentUser)).querySingle();
            prevsession.getSession().load();
            prevsession.getSession().getRoom().load();
            intent.putExtra("StartId", prevsession.getSession().getRoom().getRoomId());
        } else
            intent.putExtra("StartId", -1);
        intent.putExtra("TargetId", session.getSession().getRoom().getRoomId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

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
