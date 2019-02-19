package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StopTheSlackFragment extends Fragment {

    public StopTheSlackFragment() {
        // Required empty public constructor
    }

    public static StopTheSlackFragment newInstance(String param1, String param2) {
        StopTheSlackFragment fragment = new StopTheSlackFragment();
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
        return inflater.inflate(R.layout.stoptheslack_fragment, container, false);
    }

}