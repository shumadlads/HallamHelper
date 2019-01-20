package com.shumadlads.hallamhelper.hallamhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class TimeTableDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetabledetail);

        int id = getIntent().getIntExtra("Id" , 0);
        Toast.makeText(this, "clicked"+id , Toast.LENGTH_SHORT).show();
    }
}
