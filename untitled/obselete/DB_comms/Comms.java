package obselete.DB_comms;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Comms {
    private static final int TEAM_HQ_OFFSET = 0;

    // HQ
    /**
     * Sets the team HQ location in the next available location
     * @return true if the HQ is the first to set its location
     */
    public static boolean setTeamHQLocation(RobotController rc, MapLocation HQLocation) throws GameActionException {
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            int value = rc.readSharedArray(i + TEAM_HQ_OFFSET);
            if (decode(value, 2) == 0) {
                rc.writeSharedArray(i + TEAM_HQ_OFFSET, encode(HQLocation.x, HQLocation.y, 1));
                return i == 0;
            }
        }
        return false;
    }

    /**
     * Returns the location of the closest team HQ location
     */
    public static MapLocation getClosestTeamHQLocation(RobotController rc, MapLocation current) throws GameActionException {
        int lowest_dist = 100;
        MapLocation closest = null;
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            int value = rc.readSharedArray(i + TEAM_HQ_OFFSET);
            if (decode(value, 2) != 0) {
                int x = decode(value, 0);
                int y = decode(value, 1);
                int distance = Math.max(Math.abs(x - current.x), Math.abs(y - current.y));
                if (distance < lowest_dist) {
                    lowest_dist = distance;
                    closest = new MapLocation(x, y);
                }
            } else {
                break;
            }
        }
        return closest;
    }

    /**
     * Decodes one array value at an index 0, 1, or 2
     */
    private static int decode(int value, int index) {
        // index 0: number % 64 / 1, index 1: number % 64^2 / 64, index 3: number % 64^3 / 64^2
        return (value % (int) Math.pow(64, index + 1) / (int) Math.pow(64, index));
    }

    /**
     * Encodes a variable number of values into a single array value ready to be written
     */
    private static int encode(int... fields) {
        int result = 0;
        for (int i = 0; i < fields.length; i++) {
            result += fields[i] * Math.pow(64, i);
        }
        return result;
    }
}
