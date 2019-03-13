package com.shumadlads.hallamhelper.hallamhelper.AStarLibrary;

import java.util.*;
public class Graph {

    public ArrayList<Node> nodes =new ArrayList<Node>();

    public void addNode(Node n1){
        nodes.add(n1);
    }
    public void addStep(Node source, Node destination, double weight){
        source.steps.add(new Step(source,destination,weight));
        destination.steps.add(new Step(destination,source,weight));
    }

    public Node getN(double x, double y){
        for(int i = 0; i< nodes.size(); i++)
            if(nodes.get(i).x==x&& nodes.get(i).y==y)
                return nodes.get(i);

        return null;
    }

    public void Astar(Node start, Node destination){
        String text="Alg: A*(A star) ";
        //Init
        long startTime = System.nanoTime();
        LinkedList<Node> CLOSED = new LinkedList<Node>();
        comparator comparator = new comparator();
        PriorityQueue<Node> OPEN = new PriorityQueue<Node>(1,comparator);
        start.d_value=0;
        start.f_value=0;
        OPEN.add(start);



        while(!OPEN.isEmpty()){

            Node extracted = OPEN.poll();
            extracted.discovered=true;
            CLOSED.add(extracted);
            if(extracted==destination){
                break;
            }



            for(int i = 0; i<extracted.steps.size(); i++){

                Step step = extracted.steps.get(i);

                Node neighbor = step.destination;
                if(neighbor.discovered==false){

                    heuristic(neighbor,destination);
                    if(neighbor.f_value>extracted.f_value+ step.weight){
                        neighbor.d_value=extracted.d_value+ step.weight;
                        heuristic(neighbor,destination);
                        neighbor.f_value=neighbor.d_value+neighbor.h_value;
                        neighbor.parent=extracted;

                        OPEN.remove(neighbor);
                        OPEN.add(neighbor);
                    }
                }

            }
        }
        long stopTime = System.nanoTime();


        if(destination.parent==null)
            text="This path does not exist"; //for debug
        else{
            text+=" Node ne CLOSED: "+CLOSED.size();
            System.out.println();

            Stack<Node> stack = new Stack<Node>();
            Node current = destination;
            while(current!=null){
                stack.push(current);
                current = current.parent;
            }
            int hops = stack.size() - 1;
            double path_length = destination.d_value;
            text+=" Nr.Hops:"+hops+" Path length: "+String.format( "%.2f", path_length )+" Time: "+(stopTime-startTime)+" ns";
        }
    }
    public void heuristic(Node n, Node destination){

        n.h_value=Math.sqrt((n.x-destination.x)*(n.x-destination.x)+
                (n.y-destination.y)*(n.y-destination.y));
    }


}

