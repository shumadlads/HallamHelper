package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

public class Step {
    public GraphNode source;
    public GraphNode destination;
    public double weight;
    public int isPath=0;
    public Step(GraphNode source, GraphNode destination, double weight){
        this.source=source;
        this.destination=destination;
        this.weight=weight;
    }
}

