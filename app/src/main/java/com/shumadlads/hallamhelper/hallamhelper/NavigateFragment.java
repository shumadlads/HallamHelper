package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NavigateFragment extends Fragment {

    public NavigateFragment() {
        // Required empty public constructor
    }

    public static NavigateFragment newInstance(String param1, String param2) {
        NavigateFragment fragment = new NavigateFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.navigate_fragment, container, false);
        InitToolBar(view);
        ImageView searchFindIcon = view.findViewById(R.id.searchFindIcon);
        ImageView switchIcon = view.findViewById(R.id.switchIcon);
        final TextView toTextView = view.findViewById(R.id.textInputTo);
        final TextView fromTextView = view.findViewById(R.id.textInputFrom);
        //onSearchFindIconClick
        searchFindIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Toast is currently taking place on MapView - Earmarked for removal 19/12/2018 GS

                */

                if(!(toTextView.getText().toString().matches(""))){
                    int roomToInt = Integer.parseInt(toTextView.getText().toString());
                    if(roomToInt != 0){
                        int roomFromInt = 0;
                        if(!(fromTextView.getText().toString().matches(""))){
                            roomFromInt = Integer.parseInt(fromTextView.getText().toString());
                        }else { // Room from is empty and needs correct code generating for entrance of building
                            String knownEmptyRoomCode = "99";
                            int buildingFrom = ((((roomToInt / 10) / 10) / 10) % 10); // get first digit for building number
                            switch (buildingFrom){
                                case 3: {
                                    roomFromInt = Integer.parseInt("31" + knownEmptyRoomCode); // if room code is emb, prefix 31 to get correct entrance
                                    break;
                                }
                                case 9: {
                                    roomFromInt = Integer.parseInt("90" + knownEmptyRoomCode); // if room code is cantor, prefix 90 to get correct entrance
                                }

                            }
                        }
                        swapFragment(roomFromInt, roomToInt);
                    }
                    else {
                        errorToast();
                    }
                }
                else{
                    errorToast();
                }



            }
        });
        // onSwapTextfieldsClick
        switchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CharSequence temp = toTextView.getText(); //set toText to temp variable
                toTextView.setText(fromTextView.getText()); //set fromText
                fromTextView.setText(temp);

            }
        });
        return view;
    }

    private void swapFragment(int from, int to){
        MapFragment mapFragment = new MapFragment();
        Bundle b = new Bundle();
        b.putInt("RoomTo", to);
        b.putInt("RoomFrom", from);
        mapFragment.setArguments(b);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void InitToolBar(View view) {
        Toolbar bar = view.findViewById(R.id.navigate_Toolbar);
        bar.setTitle("Navigate");
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(bar);

    }

    private void errorToast() {
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, "Please enter valid room number", Toast.LENGTH_LONG);
        toast.show();// do something
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);}
}