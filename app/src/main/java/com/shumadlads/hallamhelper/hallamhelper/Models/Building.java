package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

import java.util.List;

@Table(database = HallamHelperDB.class, name = "Buildings")
public class Building extends BaseModel {

    @PrimaryKey(autoincrement = true)
    int BuildingId;

    @Column
    String BuildingName;


    @Column
    Blob BuildingImage;

    List<Room> Rooms;

    public Building() {
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "Rooms")
    public List<Room> getRooms() {
        if (Rooms == null || Rooms.isEmpty()) {
            Rooms = SQLite.select()
                    .from(Room.class)
                    .where(Room_Table.Building.eq(BuildingId))
                    .queryList();
        }
        return Rooms;
    }
    public Blob getBuildingImage() {
        return BuildingImage;
    }

    public void setBuildingImage(Blob buildingImage) {
        BuildingImage = buildingImage;
    }

    public void setRooms(List<Room> rooms) {
        Rooms = rooms;
    }

    public int getBuildingId() {
        return BuildingId;
    }

    public void setBuildingId(int buildingId) {
        BuildingId = buildingId;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String buildingName) {
        BuildingName = buildingName;
    }
}
