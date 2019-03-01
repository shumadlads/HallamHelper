package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =inflater.inflate(R.layout.stoptheslack_fragment, container, false);
        InitToolBar(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }

    public void InitToolBar(View view) {
        Toolbar bar = view.findViewById(R.id.slack_Toolbar);
        bar.setTitle("Slack");
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(bar);

    }

}