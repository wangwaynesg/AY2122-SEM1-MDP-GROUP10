package map;

/**
 * Class to represent nodes in arena grid.
 */
public class Node {
    private boolean isPicture;
    private boolean hasBeenVisited;
    private int pictureId;
    private boolean isVirtualObstacle;
    private int x;
    private int y;
    private int dim; // what angle dimension is this node in
    private double hCost;
    private double gCost;
    private double totalCost;

    public Node(boolean isPicture, boolean isVirtualObstacle, int x, int y, int dim) {
        this.isPicture = isPicture;
        this.isVirtualObstacle = isVirtualObstacle;
        this.x = x;
        this.y = y;
        this.dim = dim;
        this.hasBeenVisited = false;
        this.gCost = 0;
        this.hCost = 0;
        this.totalCost = 0;
    }

    public void setHasBeenVisited(boolean hasBeenVisited) {
        this.hasBeenVisited = hasBeenVisited;
    }

    public void setVirtualObstacle(boolean obstacle) {
        isVirtualObstacle = obstacle;
    }

    public void setPicture(boolean picture) {
        isPicture = picture;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public void setCost(double hCost, double gCost) {
        this.hCost = hCost;
        this.gCost = gCost;
        this.totalCost = hCost + gCost;
    }

    public boolean isVisited() {
        return hasBeenVisited;
    }

    public boolean isPicture() {
        return isPicture;
    }

    public boolean isVirtualObstacle() {
        return isVirtualObstacle;
    }

    public int getPictureId() {
        return pictureId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDim() {
        return dim;
    }

    public double getCost() {
        return totalCost;
    }

    public double getGCost() {
        return gCost;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", d=" + dim +
                '}';
    }
}
