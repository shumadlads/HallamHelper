package com.shumadlads.hallamhelper.hallamhelper.Models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.shumadlads.hallamhelper.hallamhelper.HallamHelperDB;

import java.util.List;

@Table(database = HallamHelperDB.class, name = "Modules")
public class Module extends BaseModel {

    @PrimaryKey(autoincrement = true)
     int ModuleId;

    @Column
     String ModuleName;

    @Column
    String ModuleLeader;

    public String getModuleNickname() {
        return ModuleNickname;
    }

    public void setModuleNickname(String moduleNickname) {
        ModuleNickname = moduleNickname;
    }

    @Column
    String ModuleNickname;


   @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "Sessions")
    public List<Session> getSessions() {
       if (Sessions == null || Sessions.isEmpty()) {
           Sessions = SQLite.select()
                    .from(Session.class)
                    .where(Session_Table.Module.eq(ModuleId))
                    .queryList();
        }
        return Sessions;
    }

    public void setSessions(List<Session> sessions) {
        Sessions = sessions;
    }

    List<Session> Sessions ;

    public int getModuleId() {
        return ModuleId;
    }

    public void setModuleId(int moduleId) {
        ModuleId = moduleId;
    }

    public String getModuleLeader() {
        return ModuleLeader;
    }

    public void setModuleLeader(String moduleLeader) {
        ModuleLeader = moduleLeader;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public Module() {
    }

    @Override
    public String toString() {
        return ModuleName;
    }
}
