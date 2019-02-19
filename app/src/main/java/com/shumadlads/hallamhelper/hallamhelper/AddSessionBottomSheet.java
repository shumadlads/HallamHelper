package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AddSessionBottomSheet extends BottomSheetDialogFragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.addsession_bottomsheet_layout, container, false);


        Button button1 = v.findViewById(R.id.btn1);
        Button button2 = v.findViewById(R.id.btn2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRActivity();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewTimetableActivity();

            }
        });
        return v;
    }

    public void openNewTimetableActivity(){
        Intent intent = new Intent(getView().getContext(), TimetableNewSessionActivity.class);
        startActivity(intent);

    }
    public void openQRActivity(){
        Intent intent = new Intent(getView().getContext(), QRActivity.class);
        startActivity(intent);

    }

}

