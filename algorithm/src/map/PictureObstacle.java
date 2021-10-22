package map;

import java.awt.*;

import map.MapConstants.IMAGE_DIRECTION;

/**
 * This class represents the obstacles with picture in the arena
 */
public class PictureObstacle {
    private int imageId;
    private IMAGE_DIRECTION imageDirection;
    private Point centerCoordinate;

    public PictureObstacle(int x, int y, IMAGE_DIRECTION imageDirection) {
        this.imageDirection = imageDirection;
        this.centerCoordinate = new Point(x, y);
        imageId = 0;
    }

    public int getImageId() {
        return imageId;
    }

    public IMAGE_DIRECTION getImageDirection() {
        return imageDirection;
    }

    public int getImadeDirectionAngle() {
        int degree;
        switch (imageDirection) {
            case EAST:
                degree = 0;
                break;
            case NORTH:
                degree = 90;
                break;
            case WEST:
                degree = 180;
                break;
            case SOUTH:
                degree = 270;
                break;
            default:
                degree = 0;
                break;
        }
        return degree;
    }

    public Point getCenterCoordinate() {
        return centerCoordinate;
    }

    public Point getBottomLeftCoordinate() {
        return new Point(centerCoordinate.x - MapConstants.OBSTACLE_WIDTH / 2, centerCoordinate.y - MapConstants.OBSTACLE_WIDTH / 2);
    }

    public int getX() {
        return centerCoordinate.x;
    }

    public int getY() {
        return centerCoordinate.y;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setImageDirection(IMAGE_DIRECTION imageDirection) {
        this.imageDirection = imageDirection;
    }

    public void setCenterCoordinate(Point centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
    }
}
