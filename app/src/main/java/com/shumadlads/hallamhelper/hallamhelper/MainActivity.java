package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shumadlads.hallamhelper.hallamhelper.Adapters.RoomsAdapter;
import com.shumadlads.hallamhelper.hallamhelper.Database.DatabaseAccess;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner lvRooms;
    private List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvRooms = (Spinner) findViewById(R.id.spinner_Rooms);
        LoadRooms();
        RoomSelected();
    }


    public void LoadRooms(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.Open();
        roomList = databaseAccess.GetAllRooms();
        databaseAccess.Close();
        ArrayAdapter<Room> adapter = new ArrayAdapter<Room>(this, android.R.layout.simple_spinner_item,roomList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lvRooms.setAdapter(adapter);
    }

    public void RoomSelected(){
        lvRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Room room = (Room) lvRooms.getSelectedItem();
                Toast.makeText(getApplicationContext(), room.getxCoord()+","+room.getyCoord(), Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }



}
