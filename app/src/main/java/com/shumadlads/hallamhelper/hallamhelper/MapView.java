package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Step;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Graph;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Node;


public class MapView extends View {
    int animationtime=500;
    float width,height;
    Graph graph=new Graph();
    //int buttonClicked=0; //1-start 2-stop 3-nodes 4-edge 5-edgeStart 6-edgeStop
    int counter = 0;

    float start_x,start_y;
    float stop_x,stop_y;
    Node edgeStart,edgeStop;
    int radius =25;
    Paint paint = new Paint();
    async animationthread = new async();
    public MapView(Context context){
        super(context);
        init(null,0);
    }
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public MapView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    public void init(AttributeSet attrs,int defStyle){
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

    }



    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0; i<graph.nodes.size(); i++){

            Node v = graph.nodes.get(i);

            paint.setStyle(Paint.Style.FILL);
            if(v.x==start_x&&v.y==start_y) {
                paint.setColor(Color.GREEN);
                canvas.drawCircle((float) v.x, (float) v.y, radius, paint); //comment out when all node drawn debug is uncommented
            }
            if(v.x==stop_x&&v.y==stop_y) {
                paint.setColor(Color.RED);
                canvas.drawCircle((float) v.x, (float) v.y, radius, paint); //comment out when all node drawn debug is uncommented
            }

            //canvas.drawCircle((float) v.x, (float) v.y, radius, paint); //debug draws circles on all nodes



            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            for(int j = 0; j<graph.nodes.get(i).steps.size(); j++){
                Node v1=graph.nodes.get(i);
                Node v2=v1.steps.get(j).destination;

                if(v1.steps.get(j).isPath==1) {
                    paint.setColor(getResources().getColor(R.color.colorAccent));
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(10);
                    canvas.drawLine((float) v1.x, (float) v1.y, (float) v2.x, (float) v2.y, paint);
                }
            }





        }


    }

    public boolean onPopulate(int startRoom, int stopRoom){



        //3105 Door Node
        graph.addNode(counter, 350, 250);
        counter++;
        //3105 Corridor Node
        graph.addNode(counter, 350, 300);
        counter++;
        //3106 Door Node
        graph.addNode(counter, 500, 250);
        counter++;
        //3106 Corridor Node
        graph.addNode(counter, 500, 300);
        counter++;
        //EMB Level 2 Corner Corridor Node
        graph.addNode(counter, 700, 300);
        counter++;
        //3114 Corridor Node
        graph.addNode(counter, 700, 625);
        counter++;
        //3114 Door Node
        graph.addNode(counter, 750, 625);
        counter++;
        //9999 Door/Entrance to level 2
        graph.addNode(counter, 50, 750);
        counter++;
        //3118 Corridor Node
        graph.addNode(counter, 300, 750);
        counter++;
        //middle corridor left node
        graph.addNode(counter, 325, 470);
        counter++;

        switch (stopRoom) {
            case 3114: {
                stop_x = 750;
                stop_y = 625;
                break;
            }

            case 3106:{
                stop_x = 500;
                stop_y = 250;
                break;
            }
            case 3105:{
                stop_x = 350;
                stop_y = 250;
                break;
            }
            case 9999:{
                break;
            }
            default: {
                break;
            }
        }

        switch (startRoom) {
            case 3114: {
                start_x = 750;
                start_y = 625;
                break;
            }

            case 3106:{
                start_x = 500;
                start_y = 250;
                break;
            }
            case 3105:{
                start_x = 350;
                start_y = 250;
                break;
            }
            case 9999:{
                start_x = 50;
                start_y = 750;
                break;
            }
            default:{
                break;
            }
        }
        //setup empty node;
        Node n;


        //3105 Door to Corridor
        n= getNode(350,250);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(350, 300);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3105 Corridor to 3106 Corridor
        n= getNode(350, 300);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(500, 300);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3106 Door to Corridor

        n= getNode(500,250);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(500, 300);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);

        //3106 Corridor to Corner
        n= getNode(500, 300);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(700, 300);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //Corner to 3114 Corridor
        n= getNode(700, 300);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(700, 625);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3114 Corridor to Door
        n= getNode(700, 625);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(750, 625);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //9999 Door/Entrance/Exit to 3118 Corridor Node
        n= getNode(50, 750);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(300, 750);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3118 Corridor Node to middle corridor left node
        n= getNode(300, 750);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(325, 470);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //middle corridor left node to 3105 corridor node
        n= getNode(325, 470);
        if(n!=null){
            edgeStart=n;
        }
        n= getNode(350, 300);
        if (n!=null){
            edgeStop=n;
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //render
        invalidate();
        return true;
    }

    /*public boolean checkpoint(float x, float y){
        for(int i=0;i<graph.nodes.size();i++){
            Node v= graph.nodes.get(i);
            double d = Math.sqrt((x - v.x) * (x - v.x)+(y-v.y)*(y-v.y));
            if(d<2* radius)
                return true;
        }
        return false;

    }*/
    public Node getNode(float x, float y){
        for(int i = 0; i<graph.nodes.size(); i++){
            Node v= graph.nodes.get(i);
            double d = Math.sqrt((x - v.x) * (x - v.x)+(y-v.y)*(y-v.y));
            if(d<= radius)
                return v;
        }
        return null;

    }
    public Graph freeGraph(){
        for(int i = 0; i<graph.nodes.size(); i++){
            Node current = graph.nodes.get(i);
            current.parent=null;
            current.d_value= Double.POSITIVE_INFINITY;
            current.discovered=false;
            current.h_value=0;
            current.f_value=Double.POSITIVE_INFINITY;
            for(int j = 0; j<current.steps.size(); j++){
                current.steps.get(j).isPath=0;
            }
        }
        return graph;
    }

    public String Astar(){
        if(graph.nodes.size()==0)
            return "You have not populated the route yet";
        graph=freeGraph();
        if(graph.getN(stop_x, stop_y)==null)
            return "You have not specified the destination node yet";
        graph.Astar(graph.getN(start_x, start_y), graph.getN(stop_x, stop_y));
        String text="Quickest Route";
        animationthread = new async();
        animationthread.execute();
        invalidate();
        return text;
    }
    public class async extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            Node current = graph.getN(stop_x,stop_y);
            if(current.parent==null){

                return null;
            }
            while(current!=graph.getN(start_x,start_y)){
                if (isCancelled()) break;
                Node parent = current.parent;
                Step e;
                for(int i = 0; i<current.steps.size(); i++){
                    Step current_step =current.steps.get(i);
                    if(current_step.destination==parent) {
                        current_step.isPath = 1;

                        for(int j = 0; j<parent.steps.size(); j++) {
                            if (parent.steps.get(j).destination == current) {
                                parent.steps.get(j).isPath = 1;
                            }
                        }
                    }
                }
                current=parent;
            }
            return null;
        }
    }

}
