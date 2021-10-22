package algorithms;

import map.*;
import robot.Robot;
import robot.RobotConstants;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * This class will be used for planning the maneuver from one point to another
 */
public class TripPlannerAlgo {
    private final Arena arena;
    private final int numCells = (MapConstants.ARENA_WIDTH / MapConstants.OBSTACLE_WIDTH) + MapConstants.ARENA_BORDER_SIZE * 2;
    private Node[][][] grid; // grid dimensions: x,y,direction (of which there are 4: 0=east,1=north,2=west,3=south)
    //private int[][][] turningArray; // array to keep track of whether turns are possible at this position
    private final Map<Node, Node> predMap;
    private int[] endPosition; // use this to access the end position of our car.
    private ArrayList<Node> nodePath;

    private PriorityQueue<Node> visitQueue; // min heap priority queue for nodes in the frontier
    private Node currentNode;
    private double totalCost = 0;

    public TripPlannerAlgo(Arena arena) {
        this.arena = arena;
        this.predMap = new HashMap<>();
        int robotX = arena.getRobot().getX();
        int robotY = arena.getRobot().getY();
        int robotDirection = arena.getRobot().getRobotDirectionAngle();
        endPosition = new int[]{robotX, robotY, robotDirection};
        this.visitQueue = new PriorityQueue<>(new NodeComparator());
    }

    public int[] getEndPosition() {
        return endPosition;
    }

    /**
     * Clear the map and any stored values
     */
    public void clear() {
        predMap.clear();
        constructMap();

        // initialize the arrays
        int numCells = (MapConstants.ARENA_WIDTH / MapConstants.OBSTACLE_WIDTH) + MapConstants.ARENA_BORDER_SIZE * 2;
        //this.greedyCostArray = new double[numCells][numCells][4];
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                for (int k = 0; k < 4; k++) {
                    grid[j][i][k].setCost(RobotConstants.MAX_COST, RobotConstants.MAX_COST);
                }
            }
        }

        // initialize frontier queue
        visitQueue.clear();
    }

    /**
     * Checks if a node can be visited.
     */
    private boolean canVisit(Node node) {
        return !node.isPicture() && !node.isVirtualObstacle() && !node.isVisited();
    }


    /**
     * Calculate the number of nodes required to perform a turn.
     * i.e. if turn radius = 24 cm,
     * X X X
     * X
     * X   O
     * <p>
     * Assuming the car is aligned to the center of the grid (i.e. x=5,y=5),
     * 5+24 = 29; the turning circle will be centered at x=29,y=5. (If we are facing north and turning right)
     * Thus, the robot will require 3 grid units worth of space in either direction to complete a turn.
     * i.e. after changing directions, will require ceiling(turnRadius/10)-1 blocks of room to turn.
     */
    private int calculateTurnSizeX() {
        double gridSize = MapConstants.OBSTACLE_WIDTH;
        // get the largest turn radius measure of the four measures
        double largestRadius = Math.max(RobotConstants.LEFT_TURN_RADIUS_X, RobotConstants.RIGHT_TURN_RADIUS_X);//Math.max(RobotConstants.LEFT_TURN_RADIUS_X, RobotConstants.LEFT_TURN_RADIUS_Y),Math.max(RobotConstants.RIGHT_TURN_RADIUS_X, RobotConstants.RIGHT_TURN_RADIUS_Y));
        return (int) Math.ceil(largestRadius / gridSize) + 1;
    }

    /**
     * Calculate the number of nodes required to turn in the Y direction
     */
    private int calculateTurnSizeY() {
        double gridSize = MapConstants.OBSTACLE_WIDTH;
        // get the largest turn radius measure of the four measures
        double largestRadius = Math.max(RobotConstants.LEFT_TURN_RADIUS_Y, RobotConstants.RIGHT_TURN_RADIUS_Y);//Math.max(RobotConstants.LEFT_TURN_RADIUS_X, RobotConstants.LEFT_TURN_RADIUS_Y),Math.max(RobotConstants.RIGHT_TURN_RADIUS_X, RobotConstants.RIGHT_TURN_RADIUS_Y));
        return (int) Math.ceil(largestRadius / gridSize) + 1;
    }

    /**
     * given the node of a picture obstacle, get the goal node position
     */
    private int[] getGoalNodePosition(int x, int y, int dir) {
        int dist = AlgoConstants.DISTANCE_FROM_GOAL;
        int[] coords = new int[3];
        switch (dir) {
            case 0:
                coords[0] = x + dist;
                coords[1] = y;
                coords[2] = 180;
                break;
            case 90:
                coords[0] = x;
                coords[1] = y - dist;
                coords[2] = 270;
                break;
            case 180:
                coords[0] = x - dist;
                coords[1] = y;
                coords[2] = 0;
                break;
            case 270:
                coords[0] = x;
                coords[1] = y + dist;
                coords[2] = 90;
                break;
            default:
                return null;
        }
        return coords;
    }

    /**
     * Plans a path from the car position to the selected picture obstacle using a modified A* algorithm.
     * <p>
     * Generating the 4 possible successors of the current cell
     * <p>
     * N
     * |
     * |
     * W----Cell----E
     * |
     * |
     * S
     * <p>
     * Cell-->Popped Cell (i, j)
     * N -->  North       (i-1, j)
     * S -->  South       (i+1, j)
     * E -->  East        (i, j+1)
     * W -->  West        (i, j-1)
     * <p>
     * Rules:
     * A turn cannot occur if the unit has not traveled in its current direction n nodes,
     * where n is the minimum number of nodes required for a turn to occur.
     * <p>
     * A turn cannot occur if there are obstacles within the turning area.
     * <p>
     * The robot must be facing the direction specified by endDirection by the end of the path.
     * <p>
     * input: the x,y, and direction of the picture obstacle, the robot's turn radius
     */
    public ArrayList<MoveType> planPath(int startX, int startY, int startAngle, int pictureX, int pictureY, int pictureDirInDegrees, boolean isPicturePos, boolean doBacktrack, boolean print) {
        if (0 > startX || startX >= numCells || 0 > startY || startY >= numCells) { // start is outside of bounds
            this.totalCost += 9999;
            return null;
        }
        clear();
        int endX, endY, endAngleDimension;
        ArrayList<MoveType> path = null;
        boolean goalFound = false;
        if (isPicturePos) {
            int[] goal = getGoalNodePosition(pictureX, pictureY, pictureDirInDegrees);
            endX = goal[0];
            endY = goal[1];
            int endDirInDegrees = goal[2];             // false since goal node has not yet been found
            endAngleDimension = angleToDimension(endDirInDegrees);  // which dimension of the 3d array does the goal node lie in
        } else {
            endX = pictureX;
            endY = pictureY;
            endAngleDimension = angleToDimension(pictureDirInDegrees);
        }

        // test if goal node s reachable
        if (!isValidLocation(endX, endY, endAngleDimension)) return null;

        Node goalNode = grid[endY][endX][endAngleDimension];        // fetch the goal node from the grid array.
        int maxTurnCountX = calculateTurnSizeX();            // get the number of grids that the car needs to move straight after changing directions (for a legal turn)
        int maxTurnCountY = calculateTurnSizeY();
        // this is the counter for the turnArray. Only when turnArray[y][x] = turnMaxCount is a turn allowed to be made.
        int x, y, dim;
        Node nextNode;
        int[] forwardLocation, leftLocation, rightLocation, backwardLocation;
        int nextX, nextY, nextDim, currentTurnCount;
        double currentGCost, hCost, gCost;
        // lets start searching

        int angleDimension = angleToDimension(startAngle);

        this.currentNode = grid[startY][startX][angleDimension];
        this.visitQueue.add(currentNode);
        grid[startY][startX][angleDimension].setCost(0, 0);

        while (!goalFound && !visitQueue.isEmpty()) {
            currentNode = visitQueue.remove(); // Fetch the head of the priority queue

            x = currentNode.getX();
            y = currentNode.getY();
            dim = currentNode.getDim(); // 0 = east, 1 = north, 2 = west, 3 = south (counter-clockwise)
            currentGCost = currentNode.getGCost();

            if (currentNode == goalNode) {      // we have found the goal
                goalFound = true; // otherwise, we are good to go (search over)!
                endPosition = new int[]{x, y, dim * 90};
                break;
            }

            forwardLocation = getForwardNode(x, y, dim);
            leftLocation = getLeftNode(x, y, dim, maxTurnCountX, maxTurnCountY);
            rightLocation = getRightNode(x, y, dim, maxTurnCountX, maxTurnCountY);
            backwardLocation = getBackwardNode(x, y, dim);
            if (forwardLocation != null) { // if this is a valid location to search, add it to the queue
                nextX = forwardLocation[0];
                nextY = forwardLocation[1];
                nextDim = forwardLocation[2];

                nextNode = grid[nextY][nextX][nextDim];

                gCost = currentGCost + greedy(currentNode, nextNode);
                hCost = heuristic(currentNode, goalNode, endAngleDimension);
                ;

                // if we have already added this node, only change it if the newly found path is better.
                if (gCost < nextNode.getGCost()) {
                    predMap.put(nextNode, currentNode);
                    nextNode.setCost(hCost, gCost); // set the cost for the next node and then add to the priority queue
                    visitQueue.add(nextNode);
                }
            }
            if (backwardLocation != null) {
                nextX = backwardLocation[0];
                nextY = backwardLocation[1];
                nextDim = backwardLocation[2];

                nextNode = grid[nextY][nextX][nextDim];

                gCost = currentGCost + greedy(currentNode, nextNode);
                hCost = heuristic(currentNode, goalNode, endAngleDimension);

                if (gCost < nextNode.getGCost()) {
                    predMap.put(nextNode, currentNode);
                    nextNode.setCost(hCost, gCost); // set the cost for the next node and then add to the priority queue
                    visitQueue.add(nextNode);
                }
            }
            if (leftLocation != null) { // if this is a valid location to search, add it to the queue
                nextX = leftLocation[0];
                nextY = leftLocation[1];
                nextDim = leftLocation[2];

                nextNode = grid[nextY][nextX][nextDim];

                gCost = currentGCost + greedy(currentNode, nextNode);
                hCost = heuristic(currentNode, goalNode, endAngleDimension);

                if (gCost < nextNode.getGCost()) {
                    predMap.put(nextNode, currentNode);
                    nextNode.setCost(hCost, gCost); // set the cost for the next node and then add to the priority queue
                    visitQueue.add(nextNode);
                }
            }
            if (rightLocation != null) { // if this is a valid location to search, add it to the queue
                nextX = rightLocation[0];
                nextY = rightLocation[1];
                nextDim = rightLocation[2];

                nextNode = grid[nextY][nextX][nextDim];

                gCost = currentGCost + greedy(currentNode, nextNode);
                hCost = heuristic(currentNode, goalNode, endAngleDimension);

                if (gCost < nextNode.getGCost()) {
                    predMap.put(nextNode, currentNode);
                    nextNode.setCost(hCost, gCost); // set the cost for the next node and then add to the priority queue
                    visitQueue.add(nextNode);
                }
            }
            currentNode.setHasBeenVisited(true);
        }
        // when we exit the loop, check if the goal has been found or not.
        if (!goalFound) {
            this.totalCost += 9999;
            return null;
        }
        if (doBacktrack) {
            path = backtrack(goalNode, print);
        }
        if (print && doBacktrack) {
            System.out.println("Total cost: " + goalNode.getGCost());
            System.out.println("Nodes expanded: " + predMap.size());
        }
        this.totalCost += goalNode.getGCost();
        return path;
    }

    /**
     * Calculate the coordinates to reverse to
     */
    public void clearCost() {
        this.totalCost = 0;
    }

    public double getTotalCost() {
        return totalCost;
    }

    private int[] getForwardNode(int x, int y, int dim) {
        int[] pair;
        switch (dim) {
            case 0: // east, x+1,y
                pair = new int[]{x + 1, y, dim};
                break;
            case 1: // north, x,y-1
                pair = new int[]{x, y - 1, dim};
                break;
            case 2: // west, x-1,y
                pair = new int[]{x - 1, y, dim};
                break;
            case 3: // south, x,y+1
                pair = new int[]{x, y + 1, dim};
                break;
            default: // error
                pair = null;
                break;
        }
        if (pair != null && isValidLocation(pair[0], pair[1], dim)) return pair;
        else return null;
    }

    private int[] getBackwardNode(int x, int y, int dim) {
        int[] pair;
        switch (dim) {
            case 0: // east, x+1,y
                pair = new int[]{x - 1, y, dim};
                break;
            case 1: // north, x,y-1
                pair = new int[]{x, y + 1, dim};
                break;
            case 2: // west, x-1,y
                pair = new int[]{x + 1, y, dim};
                break;
            case 3: // south, x,y-1
                pair = new int[]{x, y - 1, dim};
                break;
            default: // error
                pair = null;
                break;
        }
        if (pair != null && isValidLocation(pair[0], pair[1], dim)) return pair;
        else return null;
    }

    // get node to the left of the current node (considering the direction facing)
    private int[] getLeftNode(int x, int y, int dim, int maxTurnCountX, int maxTurnCountY) {
        int[] pair;
        int[] check; // make sure this location is not occupied in order to make the turn
        int nextPosLeft = maxTurnCountX - 1;
        int nextPosStraight = maxTurnCountY - 1;
        switch (dim) {
            case 0: // east, x,y-1 (facing north)
                pair = new int[]{x + nextPosStraight, y - nextPosLeft, 1}; //{x, y - 1, 1};
                check = new int[]{x + nextPosStraight, y, 1};
                break;
            case 1: // north, x-1,y (facing west)
                pair = new int[]{x - nextPosLeft, y - nextPosStraight, 2}; //{x - 1, y, 2};
                check = new int[]{x, y - nextPosStraight, 1};
                break;
            case 2: // west, x,y+1 (facing south)
                pair = new int[]{x - nextPosStraight, y + nextPosLeft, 3}; //{x, y + 1, 3};
                check = new int[]{x - nextPosStraight, y, 3};
                break;
            case 3: // south, x+1,y (facing east)
                pair = new int[]{x + nextPosLeft, y + nextPosStraight, 0}; //{x + 1, y, 0};
                check = new int[]{x, y + nextPosStraight, 0};
                break;
            default: // error
                pair = null;
                check = null;
                break;
        }
        if (pair != null && isValidLocation(pair[0], pair[1], pair[2]) && isValidLocation(check[0], check[1], check[2]))
            return pair;
        else return null;
    }

    private int[] getRightNode(int x, int y, int dim, int maxTurnCountX, int maxTurnCountY) {
        int[] pair;
        int[] check;
        int nextPosRight = maxTurnCountX - 1;
        int nextPosStraight = maxTurnCountY - 1;
        switch (dim) {
            case 0: // east, x,y+1 (facing south)
                pair = new int[]{x + nextPosStraight, y + nextPosRight, 3}; //{x, y + 1, 3};
                check = new int[]{x + nextPosStraight, y, 3};
                break;
            case 1: // north, x+1,y (facing east)
                pair = new int[]{x + nextPosRight, y - nextPosStraight, 0}; //{x + 1, y, 0};
                check = new int[]{x, y - nextPosStraight, 0};
                break;
            case 2: // west, x,y-1 (facing north)
                pair = new int[]{x - nextPosStraight, y - nextPosRight, 1}; //{x, y - 1, 1};
                check = new int[]{x - nextPosStraight, y, 1};
                break;
            case 3: // south, x-1,y (facing west)
                pair = new int[]{x - nextPosRight, y + nextPosStraight, 2}; //{x - 1, y, 2};
                check = new int[]{x, y + nextPosStraight, 2};
                break;
            default: // error
                pair = null;
                check = null;
                break;
        }
        if (pair != null && isValidLocation(pair[0], pair[1], pair[2]) && isValidLocation(check[0], check[1], check[2]))
            return pair;
        else return null;
    }

    /**
     * Heuristic algorithm using manhattan distance from start node to end node.
     */
    private double heuristic(Node n1, Node n2, int endDim) {
        int abs1 = Math.abs(n1.getX() - n2.getX());
        int abs2 = Math.abs(n1.getY() - n2.getY());
        // prefer nodes in the same direction as the end direction
        return (abs1 + abs2) * RobotConstants.MOVE_COST; //* directionWeight;
    }

    /**
     * Greedy algorithm to calculate the path cost. Additional weight
     * on turning to prefer a straight path when possible.
     */
    private double greedy(Node n1, Node n2) {
        int turnCost = 0;
        int cost = RobotConstants.MOVE_COST;

        // check to see if turning is required to get to that direction (not in the same angle dimension)
        if (n1.getDim() != n2.getDim()) {
            turnCost = RobotConstants.TURN_COST_90;
        } else {
            switch (n1.getDim()) {
                case 0:
                    if (n1.getX() > n2.getX())
                        cost = RobotConstants.REVERSE_COST;
                    break;
                case 1:
                    if (n1.getY() > n2.getY())
                        cost = RobotConstants.REVERSE_COST;
                    break;
                case 2:
                    if (n1.getX() < n2.getX())
                        cost = RobotConstants.REVERSE_COST;
                    break;
                case 3:
                    if (n1.getY() < n2.getY())
                        cost = RobotConstants.REVERSE_COST;
                    break;
            }
        }
        // return the sum of the cost to move 1 node + the cost to turn (if turning is done)
        return cost + turnCost;
    }

    /**
     * backtrack from the goal node to get the path
     */
    private ArrayList<MoveType> backtrack(Node end, boolean print) {
        Node curr, prev;
        ArrayList<Node> path = new ArrayList<>();
        ArrayList<MoveType> pathSegments = new ArrayList<>();
        path.add(end);
        curr = end;
        int midpoint = MapConstants.OBSTACLE_WIDTH / 2;
        int diffX = calculateTurnSizeX() * MapConstants.OBSTACLE_WIDTH - MapConstants.OBSTACLE_WIDTH;
        int diffY = calculateTurnSizeY() * MapConstants.OBSTACLE_WIDTH - MapConstants.OBSTACLE_WIDTH;
        double[] lineEnd = new double[]{end.getX() * MapConstants.OBSTACLE_WIDTH + midpoint, end.getY() * MapConstants.OBSTACLE_WIDTH + midpoint}; // keep track of the end point(x2,y2) of the line segment
        double[] lineStart = new double[2];

        int prevDir; // = curr.getDim();
        int currDir;
        double radiusX, radiusY; // the radius we want to use for the turn.
        boolean turnLeft;
        int dirInDegrees;
        boolean reversing;
        while (curr != null) {
            reversing = false;
            path.add(curr);
            prev = predMap.get(curr); // get the previous node in the backtrack
            currDir = curr.getDim();
            if (prev == null) { // if this is the last node, handle the special case.
                lineStart[0] = curr.getX() * MapConstants.OBSTACLE_WIDTH + midpoint;
                lineStart[1] = curr.getY() * MapConstants.OBSTACLE_WIDTH + midpoint;
                switch (currDir) { // check if reversing
                    case 0: // east
                        if (lineEnd[0] < lineStart[0]) reversing = true;
                        break;
                    case 1: // north
                        if (lineEnd[1] > lineStart[1]) reversing = true;
                        break;
                    case 2: // west
                        if (lineEnd[0] > lineStart[0]) reversing = true;
                        break;
                    case 3: // south
                        if (lineEnd[1] < lineStart[1]) reversing = true;
                        break;
                    default: // wut
                }
                pathSegments.add(new LineMove(lineStart[0], lineStart[1], lineEnd[0], lineEnd[1], currDir * 90, true, reversing));
            } else if (prev.getDim() != currDir) { // otherwise, only look for points where direction changes to construct the line segments
                prevDir = prev.getDim();
                lineStart[0] = curr.getX() * MapConstants.OBSTACLE_WIDTH + midpoint;
                lineStart[1] = curr.getY() * MapConstants.OBSTACLE_WIDTH + midpoint;

                // check if it's a left or right turn being made
                dirInDegrees = prev.getDim() * 90;
                if ((dirInDegrees + 90) % 360 == curr.getDim() * 90) {
                    turnLeft = true;
                    radiusX = RobotConstants.LEFT_TURN_RADIUS_X;
                    radiusY = RobotConstants.LEFT_TURN_RADIUS_Y;
                } else {
                    turnLeft = false;
                    radiusX = RobotConstants.RIGHT_TURN_RADIUS_X;
                    radiusY = RobotConstants.RIGHT_TURN_RADIUS_Y;
                }

                // calculate the correct endpoints based on the radius for the line segment portion
                switch (currDir) { // as we are turning into the line, use the X radius.
                    case 0: // east
                        lineStart[0] -= diffX - radiusX;
                        if (lineEnd[0] < lineStart[0]) reversing = true;
                        break;
                    case 1: // north
                        lineStart[1] += diffX - radiusX;
                        if (lineEnd[1] > lineStart[1]) reversing = true;
                        break;
                    case 2: // west
                        lineStart[0] += diffX - radiusX;
                        if (lineEnd[0] > lineStart[0]) reversing = true;
                        break;
                    case 3: // south
                        lineStart[1] -= diffX - radiusX;
                        if (lineEnd[1] < lineStart[1]) reversing = true;
                        break;
                    default: // wut
                }
                pathSegments.add(new LineMove(lineStart[0], lineStart[1], lineEnd[0], lineEnd[1], dirInDegrees, true, reversing));

                // now, calculate the turn between the two points.
                // Note: start of the curve = prev location (lineEnd of the next line). End of the curve = lineStart coordinates
                lineEnd[0] = prev.getX() * MapConstants.OBSTACLE_WIDTH + midpoint;
                lineEnd[1] = prev.getY() * MapConstants.OBSTACLE_WIDTH + midpoint;
                switch (prevDir) {
                    case 0: // east
                        lineEnd[0] += diffY - radiusY;
                        break;
                    case 1: // north
                        lineEnd[1] -= diffY - radiusY;
                        break;
                    case 2: // west
                        lineEnd[0] -= diffY - radiusY;
                        break;
                    case 3: // south
                        lineEnd[1] += diffY - radiusY;
                        break;
                    default: // wut
                }
                pathSegments.add(new ArcMove(lineEnd[0], lineEnd[1], lineStart[0], lineStart[1], dirInDegrees, radiusX, radiusY, false, turnLeft));
            }
            curr = prev;
        }
        Collections.reverse(path); // reverse the path and put it in the correct order
        if (print) printPath(path);
        nodePath = path; // save the path for sending to android team
        Collections.reverse(pathSegments);

        for (MoveType i : pathSegments) System.out.println(i.toString());

        return pathSegments;
    }

    public ArrayList<Node> getNodePath() {
        return nodePath;
    }

    private boolean isValidLocation(int x, int y, int dim) {
        if (x >= 0 && x < numCells && y >= 0 && y < numCells) {
            if (y >= MapConstants.ARENA_BORDER_SIZE && y <= numCells - MapConstants.ARENA_BORDER_SIZE) {
                Node n = grid[y][x][dim];
                return canVisit(n);
            }
        }
        return false;
    }

    /**
     * Instantiate the grid map.
     */
    public void constructMap() {
        ArrayList<PictureObstacle> pictureObstacleList = arena.getObstacles();

        grid = new Node[numCells][numCells][4]; // instantiate the grid (we assume it is a square grid), and that we have 4 possible cardinal directions
        // fill up the grid map
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                for (int k = 0; k < 4; k++) {
                    grid[i][j][k] = new Node(false, false, j, i, k);
                }
            }
        }

        int angleDimension, x, y, id;
        // set picture nodes to isObstacle = true
        for (PictureObstacle pictures : pictureObstacleList) {
            x = pictures.getX();
            y = pictures.getY();
            id = pictureObstacleList.indexOf(pictures);
            angleDimension = angleToDimension(pictures.getImadeDirectionAngle()); // calculate the correct angle dimension the picture node is set in.

            grid[y][x][angleDimension].setPicture(true);
            grid[y][x][angleDimension].setPictureId(id);
            for (int i = 0; i < 4; i++) {
                grid[y][x][i].setVirtualObstacle(true);
            }
            int[][] pairs = getVirtualObstaclePairs(x, y, AlgoConstants.BORDER_THICKNESS);
            int xVirtual, yVirtual;
            // set the surrounding nodes to be virtual obstacles
            for (int[] pair : pairs) {
                xVirtual = pair[0];
                yVirtual = pair[1];
                for (int i = 0; i < 4; i++) {
                    if (xVirtual >= 0 && xVirtual < numCells && yVirtual >= 0 && yVirtual < numCells) { // is the given pair a valid location
                        grid[yVirtual][xVirtual][i].setVirtualObstacle(true);
                    }
                }
            }
        }

        // initialize the arrays
        int numCells = (MapConstants.ARENA_WIDTH / MapConstants.OBSTACLE_WIDTH) + MapConstants.ARENA_BORDER_SIZE * 2;

        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                for (int k = 0; k < 4; k++) {
                    grid[j][i][k].setCost(RobotConstants.MAX_COST, RobotConstants.MAX_COST);
                }
            }
        }
        grid[endPosition[1]][endPosition[0]][endPosition[2] / 90].setCost(0, 0);
    }

    private int angleToDimension(int angle) {
        return angle / 90;
    }

    /**
     * get the locations of the virtual obstacles in terms of pairs [x,y] given a specific x,y
     */
    private int[][] getVirtualObstaclePairs(int x, int y, int thickness) {
        int numCol = 1 + 2 * thickness;
        int numPairs = numCol * numCol - 1; // how many pairs we must generate

        int[][] pairArray = new int[numPairs][];
        int[][] coordinateArray = new int[numCol][numCol];
        int dim = coordinateArray.length;
        int relativeCenter = dim / 2;
        int counter = 0;
        for (int y1 = 0; y1 < dim; y1++) {
            for (int x1 = 0; x1 < dim; x1++) {
                if (x1 != relativeCenter || y1 != relativeCenter) {
                    pairArray[counter] = new int[]{x + x1 - thickness, y + y1 - thickness};
                    counter++;
                }
            }
        }
        return pairArray;
    }

    public void printPath(List<Node> path) {

        char[][] printArray = new char[numCells][numCells];
        for (int y = 0; y < numCells; y++) {
            for (int x = 0; x < numCells; x++) {
                if (grid[y][x][0].isPicture()) printArray[y][x] = 'E';
                else if (grid[y][x][1].isPicture()) printArray[y][x] = 'N';
                else if (grid[y][x][2].isPicture()) printArray[y][x] = 'W';
                else if (grid[y][x][3].isPicture()) printArray[y][x] = 'S';
                else if (grid[y][x][0].isVirtualObstacle()) printArray[y][x] = '/';
                else printArray[y][x] = '-';
            }
        }
        for (Node n : path) {
            int dir = n.getDim();
            switch (dir) {
                case 0:
                    printArray[n.getY()][n.getX()] = '>';
                    break;
                case 1:
                    printArray[n.getY()][n.getX()] = '^';
                    break;
                case 2:
                    printArray[n.getY()][n.getX()] = '<';
                    break;
                case 3:
                    printArray[n.getY()][n.getX()] = 'v';
                    break;
                default:
                    printArray[n.getY()][n.getX()] = 'x';
                    break;
            }
            if (n.getX() == arena.getRobot().getX() && n.getY() == arena.getRobot().getY())
                printArray[n.getY()][n.getX()] = 'R';
        }
        printArray[path.get(0).getY()][path.get(0).getX()] = 'R';

        for (int y = 0; y < numCells; y++) {
            for (int x = 0; x < numCells; x++) {
                System.out.print(printArray[y][x] + "  ");
            }
            System.out.println();
        }
    }

    // code to do a reverse motion if possible.
    public int[] getReversePos(int x, int y, int dim) {
        int[] pair;
        switch (dim) {
            case 0: // east, x+1,y
                pair = new int[]{x - 1, y, dim};
                break;
            case 1: // north, x,y-1
                pair = new int[]{x, y + 1, dim};
                break;
            case 2: // west, x-1,y
                pair = new int[]{x + 1, y, dim};
                break;
            case 3: // south, x,y-1
                pair = new int[]{x, y - 1, dim};
                break;
            default: // error
                pair = null;
                break;
        }
        if (pair != null && canGo(pair[0], pair[1], dim)) return pair;
        else return null;
    }

    // just check if the node is not a virtual obstacle or a picture obstacle.
    private boolean canGo(int x, int y, int dim) {
        if (x >= 0 && x < numCells && y >= 0 && y < numCells) {
            Node n = grid[y][x][dim];
            return !n.isPicture() && !n.isVirtualObstacle();
        } else {
            return false;
        }
    }
}

