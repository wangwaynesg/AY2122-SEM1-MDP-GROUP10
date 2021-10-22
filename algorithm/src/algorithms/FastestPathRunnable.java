package algorithms;

import map.Arena;
import map.PictureObstacle;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Used for multi-threading the different path calculations
 */
public class FastestPathRunnable implements Runnable {
    private double cost;
    private int[] order;
    private Arena arena;

    public FastestPathRunnable(int[] order, Arena arena) {
        this.order = order;
        this.arena = arena;
    }

    /**
     * Do the fastest path calculation for the given permutation
     */
    public void run() {
        ArrayList<PictureObstacle> list = Arena.getObstacles();
        TripPlannerAlgo algo = new TripPlannerAlgo(arena);
        int[] indexArray = IntStream.range(0, list.size()).toArray();
        try {
            PathCostAlgo pathing = new PathCostAlgo();
            cost = pathing.getPathCost(order, list, algo, arena);
        } catch (Exception e) {
            // Throwing an exception
            e.printStackTrace();
            //System.out.println("Exception is caught");
        }
    }

    public double getCost() {
        return cost;
    }

    public int[] getPath() {
        return order;
    }
}
