package com.shumadlads.hallamhelper.hallamhelper;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.alespero.expandablecardview.ExpandableCardView;
import com.raizlabs.android.dbflow.sql.language.SQLite;


import com.shumadlads.hallamhelper.hallamhelper.Models.Building;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.regex.Pattern;


public class TimetableNewSessionActivity extends AppCompatActivity {

    public static final int TIMETABLE_FRAGMENT = 0;
    // public static final int NAVIAGTE_FRAGMENT = 1;
    // public static final int SLACK_FRAGMENT = 2;
    private SharedPreferences SharedPrefs;
    private int UserId;
    private ExpandableCardView ModuleECard;
    private ExpandableCardView RoomECard;
    private ExpandableCardView DateECard;
    private ExpandableCardView InfoECard;


    private Spinner Module_Spinner, Building_Spinner, Floor_Spinner, Room_Spinner;
    private TextInputEditText Date_EditText, Start_EditText, End_EditText, Type_EditText;
    private Switch Repeat_Switch, Semester1_Switch, Semester2_Switch, Chirstmas_Switch, Easter_Switch;
    private DatePickerDialog Date_Dialog;
    private SimpleDateFormat DateFormat;
    private TimePickerDialog Start_Dialog, End_Dialog;
    private Button AddSession_Button, AddAnother_Buttton;
    private Pattern pattern;
    private static final String TIME12HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_newsession_activity);
        ModuleECard = findViewById(R.id.Module_timetable_newsession_activity_ExpandableCardView);
        CardView mcard = ModuleECard.findViewById(R.id.card);
        TextView mtitle = ModuleECard.findViewById(R.id.title);

        RoomECard = findViewById(R.id.Room_timetable_newsession_activity_ExpandableCardView);
        CardView rcard = ModuleECard.findViewById(R.id.card);
        TextView rtitle = ModuleECard.findViewById(R.id.title);

        DateECard = findViewById(R.id.Room_timetable_newsession_activity_ExpandableCardView);
        CardView dcard = ModuleECard.findViewById(R.id.card);
        TextView dtitle = ModuleECard.findViewById(R.id.title);

        InfoECard = findViewById(R.id.Room_timetable_newsession_activity_ExpandableCardView);
        CardView icard = ModuleECard.findViewById(R.id.card);
        TextView ititle = ModuleECard.findViewById(R.id.title);


        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserId = SharedPrefs.getInt(getString(R.string.SP_UserId), -1);
        if (UserId == -1) {
            //EXIT
        }
        SetupModuleSpinner();
        SetupRoomSpinner();
       SetupInputs();
        SetupDateandTimeDialogs();
        InitToolBar();
        AddSession();
        SetDate();
        SetStartTime();
        SetEndTime();



    }

    public void SetupModuleSpinner() {

        Module_Spinner = findViewById(R.id.Module_Spinner);
        List<Module> Modules = SQLite.select().from(Module.class).queryList();
        ArrayAdapter<Module> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.timetable_spinner_item, Modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Module_Spinner.setAdapter(adapter);

    }

    public void SetupRoomSpinner() {
        Building_Spinner = findViewById(R.id.Building_Spinner);
        Floor_Spinner = findViewById(R.id.Floor_Spinner);
        Room_Spinner = findViewById(R.id.Room_Spinner);

        List<Building> Buildings = SQLite.select().from(Building.class).queryList();
        ArrayAdapter<Building> Badapter = new ArrayAdapter<>(getApplicationContext(),R.layout.timetable_spinner_item, Buildings);
        Badapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Building_Spinner.setAdapter(Badapter);


        Building_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int floors = ((Building) Building_Spinner.getSelectedItem()).getFloors();
                Integer[] items = new Integer[floors];
                for (int i = 0; i < floors; i++) {
                    items[i] = i;
                }
                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getApplicationContext(), R.layout.timetable_spinner_item, items);
                Floor_Spinner.setAdapter(adapter);
                Floor_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int b = ((Building) Building_Spinner.getSelectedItem()).getBuildingId();
                        int f = (int) Floor_Spinner.getSelectedItem();
                        List<Room> Rooms = SQLite.select().from(Room.class).leftOuterJoin(Node.class).on(Room_Table.Node.withTable().eq(Node_Table.NodeId.withTable())).where(Node_Table.Building.eq(b)).and(Node_Table.Floor.eq(f)).queryList();

                        ArrayAdapter<Room> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.timetable_spinner_item, Rooms);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Room_Spinner.setAdapter(adapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void SetupInputs() {
       DateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        Date_EditText = findViewById(R.id.date_timetable_newsession_activity_TextInputEditText);
        Date_EditText.setInputType(InputType.TYPE_NULL);
        Start_EditText = findViewById(R.id.starttime_timetable_newsession_activity_TextInputEditText);
        Start_EditText.setInputType(InputType.TYPE_NULL);
        End_EditText = findViewById(R.id.endtime_timetable_newsession_activity_TextInputEditText);
        End_EditText.setInputType(InputType.TYPE_NULL);
        Type_EditText = findViewById(R.id.type_timetable_newsession_activity_TextInputEditText);

      /*  Repeat_Switch = findViewById(R.id.repeat_timetable_newsession_activity_Switch);
        Semester1_Switch = findViewById(R.id.sem1_timetable_newsession_activity_Switch);
        Semester2_Switch = findViewById(R.id.sem2_timetable_newsession_activity_Switch);
        Chirstmas_Switch = findViewById(R.id.christmas_timetable_newsession_activity_Switch);
        Easter_Switch = findViewById(R.id.easter_timetable_newsession_activity_Switch);
*/
        AddSession_Button = findViewById(R.id.addandfinishSession_Button);
        AddAnother_Buttton = findViewById(R.id.AddAnotherSession_Button);

    }

    public boolean validate(final String time) {
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }



    public void SetupDateandTimeDialogs() {
        pattern = Pattern.compile(TIME12HOURS_PATTERN);

        Calendar newCalendar = Calendar.getInstance();
        Date_Dialog = new DatePickerDialog(this, new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Date_EditText.setText(DateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        Start_Dialog = new TimePickerDialog(this, new OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Start_EditText.setText(String.format(Locale.ENGLISH, "%02d:%02d", selectedHour, selectedMinute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        End_Dialog = new TimePickerDialog(this, new OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                End_EditText.setText(String.format(Locale.ENGLISH, "%02d:%02d", selectedHour, selectedMinute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    public void AddSession() {
        AddSession_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAndLeave();
            }
        });
        AddAnother_Buttton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitAndClear();
            }
        });
    }

    public boolean Submit() {
        User user = SQLite.select().from(User.class).where(User_Table.UserId.eq(UserId)).querySingle();
        Session session = new Session();

        session.setSemester1(Semester1_Switch.isChecked() && Repeat_Switch.isChecked() ? 1 : 0);
        session.setSemester2(Semester2_Switch.isChecked() && Repeat_Switch.isChecked() ? 1 : 0);
        session.setChristmas(Chirstmas_Switch.isChecked() && Repeat_Switch.isChecked() ? 1 : 0);
        session.setEaster(Easter_Switch.isChecked() && Repeat_Switch.isChecked() ? 1 : 0);

        if (ValidateModule(session) && ValidateRoom(session) && ValidateDate(session) && ValidateStart(session) && ValidateEnd(session) && ValidateType(session)) {
            session.save();
            User_Session newUSession = new User_Session();
            newUSession.setUser(user);
            newUSession.setSession(session);
            newUSession.save();
            return true;
        } else
        return false;
    }

    public void SubmitAndLeave() {
        if (Submit()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("LoadDefaultFragment", TIMETABLE_FRAGMENT);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

    public void SubmitAndClear() {
        if (Submit()) {
            Date_EditText.setText("");
            Start_EditText.setText("");
            End_EditText.setText("");
            Type_EditText.setText("");
        }
    }

    public boolean ValidateModule(Session session) {
        Module selectedModule = ((Module) Module_Spinner.getSelectedItem());
        if (selectedModule != null) {
            session.setModule(selectedModule);
            return true;
        } else
            return false;
    }

    public boolean ValidateRoom(Session session) {
        Room selectedRoom = ((Room) Room_Spinner.getSelectedItem());
        if (selectedRoom != null) {
            session.setRoom(selectedRoom);
            return true;
        } else
            return false;
    }

    public boolean ValidateDate(Session session) {
        try {
            if (Date_EditText.getText() != null) {
                Date date = DateFormat.parse(Date_EditText.getText().toString());
                if (Date_EditText.getText().toString().equals(DateFormat.format(date))) {
                    session.setDate(Date_EditText.getText().toString());
                    return true;
                } else
                    return false;
            } else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ValidateStart(Session session) {
        if (Start_EditText.getText() != null) {
            if (validate(Start_EditText.getText().toString())) {
                session.setStartTime(Start_EditText.getText().toString());
                return true;
            } else
                return false;
        } else
            return false;
    }

    public boolean ValidateEnd(Session session) {
        if (End_EditText.getText() != null) {
            if (validate(End_EditText.getText().toString())) {
                session.setEndTime(End_EditText.getText().toString());
                return true;
            } else
                return false;
        } else
            return false;
    }

    public boolean ValidateType(Session session) {
        if (Type_EditText.getText() != null) {
            if (!Type_EditText.getText().toString().isEmpty()) {
                session.setType(Type_EditText.getText().toString());
                return true;
            } else
                return false;
        } else
            return false;
    }

    public void InitToolBar() {
        Toolbar bar = findViewById(R.id.timetable_newsession_activity_toolbar);
        bar.setTitle("New Session");
        setSupportActionBar(bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    public void SetDate() {
        Date_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date_Dialog.show();
            }
        });
    }

    public void SetStartTime() {
        Start_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_Dialog.show();
            }
        });
    }

    public void SetEndTime() {
        End_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                End_Dialog.show();
            }
        });
    }
}
