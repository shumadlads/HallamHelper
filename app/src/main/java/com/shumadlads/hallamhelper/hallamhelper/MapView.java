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
        int colourStart = getResources().getColor(R.color.defaultStart);
        int colourEnd = getResources().getColor(R.color.defaultEnd);
        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String colourBlindMode = SharedPrefs.getString(getContext().getString(R.string.SP_ColorBlindMode), "None");
        if (colourBlindMode.equals("Protanopia") || colourBlindMode.equals("Deuteranopia") || colourBlindMode.equals("Tritanopia")){
            colourStart = getResources().getColor(R.color.standardCBStart);
            colourEnd = getResources().getColor(R.color.standardCBEnd);
        }
        if (colourBlindMode.equals("Achromatopsia"))
        {
            colourStart = getResources().getColor(R.color.monoCBStart);
            colourEnd = getResources().getColor(R.color.monoCBEnd);
        }
        for (int i = 0; i < graph.graphNodes.size(); i++) {

            GraphNode v = graph.graphNodes.get(i);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.colorAccent));
            if (v.x == start_x && v.y == start_y) {
                paint.setColor(colourStart);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }
            if (v.x == stop_x && v.y == stop_y) {
                paint.setColor(colourEnd);
                canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), radius * density, paint); // comment out when all node drawn debug is uncommented
            }

            //canvas.drawCircle((((float) v.x) * density), (((float) v.y) * density), ((float) radius) * density, paint); // Uncomment for draws circles on all nodes


            paint.setColor(getResources().getColor(R.color.colorAccent));
            for (int j = 0; j < graph.graphNodes.get(i).steps.size(); j++) {
                GraphNode v1 = graph.graphNodes.get(i);
                GraphNode v2 = v1.steps.get(j).destination;

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
        start_y = 0;
        start_x = 0;
        stop_y = 0;
        stop_x = 0;
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
            case 3: {
                cantorLevel3NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }
            case 4:{
                cantorLevel4NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }
            default: {
                cantorLevel0NodesAndRoutes(levelFrom, roomFrom, roomTo);
                break;
            }

        }
    }

    public void cantorLevel0NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel0 = new ArrayList<GraphNode>();
        List<Node> data = SQLite.select().from(Node.class).where(Node_Table.Building.eq(1)).and(Node_Table.Floor.eq(0)).queryList();
        //StairsAndLifts
        //cantorLevel0.add(new GraphNode("StairsAndLiftBottomLeft", counter, 45, 368));
        // cantorLevel0.add(new GraphNode("StairsAndLiftTop", counter, 105, 55));
        //StairsOnly
        for (Node node : data) {
            if (node.getNodeName() == "StairsToLevel1" || node.getNodeName() == "StairsOnlyBottomRight") {
                if (!useLiftsOnly)
                    cantorLevel0.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
            } else
                cantorLevel0.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
        }
        
        for (int i = 0; i < cantorLevel0.size(); i++) {
            GraphNode temp = cantorLevel0.get(i);
            graph.addNode(cantorLevel0.get(i));
        }

        //setup empty node;
        GraphNode n = null;

        if (!useLiftsOnly) {
            addStep("BottomMainLobby", "StairsToLevel1");
            // addStep("9021Corridor", "StairsToLevel1");
            addStep("AboveStairsOnlyBottomRight", "StairsOnlyBottomRight");
            addStep("StairsOnlyBottomRight", "9024And9025Door");
        } else {
            addStep("9024And9025Door", "AboveStairsOnlyBottomRight"); //Directly link these nodes since the corridor nodes rely on the stairwell
        }

        addStep("9099DoorEntrance", "InnerDoorTop");
        addStep("InnerDoorTop", "CafeCorridor");
        addStep("CafeCorridor", "StairsAndLiftBottomLeft");
        addStep("InnerDoorTop", "BottomMainLobby");
        addStep("BottomMainLobby", "9021Corridor");
        addStep("9021Corridor", "9021Reception");
        addStep("9021Corridor", "9001Corridor");
        addStep("9001Corridor", "9001Door");
        addStep("9001Corridor", "9018Corridor");
        addStep("9018Corridor", "9018Door");
        addStep("9018Corridor", "9002And9022Corridor");
        addStep("9002And9022Corridor", "9002Door");
        addStep("9002And9022Corridor", "9022Door");
        addStep("9002And9022Corridor", "9003And9020Corridor");
        addStep("9003And9020Corridor", "9003Door");
        addStep("9003And9020Corridor", "9020Door");
        addStep("9003And9020Corridor", "StairsAndLiftTop");
        addStep("StairsAndLiftTop", "9006Door");
        addStep("9006Door", "9005Door"); // 9006 door doubles as corridor node
        addStep("StairsAndElevatorTop", "9011And9012Door"); // 9011And9012Door has only a door node
        addStep("9011And9012Door", "9013Door"); //9013Door has only a door node
        addStep("9013Door", "9015And9016Door");
        addStep("BottomMainLobby", "BottomUnderStairs");
        addStep("BottomUnderStairs", "9019Corridor");
        addStep("9019Corridor", "9019Door");
        addStep("9019Corridor", "AboveStairsOnlyBottomRight");


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
                case 9011:
                case 9012: {
                    setStartNode("9011And9012Door");
                    break;
                }
                case 9013: {
                    setStartNode("9013Door");
                    break;
                }
                case 9015:
                case 9016: {
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
                case 9024:
                case 9025: {
                    setStartNode("9024And9025Door");
                    break;
                }
                default: {
                    setStartNode("9099DoorEntrance"); // If node isn't found, set start node as main entrance
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            GraphNode stairwellFrom = getStairwellNode(stairwellNode);
            if (stairwellFrom != null)
                setStartNode(stairwellFrom.getName());
        }

        //CANTOR LEVEL 0 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9099: { //entrance start node
                    setEndNode("9099DoorEntrance");
                    break;
                }
                case 9098: {
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
                case 9011:
                case 9012: {
                    setEndNode("9011And9012Door");
                    break;
                }
                case 9013: {
                    setEndNode("9013Door");
                    break;
                }
                case 9015:
                case 9016: {
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
                case 9024:
                case 9025: {
                    setEndNode("9024And9025Door");
                    break;
                }
                default: {
                    setEndNode("9099DoorEntrance");
                    break;
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                { //entrance start node
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft", "StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsToLevel1", "StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                    break;
                }
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
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft"};
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                }
            }
        }

    }

    public void cantorLevel1NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel1 = new ArrayList<GraphNode>();
        List<Node> data = SQLite.select().from(Node.class).where(Node_Table.Building.eq(1)).and(Node_Table.Floor.eq(1)).queryList();
        for (Node node : data) {
            if (node.getNodeName() == "StairsToLevel0" || node.getNodeName() == "StairsOnlyBottomRight") {
                if (!useLiftsOnly)
                    cantorLevel1.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
            } else
                cantorLevel1.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
        }

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

        addStep("CorridorBalconyBottomLeft", "9102Door");
        addStep("9102Door", "9103Door");
        addStep("9103Door", "9104Door");
        addStep("9104Door", "9111Corridor");
        addStep("9111Corridor", "9111Door");
        addStep("9111Corridor", "9106Corridor");
        addStep("9106Corridor", "9106Door");
        addStep("9106Corridor", "9107And9109Door");
        addStep("9111Corridor", "9112Corridor");
        addStep("9112Corridor", "9112Door");
        addStep("9112Corridor", "StairsAndLiftTop");
        addStep("9112Corridor", "9114Corridor");
        addStep("9114Corridor", "9114Door");
        addStep("9114Corridor", "9115And9116Corridor");
        addStep("9115And9116Corridor", "9115Door");
        addStep("9115And9116Corridor", "9116Door");
        addStep("9115And9116Corridor", "9118Corridor");
        addStep("9118Corridor", "9118Door");
        addStep("9118Corridor", "9119And9120Door");
        addStep("9118Corridor", "9121Door");
        addStep("9121Door", "9122Door");
        addStep("9122Door", "9123And9124Door");
        addStep("9123And9124Door", "9125Door");
        addStep("9125Door", "9126Door");

        addStep("9126Door", "9128Door");
        addStep("9128Door", "9129Door");
        addStep("9129Door", "9131Door");
        addStep("9131Door", "9130Door");
        addStep("9132Door", "9132Corridor");
        addStep("9131Door", "9132Corridor");
        addStep("9130Door", "9132Corridor");
        addStep("StairsToLevel0Corridor", "9132Corridor");
        addStep("StairsToLevel0Corridor", "BalconyBottomRight");

        if (!useLiftsOnly) {
            addStep("StairsToLevel0Corridor", "StairsToLevel0");
            addStep("StairsOnlyBottomRight", "BalconyBottomRight");
            addStep("StairsOnlyBottomRight", "9135Door");
        } else {
            addStep("9135Door", "BalconyBottomRight");
        }

        addStep("9135Door", "9136And9137And9138Door");
        addStep("9136And9137And9138Door", "9139Door");
        addStep("9139Door", "9140Door");
        addStep("9140Door", "9141Door");

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
                case 9107:
                case 9109: {
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
                case 9119:
                case 9120: {
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
                case 9123:
                case 9124: {
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
                case 9136:
                case 9137:
                case 9138: {
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
            GraphNode stairwellFrom = getStairwellNode(stairwellNode);
            if (stairwellFrom != null)
                setStartNode(stairwellFrom.getName());
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
                case 9107:
                case 9109: {
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
                case 9119:
                case 9120: {
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
                case 9123:
                case 9124: {
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
                case 9136:
                case 9137:
                case 9138: {
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
                { //entrance start node
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft", "StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsToLevel0", "StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                    break;
                }
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
                case 4: {
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft"};
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                }
            }
        }
    }

    public void cantorLevel2NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel2 = new ArrayList<GraphNode>();
        List<Node> data = SQLite.select().from(Node.class).where(Node_Table.Building.eq(1)).and(Node_Table.Floor.eq(2)).queryList();
        for (Node node : data) {
            if (node.getNodeName() == "StairsOnlyBottomRight"){
                if (!useLiftsOnly)
                    cantorLevel2.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
            } else
                cantorLevel2.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
        }

        for (int i = 0; i < cantorLevel2.size(); i++) {
            graph.addNode(cantorLevel2.get(i));
        }


        addStep("9235Door", "StairsAndLiftBottomLeft");
        addStep("StairsAndLiftBottomLeft", "BalconyBottomLeft");
        addStep("BalconyBottomLeft", "9200Door");
        addStep("9200Door", "9200Corridor");
        addStep("9200Corridor", "9201Door");
        addStep("9201Door", "9202Door");
        addStep("9202Door", "9203Door");
        addStep("9203Door", "9206Door");
        addStep("9205Door", "9206Door");
        addStep("9206Door", "9207Door");
        addStep("9207Door", "StairsAndLiftTop");

        addStep("StairsAndLiftTop", "9208Door");
        addStep("9208Door", "9210And9211Door");
        addStep("9210And9211Door", "9212Door");
        addStep("9212Door", "9214Door");

        addStep("9212Door", "9215Door");
        addStep("9215Door", "9216And9217Door");
        addStep("9216And9217Door", "9218Door");
        addStep("9218Door", "9219Door");
        addStep("9219Door", "9220Door");
        addStep("9220Door", "9221Door");
        addStep("9221Door", "9222Door");
        addStep("9222Door", "9223Door");
        addStep("9223Door", "9224Corridor");
        addStep("9224Corridor", "9224Door");
        addStep("9224Corridor", "9226Corridor");
        addStep("9226Corridor", "9226Door");

        if (!useLiftsOnly) {
            addStep("9226Corridor", "StairsOnlyBottomRight");
            addStep("StairsOnlyBottomRight", "9228Door");
        } else {
            addStep("9226Corridor", "9228Door");
        }

        addStep("9228Door", "9231Door");
        addStep("9231Door", "9232Door");
        addStep("9232Door", "9233Door");
        addStep("9233Door", "9234Door");
        addStep("9234Door", "9235Door");


        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 2 - SET START
        if (levelFrom == roomCodeLevelFrom) {


            switch (roomFrom) {
                case 9200: {
                    setStartNode("9200Door");
                    break;
                }
                case 9201: {
                    setStartNode("9201Door");
                    break;
                }
                case 9202: {
                    setStartNode("9202Door");
                    break;
                }
                case 9203: {
                    setStartNode("9203Door");
                    break;
                }
                case 9205: {
                    setStartNode("9205Door");
                    break;
                }
                case 9206: {
                    setStartNode("9206Door");
                    break;
                }
                case 9207: {
                    setStartNode("9207Door");
                    break;
                }
                case 9208: {
                    setStartNode("9208Door");
                    break;
                }
                case 9210:
                case 9211: {
                    setStartNode("9210And9211Door");
                    break;
                }
                case 9212: {
                    setStartNode("9212Door");
                    break;
                }
                case 9214: {
                    setStartNode("9214Door");
                    break;
                }
                case 9215: {
                    setStartNode("9215Door");
                    break;
                }
                case 9216:
                case 9217: {
                    setStartNode("9216And9217Door");
                    break;
                }
                case 9218: {
                    setStartNode("9218Door");
                    break;
                }
                case 9219: {
                    setStartNode("9219Door");
                    break;
                }
                case 9220: {
                    setStartNode("9220Door");
                    break;
                }
                case 9221: {
                    setStartNode("9221Door");
                    break;
                }
                case 9222: {
                    setStartNode("9222Door");
                    break;
                }
                case 9223: {
                    setStartNode("9223Door");
                    break;
                }
                case 9224: {
                    setStartNode("9224Door");
                    break;
                }
                case 9226: {
                    setStartNode("9226Door");
                    break;
                }
                case 9228: {
                    setStartNode("9228Door");
                    break;
                }
                case 9231: {
                    setStartNode("9231Door");
                    break;
                }
                case 9232: {
                    setStartNode("9232Door");
                    break;
                }
                case 9233: {
                    setStartNode("9233Door");
                    break;
                }
                case 9234: {
                    setStartNode("9234Door");
                    break;
                }
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
            GraphNode stairwellFrom = getStairwellNode(stairwellNode);
            if (stairwellFrom != null)
                setStartNode(stairwellFrom.getName());
        }

        //CANTOR LEVEL 2 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9200: {
                    setEndNode("9200Door");
                    break;
                }
                case 9201: {
                    setEndNode("9201Door");
                    break;
                }
                case 9202: {
                    setEndNode("9202Door");
                    break;
                }
                case 9203: {
                    setEndNode("9203Door");
                    break;
                }
                case 9205: {
                    setEndNode("9205Door");
                    break;
                }
                case 9206: {
                    setEndNode("9206Door");
                    break;
                }
                case 9207: {
                    setEndNode("9207Door");
                    break;
                }
                case 9208: {
                    setEndNode("9208Door");
                    break;
                }
                case 9210:
                case 9211: {
                    setEndNode("9210And9211Door");
                    break;
                }
                case 9212: {
                    setEndNode("9212Door");
                    break;
                }
                case 9214: {
                    setEndNode("9214Door");
                    break;
                }
                case 9215: {
                    setEndNode("9215Door");
                    break;
                }
                case 9216:
                case 9217: {
                    setEndNode("9216And9217Door");
                    break;
                }
                case 9218: {
                    setEndNode("9218Door");
                    break;
                }
                case 9219: {
                    setEndNode("9219Door");
                    break;
                }
                case 9220: {
                    setEndNode("9220Door");
                    break;
                }
                case 9221: {
                    setEndNode("9221Door");
                    break;
                }
                case 9222: {
                    setEndNode("9222Door");
                    break;
                }
                case 9223: {
                    setEndNode("9223Door");
                    break;
                }
                case 9224: {
                    setEndNode("9224Door");
                    break;
                }
                case 9226: {
                    setEndNode("9226Door");
                    break;
                }
                case 9228: {
                    setEndNode("9228Door");
                    break;
                }
                case 9231: {
                    setEndNode("9231Door");
                    break;
                }
                case 9232: {
                    setEndNode("9232Door");
                    break;
                }
                case 9233: {
                    setEndNode("9233Door");
                    break;
                }
                case 9234: {
                    setEndNode("9234Door");
                    break;
                }
                case 9235: {
                    setEndNode("9235Door");
                    break;
                }
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
                case 4: {
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft"};
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                }
            }
        }

    }

    public void cantorLevel3NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel3 = new ArrayList<GraphNode>();
        List<Node> data = SQLite.select().from(Node.class).where(Node_Table.Building.eq(1)).and(Node_Table.Floor.eq(3)).queryList();
        for (Node node : data) {
            if (node.getNodeName() == "StairsOnlyBottomRight"){
                if (!useLiftsOnly)
                    cantorLevel3.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
            } else
                cantorLevel3.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
        }

        for (int i = 0; i < cantorLevel3.size(); i++) {
            graph.addNode(cantorLevel3.get(i));
        }

        addStep("StairsAndLiftBottomLeft", "BalconyBottomLeft");
        addStep("BalconyBottomLeft", "BalconyBottomLeft2");
        addStep("BalconyBottomLeft2", "9303Door");
        addStep("9303Door", "9305Door");
        addStep("9305Door", "9306Door");
        addStep("9306Door", "9307Door");
        addStep("9307Door", "9310Door");
        addStep("9310Door", "9309Door");
        addStep("9310Door", "9311Door");
        addStep("9311Door", "StairsAndLiftTop");

        addStep("StairsAndLiftTop", "9312Door");
        addStep("9312Door", "9314And9316Door");
        addStep("9314And9316Door", "9315Door");
        addStep("9315Door", "9318Door");
        addStep("9315Door", "9320Door");
        addStep("9320Door", "9321And9322Door");
        addStep("9321And9322Door", "9324Door");
        addStep("9324Door", "9323And9325Door");
        addStep("9323And9325Door", "9327And9332Door");
        addStep("9327And9332Door", "9330Door");
        addStep("9330Door", "9328And9329Door");
        addStep("9328And9329Door", "9331Door");
        addStep("9331Door", "9326Door");

        if (!useLiftsOnly) {
            addStep("9326Door", "StairsOnlyBottomRight");
            addStep("StairsOnlyBottomRight", "9335Door");
        } else {
            addStep("9326Door", "9335Door");
        }

        addStep("9335Door", "9336Door");
        addStep("9336Door", "9339Door");
        addStep("9339Door", "9342Door");
        addStep("9342Door", "9340Door");
        addStep("9340Door", "9344Door");
        addStep("9344Door", "9343Door");
        addStep("9343Door", "StairsAndLiftBottomLeft");

        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 3 - SET START
        if (levelFrom == roomCodeLevelFrom) {


            switch (roomFrom) {
                case 9303: {
                    setStartNode("9303Door");
                    break;
                }
                case 9305: {
                    setStartNode("9305Door");
                    break;
                }
                case 9306: {
                    setStartNode("9306Door");
                    break;
                }
                case 9307: {
                    setStartNode("9307Door");
                    break;
                }
                case 9309: {
                    setStartNode("9309Door");
                    break;
                }
                case 9310: {
                    setStartNode("9310Door");
                    break;
                }
                case 9311: {
                    setStartNode("9311Door");
                    break;
                }
                case 9312: {
                    setStartNode("9312Door");
                    break;
                }
                case 9314:
                case 9316: {
                    setStartNode("9314And9316Door");
                    break;
                }
                case 9315: {
                    setStartNode("9315Door");
                    break;
                }
                case 9318: {
                    setStartNode("9318Door");
                    break;
                }
                case 9320: {
                    setStartNode("9320Door");
                    break;
                }
                case 9321:
                case 9322: {
                    setStartNode("9321And9322Door");
                    break;
                }
                case 9324: {
                    setStartNode("9324Door");
                    break;
                }
                case 9323:
                case 9325: {
                    setStartNode("9323And9325Door");
                    break;
                }
                case 9326: {
                    setStartNode("9326Door");
                    break;
                }
                case 9327:
                case 9332: {
                    setStartNode("9327And9332Door");
                    break;
                }
                case 9330: {
                    setStartNode("9330Door");
                    break;
                }
                case 9328:
                case 9329: {
                    setStartNode("9328And9329Door");
                    break;
                }
                case 9331: {
                    setStartNode("9331Door");
                    break;
                }
                case 9335: {
                    setStartNode("9335Door");
                    break;
                }
                case 9336: {
                    setStartNode("9336Door");
                    break;
                }
                case 9339: {
                    setStartNode("9339Door");
                    break;
                }
                case 9342: {
                    setStartNode("9342Door");
                    break;
                }
                case 9340: {
                    setStartNode("9340Door");
                    break;
                }
                case 9343: {
                    setStartNode("9343Door");
                    break;
                }
                case 9344: {
                    setStartNode("9344Door");
                    break;
                }
                default: {
                    setStartNode("9303Door");
                    break;
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            GraphNode stairwellFrom = getStairwellNode(stairwellNode);
            if (stairwellFrom != null)
                setStartNode(stairwellFrom.getName());
        }

        //CANTOR LEVEL 3 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9303: {
                    setEndNode("9303Door");
                    break;
                }
                case 9305: {
                    setEndNode("9305Door");
                    break;
                }
                case 9306: {
                    setEndNode("9306Door");
                    break;
                }
                case 9307: {
                    setEndNode("9307Door");
                    break;
                }
                case 9309: {
                    setEndNode("9309Door");
                    break;
                }
                case 9310: {
                    setEndNode("9310Door");
                    break;
                }
                case 9311: {
                    setEndNode("9311Door");
                    break;
                }
                case 9312: {
                    setEndNode("9312Door");
                    break;
                }
                case 9314: case 9316: {
                    setEndNode("9314And9316Door");
                    break;
                }
                case 9315: {
                    setEndNode("9315Door");
                    break;
                }
                case 9318: {
                    setEndNode("9318Door");
                    break;
                }
                case 9320: {
                    setEndNode("9320Door");
                    break;
                }
                case 9321: case 9322: {
                    setEndNode("9321And9322Door");
                    break;
                }
                case 9324: {
                    setEndNode("9324Door");
                    break;
                }
                case 9323: case 9325: {
                    setEndNode("9323And9325Door");
                    break;
                }
                case 9326: {
                    setEndNode("9326Door");
                    break;
                }
                case 9327: case 9332: {
                    setEndNode("9327And9332Door");
                    break;
                }
                case 9330: {
                    setEndNode("9330Door");
                    break;
                }
                case 9328: case 9329: {
                    setEndNode("9328And9329Door");
                    break;
                }
                case 9331: {
                    setEndNode("9331Door");
                    break;
                }
                case 9335: {
                    setEndNode("9335Door");
                    break;
                }
                case 9336: {
                    setEndNode("9336Door");
                    break;
                }
                case 9339: {
                    setEndNode("9339Door");
                    break;
                }
                case 9342: {
                    setEndNode("9342Door");
                    break;
                }
                case 9340: {
                    setEndNode("9340Door");
                    break;
                }
                case 9343: {
                    setEndNode("9343Door");
                    break;
                }
                case 9344: {
                    setEndNode("9344Door");
                    break;
                }
                default: {
                    setEndNode("9303Door");
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
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                    break;
                }
                case 4: {
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftBottomLeft"};
                    String[] stairsOnlyArray = new String[]{"StairsOnlyBottomRight"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    setEndNode(stairNode.name);
                }
            }
        }

    }

    public void cantorLevel4NodesAndRoutes(int levelFrom, int roomFrom, int roomTo) {
        List<GraphNode> cantorLevel4 = new ArrayList<GraphNode>();
        List<Node> data = SQLite.select().from(Node.class).where(Node_Table.Building.eq(1)).and(Node_Table.Floor.eq(4)).queryList();
        for (Node node : data) {
            if (node.getNodeName() == "StairsOnlyBottom"){
                if (!useLiftsOnly)
                    cantorLevel4.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
            } else
                cantorLevel4.add(new GraphNode(node.getNodeName(), counter, node.getXCoord(), node.getYCoord()));
        }

        addListNodesToGraph(cantorLevel4);

        addStep("StairsAndLiftTop", "CorridorTop");
        addStep("CorridorTop", "9420Door");
        addStep("9420Door", "9418And9419Door");

        addStep("CorridorTop", "9416Door");
        addStep("9416Door", "9415And9400Door");
        addStep("9415And9400Door", "CorridorMid");

        addStep("CorridorMid", "9417Door");
        addStep("CorridorMid", "9402And9414Door");
        addStep("9402And9414Door", "9404Door");
        addStep("9404Door", "9403And9413Door");
        addStep("9403And9413Door", "9407Door");

        addStep("9407Door", "9408And9413Door");
        addStep("9408And9413Door", "9409Door");
        addStep("9409Door", "9410And9411Door");
        addStep("9407Door", "9406Door");
        addStep("9406Door", "StairsOnlyBottom");




        int roomCodeLevelFrom = (((roomFrom / 10) / 10) % 10); // get the second digit for floor number
        int roomCodeLevelTo = (((roomTo / 10) / 10) % 10); // get the second digit for floor number

        //CANTOR LEVEL 4 - SET START
        if (levelFrom == roomCodeLevelFrom) {


            switch (roomFrom) {
                case 9420: {
                    setStartNode("9420Door");
                    break;
                }
                case 9419: case 9418: {
                    setStartNode("9418And9419Door");
                    break;
                }
                case 9416: {
                    setStartNode("9416Door");
                    break;
                }
                case 9415: case 9400: {
                    setStartNode("9415And9400Door");
                    break;
                }
                case 9402: case 9414: {
                    setStartNode("9402And9414Door");
                    break;
                }
                case 9404: {
                    setStartNode("9404Door");
                    break;
                }
                case 9403: case 9413: {
                    setStartNode("9403And9413Door");
                    break;
                }
                case 9407: {
                    setStartNode("9407Door");
                    break;
                }
                case 9408: case 9412: {
                    setStartNode("9408And9412Door");
                    break;
                }
                case 9409: {
                    setStartNode("9409Door");
                    break;
                }
                case 9410: case 9411: {
                    setStartNode("9410And9411Door");
                    break;
                }
                case 9406: {
                    setStartNode("9406Door");
                    break;
                }
                default: {
                    setStartNode("9406Door");
                    break;
                }
            }
        } else if (roomCodeLevelTo == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelFrom) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    //GraphNode stairwellFrom = getStairwellNode(stairwellNode);

                    if (stairwellNode.getName().equals("StairsAndLiftBottomLeft")){
                        setStartNode("StairsAndLiftTop");
                    } else {
                        setStartNode("StairsOnlyBottom");
                    }
                }
            }
        }

        //CANTOR LEVEL 4 - SET STOP
        if (roomCodeLevelTo == levelFrom) {
            switch (roomTo) {
                case 9420: {
                    setEndNode("9420Door");
                    break;
                }
                case 9419: case 9418: {
                    setEndNode("9418And9419Door");
                    break;
                }
                case 9416: {
                    setEndNode("9416Door");
                    break;
                }
                case 9415: case 9400: {
                    setEndNode("9415And9400Door");
                    break;
                }
                case 9402: case 9414: {
                    setEndNode("9402And9414Door");
                    break;
                }
                case 9404: {
                    setEndNode("9404Door");
                    break;
                }
                case 9403: case 9413: {
                    setEndNode("9403And9413Door");
                    break;
                }
                case 9407: {
                    setEndNode("9407Door");
                    break;
                }
                case 9408: case 9412: {
                    setEndNode("9408And9412Door");
                    break;
                }
                case 9409: {
                    setEndNode("9409Door");
                    break;
                }
                case 9410: case 9411: {
                    setEndNode("9410And9411Door");
                    break;
                }
                case 9406: {
                    setEndNode("9406Door");
                    break;
                }
                default: {
                    setEndNode("9406Door");
                    break;
                }
            }
        } else if (roomCodeLevelFrom == levelFrom) { // Route starts on this floor
            switch (roomCodeLevelTo) {
                case 0:
                case 1:
                case 2:
                case 3: { //entrance start node
                    String[] stairsAndLiftsArray = new String[]{"StairsAndLiftTop"};
                    String[] stairsOnlyArray = new String[]{"StairsAndLiftBottom"};

                    GraphNode startNode = graph.getN(start_x, start_y);
                    GraphNode stairNode = getNearestStairNode(startNode, stairsAndLiftsArray, stairsOnlyArray);
                    if (stairNode.getName().equals("StairsAndLiftTop")){
                        stairwellNode = new GraphNode("StairsAndLiftBottomLeft", counter, 45, 368);
                    } else {
                        stairwellNode = new GraphNode("StairsOnlyBottomRight", counter, 215, 357);
                    }
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
        for (Node n : nodes) {
            embLevel2.add(new GraphNode(n.getNodeName(), counter, n.getXCoord(), n.getYCoord()));
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

    public void addListNodesToGraph(List<GraphNode> graphNodes){
        for (int i = 0; i < graphNodes.size(); i++) {
            graph.addNode(graphNodes.get(i));
        }
    }
}
