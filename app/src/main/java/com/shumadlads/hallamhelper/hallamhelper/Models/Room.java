package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

import java.util.List;

@Table(database = HallamHelperDB.class, name = "Rooms")
public class Room extends BaseModel {

    public Room() {
    }

    @PrimaryKey
    int RoomId;

    @Column
    String RoomName;

    @Column
    int Capacity;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Node", foreignKeyColumnName = "NodeId")})
    Node Node;

    List<Session> Sessions;

    public int getRoomId() {
        return RoomId;
    }
    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    public String getRoomName() {
        return RoomName;
    }
    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public int getCapacity() {
        return Capacity;
    }
    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public com.shumadlads.hallamhelper.hallamhelper.Models.Node getNode() {
        return Node;
    }
    public void setNode(com.shumadlads.hallamhelper.hallamhelper.Models.Node node) { Node = node; }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "Sessions")
    public List<Session> getSessions() {
        if (Sessions == null || Sessions.isEmpty()) {
            Sessions = SQLite.select()
                    .from(Session.class)
                    .where(Session_Table.Room.eq(RoomId))
                    .queryList();
        }
        return Sessions;
    }
    public void setSessions(List<Session> sessions) { Sessions = sessions; }

    @Override
    public String toString() {
        return RoomName;
    }
}
