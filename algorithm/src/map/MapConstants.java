package map;

public class MapConstants {
    public static final int OBSTACLE_WIDTH = 10;
    public static final int ARENA_BORDER_SIZE = 0;

    public static final int ARENA_WIDTH = 200;

    public enum IMAGE_DIRECTION {
        NORTH, EAST, SOUTH, WEST;

        public static char print(IMAGE_DIRECTION d) {
            switch (d) {
                case NORTH:
                    return 'N';
                case EAST:
                    return 'E';
                case SOUTH:
                    return 'S';
                case WEST:
                    return 'W';
                default:
                    return 'X';
            }
        }

        public static IMAGE_DIRECTION getImageDirection(String s) {
            switch (s) {
                case "N":
                    return NORTH;
                case "E":
                    return EAST;
                case "S":
                    return SOUTH;
                case "W":
                    return WEST;
                default:
                    return null;
            }
        }
    }
}
