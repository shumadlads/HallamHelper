package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

@Table(database = HallamHelperDB.class, name = "Sessions")
public class Session extends BaseModel {

    public Session() {
    }

    @PrimaryKey(autoincrement = true)
    int SessionId;

    @Column
    String Type;

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

    @Column
    int Semester1;

    @Column
    int Semester2;

    @Column
    int Christmas;

    @Column
    int Easter;

    public int getSessionId() {
        return SessionId;
    }
    public void setSessionId(int sessionId) {
        SessionId = sessionId;
    }

    public String getType() {
        return Type;
    }
    public void setType(String type) {
        Type = type;
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
    public void setModule(com.shumadlads.hallamhelper.hallamhelper.Models.Module module) { Module = module; }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Room getRoom() {
        return Room;
    }
    public void setRoom(com.shumadlads.hallamhelper.hallamhelper.Models.Room room) {
        Room = room;
    }

    public int getSemester1() {
        return Semester1;
    }
    public void setSemester1(int semester1) {
        Semester1 = semester1;
    }

    public int getSemester2() {
        return Semester2;
    }
    public void setSemester2(int semester2) {
        Semester2 = semester2;
    }

    public int getChristmas() {
        return Christmas;
    }
    public void setChristmas(int christmas) {
        Christmas = christmas;
    }

    public int getEaster() {
        return Easter;
    }
    public void setEaster(int easter) {
        Easter = easter;
    }
}
