package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, tempFrom + " " + tempTo, Toast.LENGTH_LONG);
        toast.show();// do something
        MapView mapView = getActivity().findViewById(R.id.mapView);
        mapView.onPopulate(tempFrom, tempTo);
        String text = mapView.Astar();

    }

}