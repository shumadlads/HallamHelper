package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

@Table(database = HallamHelperDB.class, name = "UserModules")
public class User_Module extends BaseModel {

        @PrimaryKey(
                autoincrement = true
        )
        long _id;

        @ForeignKey( stubbedRelationship = true,
                saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "User", foreignKeyColumnName = "UserId")}
        )
        User user;

        @ForeignKey(stubbedRelationship = true,
                saveForeignKeyModel = false, references = {@ForeignKeyReference(columnName = "Module", foreignKeyColumnName = "ModuleId")}
        )
        Module module;

        public final long getId() {
            return _id;
        }

        public final User getUser() {
            return user;
        }

        public final void setUser(User param) {
            user = param;
        }

        public final Module getModule() {
            return module;
        }

        public final void setModule(Module param) {
            module = param;
        }
    }

