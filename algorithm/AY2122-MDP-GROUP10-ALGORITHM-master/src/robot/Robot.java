package robot;

import java.awt.*;

import robot.RobotConstants.ROBOT_DIRECTION;

/**
 * This class represents the robot
 */
public class Robot {
    private Point centerCoordinate;
    private ROBOT_DIRECTION direction;
    private Boolean realBot;

    public Robot(Point centerCoordinate, ROBOT_DIRECTION direction, Boolean realBot) {
        this.centerCoordinate = centerCoordinate;
        this.direction = direction;
        this.realBot = realBot;
    }

    public Point getCenterCoordinate() {
        return centerCoordinate;
    }

    public ROBOT_DIRECTION getDirection() {
        return direction;
    }

    public int getRobotDirectionAngle() {
        int degree;
        switch (direction) {
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
                degree = -1;
                break;
        }
        return degree;
    }

    public int getX() {
        return centerCoordinate.x;
    }

    public int getY() {
        return centerCoordinate.y;
    }

    public Boolean getRealBot() {
        return realBot;
    }

    public void setCenterCoordinate(Point centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
    }

    public void setRealBot(Boolean realBot) {
        this.realBot = realBot;
    }

    public void setDirection(ROBOT_DIRECTION direction) {
        this.direction = direction;
    }

    public void setDirection(int angle) {
        switch (angle) {
            case 0:
                direction = ROBOT_DIRECTION.EAST;
                break;
            case 90:
                direction = ROBOT_DIRECTION.NORTH;
                break;
            case 180:
                direction = ROBOT_DIRECTION.WEST;
                break;
            case 270:
                direction = ROBOT_DIRECTION.SOUTH;
                break;
            default:
                direction = ROBOT_DIRECTION.EAST;
                break;
        }
    }
}
