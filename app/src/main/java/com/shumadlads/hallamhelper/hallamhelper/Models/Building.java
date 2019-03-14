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

    public Building() {
    }

    @PrimaryKey(autoincrement = true)
    int BuildingId;

    @Column
    String BuildingName;

    @Column
    Blob BuildingImage;

    List<Node> Nodes;

    public int getBuildingId() { return BuildingId; }
    public void setBuildingId(int buildingId) { BuildingId = buildingId; }

    public String getBuildingName() { return BuildingName; }
    public void setBuildingName(String buildingName) { BuildingName = buildingName; }

    public Blob getBuildingImage() {
        return BuildingImage;
    }
    public void setBuildingImage(Blob buildingImage) { BuildingImage = buildingImage; }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "Nodes")
    public List<Node> getNodes() {
        if (Nodes == null || Nodes.isEmpty()) {
            Nodes = SQLite.select()
                    .from(Node.class)
                    .where(Node_Table.Building.eq(BuildingId))
                    .queryList();
        }
        return Nodes;
    }
    public void setNodes(List<Node> nodes) { Nodes = nodes; }
}
