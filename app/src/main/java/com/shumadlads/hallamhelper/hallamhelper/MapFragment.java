package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;


public class MapFragment extends Fragment {



    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onStart() {
        super.onStart();
        int tempFrom = getArguments().getInt("RoomFrom");
        int tempTo = getArguments().getInt("RoomTo");

        int levelFrom = (((tempFrom / 10) / 10) % 10);
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, "Level is " + levelFrom, Toast.LENGTH_LONG);
        toast.show();// do something

        ImageView mapBg = getActivity().findViewById(R.id.backgroundImageView);
        if (levelFrom == 0) {

            mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24px));
        }
        MapView mapView = getActivity().findViewById(R.id.mapView);
        mapView.onPopulate(tempFrom, tempTo);
        changeRoomColour(tempTo);
        String text = mapView.Astar();

    }

    public void onResume() {

        super.onResume();
        //Following code has to be in onResume otherwise it is overwritten at the end of onStart
        ImageView mapBg = getActivity().findViewById(R.id.backgroundImageView);
        int tempFrom = getArguments().getInt("RoomFrom");
        int levelFrom = (((tempFrom / 10) / 10) % 10);
        if (levelFrom == 0) {

            mapBg.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24px));
        }
    }

    public void changeRoomColour(int roomTo){
        ImageView map = getActivity().findViewById(R.id.backgroundImageView);
        String roomToString = Integer.toString(roomTo);
        VectorChildFinder vector = new VectorChildFinder(getActivity().getApplicationContext(),
                R.drawable.ic_emb_level_2, map);
        VectorDrawableCompat.VFullPath changePathTo = vector.findPathByName(roomToString);
        changePathTo.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
    }

}