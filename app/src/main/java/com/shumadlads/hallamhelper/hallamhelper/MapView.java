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
    float width, height;
    Graph graph = new Graph();
    int counter = 0;
    boolean useLiftsOnly = true;

    DisplayMetrics dm = getResources().getDisplayMetrics();
    float density = (dm.density); // Used to convert pixels set on nodes to dp
    float start_x, start_y;
    float stop_x, stop_y;
    Node edgeStart, edgeStop;
    int radius = 8;
    Paint paint = new Paint();
    async animationthread = new async(); //draws line
    Node stairwellNode;

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
            paint.setColor(getResources().getColor(R.color.colorAccent));
            if (v.x == start_x && v.y == start_y) {
                paint.setColor(Color.GREEN);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }
            if (v.x == stop_x && v.y == stop_y) {
                paint.setColor(Color.RED);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }

            //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), ((float) radius) * density, paint); // Uncomment for draws circles on all nodes


            paint.setColor(getResources().getColor(R.color.colorAccent));
            for (int j = 0; j < graph.nodes.get(i).steps.size(); j++) {
                Node v1 = graph.nodes.get(i);
                Node v2 = v1.steps.get(j).destination;

                if (v1.steps.get(j).isPath == 1) {
                    paint.setColor(getResources().getColor(R.color.colorAccent));  // Comment when all edge debug
                    paint.setStyle(Paint.Style.STROKE);                            // Comment when all edge debug
                    paint.setStrokeWidth(5 * density);                            // Comment when all edge debug
                    paint.setStrokeCap(Paint.Cap.ROUND); //Cap rounds corners     // Comment when all edge debug
                    canvas.drawLine((((float) v1.x) * density), (((float) v1.y) * density), (((float) v2.x) * density), (((float) v2.y) * density), paint); // Comment when all edge debug
                }
/*                paint.setColor(getResources().getColor(R.color.colorAccent));      // Uncomment for all edge debug
                paint.setStyle(Paint.Style.STROKE);                                // Uncomment for all edge debug
                paint.setStrokeWidth(5 * density);                                 // Uncomment for all edge debug
                canvas.drawLine((((float) v1.x) * density), (((float) v1.y) * density), (((float) v2.x) * density), (((float) v2.y) * density), paint); // Uncomment for all edge debug*/
            }

/*            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(7 * density);
            canvas.drawText(v.getName(), ((float) v.x) * density, ((float) v.y - 10) * density, paint);*/
        }
    }

    public boolean onPopulate(int levelFrom, int roomFrom, int roomTo) {
        graph.nodes.clear(); // Reset graph nodes if MapView is being repopulated
        //graph = new Graph();

        int buildingFrom = ((((roomFrom / 10) / 10) / 10) % 10); // get first digit for building number
        //int levelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number

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
                cantorLevel0NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }
            case 1: {
                cantorLevel1NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }

        }
    }

    public void cantorLevel0NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<Node> cantorLevel0 = new ArrayList<Node>();
        //StairsAndLifts
        cantorLevel0.add(new Node("StairsAndLiftBottomLeft", counter, 45, 368));
        cantorLevel0.add(new Node("StairsAndLiftTop", counter, 105, 55));
        //StairsOnly

        if (!useLiftsOnly) {
            cantorLevel0.add(new Node("StairsToLevel1", counter, 140, 310));
            cantorLevel0.add(new Node("StairsOnlyBottomRight", counter, 215, 360));

        }

        cantorLevel0.add(new Node("9099DoorEntrance", counter, 18, 320));
        cantorLevel0.add(new Node("InnerDoorTop", counter, 65, 320));
        cantorLevel0.add(new Node("CafeCorridor", counter, 65, 368));

        cantorLevel0.add(new Node("BottomMainLobby", counter, 105, 320));
        cantorLevel0.add(new Node("BottomUnderStairs", counter, 140, 325));

        cantorLevel0.add(new Node("9021Reception", counter, 140, 273));
        cantorLevel0.add(new Node("9021Corridor", counter, 105, 273));

        cantorLevel0.add(new Node("9001Door", counter, 95, 215));
        cantorLevel0.add(new Node("9001Corridor", counter, 105, 215));

        cantorLevel0.add(new Node("9002Door", counter, 95, 162));
        cantorLevel0.add(new Node("9002And9022Corridor", counter, 105, 162));
        cantorLevel0.add(new Node("9003Door", counter, 95, 112));
        cantorLevel0.add(new Node("9003And9020Corridor", counter, 105, 112));
        cantorLevel0.add(new Node("9005Door", counter, 71, 55));
        cantorLevel0.add(new Node("9006Door", counter, 90, 55));

        cantorLevel0.add(new Node("9011And9012Door", counter, 165, 55));
        cantorLevel0.add(new Node("9013Door", counter, 200, 55));
        cantorLevel0.add(new Node("9015And9016Door", counter, 227, 55));
        cantorLevel0.add(new Node("9020Door", counter, 165, 112));
        cantorLevel0.add(new Node("9022Door", counter, 140, 162));

        cantorLevel0.add(new Node("9018Door", counter, 140, 185));
        cantorLevel0.add(new Node("9018Corridor", counter, 105, 185));

        cantorLevel0.add(new Node("9019Door", counter, 205, 295));
        cantorLevel0.add(new Node("9019Corridor", counter, 205, 325));
        cantorLevel0.add(new Node("9024And9025Door", counter, 215, 400));
        cantorLevel0.add(new Node("AboveStairsOnlyBottomRight", counter, 215, 325));

        for (int i = 0; i < cantorLevel0.size(); i++) {
            Node temp = cantorLevel0.get(i);
            graph.addNode(cantorLevel0.get(i));
        }

        //setup empty node;
        Node n = null;

        if (!useLiftsOnly) {
            addStep("BottomMainLobby", "StairsToLevel1");
            addStep("9021Corridor", "StairsToLevel1");
            addStep( "AboveStairsOnlyBottomRight", "StairsOnlyBottomRight");
            addStep( "StairsOnlyBottomRight", "9024And9025Door");
        } else {
            addStep( "9024And9025Door", "AboveStairsOnlyBottomRight"); //Directly link these nodes since the corridor nodes rely on the stairwell
        }

        addStep("9099DoorEntrance", "InnerDoorTop");
        addStep("InnerDoorTop", "CafeCorridor");
        addStep("CafeCorridor", "StairsAndLiftBottomLeft");
        addStep("InnerDoorTop", "BottomMainLobby");
        addStep("BottomMainLobby", "9021Corridor");
        addStep( "9021Corridor", "9021Reception");
        addStep( "9021Corridor", "9001Corridor");
        addStep( "9001Corridor", "9001Door");
        addStep( "9001Corridor", "9018Corridor");
        addStep( "9018Corridor", "9018Door");
        addStep( "9018Corridor", "9002And9022Corridor");
        addStep( "9002And9022Corridor", "9002Door");
        addStep( "9002And9022Corridor", "9022Door");
        addStep( "9002And9022Corridor", "9003And9020Corridor");
        addStep( "9003And9020Corridor", "9003Door");
        addStep( "9003And9020Corridor", "9020Door");
        addStep( "9003And9020Corridor", "StairsAndLiftTop");
        addStep( "StairsAndLiftTop", "9006Door");
        addStep( "9006Door", "9005Door"); // 9006 door doubles as corridor node
        addStep( "StairsAndElevatorTop", "9011And9012Door"); // 9011And9012Door has only a door node
        addStep( "9011And9012Door", "9013Door"); //9013Door has only a door node
        addStep( "9013Door", "9015And9016Door");
        addStep( "BottomMainLobby", "BottomUnderStairs");
        addStep( "BottomUnderStairs", "9019Corridor");
        addStep( "9019Corridor", "9019Door");
        addStep( "9019Corridor", "AboveStairsOnlyBottomRight");


        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number


        //CANTOR LEVEL 0 - SET START
        if (roomCodeLevelFrom == levelFrom) {
            switch (roomFrom) {
                case 9099: { //entrance start node
                    setStartNode("9099DoorEntrance");
                    break;
                }
                case 9098: { //Building select node
                    //do not assign node.
                    break;
                }
                case 9001: {
                    setStartNode("9001Door");
                    break;
                }
                case 9002: {
                    setStartNode("9002Door");
                    break;
                }
                case 9003: {
                    setStartNode("9003Door");
                    break;
                }
                case 9005: {
                    setStartNode("9005Door");
                    break;
                }
                case 9006: {
                    setStartNode("9006Door");
                    break;
                }
                case 9011: case 9012: {
                    setStartNode("9011And9012Door");
                    break;
                }
                case 9013: {
                    setStartNode("9013Door");
                    break;
                }
                case 9015: case 9016: {
                    setStartNode("9015And9016Door");
                    break;
                }
                case 9018: {
                    setStartNode("9018Door");
                    break;
                }
                case 9019: {
                    setStartNode("9019Door");
                    break;
                }
                case 9020: {
                    setStartNode("9020Door");
                    break;
                }
                case 9021: {
                    setStartNode("9021Reception");
                    break;
                }
                case 9022: {
                    setStartNode("9022Door");
                    break;
                }
                case 9024: case 9025: {
                    setStartNode("9024And9025Door");
                    break;
                }
                default: {
                    setStartNode("9099DoorEntrance"); // If node isn't found, set start node as main entrance
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelFrom) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    Node stairwellFrom = getStairwellNode(stairwellNode);
                    if (stairwellFrom != null)
                        setStartNode(stairwellFrom.getName());
                    break;
                }
                case 4: { //entrance start node
                    setEndNode("StairsAndLiftBottomLeft");
                    break;
                }
            }
        }

        //CANTOR LEVEL 0 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9099: { //entrance start node
                    setEndNode("9099DoorEntrance");
                    break;
                }
                case 9001: {
                    setEndNode("9001Door");
                    break;
                }
                case 9002: {
                    setEndNode("9002Door");
                    break;
                }
                case 9003: {
                    setEndNode("9003Door");
                    break;
                }
                case 9005: {
                    setEndNode("9005Door");
                    break;
                }
                case 9006: {
                    setEndNode("9006Door");
                    break;
                }
                case 9011: case 9012: {
                    setEndNode("9011And9012Door");
                    break;
                }
                case 9013: {
                    setEndNode("9013Door");
                    break;
                }
                case 9015: case 9016: {
                    setEndNode("9015And9016Door");
                    break;
                }
                case 9018: {
                    setEndNode("9018Door");
                    break;
                }
                case 9019: {
                    setEndNode("9019Door");
                    break;
                }
                case 9020: {
                    setEndNode("9020Door");
                    break;
                }
                case 9021: {
                    setEndNode("9021Reception");
                    break;
                }
                case 9022: {
                    setEndNode("9022Door");
                    break;
                }
                case 9024: case 9025: {
                    setEndNode("9024And9025Door");
                    break;
                }
                default:{
                    setEndNode("9099DoorEntrance");
                    break;
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    List<String> stairsAndElevators = new ArrayList<String>();
                    stairsAndElevators.add("StairsAndLiftBottomLeft");
                    stairsAndElevators.add("StairsAndLiftTop");
                    List<String> stairsOnly = new ArrayList<String>();
                    stairsOnly.add("StairsToLevel1");
                    stairsOnly.add("StairsOnlyBottomRight");

                    Node startNode = graph.getN(start_x, start_y);
                    Node stairNode = getNearestStairNode(startNode, stairsAndElevators, stairsOnly);
                    setEndNode(stairNode.name);
                    break;
                }
                case 4: { //entrance start node
                    setEndNode("StairsAndLiftBottomLeft");
                    break;
                }
            }
        }

    }

    public void cantorLevel1NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<Node> cantorLevel1 = new ArrayList<Node>();
        //StairsAndLifts
        cantorLevel1.add(new Node("StairsAndLiftBottomLeft", counter, 50, 365));
        //StairsOnly
        //boolean useLiftsOnly = false;
        if (!useLiftsOnly) {
            cantorLevel1.add(new Node("StairsToLevel0", counter, 185, 308));
        }

        cantorLevel1.add(new Node("StairsAndLiftBottomLeftCorridor", counter, 58, 365));
        cantorLevel1.add(new Node("9141Door", counter, 58, 390));
        cantorLevel1.add(new Node("9141Corridor", counter, 58, 380));

        for (int i = 0; i < cantorLevel1.size(); i++) {
            Node temp = cantorLevel1.get(i);
            graph.addNode(cantorLevel1.get(i));
        }

        addStep("StairsAndLiftBottomLeft", "StairsAndLiftBottomLeftCorridor");
        addStep("StairsAndLiftBottomLeftCorridor", "9141Corridor");
        addStep("9141Corridor", "9141Door");

        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 1 - SET START
        if (levelFrom == roomCodeLevelFrom) {
            switch (roomFrom) {
                case 9141: {
                    setStartNode("9141Door");
                }
                default: {
                    setStartNode("9141Door");
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelFrom) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    Node stairwellFrom = getStairwellNode(stairwellNode);
                    if (stairwellFrom != null)
                        setStartNode(stairwellFrom.getName());
                    break;
                }
                case 4: { //entrance start node
                    setEndNode("StairsAndLiftBottomLeft");
                    break;
                }
            }
        }

        //CANTOR LEVEL 1 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9141: {
                    setEndNode("9141Door");
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    List<String> stairsAndElevators = new ArrayList<String>();
                    stairsAndElevators.add("StairsAndLiftBottomLeft");
                    List<String> stairsOnly = new ArrayList<String>();
                    //stairsOnly.add("StairsToLevel0");

                    Node startNode = graph.getN(start_x, start_y);
                    Node stairNode = getNearestStairNode(startNode, stairsAndElevators, stairsOnly);
                    setEndNode(stairNode.name);
                    break;
                }
                case 4: { //entrance start node
                    setEndNode("StairsAndLiftBottomLeft");
                    break;
                }
            }
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

    public void setStartNode(String nodeStart) {
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (nodeStart.equals(graph.nodes.get(i).getName())) {
                start_x = (float) graph.nodes.get(i).getX();
                start_y = (float) graph.nodes.get(i).getY();
                break;
            }
        }
    }

    public void setEndNode(String nodeEnd) {
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (nodeEnd.equals(graph.nodes.get(i).getName())) {
                stop_x = (float) graph.nodes.get(i).getX();
                stop_y = (float) graph.nodes.get(i).getY();
                break;
            }
        }
    }

    public Node getNearestStairNode(Node startNode, List<String> elevatorAndStairNames, List<String> stairOnlyNames) {
        Node returnNode = new Node("", 0, 0, 0);
        double leastSteps = Double.POSITIVE_INFINITY;

        if (!useLiftsOnly) {
            elevatorAndStairNames.addAll(stairOnlyNames);
        }

        for (int i = 0; i < elevatorAndStairNames.size(); i++) {
            for (int j = 0; j < graph.nodes.size(); j++) {
                if (elevatorAndStairNames.get(i).equals(graph.nodes.get(j).getName())) {

                    Node tempNode = graph.nodes.get(j);
                    graph = freeGraph();
                    double compareSteps = graph.pathCount(startNode, tempNode);

                    if (leastSteps > compareSteps) {
                        leastSteps = compareSteps;
                        returnNode = tempNode;
                    }
                    break;
                }
            }
        }
        stairwellNode = returnNode;
        return returnNode;
    }

    public void addStep(String stepStart, String stepEnd) {
        Node n;
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (stepStart.equals(graph.nodes.get(i).getName())) {
                n = graph.nodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (stepEnd.equals(graph.nodes.get(i).getName())) {
                //n = getNode((float) embLevel2.get(i).getX(), (float) embLevel2.get(i).getY());
                n = graph.nodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
    }

    public Node getStairwellNode(Node n) {
        double x = n.getX();
        double y = n.getY();
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node v = graph.nodes.get(i);
            double d = Math.sqrt((x - v.getX()) * (x - v.getX()) + (y - v.getY()) * (y - v.getY())); // Returns correctly rounded square root of the potential vs the stairwell
            if (d <= radius)
                return v;
        }
        return null;

    }
}
