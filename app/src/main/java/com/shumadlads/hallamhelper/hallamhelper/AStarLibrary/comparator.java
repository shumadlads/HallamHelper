package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

import java.util.Comparator;


public class comparator implements Comparator<Node>{

    @Override
    public int compare(Node arg0, Node arg1) {
        // TODO Auto-generated method stub
        return Double.compare(arg0.f_value,arg1.f_value);
    }

}

