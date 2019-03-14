package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

@Table(database = HallamHelperDB.class, name = "UserSession")
public class User_Session extends BaseModel {

    public User_Session() {
    }

    @PrimaryKey(autoincrement = true)
    long _id;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "User", foreignKeyColumnName = "UserId")})
    User user;

    @ForeignKey(stubbedRelationship = true, saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Session", foreignKeyColumnName = "SessionId")})
    Session session;

    public final long getId() {
        return _id;
    }

    public final User getUser() {
        return user;
    }
    public final void setUser(User param) {
        user = param;
    }

    public final Session getSession() {
        return session;
    }
    public final void setSession(Session param) {
        session = param;
    }
}

