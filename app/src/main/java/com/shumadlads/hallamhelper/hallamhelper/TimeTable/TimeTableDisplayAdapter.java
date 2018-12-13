package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shumadlads.hallamhelper.hallamhelper.R;

import java.util.ArrayList;

public class TimeTableDisplayAdapter extends ArrayAdapter<TimeTableDispayModel> {
    public TimeTableDisplayAdapter(Context context, ArrayList<TimeTableDispayModel> timeTable) {
        super(context, 0, timeTable);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeTableDispayModel timeTable = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_listview, parent, false);
        }
        TextView Name = (TextView) convertView.findViewById(R.id.ModuleName_TextView);
        TextView Leader = (TextView) convertView.findViewById(R.id.ModuleLeader_TextView);
        TextView Type = (TextView) convertView.findViewById(R.id.Type_TextView);
        TextView Date = (TextView) convertView.findViewById(R.id.Date_TextView);
        TextView Start = (TextView) convertView.findViewById(R.id.Start_TextView);
        TextView End = (TextView) convertView.findViewById(R.id.End_TExtView);
        TextView Room = (TextView) convertView.findViewById(R.id.Room_TextView);

        Name.setText(timeTable.Module);
        Leader.setText(timeTable.Teacher);
        Type.setText(timeTable.Type);
        Date.setText(timeTable.Date);
        Start.setText(timeTable.StartTime);
        End.setText(timeTable.EndTime);
        Room.setText(timeTable.Room);
        return convertView;
    }
}
