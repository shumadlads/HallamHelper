package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity {


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
        Spinner spinner = findViewById(R.id.spinner);
        String[] plants = new String[]{
                "Black birch",
                "European weeping birch"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.building_level_select_item,plantsList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.building_level_select_item);
        spinner.setAdapter(spinnerArrayAdapter);

        MapView mapView = findViewById(R.id.mapView);
        mapView.onPopulate(tempFrom, tempTo);
        String text = mapView.Astar(); // todo - change to void method - Gareth 11/03/2019

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
        int buildingFrom = ((((roomFrom / 10) / 10) / 10) % 10); // get first digit for building number
        int levelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        MapView mapBg = findViewById(R.id.mapView);

        switch (buildingFrom) {
            case 9: {
                displayCantorMapBg(levelFrom, roomTo, mapBg);
                break;
            }
            case 3: {
                displayEMBMapBg(levelFrom, roomTo, mapBg);
                break;
            }
        }
    }

    public void displayCantorMapBg(int levelFrom, int roomTo, MapView mapBg){
        mapBg.requestLayout();
        mapBg.getLayoutParams().height = (int) getResources().getDimension(R.dimen.cantorMapBg_height);
        mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.cantorMapBg_width);
        switch (levelFrom){
            case 0: {
                mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_cantor_level_0));
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
        int bgDrawableInt;
        switch (levelFrom) {
            case 1: {
                mapBg.getLayoutParams().height = (int) getResources().getDimension(R.dimen.embMapBg_height);
                mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.embMapBg_width);
                bgDrawable = getResources().getDrawable(R.drawable.ic_emb_level_2);
                bgDrawableInt = R.drawable.ic_emb_level_2;
                mapBg.setImageDrawable(bgDrawable);
                changeRoomColour(roomTo, bgDrawableInt);
                break;
            }
            case 9: { //todo DEBUG NEEDS RETHINKING
                mapBg.getLayoutParams().height = (int) getResources().getDimension(R.dimen.embMapBg_height);
                mapBg.getLayoutParams().width = (int) getResources().getDimension(R.dimen.embMapBg_width);
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
        changePathTo.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
    }

}
