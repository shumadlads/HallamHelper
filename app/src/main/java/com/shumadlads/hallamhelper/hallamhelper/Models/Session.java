package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

@Table(database = HallamHelperDB.class, name = "Classes")
public class Session extends BaseModel {

    @PrimaryKey(autoincrement = true)
    int ClassId;

    @Column
    String ClassType;

    @Column
    String Date;

    @Column
    String StartTime;

    @Column
    String EndTime;

    @ForeignKey(stubbedRelationship =true ,saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Module", foreignKeyColumnName = "ModuleId")})
    com.shumadlads.hallamhelper.hallamhelper.Models.Module Module;

    @ForeignKey(stubbedRelationship  = true,saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Room", foreignKeyColumnName = "RoomId")})
    com.shumadlads.hallamhelper.hallamhelper.Models.Room Room;

    public Session() {
    }

    public int getClassId() {
        return ClassId;
    }

    public void setClassId(int classId) {
        ClassId = classId;
    }

    public String getClassType() {
        return ClassType;
    }

    public void setClassType(String classType) {
        ClassType = classType;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Module getModule() {
        return Module;
    }

    public void setModule(com.shumadlads.hallamhelper.hallamhelper.Models.Module module) {
        Module = module;
    }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Room getRoom() {
        return Room;
    }

    public void setRoom(com.shumadlads.hallamhelper.hallamhelper.Models.Room room) {
        Room = room;
    }
}
