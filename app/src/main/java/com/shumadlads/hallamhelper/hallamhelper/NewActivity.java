package com.shumadlads.hallamhelper.hallamhelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.regex.Matcher;

import java.util.regex.Pattern;


public class NewActivity extends AppCompatActivity {

    static int CurrentUser = 1;

    Spinner Module_Spinner, Room_Spinner;

    EditText Date_EditText, Start_EditText, End_EditText, Type_EditText;

    DatePickerDialog Date_Dialog;

    SimpleDateFormat DateFormat;

    TimePickerDialog Start_Dialog, End_Dialog;

    Button AddSession_Button;

    private Pattern pattern;
    private static final String TIME12HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    private Matcher matcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        SetupModuleSpinner();
        SetupRoomSpinner();
        SetupInputs();
        SetupDateandTimeDialogs();

        AddSession();
        SetDate();
        SetStartTime();
        SetEndTime();
    }

    public void SetupModuleSpinner() {
        Module_Spinner = (Spinner) findViewById(R.id.Module_Spinner);
        List<Module> Modules = SQLite.select().from(Module.class).queryList();
        if (Modules != null) {
            ArrayAdapter<Module> adapter = new ArrayAdapter<Module>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Modules);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Module_Spinner.setAdapter(adapter);
        }
        //Else ERROR HANDLE
    }

    public void SetupRoomSpinner() {
        Room_Spinner = (Spinner) findViewById(R.id.Room_Spinner);
        List<Room> Rooms = SQLite.select().from(Room.class).queryList();
        if (Rooms != null) {
            ArrayAdapter<Room> adapter = new ArrayAdapter<Room>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, Rooms);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Room_Spinner.setAdapter(adapter);
        }
        //Else ERROR HANDLE
    }

    public void SetupInputs() {
        DateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        Date_EditText = (EditText) findViewById(R.id.Date_EditText);
        Date_EditText.setInputType(InputType.TYPE_NULL);
        Start_EditText = (EditText) findViewById(R.id.Start_EditText);
        Start_EditText.setInputType(InputType.TYPE_NULL);
        End_EditText = (EditText) findViewById(R.id.End_EditText);
        End_EditText.setInputType(InputType.TYPE_NULL);
        Type_EditText = (EditText) findViewById(R.id.Type_EditText);
        AddSession_Button = (Button) findViewById(R.id.AddSession_Button);
    }

    public boolean validate(final String time) {
        matcher = pattern.matcher(time);
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
                Start_EditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        End_Dialog = new TimePickerDialog(this, new OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                End_EditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    public void AddSession() {
        AddSession_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Session newSession = new Session();
                    Module selectedModule = ((Module) Module_Spinner.getSelectedItem());
                    if (selectedModule != null) {
                        Room selectedRoom = ((Room) Room_Spinner.getSelectedItem());
                        if (selectedRoom != null) {
                            Date date = DateFormat.parse(Date_EditText.getText().toString());
                            if (Date_EditText.getText().toString().equals(DateFormat.format(date))) {
                                if (validate(Start_EditText.getText().toString())) {
                                    if (validate(End_EditText.getText().toString())) {
                                        if (!Type_EditText.getText().toString().isEmpty()) {
                                            newSession.setClassType(Type_EditText.getText().toString());
                                            newSession.setDate(Date_EditText.getText().toString());
                                            newSession.setStartTime(Start_EditText.getText().toString());
                                            newSession.setEndTime(End_EditText.getText().toString());
                                            newSession.setModule(selectedModule);
                                            newSession.setRoom(selectedRoom);
                                            newSession.save();
                                            Toast.makeText(getApplicationContext(), "Success" , Toast.LENGTH_SHORT).show();
                                        } else {
                                            //ERROR HANDLE
                                        }
                                    } else {
                                        //ERROR HANDLE
                                    }
                                } else {
                                    //ERROR HANDLE
                                }
                            } else {
                                //ERROR HANDLE
                            }
                        } else {
                            //ERROR HANDLE
                            return;
                        }
                    } else {
                        //ERROR HANDLE
                        return;
                    }
                } catch (Exception ex) {
                    //ERROR HANDLE
                }
            }
        });
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
