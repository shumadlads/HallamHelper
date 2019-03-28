package com.shumadlads.hallamhelper.hallamhelper.Navigation;

import com.raizlabs.android.dbflow.data.Blob;
import com.shumadlads.hallamhelper.hallamhelper.TimeTable.TimetableRecyclerViewModel;

public class NavigationRecyclerViewModel {

    public int getId() {
        return Id;
    }

    private int Id;
    public String BuildingName;
    public Blob BuildingImage;

    public NavigationRecyclerViewModel(int id,Blob image, String name) {
        Id = id;
        BuildingImage = image;
        BuildingName = name;
    }

}
