package algorithms;

import map.Arena;
import map.PictureObstacle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Algorithm class for calculating the shortest Hamiltonian path to each obstacle
 */
public class FastestPathAlgo {

    private final Arena arena;

    public FastestPathAlgo(Arena arena) {
        this.arena = arena;
    }

    /**
     * Plan the fastest path and return as an array
     */
    public int[] planFastestPath() {
        ArrayList<PictureObstacle> list = Arena.getObstacles();
        int[] indexArray = IntStream.range(0, list.size()).toArray();
        List<int[]> permutations = getPermutations(indexArray); //getPermutations(Arena.getObstacles().keySet().stream().mapToInt(i -> i).toArray());
        double smallestCost = Double.MAX_VALUE;
        int[] shortestPath = permutations.get(0);
        int numOfThreads = 6;
        // 4: 24
        // 6: 22
        // 8: 21
        int size = permutations.size();
        for (int i = 0; i < size; i += numOfThreads) {
            //double pathCost = getPathCost(permutation, list, algo);
            Thread[] threads = new Thread[numOfThreads];
            FastestPathRunnable[] runnables = new FastestPathRunnable[numOfThreads];
            for (int j = 0; j < numOfThreads; j++) {
                if (i + j < size) {
                    runnables[j] = new FastestPathRunnable(permutations.get(i + j), arena);
                    threads[j] = new Thread(runnables[j]);
                    threads[j].start();
                }
            }

            for (int j = 0; j < numOfThreads; j++) {
                if (i + j < size) {
                    try {
                        threads[j].join();
                        if (runnables[j].getCost() < smallestCost) {
                            smallestCost = runnables[j].getCost();
                            shortestPath = runnables[j].getPath();
                        }
                    } catch (Exception e) {
                        System.out.println("Exception occurred while joining thread: " + e);
                    }
                }
            }
        }
        System.out.println("Shortest path cost: " + smallestCost);
        return shortestPath;
    }

    /**
     * Get all the possible permutations
     */
    private List<int[]> getPermutations(int[] nodes) {
        List<int[]> permutations = new ArrayList<>();
        generateHeapPermutations(nodes, permutations, nodes.length);
        return permutations;
    }

    /**
     * Helper function for generating permutations
     */
    private void swap(int[] array, int index1, int index2) {
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    /**
     * Generate the permutations heap
     */
    private void generateHeapPermutations(int[] permutation, List<int[]> permutations, int n) {
        if (n <= 0) {
            permutations.add(permutation);
            return;
        }
        int[] tempPermutation = Arrays.copyOf(permutation, permutation.length);
        for (int i = 0; i < n; i++) {
            swap(tempPermutation, i, n - 1);
            generateHeapPermutations(tempPermutation, permutations, n - 1);
            swap(tempPermutation, i, n - 1);
        }
    }
}
