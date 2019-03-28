package com.shumadlads.hallamhelper.hallamhelper.Navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shumadlads.hallamhelper.hallamhelper.R;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewHolder;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewListener;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewModel;

import java.util.ArrayList;
import java.util.List;

public class NavigationRecyclerViewAdapter extends RecyclerView.Adapter<NavigationRecyclerViewHolder> {
    private ArrayList<NavigationRecyclerViewModel> Model;
    private NavigationRecyclerViewListener Listener;
    private Context mContext;

    public NavigationRecyclerViewAdapter(Context context, ArrayList<NavigationRecyclerViewModel> model, NavigationRecyclerViewListener navigationRecyclerViewListener) {
        mContext = context;
        Model = model;
        Listener = navigationRecyclerViewListener;
    }

    @NonNull
    @Override
    public NavigationRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.navigation_recyclerview_item, viewGroup, false);
        NavigationRecyclerViewHolder holder = new NavigationRecyclerViewHolder(view, Listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationRecyclerViewHolder navigationRecyclerViewHolder, int i) {
        if (Model.get(i).BuildingImage != null) {
            byte[] imageData = Model.get(i).BuildingImage.getBlob();
            Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            navigationRecyclerViewHolder.Image.setImageBitmap(image);
        }
        navigationRecyclerViewHolder.Name.setText(Model.get(i).BuildingName);
    }

    @Override
    public int getItemCount() {
        return Model.size();
    }


}
