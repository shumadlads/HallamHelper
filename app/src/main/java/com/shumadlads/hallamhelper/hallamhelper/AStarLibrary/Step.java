package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

public class Step {
    public Node source;
    public Node destination;
    public double weight;
    public int isPath=0;
    public Step(Node source, Node destination, double weight){
        this.source=source;
        this.destination=destination;
        this.weight=weight;
    }
}

