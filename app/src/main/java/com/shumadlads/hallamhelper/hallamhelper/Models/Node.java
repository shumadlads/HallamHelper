package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

@Table(database = HallamHelperDB.class, name = "Nodes")
public class Node extends BaseModel {

    public Node() {
    }

    @PrimaryKey(autoincrement = true)
    int NodeId;

    @Column
    String NodeName;

    @Column
    int XCoord;

    @Column
    int YCoord;

    @Column
    int Floor;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Building", foreignKeyColumnName = "BuildingId")})
    com.shumadlads.hallamhelper.hallamhelper.Models.Building Building;

    Room Room;

    public int getNodeId() {
        return NodeId;
    }
    public void setNodeId(int nodeId) {
        NodeId = nodeId;
    }

    public String getNodeName() { return NodeName; }
    public void setNodeName(String nodeName) { NodeName = nodeName; }

    public int getXCoord() {
        return XCoord;
    }
    public void setXCoord(int XCoord) {
        this.XCoord = XCoord;
    }

    public int getYCoord() {
        return YCoord;
    }
    public void setYCoord(int YCoord) {
        this.YCoord = YCoord;
    }

    public int getFloor() { return Floor; }
    public void setFloor(int floor) { Floor = floor; }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Building getBuilding() {return Building; }
    public void setBuilding(com.shumadlads.hallamhelper.hallamhelper.Models.Building building) { Building = building; }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Room getRoom() {
        if (Room == null) {
            Room = SQLite.select()
                    .from(Room.class)
                    .where(Room_Table.Node.eq(NodeId))
                    .querySingle();
        }
        return Room;
    }
    public void setRoom(com.shumadlads.hallamhelper.hallamhelper.Models.Room room) {
        Room = room;
    }
}
