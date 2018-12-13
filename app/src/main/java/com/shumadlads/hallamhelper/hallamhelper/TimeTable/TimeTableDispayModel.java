package com.shumadlads.hallamhelper.hallamhelper.TimeTable;

public class TimeTableDispayModel {

    public String Module;
    public String Teacher;
    public String Type;
    public String Date;
    public String StartTime;
    public String EndTime;
    public String Room;

    public TimeTableDispayModel(String module, String teacher, String type, String date, String startTime, String endTime, String room) {
        Module = module;
        Teacher = teacher;
        Type = type;
        Date = date;
        StartTime = startTime;
        EndTime = endTime;
        Room = room;
    }
}
