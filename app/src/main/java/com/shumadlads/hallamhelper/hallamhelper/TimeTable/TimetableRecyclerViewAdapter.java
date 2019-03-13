package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import com.shumadlads.hallamhelper.hallamhelper.R;

import java.util.ArrayList;
import java.util.List;

public class TimetableRecyclerViewAdapter extends RecyclerView.Adapter<TimetableRecyclerViewHolder> implements Filterable {

    private ArrayList<TimetableRecyclerViewModel> Model;
    private List<TimetableRecyclerViewModel> ModelUnfiltered;
    private TimetableRecyclerViewListener Listener;
    private Context mContext;
    private Filter FilterModel = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TimetableRecyclerViewModel> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(ModelUnfiltered);
            } else {
                String filterparam = constraint.toString().toLowerCase();
                for (TimetableRecyclerViewModel item : ModelUnfiltered) {
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

    public TimetableRecyclerViewAdapter(Context context, ArrayList<TimetableRecyclerViewModel> model, TimetableRecyclerViewListener timeTableRecyclerViewListener) {
        mContext = context;
        Model = model;
        ModelUnfiltered = new ArrayList<>() ;
        ModelUnfiltered.addAll(model);
        Listener = timeTableRecyclerViewListener;
    }

    @NonNull
    @Override
    public TimetableRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timetable_recyclerview_item, viewGroup, false);
        TimetableRecyclerViewHolder holder = new TimetableRecyclerViewHolder(view, Listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableRecyclerViewHolder timeTableRecyclerViewHolder, int i) {
        if(Model.get(i).ModuleImage != null){
        byte[] imageData = Model.get(i).ModuleImage.getBlob();
        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        timeTableRecyclerViewHolder.Icon.setImageBitmap(image);}
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
