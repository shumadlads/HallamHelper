package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

import java.util.*;
public class Node implements Comparable<Node>{
    public int id;
    public Node parent=null;
    public double d_value=Double.POSITIVE_INFINITY;
    public LinkedList<Step> steps =new LinkedList<Step>();
    public boolean discovered = false;

    public double x,y;

    public double h_value=0;

    public double f_value=Double.POSITIVE_INFINITY;//(f_value=d_value+h_value)


    public Node(int id, double x, double y){
        this.id=id;
        this.x=x;
        this.y=y;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.d_value,o.d_value);
    }




}

