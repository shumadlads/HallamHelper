package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Building;
import com.shumadlads.hallamhelper.hallamhelper.Navigation.NavigationRecyclerViewAdapter;
import com.shumadlads.hallamhelper.hallamhelper.Navigation.NavigationRecyclerViewListener;
import com.shumadlads.hallamhelper.hallamhelper.Navigation.NavigationRecyclerViewModel;

import java.util.ArrayList;
import java.util.List;


public class NavigateFragment extends Fragment implements NavigationRecyclerViewListener {


    private NavigationRecyclerViewAdapter NavigationAdapter;
    private ArrayList<NavigationRecyclerViewModel> NavigationBuildings;
    private RecyclerView recyclerView;

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

        FillBuildings();
    }

    private void FillBuildings() {
        NavigationBuildings = new ArrayList<>();
        List<Building> buildings = SQLite.select().from(Building.class).queryList();
        for (Building build : buildings){
            NavigationBuildings.add(new NavigationRecyclerViewModel(build.getBuildingId(), build.getBuildingImage(),build.getBuildingName()));
            NavigationAdapter = new NavigationRecyclerViewAdapter(getActivity().getApplicationContext() , NavigationBuildings,this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.navigate_fragment, container, false);
        InitToolBar(view);
        InitRecylcerView(view);
        Button searchFindIcon = view.findViewById(R.id.searchFindIcon);
        //Button switchButton = view.findViewById(R.id.switchButton);
        final TextView toTextView = view.findViewById(R.id.textInputTo);
        final TextView fromTextView = view.findViewById(R.id.textInputFrom);
       /* final CardView cardCantor = view.findViewById(R.id.card_view_cantor);
       final CardView cardEmb = view.findViewById(R.id.card_view_emb);

        cardCantor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(9098, 9098); // roomTo building select node, knows to not assign start

            }
        });
        cardEmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(3000, 0000);

            }
        });
*/


        //onSearchFindIconClick
        searchFindIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Toast is currently taking place on MapView - Earmarked for removal 19/12/2018 GS

                 */

                if (!(toTextView.getText().toString().matches(""))) {
                    int roomToInt = Integer.parseInt(toTextView.getText().toString());
                    if (roomToInt != 0) {
                        int roomFromInt = 0;
                        if (!(fromTextView.getText().toString().matches(""))) {
                            roomFromInt = Integer.parseInt(fromTextView.getText().toString());
                        } else { // Room from is empty and needs correct code generating for entrance of building
                            String knownEmptyRoomCode = "99";
                            int buildingFrom = ((((roomToInt / 10) / 10) / 10) % 10); // get first digit for building number
                            switch (buildingFrom) {
                                case 3: {
                                    roomFromInt = Integer.parseInt("31" + knownEmptyRoomCode); // if room code is emb, prefix 31 to get correct entrance
                                    break;
                                }
                                case 9: {
                                    roomFromInt = Integer.parseInt("90" + knownEmptyRoomCode); // if room code is cantor, prefix 90 to get correct entrance
                                }

                            }
                        }
                        setIntent(roomFromInt, roomToInt);
                    } else {
                        errorToast();
                    }
                } else {
                    errorToast();
                }
            }
        });
        // onSwapTextfieldsClick

        return view;
    }

    private void InitRecylcerView(View view) {

        recyclerView = view.findViewById(R.id.nav_recyclerview);
        recyclerView.setAdapter(NavigationAdapter);
        if (getActivity() != null)
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    private void setIntent(int from, int to) {
        //MapFragment mapFragment = new MapFragment();
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        Bundle b = new Bundle();
        b.putInt("RoomTo", to);
        b.putInt("RoomFrom", from);
        mapIntent.putExtras(b);
        startActivity(mapIntent);
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
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public void OnBuildingClick(int pos) {

        switch (NavigationBuildings.get(pos).getId()) {
            case 1:  setIntent(9098, 9098);  //Cantor
                break;
            case 2: //Owen
                break;
            case 3://Howard
                break;
            case 4: setIntent(3100, 3100); //EMB
                break;
        }

    }
}