package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

import java.util.List;

@Table(database = HallamHelperDB.class, name = "Users")
public class User extends BaseModel {

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    @PrimaryKey(autoincrement = true)
     int UserId;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Column
     String UserName;

    @Column
     String Password;

    @Column
     int ThemeSetting;

    public User() {
    }
}
