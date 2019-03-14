package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.GraphNode;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Step;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Graph;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node_Table;

import java.util.ArrayList;
import java.util.List;


public class MapView extends AppCompatImageView {
    int animationtime = 500;
    float width, height;
    Graph graph = new Graph();
    //int buttonClicked=0; //1-start 2-stop 3-graphNodes 4-edge 5-edgeStart 6-edgeStop
    int counter = 0;

    float start_x, start_y;
    float stop_x, stop_y;
    GraphNode edgeStart, edgeStop;
    int radius = 25;
    Paint paint = new Paint();
    async animationthread = new async();

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
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = (dm.density); // Used to convert pixels set on graphNodes to dp
        for (int i = 0; i < graph.graphNodes.size(); i++) {

            GraphNode v = graph.graphNodes.get(i);

            paint.setStyle(Paint.Style.FILL);
            if (v.x == start_x && v.y == start_y) {
                paint.setColor(Color.GREEN);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius, paint); //comment out when all node drawn debug is uncommented
            }
            if (v.x == stop_x && v.y == stop_y) {
                paint.setColor(Color.RED);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius, paint); //comment out when all node drawn debug is uncommented
            }

            //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius, paint); //debug draws circles on all graphNodes


            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            for (int j = 0; j < graph.graphNodes.get(i).steps.size(); j++) {
                GraphNode v1 = graph.graphNodes.get(i);
                GraphNode v2 = v1.steps.get(j).destination;

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
            }
            case 9: { // Building Code: Cantor
                cantor(levelFrom, roomFrom, roomTo);
            }
        }


        //render
        invalidate();
        return true;
    }

    public GraphNode getNode(float x, float y) {
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            GraphNode v = graph.graphNodes.get(i);
            double d = Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
            if (d <= radius)
                return v;
        }
        return null;

    }

    public Graph freeGraph() {
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            GraphNode current = graph.graphNodes.get(i);
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
        if (graph.graphNodes.size() == 0)
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

            GraphNode current = graph.getN(stop_x, stop_y);
            if (current.parent == null) {

                return null;
            }
            while (current != graph.getN(start_x, start_y)) {
                if (isCancelled()) break;
                GraphNode parent = current.parent;
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
            }
            case 9: { //THIS IS DEBUG NEEDS RECONSIDERING
                embLevel1NodesAndRoutes(roomFrom, roomTo);
            }
        }
    }

    public void cantor(int levelFrom, int roomFrom, int roomTo) {
        switch (levelFrom) {
            case 0: {
                embLevel1NodesAndRoutes(roomFrom, roomTo);
            }

        }
    }


    public void embLevel1NodesAndRoutes(int roomFrom, int roomTo) {
        //This will be removed when graphNodes are added to the database
        List<Node> nodes = SQLite.select().from(Node.class).where(Node_Table.Building.eq(4)).queryList();
        List<GraphNode> embLevel2 = new ArrayList();
        for (Node n:nodes) {
            embLevel2.add(new GraphNode(n.getNodeName(),counter,n.getXCoord(),n.getYCoord()));
        }

        for (int i = 0; i < embLevel2.size(); i++) {
            GraphNode temp = embLevel2.get(i);
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
            case 3999: {
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
            case 3999: {
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
        GraphNode n = null;


        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Door".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.graphNodes.get(i).getName())) {
                //n = getNode((float) embLevel2.get(i).getX(), (float) embLevel2.get(i).getY());
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3105 Corridor to 3106 Corridor
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3106 Door to Corridor

        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Door".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);

        //3106 Corridor to Corner
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3106Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("NorthEastCornerCorridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //Corner to 3114 Corridor
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("NorthEastCornerCorridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3114 Corridor to Door
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3114Door".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //9999 Door/Entrance/Exit to 3118 Corridor GraphNode
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("9999DoorEntrance".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3118Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //3118 Corridor GraphNode to middle corridor left node
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3118Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("MiddleCorridorWest".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
        //middle corridor left node to 3105 corridor node
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("MiddleCorridorWest".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < embLevel2.size(); i++) {
            if ("3105Corridor".equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
    }
}
