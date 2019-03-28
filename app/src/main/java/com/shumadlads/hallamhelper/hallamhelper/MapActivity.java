package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MapActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        Toolbar toolbar = findViewById(R.id.map_Toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Building Map");

    }

    public void onStart() {
        super.onStart();
        Bundle args = getIntent().getExtras();
        //getSupportActionBar().setTitle(args.getString("Building"));
        int tempFrom = args.getInt("RoomFrom");
        int tempTo = args.getInt("RoomTo");

        int levelFrom = (((tempFrom / 10) / 10) % 10);
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Level is " + levelFrom, Toast.LENGTH_LONG);
        toast.show();// do something

        /*
        MapView mapView = findViewById(R.id.mapView);
        mapView.onPopulate(tempFrom, tempTo);
        String text = mapView.Astar(); // todo - change to void method - Gareth 11/03/2019
        */

    }

    public void onResume() {

        super.onResume();
        //Following code has to be in onResume otherwise it is overwritten at the end of onStart
        Bundle args = getIntent().getExtras();
        int tempFrom = args.getInt("RoomFrom");
        int tempTo = args.getInt("RoomTo");
        displayMapBg(tempFrom, tempTo);

    }

    public void displayMapBg(int roomFrom, int roomTo) {
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        int buildingFrom = ((((roomFrom / 10) / 10) / 10) % 10); // get first digit for building number
        int levelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        MapView mapBg = findViewById(R.id.mapView);

        switch (buildingFrom) {
            case 9: {
                cantor(levelFrom, roomTo, mapBg);
                break;
            }
            case 3: {
                emb(levelFrom, roomTo, mapBg);
                break;
            }
        }
    }

    public void cantor(int levelFrom, int roomTo, MapView mapBg){
        Spinner spinner = findViewById(R.id.spinner);
        String[] levels = new String[]{
                "Level - 0",
                "Level - 1",
                "Level - 2",
                "Level - 3",
                "Level - 4"
        };

        final List<String> levelsList = new ArrayList<>(Arrays.asList(levels));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.building_level_select_item,levelsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.building_level_select_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(levelFrom);
    }

    public void emb(int levelFrom, int roomTo, MapView mapBg){
        Spinner spinner = findViewById(R.id.spinner);
        String[] levels = new String[]{
                "Level - 1"
        };

        final List<String> levelsList = new ArrayList<>(Arrays.asList(levels));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.building_level_select_item, levelsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.building_level_select_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(levelFrom - 1); // -1 needs removing if multiple levels are added

    }

    public void displayCantorMapBg(int levelFrom, int roomTo, MapView mapBg){

        mapBg.requestLayout();
        mapBg.getLayoutParams().height = (int) getResources().getDimension(R.dimen.cantorMapBg_height);
        mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.cantorMapBg_width);
        int bgDrawableInt;
        switch (levelFrom){
            case 0: {
                bgDrawableInt = R.drawable.ic_cantor_level_0;
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_0));
                changeRoomColour(roomTo, bgDrawableInt);
                break;
            }
            case 1: {
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_1));
                break;
            }
            case 2: {
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_2));
                break;
            }
            case 3: {
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_3));
                break;
            }
            case 4: {
                mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.cantorTopMapBg_width);
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_4));
                break;
            }
        }
    }

    public void displayEMBMapBg(int levelFrom, int roomTo, MapView mapBg){
        Drawable bgDrawable;
        mapBg.getLayoutParams().height = (int) getResources().getDimension(R.dimen.embMapBg_height);
        mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.embMapBg_width);
        int bgDrawableInt;
        switch (levelFrom) {
            case 1: {
                bgDrawable = getResources().getDrawable(R.drawable.ic_emb_level_2);
                bgDrawableInt = R.drawable.ic_emb_level_2;
                mapBg.setImageDrawable(bgDrawable);
                changeRoomColour(roomTo, bgDrawableInt);
                break;
            }
            case 9: { //todo DEBUG NEEDS RETHINKING
                bgDrawable = getResources().getDrawable(R.drawable.ic_emb_level_2);
                bgDrawableInt = R.drawable.ic_emb_level_2;
                mapBg.setImageDrawable(bgDrawable);
                changeRoomColour(roomTo, bgDrawableInt);
                break;
            }
        }

    }

    // Function that will change destination room colour to highlighted secondary hallam colour
    public void changeRoomColour(int roomTo, int bgDrawable){
        MapView map = findViewById(R.id.mapView);
        String roomToString = Integer.toString(roomTo);
        VectorChildFinder vector = new VectorChildFinder(getApplicationContext(),
                bgDrawable, map);
        VectorDrawableCompat.VFullPath changePathTo = vector.findPathByName(roomToString);
        if(changePathTo != null){
            changePathTo.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }



    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Bundle args = getIntent().getExtras();
        //getSupportActionBar().setTitle(args.getString("Building"));
        int roomFrom = args.getInt("RoomFrom");
        int roomTo = args.getInt("RoomTo");

        int buildingNo = ((((roomTo / 10) / 10) / 10) % 10); // get first digit for building number

        Toast.makeText(parent.getContext(),
                parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        String newLevelStr = parent.getItemAtPosition(pos).toString(); //Get selected item from spinner
        int newLevel = parseInt(newLevelStr.substring(newLevelStr.length() - 1)); //Turn last digit (level number) to int

        MapView mapView = findViewById(R.id.mapView);


        switch (buildingNo){
            case 3: {
                displayEMBMapBg(newLevel, roomTo, mapView);
                break;
            }
            case 9: {
                displayCantorMapBg(newLevel, roomTo, mapView);
                break;
            }
        }

        mapView.onPopulate(newLevel, roomFrom, roomTo);
        String text = mapView.Astar();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
