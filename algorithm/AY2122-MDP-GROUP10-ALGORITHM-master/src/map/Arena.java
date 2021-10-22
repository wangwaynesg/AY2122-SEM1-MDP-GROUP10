package map;

import robot.Robot;
import robot.RobotConstants;

import javax.swing.*;
import java.awt.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the arena in which the car will run
 */
public class Arena extends JPanel {
    private static ArrayList<PictureObstacle> obstacles;
    private static Robot bot;

    public Arena(Robot bot) {
        this.bot = bot;
        System.out.printf("Bot at %d, %d\n", bot.getX(), bot.getY());
        obstacles = new ArrayList<>();
    }

    /**
     * add obstacle into the arena
     * @param x x coord
     * @param y y coord
     * @param imageDirection direction of the image
     * @return whether the obstacle has been added successfully
     */
    public boolean addPictureObstacle(int x, int y, MapConstants.IMAGE_DIRECTION imageDirection) {
        int numGrids = (MapConstants.ARENA_WIDTH / MapConstants.OBSTACLE_WIDTH) + MapConstants.ARENA_BORDER_SIZE * 2;
        PictureObstacle obstacle = new PictureObstacle(x + MapConstants.ARENA_BORDER_SIZE, y + MapConstants.ARENA_BORDER_SIZE, imageDirection);
        if (x < 0 || x >= numGrids || y < 0 || y >= numGrids) {
            System.out.println("Position is out of bounds");
            return false;
        }
        if (overlapWithCar(obstacle)) {
            System.out.printf("Cannot add obstacle centered at <%d, %d> due to overlap with car\n", x, y);
            return false;
        }
        for (PictureObstacle i : obstacles) {
            if (overlapWithObstacle(i, obstacle)) {
                System.out.printf("Cannot add obstacle centered at <%d, %d> due to overlap with obstacle\n", x, y);
                return false;
            }
        }
        obstacles.add(obstacle);
        System.out.printf("Added obstacle centered at <%d, %d>, with direction %c\n", x, y, MapConstants.IMAGE_DIRECTION.print(imageDirection));
        return true;
    }

    /**
     * check whether the new obstacle is overlapping with the car
     */
    private boolean overlapWithCar(PictureObstacle obstacle) {
        int minimumGap = (RobotConstants.ROBOT_VIRTUAL_WIDTH - MapConstants.OBSTACLE_WIDTH) / 2 / MapConstants.OBSTACLE_WIDTH;

        return (Math.abs(obstacle.getX() - bot.getX()) < minimumGap + 1 && Math.abs(obstacle.getY() - bot.getY()) < minimumGap + 1);
    }

    /**
     * check whether the new obstacle has been overlapping with existing obstacles
     * @param obs1
     * @param obs2
     * @return
     */
    private boolean overlapWithObstacle(PictureObstacle obs1, PictureObstacle obs2) {
        return (obs1.getX() == obs2.getX()) && (obs1.getY() == obs2.getY());
    }

    public static ArrayList<PictureObstacle> getObstacles() {
        return obstacles;
    }

    public Robot getRobot() {
        return bot;
    }
}
