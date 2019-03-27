package com.shumadlads.hallamhelper.hallamhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.GraphNode;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Step;
import com.shumadlads.hallamhelper.hallamhelper.AStarLibrary.Graph;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node;
import com.shumadlads.hallamhelper.hallamhelper.Models.Node_Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapView extends AppCompatImageView {

    private SharedPreferences SharedPrefs;
    float width, height;
    Graph graph = new Graph();
    int counter = 0;
    boolean useLiftsOnly = false;

    DisplayMetrics dm = getResources().getDisplayMetrics();
    float density = (dm.density); // Used to convert pixels set on nodes to dp
    float start_x, start_y;
    float stop_x, stop_y;
    GraphNode edgeStart, edgeStop;
    int radius = 8;
    Paint paint = new Paint();
    async animationthread = new async(); //draws line
    GraphNode stairwellNode;

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

        for (int i = 0; i < graph.graphNodes.size(); i++) {

            GraphNode v = graph.graphNodes.get(i);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            if (v.x == start_x && v.y == start_y) {
                paint.setColor(Color.GREEN);
                //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }
            if (v.x == stop_x && v.y == stop_y) {
                paint.setColor(Color.RED);
                //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }

            canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), ((float) radius) * density, paint); // Uncomment for draws circles on all nodes


            paint.setColor(getResources().getColor(R.color.colorAccent));
            for (int j = 0; j < graph.graphNodes.get(i).steps.size(); j++) {
                GraphNode v1 = graph.graphNodes.get(i);
                GraphNode v2 = v1.steps.get(j).destination;

                if (v1.steps.get(j).isPath == 1) {
/*                    paint.setColor(getResources().getColor(R.color.colorAccent));  // Comment when all edge debug
                    paint.setStyle(Paint.Style.STROKE);                            // Comment when all edge debug
                    paint.setStrokeWidth(5 * density);                            // Comment when all edge debug
                    paint.setStrokeCap(Paint.Cap.ROUND); //Cap rounds corners     // Comment when all edge debug
                    canvas.drawLine((((float) v1.x) * density), (((float) v1.y) * density), (((float) v2.x) * density), (((float) v2.y) * density), paint); // Comment when all edge debug*/
                }
                paint.setColor(getResources().getColor(R.color.colorAccent));      // Uncomment for all edge debug
                paint.setStyle(Paint.Style.STROKE);                                // Uncomment for all edge debug
                paint.setStrokeWidth(5 * density);                                 // Uncomment for all edge debug
                canvas.drawLine((((float) v1.x) * density), (((float) v1.y) * density), (((float) v2.x) * density), (((float) v2.y) * density), paint); // Uncomment for all edge debug
            }

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(7 * density);
            canvas.drawText(v.getName(), ((float) v.x) * density, ((float) v.y - 10) * density, paint);
        }
    }

    public boolean onPopulate(int levelFrom, int roomFrom, int roomTo) {

        graph.graphNodes.clear(); // Reset graph nodes if MapView is being repopulated
        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        useLiftsOnly = SharedPrefs.getBoolean(getContext().getString(R.string.SP_UseLift), false);

        //graph = new Graph();

        int buildingFrom = ((((roomFrom / 10) / 10) / 10) % 10); // get first digit for building number

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
            case 2: {
                cantorLevel2NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }

        }
    }

    public void cantorLevel0NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel0 = new ArrayList<GraphNode>();
        //StairsAndLifts
        cantorLevel0.add(new GraphNode("StairsAndLiftBottomLeft", counter, 45, 368));
        cantorLevel0.add(new GraphNode("StairsAndLiftTop", counter, 105, 55));
        //StairsOnly

        if (!useLiftsOnly) {
            cantorLevel0.add(new GraphNode("StairsToLevel1", counter, 140, 310));
            cantorLevel0.add(new GraphNode("StairsOnlyBottomRight", counter, 215, 360));

        }
        cantorLevel0.add(new GraphNode("9098", counter, 1000, 1000)); // EMPTY NODE FOR BUILDING DISPLAY

        cantorLevel0.add(new GraphNode("9099DoorEntrance", counter, 18, 320));
        cantorLevel0.add(new GraphNode("InnerDoorTop", counter, 65, 320));
        cantorLevel0.add(new GraphNode("CafeCorridor", counter, 65, 368));

        cantorLevel0.add(new GraphNode("BottomMainLobby", counter, 105, 320));
        cantorLevel0.add(new GraphNode("BottomUnderStairs", counter, 140, 325));

        cantorLevel0.add(new GraphNode("9021Reception", counter, 140, 273));
        cantorLevel0.add(new GraphNode("9021Corridor", counter, 105, 273));

        cantorLevel0.add(new GraphNode("9001Door", counter, 95, 215));
        cantorLevel0.add(new GraphNode("9001Corridor", counter, 105, 215));

        cantorLevel0.add(new GraphNode("9002Door", counter, 95, 162));
        cantorLevel0.add(new GraphNode("9002And9022Corridor", counter, 105, 162));
        cantorLevel0.add(new GraphNode("9003Door", counter, 95, 112));
        cantorLevel0.add(new GraphNode("9003And9020Corridor", counter, 105, 112));
        cantorLevel0.add(new GraphNode("9005Door", counter, 71, 55));
        cantorLevel0.add(new GraphNode("9006Door", counter, 90, 55));

        cantorLevel0.add(new GraphNode("9011And9012Door", counter, 165, 55));
        cantorLevel0.add(new GraphNode("9013Door", counter, 200, 55));
        cantorLevel0.add(new GraphNode("9015And9016Door", counter, 227, 55));
        cantorLevel0.add(new GraphNode("9020Door", counter, 165, 112));
        cantorLevel0.add(new GraphNode("9022Door", counter, 140, 162));

        cantorLevel0.add(new GraphNode("9018Door", counter, 140, 185));
        cantorLevel0.add(new GraphNode("9018Corridor", counter, 105, 185));

        cantorLevel0.add(new GraphNode("9019Door", counter, 205, 295));
        cantorLevel0.add(new GraphNode("9019Corridor", counter, 205, 325));
        cantorLevel0.add(new GraphNode("9024And9025Door", counter, 215, 400));
        cantorLevel0.add(new GraphNode("AboveStairsOnlyBottomRight", counter, 215, 325));

        for (int i = 0; i < cantorLevel0.size(); i++) {
            GraphNode temp = cantorLevel0.get(i);
            graph.addNode(cantorLevel0.get(i));
        }

        //setup empty node;
        GraphNode n = null;

        if (!useLiftsOnly) {
            addStep("BottomMainLobby", "StairsToLevel1");
            // addStep("9021Corridor", "StairsToLevel1");
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
                    setStartNode("9098");
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
                    GraphNode stairwellFrom = getStairwellNode(stairwellNode);
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
                case 9098:{
                    setEndNode("9098");
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
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft", "StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsToLevel1", "StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
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
        List<GraphNode> cantorLevel1 = new ArrayList<GraphNode>();
        //StairsAndLifts
        cantorLevel1.add(new GraphNode("StairsAndLiftBottomLeft", counter, 50, 365));
        cantorLevel1.add(new GraphNode("StairsAndLiftTop", counter, 110, 55));
        //StairsOnly

        if (!useLiftsOnly) {
            cantorLevel1.add(new GraphNode("StairsToLevel0", counter, 190, 308));
            cantorLevel1.add(new GraphNode("StairsOnlyBottomRight", counter, 215, 357));

        }

        cantorLevel1.add(new GraphNode("StairsAndLiftBottomLeftCorridor", counter, 58, 365));
        cantorLevel1.add(new GraphNode("9100Door", counter, 50, 325));
        cantorLevel1.add(new GraphNode("9100Corridor", counter, 58, 325));

        cantorLevel1.add(new GraphNode("9101Door", counter, 50, 311));
        cantorLevel1.add(new GraphNode("9101Corridor", counter, 58, 311));

        cantorLevel1.add(new GraphNode("CorridorBalconyBottomLeft", counter, 90, 311));
        cantorLevel1.add(new GraphNode("9102Door", counter, 90, 220));           // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9103Door", counter, 90, 165));           // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9104Door", counter, 90, 115));           // Doubles as Corridor Node'

        cantorLevel1.add(new GraphNode("9106Door", counter, 52, 65));
        cantorLevel1.add(new GraphNode("9106Corridor", counter, 52, 60));

        cantorLevel1.add(new GraphNode("9107And9109Door", counter, 40, 55));     // Doubles as Corridor Node

        cantorLevel1.add(new GraphNode("9111Door", counter, 90, 55));
        cantorLevel1.add(new GraphNode("9111Corridor", counter, 90, 60));

        cantorLevel1.add(new GraphNode("9112Door", counter, 110, 65));
        cantorLevel1.add(new GraphNode("9112Corridor", counter, 110, 60));

        cantorLevel1.add(new GraphNode("9114Door", counter, 145, 65));
        cantorLevel1.add(new GraphNode("9114Corridor", counter, 145, 60));


        cantorLevel1.add(new GraphNode("9115Door", counter, 163, 55));
        cantorLevel1.add(new GraphNode("9116Door", counter, 163, 75));
        cantorLevel1.add(new GraphNode("9115And9116Corridor", counter, 163, 60));

        cantorLevel1.add(new GraphNode("9118Door", counter, 219, 55));
        cantorLevel1.add(new GraphNode("9118Corridor", counter, 219, 60));

        cantorLevel1.add(new GraphNode("9119And9120Door", counter, 250, 60));    // Doubles as Corridor Node

        cantorLevel1.add(new GraphNode("9121Door", counter, 219, 75)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9122Door", counter, 219, 95)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9123And9124Door", counter, 219, 113)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9125Door", counter, 219, 128)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9126Door", counter, 219, 143)); // Doubles as Corridor Node

        cantorLevel1.add(new GraphNode("9128Door", counter, 219, 185)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9129Door", counter, 219, 230)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9130Door", counter, 205, 250)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9131Door", counter, 219, 250)); // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9132Door", counter, 205, 285));
        cantorLevel1.add(new GraphNode("9132Corridor", counter, 212, 285));
        cantorLevel1.add(new GraphNode("StairsToLevel0Corridor", counter, 212, 308));
        cantorLevel1.add(new GraphNode("BalconyBottomRight", counter, 215, 330));

        cantorLevel1.add(new GraphNode("9135Door", counter, 215, 380));                  // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9136And9137And9138Door", counter, 165, 380));    // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9139Door", counter, 120, 380));                  // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9140Door", counter, 105, 380));                   // Doubles as Corridor Node
        cantorLevel1.add(new GraphNode("9141Door", counter, 58, 380));                   // Doubles as Corridor Node


        for (int i = 0; i < cantorLevel1.size(); i++) {
            GraphNode temp = cantorLevel1.get(i);
            graph.addNode(cantorLevel1.get(i));
        }

        addStep("StairsAndLiftBottomLeft", "StairsAndLiftBottomLeftCorridor");
        addStep("StairsAndLiftBottomLeftCorridor", "9100Corridor");
        addStep("9100Corridor", "9100Door");
        addStep("9100Corridor", "9101Corridor");
        addStep("9101Corridor", "9101Door");
        addStep("9101Corridor", "CorridorBalconyBottomLeft");

        addStep( "CorridorBalconyBottomLeft", "9102Door");
        addStep( "9102Door", "9103Door");
        addStep( "9103Door", "9104Door");
        addStep( "9104Door", "9111Corridor");
        addStep( "9111Corridor", "9111Door");
        addStep( "9111Corridor", "9106Corridor");
        addStep( "9106Corridor", "9106Door");
        addStep( "9106Corridor", "9107And9109Door");
        addStep( "9111Corridor", "9112Corridor");
        addStep( "9112Corridor", "9112Door");
        addStep( "9112Corridor", "StairsAndLiftTop");
        addStep( "9112Corridor", "9114Corridor");
        addStep( "9114Corridor", "9114Door");
        addStep( "9114Corridor", "9115And9116Corridor");
        addStep( "9115And9116Corridor", "9115Door");
        addStep( "9115And9116Corridor", "9116Door");
        addStep( "9115And9116Corridor", "9118Corridor");
        addStep( "9118Corridor", "9118Door");
        addStep( "9118Corridor", "9119And9120Door");
        addStep( "9118Corridor", "9121Door");
        addStep( "9121Door", "9122Door");
        addStep( "9122Door", "9123And9124Door");
        addStep( "9123And9124Door", "9125Door");
        addStep( "9125Door", "9126Door");

        addStep( "9126Door", "9128Door");
        addStep( "9128Door", "9129Door");
        addStep( "9129Door", "9131Door");
        addStep( "9131Door", "9130Door");
        addStep( "9132Door", "9132Corridor");
        addStep( "9131Door", "9132Corridor");
        addStep( "9130Door", "9132Corridor");
        addStep( "StairsToLevel0Corridor", "9132Corridor");
        addStep( "StairsToLevel0Corridor", "BalconyBottomRight");

        if(!useLiftsOnly){
            addStep( "StairsToLevel0Corridor", "StairsToLevel0");
            addStep( "StairsOnlyBottomRight", "BalconyBottomRight");
            addStep( "StairsOnlyBottomRight", "9135Door");
        } else {
            addStep( "9135Door", "BalconyBottomRight");
        }

        addStep( "9135Door", "9136And9137And9138Door");
        addStep( "9136And9137And9138Door", "9139Door");
        addStep( "9139Door", "9140Door");
        addStep( "9140Door", "9141Door");

        addStep("StairsAndLiftBottomLeftCorridor", "9141Door");

        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 1 - SET START
        if (levelFrom == roomCodeLevelFrom) {
            switch (roomFrom) {
                case 9100: {
                    setStartNode("9100Door");
                    break;
                }
                case 9101: {
                    setStartNode("9101Door");
                    break;
                }
                case 9102: {
                    setStartNode("9102Door");
                    break;
                }
                case 9103: {
                    setStartNode("9103Door");
                    break;
                }
                case 9104: {
                    setStartNode("9104Door");
                    break;
                }
                case 9106: {
                    setStartNode("9106Door");
                    break;
                }
                case 9107: case 9109: {
                    setStartNode("9107And9109Door");
                    break;
                }
                case 9111: {
                    setStartNode("9111Door");
                    break;
                }
                case 9112: {
                    setStartNode("9112Door");
                    break;
                }
                case 9114: {
                    setStartNode("9114Door");
                    break;
                }
                case 9115: {
                    setStartNode("9115Door");
                    break;
                }
                case 9116: {
                    setStartNode("9116Door");
                    break;
                }
                case 9118: {
                    setStartNode("9118Door");
                    break;
                }
                case 9119: case 9120: {
                    setStartNode("9119And9120Door");
                    break;
                }
                case 9121: {
                    setStartNode("9121Door");
                    break;
                }
                case 9122: {
                    setStartNode("9122Door");
                    break;
                }
                case 9123: case 9124: {
                    setStartNode("9123And9124Door");
                    break;
                }
                case 9125: {
                    setStartNode("9125Door");
                    break;
                }
                case 9126: {
                    setStartNode("9126Door");
                    break;
                }
                case 9128: {
                    setStartNode("9128Door");
                    break;
                }
                case 9129: {
                    setStartNode("9129Door");
                    break;
                }
                case 9130: {
                    setStartNode("9130Door");
                    break;
                }
                case 9131: {
                    setStartNode("9131Door");
                    break;
                }
                case 9132: {
                    setStartNode("9132Door");
                    break;
                }
                case 9135: {
                    setStartNode("9135Door");
                    break;
                }
                case 9136: case 9137: case 9138: {
                    setStartNode("9136And9137And9138Door");
                    break;
                }
                case 9139: {
                    setStartNode("9139Door");
                    break;
                }
                case 9140: {
                    setStartNode("9140Door");
                    break;
                }
                case 9141: {
                    setStartNode("9141Door");
                    break;
                }
                default: {
                    setStartNode("9141Door");
                    break;
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelFrom) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    GraphNode stairwellFrom = getStairwellNode(stairwellNode);
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
                case 9100: {
                    setEndNode("9100Door");
                    break;
                }
                case 9101: {
                    setEndNode("9101Door");
                    break;
                }
                case 9102: {
                    setEndNode("9102Door");
                    break;
                }
                case 9103: {
                    setEndNode("9103Door");
                    break;
                }
                case 9104: {
                    setEndNode("9104Door");
                    break;
                }
                case 9106: {
                    setEndNode("9106Door");
                    break;
                }
                case 9107: case 9109: {
                    setEndNode("9107And9109Door");
                    break;
                }
                case 9111: {
                    setEndNode("9111Door");
                    break;
                }
                case 9112: {
                    setEndNode("9112Door");
                    break;
                }
                case 9114: {
                    setEndNode("9114Door");
                    break;
                }
                case 9115: {
                    setEndNode("9115Door");
                    break;
                }
                case 9116: {
                    setEndNode("9116Door");
                    break;
                }
                case 9118: {
                    setEndNode("9118Door");
                    break;
                }
                case 9119: case 9120: {
                    setEndNode("9119And9120Door");
                    break;
                }
                case 9121: {
                    setEndNode("9121Door");
                    break;
                }
                case 9122: {
                    setEndNode("9122Door");
                    break;
                }
                case 9123: case 9124: {
                    setEndNode("9123And9124Door");
                    break;
                }
                case 9125: {
                    setEndNode("9125Door");
                    break;
                }
                case 9126: {
                    setEndNode("9126Door");
                    break;
                }
                case 9128: {
                    setEndNode("9128Door");
                    break;
                }
                case 9129: {
                    setEndNode("9129Door");
                    break;
                }
                case 9130: {
                    setEndNode("9130Door");
                    break;
                }
                case 9131: {
                    setEndNode("9131Door");
                    break;
                }
                case 9132: {
                    setEndNode("9132Door");
                    break;
                }
                case 9135: {
                    setEndNode("9135Door");
                    break;
                }
                case 9136: case 9137: case 9138: {
                    setEndNode("9136And9137And9138Door");
                    break;
                }
                case 9139: {
                    setEndNode("9139Door");
                    break;
                }
                case 9140: {
                    setEndNode("9140Door");
                    break;
                }
                case 9141: {
                    setEndNode("9141Door");
                    break;
                }
                default: {
                    setEndNode("9141Door");
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft", "StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsToLevel0", "StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
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

    public void cantorLevel2NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel2 = new ArrayList<GraphNode>();
        //StairsAndLifts
        cantorLevel2.add(new GraphNode("StairsAndLiftBottomLeft", counter, 60, 365));
        cantorLevel2.add(new GraphNode("StairsAndLiftTop", counter, 110, 60));

        //StairsOnly
        if (!useLiftsOnly) {
            cantorLevel2.add(new GraphNode("StairsOnlyBottomRight", counter, 215, 357));
        }

        cantorLevel2.add(new GraphNode("BalconyBottomLeft", counter, 60, 330));
        cantorLevel2.add(new GraphNode("9200Door", counter, 80, 300));
        cantorLevel2.add(new GraphNode("9200Corridor", counter, 90, 300));
        cantorLevel2.add(new GraphNode("9201Door", counter, 90, 210));
        cantorLevel2.add(new GraphNode("9202Door", counter, 90, 165));
        cantorLevel2.add(new GraphNode("9203Door", counter, 90, 120));

        cantorLevel2.add(new GraphNode("9205Door", counter, 65, 60));
        cantorLevel2.add(new GraphNode("9206Door", counter, 90, 60));
        cantorLevel2.add(new GraphNode("9207Door", counter, 100, 60));
        cantorLevel2.add(new GraphNode("9208Door", counter, 125, 60));
        cantorLevel2.add(new GraphNode("9210And9211Door", counter, 160, 60));
        cantorLevel2.add(new GraphNode("9212Door", counter, 220, 60));
        cantorLevel2.add(new GraphNode("9214Door", counter, 245, 60));

        cantorLevel2.add(new GraphNode("9215Door", counter, 220, 90));

        cantorLevel2.add(new GraphNode("9235Door", counter, 60, 380));

        for (int i = 0; i < cantorLevel2.size(); i++) {
            graph.addNode(cantorLevel2.get(i));
        }


        addStep("9235Door", "StairsAndLiftBottomLeft");


        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 2 - SET START
        if (levelFrom == roomCodeLevelFrom) {
            switch (roomFrom) {
                case 9235: {
                    setStartNode("9235Door");
                    break;
                }
                default: {
                    setStartNode("9235Door");
                    break;
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelFrom) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    GraphNode stairwellFrom = getStairwellNode(stairwellNode);
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

        //CANTOR LEVEL 2 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                default: {
                    setEndNode("9235Door");
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft", "StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
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

    public void setStartNode(String nodeStart) {
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            if (nodeStart.equals(graph.graphNodes.get(i).getName())) {
                start_x = (float) graph.graphNodes.get(i).getX();
                start_y = (float) graph.graphNodes.get(i).getY();
                break;
            }
        }
    }

    public void setEndNode(String nodeEnd) {
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            if (nodeEnd.equals(graph.graphNodes.get(i).getName())) {
                stop_x = (float) graph.graphNodes.get(i).getX();
                stop_y = (float) graph.graphNodes.get(i).getY();
                break;
            }
        }
    }

    public GraphNode getNearestStairNode(GraphNode startNode, String[] stairsAndLiftsNames, String[] stairsOnlyNames) {
        GraphNode returnNode = new GraphNode("", 0, 0, 0);
        double leastSteps = Double.POSITIVE_INFINITY;


        List<String> stairsAndLifts = new ArrayList<String>(Arrays.asList(stairsAndLiftsNames));
        List<String> stairsOnly = new ArrayList<String>(Arrays.asList(stairsOnlyNames));

        if (!useLiftsOnly) {
            stairsAndLifts.addAll(stairsOnly);
        }

        for (int i = 0; i < stairsAndLifts.size(); i++) {
            for (int j = 0; j < graph.graphNodes.size(); j++) {
                if (stairsAndLifts.get(i).equals(graph.graphNodes.get(j).getName())) {

                    GraphNode tempNode = graph.graphNodes.get(j);
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
        GraphNode n;
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            if (stepStart.equals(graph.graphNodes.get(i).getName())) {
                n = graph.graphNodes.get(i).getThisNode();
                edgeStart = n;
                break;
            }
        }
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            if (stepEnd.equals(graph.graphNodes.get(i).getName())) {
                //n = getNode((float) embLevel2.get(i).getX(), (float) embLevel2.get(i).getY());
                n = graph.graphNodes.get(i).getThisNode();
                edgeStop = n;
                break;
            }
        }
        graph.addStep(edgeStart, edgeStop, 1);
    }

    public GraphNode getStairwellNode(GraphNode n) {
        double x = n.getX();
        double y = n.getY();
        for (int i = 0; i < graph.graphNodes.size(); i++) {
            GraphNode v = graph.graphNodes.get(i);
            double d = Math.sqrt((x - v.getX()) * (x - v.getX()) + (y - v.getY()) * (y - v.getY())); // Returns correctly rounded square root of the potential vs the stairwell
            if (d <= 55) //In 25 pixels
                return v;
        }
        return null;

    }
}
