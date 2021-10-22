package algorithms;

/**
 * This class represents the turning movement.
 */
public class ArcMove extends MoveType {
    private double radiusX;
    private double radiusY;
    private boolean turnLeft;

    public ArcMove(double x1, double y1, double x2, double y2, int dirInDegrees, double radiusX, double radiusY, boolean isLine, boolean turnLeft) {
        super(x1, y1, x2, y2, dirInDegrees, isLine, false);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.turnLeft = turnLeft;
    }

    /**
     * Get the x radius
     */
    public double getRadiusX() {
        return radiusX;
    }

    /**
     * Get the y radius
     */
    public double getRadiusY() {
        return radiusY;
    }

    /**
     * Check if the turn is a left turn (otherwise is a right turn)
     */
    public boolean isTurnLeft() {
        return turnLeft;
    }

    /**
     * Return arc length
     */
    @Override
    public double getLength() {
        return 2 * Math.PI * radiusX * 0.25;
    }

    /**
     * Return string of arc variables
     */
    @Override
    public String toString() {
        if (turnLeft)
            return "Arc: Turning left, " + super.toString();
        else
            return "Arc: Turning right, " + super.toString();
    }
}
