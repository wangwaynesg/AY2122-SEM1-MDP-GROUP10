package utils;

/**
 * xxxy
 * <p>
 * x is the distance (in cm) or angle (in degrees),whether it’s distance or angle is depending on the direction
 * <p>
 * And y is the direction of motion
 * <p>
 * Values of y with their corresponding motions are:
 * 0, forwards
 * 1, backwards
 * 2, forward left
 * 3, forward right
 * 4, backwards left
 * 5, backwards right
 * 6, some special algo to position the robot to the next face of the obstacle
 * 7, stop movement for time specified by rpi
 * 8, reset front wheels (note that effectiveness of resetting wheels might depend on the ground type that the robot
 * is on, I haven’t had the chance to test it out extensively yet though)
 * <p>
 * Eg. 1000 is equal to 100 cm forwards
 * 1002 is equal to 100 degrees forward left
 * And special case 1007 is stop movement for 1 second (to get 1 second take the 100 before the direction number and
 * multiply by 10 to get 1000ms, yes it’s calculated in milliseconds so that is equal to 1 second)
 */

public class CommConstants {
    public static final String HOST_ADDRESS = "192.168.10.1";
    public static final int PORT = 5000;

    public enum INSTRUCTION_TYPE {
        FORWARD, BACKWARD, FORWARD_LEFT, FORWARD_RIGHT, BACKWARD_LEFT, BACKWARD_RIGHT, SPECIAL, STOP_AFTER, RESET_WHEELS;

        public static String encode(CommConstants.INSTRUCTION_TYPE i) {
            switch (i) {
                case FORWARD:
                    return "0";
                case BACKWARD:
                    return "1";
                case FORWARD_LEFT:
                    return "2";
                case FORWARD_RIGHT:
                    return "3";
                case BACKWARD_LEFT:
                    return "4";
                case BACKWARD_RIGHT:
                    return "5";
                case SPECIAL:
                    return "6";
                case STOP_AFTER:
                    return "7";
                case RESET_WHEELS:
                    return "8";
                default:
                    return "-1";
            }
        }
    }

}
