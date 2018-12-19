package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RouteNavigationTestActivity extends AppCompatActivity {
    Button populateButton;
    Button navigateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_navigation_test);
        final MapView mapView = findViewById(R.id.mapView);
        //mapView.requestLayout();

        populateButton = findViewById(R.id.populateButton);
        navigateButton = findViewById(R.id.navigateButton);
        //mapView.onPopulate();
        String text = mapView.Astar();
        populateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mapView.onPopulate();
            }
        });
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, mapView.Astar(), duration);
                toast.show();
            }
        });
    }


}
