package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

import java.util.List;

@Table(database = HallamHelperDB.class, name = "Users")
public class User extends BaseModel {

    public User() {
    }

    @PrimaryKey(autoincrement = true)
    int UserId;

    @Column
    String UserName;

    @Column
    String Password;

    @Column
    int ThemeSetting;

    public int getUserId() {
        return UserId;
    }
    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public int getThemeSetting() { return ThemeSetting; }
    public void setThemeSetting(int themeSetting) { ThemeSetting = themeSetting; }
}
