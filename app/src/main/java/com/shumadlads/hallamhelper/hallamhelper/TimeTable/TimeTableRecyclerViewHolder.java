package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shumadlads.hallamhelper.hallamhelper.R;

public class TimeTableRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView Title;
    TextView Subtitle;
    ImageView Icon;
    TextView Meta;
    ConstraintLayout Parent;
    TimeTableRecyclerViewListener Listener;

    public TimeTableRecyclerViewHolder(@NonNull View itemView, TimeTableRecyclerViewListener timeTableRecyclerViewListener) {
        super(itemView);
        Listener = timeTableRecyclerViewListener;
        Title = itemView.findViewById(R.id.Title_TextView);
        Subtitle = itemView.findViewById(R.id.SubTitle_TextView);
        Icon = itemView.findViewById(R.id.Icon_ImageView);
        Meta = itemView.findViewById(R.id.Meta_TextView);
        Parent = itemView.findViewById(R.id.parent_layout);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Listener.OnSessionClick(getAdapterPosition());
    }
}
