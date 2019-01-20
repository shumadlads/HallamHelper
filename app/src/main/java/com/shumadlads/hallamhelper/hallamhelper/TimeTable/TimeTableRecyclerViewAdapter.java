package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.R;
import com.shumadlads.hallamhelper.hallamhelper.TimeTableDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeTableRecyclerViewAdapter extends RecyclerView.Adapter<TimeTableRecyclerViewHolder> {

    ArrayList<TimeTableRecyclerViewModel> Model = new ArrayList<TimeTableRecyclerViewModel>();
    TimeTableRecyclerViewListener Listener ;
    Context mContext;

    public TimeTableRecyclerViewAdapter(Context context, ArrayList<TimeTableRecyclerViewModel> model , TimeTableRecyclerViewListener timeTableRecyclerViewListener) {
        mContext = context;
        Model = model;
        Listener = timeTableRecyclerViewListener;
    }

    @NonNull
    @Override
    public TimeTableRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_timetables, viewGroup,false);
        TimeTableRecyclerViewHolder holder = new TimeTableRecyclerViewHolder(view , Listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableRecyclerViewHolder timeTableRecyclerViewHolder, int i) {
        timeTableRecyclerViewHolder.Icon.setImageResource(R.drawable.homeimage);
        timeTableRecyclerViewHolder.Title.setText(Model.get(i).ModuleNickName);
        timeTableRecyclerViewHolder.Subtitle.setText(Model.get(i).StartTime +"-"+ Model.get(i).EndTime);
        timeTableRecyclerViewHolder.Meta.setText(Model.get(i).Room);

    }

    @Override
    public int getItemCount() {
        return Model.size();
    }
}
