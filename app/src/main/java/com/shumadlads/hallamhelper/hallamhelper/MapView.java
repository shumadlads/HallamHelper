package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Step;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Graph;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Node;

import java.util.ArrayList;
import java.util.List;


public class MapView extends AppCompatImageView {
    int animationtime = 500;
    float width, height;
    Graph graph = new Graph();
    //int buttonClicked=0; //1-start 2-stop 3-nodes 4-edge 5-edgeStart 6-edgeStop
    int counter = 0;

    DisplayMetrics dm = getResources().getDisplayMetrics();
    float density = (dm.density); // Used to convert pixels set on nodes to dp
    float start_x, start_y;
    float stop_x, stop_y;
    Node edgeStart, edgeStop;
    int radius = 8;
    Paint paint = new Paint();
    async animationthread = new async(); //draws line

    public MapView(Context context) {
        super(context);
        init(null, 0);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < graph.nodes.size(); i++) {

            Node v = graph.nodes.get(i);

            paint.setStyle(Paint.Style.FILL);
            if (v.x == start_x && v.y == start_y) {
                paint.setColor(Color.GREEN);
                //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius, paint); //comment out when all node drawn debug is uncommented
            }
            if (v.x == stop_x && v.y == stop_y) {
                paint.setColor(Color.RED);
                //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius, paint); //comment out when all node drawn debug is uncommented
            }

            canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), ((float) radius) * density, paint); //debug draws circles on all nodes
            //canvas.drawText(v.getName(), ((float) v.x) *density, ((float) v.y) *density, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            for (int j = 0; j < graph.nodes.get(i).steps.size(); j++) {
                Node v1 = graph.nodes.get(i);
                Node v2 = v1.steps.get(j).destination;

                if (v1.steps.get(j).isPath == 1) {
                    paint.setColor(getResources().getColor(R.color.colorAccent));
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(10);
                    canvas.drawLine((((float) v1.x) * density), (((float) v1.y) * density), (((float) v2.x) * density), (((float) v2.y) * density), paint);
                }
            }
        }
    }

    public boolean onPopulate(int roomFrom, int roomTo) {

        int buildingFrom = ((((roomFrom / 10) / 10) / 10) % 10); // get first digit for building number
        int levelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number

        switch (buildingFrom) {
            case 3: {
                emb(levelFrom, roomFrom, roomTo);
                break;
            }
            case 9: { // Building Code: Cantor
                cantor(levelFrom, roomFrom, roomTo);
                break;
            }
        }


        //render
        invalidate();
        return true;
    }

    public Node getNode(float x, float y) {
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node v = graph.nodes.get(i);
            double d = Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
            if (d <= radius)
                return v;
        }
        return null;

    }

    public Graph freeGraph() {
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node current = graph.nodes.get(i);
            current.parent = null;
            current.d_value = Double.POSITIVE_INFINITY;
            current.discovered = false;
            current.h_value = 0;
            current.f_value = Double.POSITIVE_INFINITY;
            for (int j = 0; j < current.steps.size(); j++) {
                current.steps.get(j).isPath = 0;
            }
        }
        return graph;
    }

    public String Astar() {
        if (graph.nodes.size() == 0)
            return "You have not populated the route yet";
        graph = freeGraph();
        if (graph.getN(stop_x, stop_y) == null)
            return "You have not specified the destination node yet";
        graph.Astar(graph.getN(start_x, start_y), graph.getN(stop_x, stop_y));
        String text = "Quickest Route";
        animationthread = new async();
        animationthread.execute();
        invalidate();
        return text;
    }

    public class async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Node current = graph.getN(stop_x, stop_y);
            if (current.parent == null) {

                return null;
            }
            while (current != graph.getN(start_x, start_y)) {
                if (isCancelled()) break;
                Node parent = current.parent;
                Step e;
                for (int i = 0; i < current.steps.size(); i++) {
                    Step current_step = current.steps.get(i);
                    if (current_step.destination == parent) {
                        current_step.isPath = 1;

                        for (int j = 0; j < parent.steps.size(); j++) {
                            if (parent.steps.get(j).destination == current) {
                                parent.steps.get(j).isPath = 1;
                            }
                        }
                    }
                }
                current = parent;
            }
            return null;
        }
    }

    public void emb(int levelFrom, int roomFrom, int roomTo) {
        switch (levelFrom) {
            case 1: {
                embLevel1NodesAndRoutes(roomFrom, roomTo);
                break;
            }
            case 9: { //THIS IS DEBUG NEEDS RECONSIDERING
                embLevel1NodesAndRoutes(roomFrom, roomTo);
                break;
            }
        }
    }

    public void cantor(int levelFrom, int roomFrom, int roomTo) {
        switch (levelFrom) {
            case 0: {
                cantorLevel0NodesAndRoutes(roomFrom, roomTo);
                break;
            }
            case 1: {
                cantorLevel1NodesAndRoutes(roomFrom, roomTo);
                break;
            }

        }
    }

    public void cantorLevel0NodesAndRoutes(int roomFrom, int roomTo) {
        List<Node> cantorLevel0 = new ArrayList<Node>();
        //StairsAndLifts
        cantorLevel0.add(new Node("StairsAndLiftBottomLeft", counter, 48, 385));
        //StairsOnly
        boolean useLiftsOnly = true;
        if (useLiftsOnly == true) {
            cantorLevel0.add(new Node("StairsToLevel1", counter, 145, 323));
        }

        cantorLevel0.add(new Node("9099DoorEntrance", counter, 20, 335));
        cantorLevel0.add(new Node("InnerDoorTop", counter, 75, 335));
        cantorLevel0.add(new Node("BottomStairwell", counter, 75, 385));

        cantorLevel0.add(new Node("BottomMainLobby", counter, 110, 335));

        cantorLevel0.add(new Node("9021Reception", counter, 145, 285));
        cantorLevel0.add(new Node("9001Door", counter, 100, 230));
        cantorLevel0.add(new Node("9002Door", counter, 100, 165));
        cantorLevel0.add(new Node("9003Door", counter, 100, 115));
        cantorLevel0.add(new Node("9005Door", counter, 80, 58));
        cantorLevel0.add(new Node("9006Door", counter, 95, 58));
        cantorLevel0.add(new Node("9011And9012Door", counter, 170, 58));
        cantorLevel0.add(new Node("9013Door", counter, 205, 58));
        cantorLevel0.add(new Node("9015And9016Door", counter, 235, 58));
        cantorLevel0.add(new Node("9020Door", counter, 170, 115));
        cantorLevel0.add(new Node("9022Door", counter, 145, 165));
        cantorLevel0.add(new Node("9018Door", counter, 145, 190));
        cantorLevel0.add(new Node("9019Door", counter, 210, 310));
        cantorLevel0.add(new Node("9024And9025Door", counter, 223, 415));

        for (int i = 0; i < cantorLevel0.size(); i++) {
            Node temp = cantorLevel0.get(i);
            graph.addNode(cantorLevel0.get(i));
        }

        switch (roomFrom) {
            case 9099: { //entrance start node
                for (int i = 0; i < graph.nodes.size(); i++) {
                    if ("9099DoorEntrance".equals(graph.nodes.get(i).getName())) {
                        start_x = (float) graph.nodes.get(i).getX();
                        start_y = (float) graph.nodes.get(i).getY();
                        break;
                    }
                }
                break;
            }
        }


    }

    public void cantorLevel1NodesAndRoutes(int roomFrom, int roomTo) {
        List<Node> cantorLevel1 = new ArrayList<Node>();
        //StairsAndLifts
        cantorLevel1.add(new Node("StairsAndLiftBottomLeft", counter,50, 383));
        //StairsOnly
        boolean useLiftsOnly = true;
        if (useLiftsOnly == true)
        {
            cantorLevel1.add(new Node("StairsToLevel0", counter,200, 322));
        }

        for (int i = 0; i < cantorLevel1.size(); i++) {
            Node temp = cantorLevel1.get(i);
            graph.addNode(cantorLevel1.get(i));
        }
    }


    public void embLevel1NodesAndRoutes(int roomFrom, int roomTo) {
        //This will be removed when nodes are added to the database
        List<Node> embLevel2 = new ArrayList<Node>();
        embLevel2.add(new Node("3105Door", counter, 115, 100));
        embLevel2.add(new Node("3105Corridor", counter, 115, 115));
        embLevel2.add(new Node("3106Door", counter, 170, 100));
        embLevel2.add(new Node("3106Corridor", counter, 170, 115));
        embLevel2.add(new Node("NorthEastCornerCorridor", counter, 230, 115));
        embLevel2.add(new Node("3114Door", counter, 250, 190));
        embLevel2.add(new Node("3114Corridor", counter, 230, 190));
        embLevel2.add(new Node("9999DoorEntrance", counter, 10, 245));
        embLevel2.add(new Node("3118Corridor", counter, 80, 245));
        embLevel2.add(new Node("MiddleCorridorWest", counter, 100, 160));

        for (int i = 0; i < embLevel2.size(); i++) {
            Node temp = embLevel2.get(i);
            graph.addNode(embLevel2.get(i));
        }

        switch (roomTo) {
            case 3114: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3114Door".equals(embLevel2.get(i).getName())) {
                        stop_x = (float) embLevel2.get(i).getX();
                        stop_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }

            case 3106: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3106Door".equals(embLevel2.get(i).getName())) {
                        stop_x = (float) embLevel2.get(i).getX();
                        stop_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            case 3105: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3105Door".equals(embLevel2.get(i).getName())) {
                        stop_x = (float) embLevel2.get(i).getX();
                        stop_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            case 3199: {
                break;
            }
            default: {
                break;
            }
        }

        switch (roomFrom) {
            case 3114: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3114Door".equals(embLevel2.get(i).getName())) {
                        start_x = (float) embLevel2.get(i).getX();
                        start_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }

            case 3106: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3106Door".equals(embLevel2.get(i).getName())) {
                        start_x = (float) embLevel2.get(i).getX();
                        start_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            case 3105: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3105Door".equals(embLevel2.get(i).getName())) {
                        start_x = (float) embLevel2.get(i).getX();
                        start_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            case 3199: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("9999DoorEntrance".equals(embLevel2.get(i).getName())) {
                        start_x = (float) embLevel2.get(i).getX();
                        start_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            case 0006: {
                for (int i = 0; i < embLevel2.size(); i++) {
                    if ("3106Door".equals(embLevel2.get(i).getName())) {
                        start_x = (float) embLevel2.get(i).getX();
                        start_y = (float) embLevel2.get(i).getY();
                        break;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
        //setup empty node;
        Node n = null;


        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Door".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.nodes.get(i).getName())) {
                //n = getNode((float) embLevel2.get(i).getX(), (float) embLevel2.get(i).getY());
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3105 Corridor to 3106 Corridor
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3106 Door to Corridor

        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Door".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);

        //3106 Corridor to Corner
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("NorthEastCornerCorridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //Corner to 3114 Corridor
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("NorthEastCornerCorridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3114 Corridor to Door
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Door".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //9999 Door/Entrance/Exit to 3118 Corridor Node
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("9999DoorEntrance".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3118Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3118 Corridor Node to middle corridor left node
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3118Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("MiddleCorridorWest".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //middle corridor left node to 3105 corridor node
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("MiddleCorridorWest".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
    }
}
