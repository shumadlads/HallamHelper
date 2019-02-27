package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

import com.raizlabs.android.dbflow.data.Blob;

import java.util.Date;

public class TimetableRecyclerViewModel implements Comparable<TimetableRecyclerViewModel>{

    public int getId() {
        return Id;
    }

    private int Id;
    public String ModuleNickName;
    public Blob ModuleImage;
    public String Type;
    public String StartTime;
    public String EndTime;
    public String Room;

    public TimetableRecyclerViewModel(int id , String module,Blob image, String type, String startTime, String endTime, String room) {
        Id = id;
        ModuleImage = image;
        ModuleNickName = module;
        Type = type;
        StartTime = startTime;
        EndTime = endTime;
        Room = room;
    }

    @Override
    public int compareTo(TimetableRecyclerViewModel o) {
        return StartTime.compareTo(o.StartTime);
    }
}
