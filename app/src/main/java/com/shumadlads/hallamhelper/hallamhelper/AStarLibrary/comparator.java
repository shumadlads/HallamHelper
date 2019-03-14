package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

import java.util.Comparator;


public class comparator implements Comparator<GraphNode>{

    @Override
    public int compare(GraphNode arg0, GraphNode arg1) {
        // TODO Auto-generated method stub
        return Double.compare(arg0.f_value,arg1.f_value);
    }

}

