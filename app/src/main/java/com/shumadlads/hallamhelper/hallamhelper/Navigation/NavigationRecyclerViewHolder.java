package com.shumadlads.hallamhelper.hallamhelper.Navigation;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shumadlads.hallamhelper.hallamhelper.R;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewListener;

public class NavigationRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView Name;
    ImageView Image;
CardView Parent;
    NavigationRecyclerViewListener Listener;

    public NavigationRecyclerViewHolder(@NonNull View itemView, NavigationRecyclerViewListener navigationRecyclerViewListener) {
        super(itemView);
        Listener = navigationRecyclerViewListener;
        Name = itemView.findViewById(R.id.name);
        Image = itemView.findViewById(R.id.thumbnail);
        Parent = itemView.findViewById(R.id.card_view_owen);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Listener.OnBuildingClick(getAdapterPosition());
    }
}
