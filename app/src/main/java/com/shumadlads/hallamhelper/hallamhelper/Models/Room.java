package com.shumadlads.hallamhelper.hallamhelper.Models;

public class Room {
    private int RoomId;
    private String RoomName;
    private String Floor;
    private String Building;
    private float xCoord;
    private float yCoord;

    public Room(int roomId, String roomName, String floor, String building, float xCoord, float yCoord) {
        RoomId = roomId;
        RoomName = roomName;
        Floor = floor;
        Building = building;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public void setBuilding(String building) {
        Building = building;
    }

    public void setxCoord(float xCoord) {
        this.xCoord = xCoord;
    }

    public void setyCoord(float yCoord) {
        this.yCoord = yCoord;
    }
    public String getFloor() {
        return Floor;
    }

    public String getBuilding() {
        return Building;
    }

    public float getxCoord() {
        return xCoord;
    }

    public float getyCoord() {
        return yCoord;
    }
    public int getRoomId() {
        return RoomId;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    @Override
    public String toString() {
        return RoomName;
    }
}
