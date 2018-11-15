package com.shumadlads.hallamhelper.hallamhelper.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shumadlads.hallamhelper.hallamhelper.Models.Room;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    Cursor c = null;

    private DatabaseAccess(Context context){
        this.openHelper = new DBOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null ){
            instance = new DatabaseAccess(context);
        }
        return  instance;
    }

    public void Open(){
        this.database = openHelper.getWritableDatabase();
    }

    public void Close(){
        if(this.database != null){
        this.database.close();
        }
    }

    public List<Room> GetAllRooms(){
        Room room = null;
        List<Room> roomList = new ArrayList<>();
        c=database.rawQuery("select * from Rooms",null );
        while (c.moveToNext()){
            room = new Room(c.getInt(0), c.getString(1), c.getString(2) , c.getString(3), c.getFloat(4) , c.getFloat(5));
            roomList.add(room);
        }
        c.close();
        return  roomList;
    }
}
