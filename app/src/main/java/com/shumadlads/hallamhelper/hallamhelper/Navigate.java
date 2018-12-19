package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Navigate extends Fragment {



    public Navigate() {
        // Required empty public constructor
    }

    public static Navigate newInstance(String param1, String param2) {
        Navigate fragment = new Navigate();
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
        //return inflater.inflate(R.layout.fragment_navigate, container, false);
        View view = inflater.inflate(R.layout.fragment_navigate, container, false);
        ImageView searchFindIcon = view.findViewById(R.id.searchFindIcon);
        ImageView switchIcon = view.findViewById(R.id.switchIcon);
        final TextView toTextView = view.findViewById(R.id.textInputTo);
        final TextView fromTextView = view.findViewById(R.id.textInputFrom);
        //onSearchFindIconClick
        searchFindIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* Toast is currently taking place on MapView - Earmarked for removal 19/12/2018 GS

                */

                if(!(toTextView.getText().toString().matches(""))){
                    int roomToInt = Integer.parseInt(toTextView.getText().toString());
                    if(roomToInt != 0){
                        int roomFromInt;
                        if(!(fromTextView.getText().toString().matches(""))){
                            roomFromInt = Integer.parseInt(fromTextView.getText().toString());
                        }else
                            roomFromInt = 9999; //known empty
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
        //onSwapTextfieldsClick
        switchIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
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

    private void errorToast(){
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, "Please enter valid room number", Toast.LENGTH_LONG);
        toast.show();// do something
    }









}