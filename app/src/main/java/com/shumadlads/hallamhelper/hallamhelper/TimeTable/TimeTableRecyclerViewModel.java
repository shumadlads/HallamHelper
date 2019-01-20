package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

public class TimeTableRecyclerViewModel {

    public int getId() {
        return Id;
    }

    private int Id;
    public String ModuleNickName;
    public String Type;
    public String StartTime;
    public String EndTime;
    public String Room;

    public TimeTableRecyclerViewModel(int id , String module, String type, String startTime, String endTime, String room) {
        Id = id;
        ModuleNickName = module;
        Type = type;
        StartTime = startTime;
        EndTime = endTime;
        Room = room;
    }
}
