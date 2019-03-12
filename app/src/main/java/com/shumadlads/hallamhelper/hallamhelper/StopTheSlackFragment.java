package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class StopTheSlackFragment extends Fragment {

    public StopTheSlackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        View view =inflater.inflate(R.layout.stoptheslack_fragment, container, false);
        InitToolBar(view);
        return view;
    }

    public void InitToolBar(View view) {
        Toolbar bar = view.findViewById(R.id.slack_Toolbar);
        bar.setTitle("Slack");
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(bar);

    }

}