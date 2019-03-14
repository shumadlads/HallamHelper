package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

import java.util.*;
public class GraphNode implements Comparable<GraphNode>{
    public int id;
    public GraphNode parent=null;
    public String name;
    public double d_value=Double.POSITIVE_INFINITY;
    public LinkedList<Step> steps =new LinkedList<Step>();
    public boolean discovered = false;

    public double x,y;

    public double h_value=0;

    public double f_value=Double.POSITIVE_INFINITY;//(f_value=d_value+h_value)


    public GraphNode(String name, int id, double x, double y){
        this.name=name;
        this.id=id;
        this.x=x;
        this.y=y;
    }

    @Override
    public int compareTo(GraphNode o) {
        return Double.compare(this.d_value,o.d_value);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public GraphNode getThisNode() {return this;}







}

