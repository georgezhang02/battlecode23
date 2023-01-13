package BB_kiteexplore;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Comms {
    private static final int COUNT_OFFSET = 0;
    private static final int TEAM_HQ_OFFSET = 1;
    private static final int WELL_OFFSET = 9;
    private static final int MAX_WELLS = 8;

    /**
     * Sets the team HQ location in the next available location
     * @return the index corresponding to the HQ
     */
    public static int getNumHQs(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET), 0);
    }



    // HQ
    /**
     * Sets the team HQ location in the next available location
     * @return the index corresponding to the HQ
     */
    public static int setTeamHQLocation(RobotController rc, MapLocation HQLocation) throws GameActionException {
        int count = decode(rc.readSharedArray(COUNT_OFFSET), 0);
        rc.writeSharedArray(count + TEAM_HQ_OFFSET, encode(HQLocation.x, HQLocation.y));
        rc.writeSharedArray(COUNT_OFFSET, encode(count + 1));
        return count;
    }

    /**
     * Returns the location of the closest team HQ location
     */
    public static MapLocation getTeamHQLocation(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + TEAM_HQ_OFFSET);
        if(index == 0){
            rc.setIndicatorString(decode(value, 2)+"" );
        }

        int x = decode(value, 0);
        int y = decode(value, 1);
        return new MapLocation(x, y);

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
                int distance = Helper.distanceTo(current.x, current.y, x, y);
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
     * Add the location of a newly discovered well
     */
    public static int addWellLocation(RobotController rc, MapLocation well) throws GameActionException {
        int count = decode(rc.readSharedArray(COUNT_OFFSET), 1);
        if (count < MAX_WELLS) {
            for (int i = 0; i < count; i++) {
                int value = rc.readSharedArray(i + WELL_OFFSET);
                int wellXAtIndex = decode(value, 0);
                int wellYAtIndex = decode(value, 1);
                if (wellXAtIndex == well.x && wellYAtIndex == well.y) {
                    return -1;
                }
            }
            rc.writeSharedArray(count + WELL_OFFSET, encode(well.x, well.y));
            int HQCount = decode(rc.readSharedArray(COUNT_OFFSET), 0);
            rc.writeSharedArray(COUNT_OFFSET, encode(HQCount, count + 1));
            return count;
        }
        return -1;
    }

    public static MapLocation getWellLocation(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_OFFSET);
        int x = decode(value, 0);
        int y = decode(value, 1);
        return new MapLocation(x, y);
    }

    /**
     * Sets the status of the well
     * 5: starter well
     */
    public static void setWellStatus(RobotController rc, int wellIndex, int status) throws GameActionException {
        int value = rc.readSharedArray(wellIndex + WELL_OFFSET);
        int x = decode(value, 0);
        int y = decode(value, 1);
        rc.writeSharedArray(wellIndex + WELL_OFFSET, encode(x, y, status));
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
