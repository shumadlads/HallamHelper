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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.R;
import com.shumadlads.hallamhelper.hallamhelper.TimeTableDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class TimeTableRecyclerViewAdapter extends RecyclerView.Adapter<TimeTableRecyclerViewHolder> implements Filterable {

    private ArrayList<TimeTableRecyclerViewModel> Model;
    private List<TimeTableRecyclerViewModel> ModelUnfiltered;
    private TimeTableRecyclerViewListener Listener;
    private Context mContext;
    private Filter FilterModel = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TimeTableRecyclerViewModel> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(ModelUnfiltered);
            } else {
                String filterparam = constraint.toString().toLowerCase();
                //  Toast.makeText(mContext, filterparam, Toast.LENGTH_SHORT).show();
                for (TimeTableRecyclerViewModel item : ModelUnfiltered) {
                    if (item.ModuleNickName.toLowerCase().contains(filterparam)) {
                        filtered.add(item);
                    }
                }
            }
            Filter.FilterResults result = new Filter.FilterResults();
            result.values = filtered;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            Model.clear();
            Model.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public TimeTableRecyclerViewAdapter(Context context, ArrayList<TimeTableRecyclerViewModel> model, TimeTableRecyclerViewListener timeTableRecyclerViewListener) {
        mContext = context;
        Model = model;
        ModelUnfiltered = new ArrayList<>() ;

        ModelUnfiltered.addAll(model);
        Listener = timeTableRecyclerViewListener;
    }

    @NonNull
    @Override
    public TimeTableRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_timetables, viewGroup, false);
        TimeTableRecyclerViewHolder holder = new TimeTableRecyclerViewHolder(view, Listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableRecyclerViewHolder timeTableRecyclerViewHolder, int i) {
        timeTableRecyclerViewHolder.Icon.setImageResource(R.drawable.newshu);
        timeTableRecyclerViewHolder.Title.setText(Model.get(i).ModuleNickName);
        timeTableRecyclerViewHolder.Subtitle.setText(Model.get(i).StartTime + "-" + Model.get(i).EndTime);
        timeTableRecyclerViewHolder.Meta.setText(Model.get(i).Room);

    }

    @Override
    public int getItemCount() {
        return Model.size();
    }

    @Override
    public Filter getFilter() {
        return FilterModel;
    }
}
