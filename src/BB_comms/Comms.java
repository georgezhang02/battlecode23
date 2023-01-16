package BB_comms;

import battlecode.common.*;

public class Comms {
    private static final int COUNT_OFFSET = 0;
    private static final int TEAM_HQ_OFFSET = 1;
    private static final int WELL_COMMAND_OFFSET = 9;
    private static final int WELL_OFFSET = 13;
    private static final int MAX_WELLS = 12;
    private static final int WELL_REPORT_OFFSET = WELL_OFFSET + MAX_WELLS;

    /**
     * Sets the team HQ location in the next available location
     * @return the number of HQs
     */
    public static int getNumHQs(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET), 0);
    }

    // HQ
    /**
     * Sets the team HQ location in the next available location
     * @return the index corresponding to the HQ
     */
    public static int setTeamHQLocation(RobotController rc, MapLocation HQLocation, int id) throws GameActionException {
        int count = getNumHQs(rc);
        int wellCount = getNumWells(rc);
        rc.writeSharedArray(count + TEAM_HQ_OFFSET, encode(HQLocation.x, HQLocation.y, id));
        rc.writeSharedArray(COUNT_OFFSET, encode(count + 1, wellCount));
        return count;
    }

    public static int getHQIndexByID(RobotController rc, int HQID) throws GameActionException {
        for (int i = 0; i < Comms.getNumHQs(rc); i++) {
            int id = decode(rc.readSharedArray(i + TEAM_HQ_OFFSET), 2);
            if (HQID == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the location of the closest team HQ location
     */
    public static MapLocation getTeamHQLocation(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + TEAM_HQ_OFFSET);
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

    public static int getNumWells(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET), 1);
    }

    public static void addWellLocation(RobotController rc, MapLocation well) throws GameActionException {
        int count = getNumWells(rc);
        if (count < MAX_WELLS) {
            for (int i = 0; i < count; i++) {
                int value = rc.readSharedArray(i + WELL_OFFSET);
                int wellXAtIndex = decode(value, 0);
                int wellYAtIndex = decode(value, 1);
                if (wellXAtIndex == well.x && wellYAtIndex == well.y) {
                    return;
                }
            }
            rc.writeSharedArray(count + WELL_OFFSET, encode(well.x, well.y));
            int HQCount = decode(rc.readSharedArray(COUNT_OFFSET), 0);
            rc.writeSharedArray(COUNT_OFFSET, encode(HQCount, count + 1));
        }
    }

    public static int[] getAllWellValues(RobotController rc) throws GameActionException {
        int count = getNumWells(rc);
        int[] allWells = new int[count];
        for (int i = 0; i < count; i++) {
            allWells[i] = rc.readSharedArray(i + WELL_OFFSET);
        }
        return allWells;
    }

    public static MapLocation getWellCommand(RobotController rc, int HQIndex) throws GameActionException {
        int value = rc.readSharedArray(WELL_COMMAND_OFFSET + HQIndex);
        int x = decode(value, 0);
        int y = decode(value, 1);
        int assigned = decode(value, 2);
        if (assigned == 1) {
            return new MapLocation(x, y);
        }
        return null;
    }
    public static void writeWellCommand(RobotController rc, int HQIndex, MapLocation well) throws GameActionException {
        rc.writeSharedArray(HQIndex + WELL_COMMAND_OFFSET, encode(well.x, well.y, 1));
    }

    public static void clearWellCommand(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + WELL_COMMAND_OFFSET, encode(0, 0, 0));
    }

    public static boolean reportWellLocation(RobotController rc, int index, WellInfo well) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_REPORT_OFFSET);
        if (decode(value, 2) == 0) {
            MapLocation loc = well.getMapLocation();
            int wellType = 0;
            switch (well.getResourceType()) {
                case ADAMANTIUM:
                    wellType = 1;
                    break;
                case MANA:
                    wellType = 2;
                    break;
                case ELIXIR:
                    wellType = 3;
                    break;
            }
            rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(loc.x, loc.y, wellType));
            return true;
        }
        return false;
    }

    public static int[] readWellReport(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_REPORT_OFFSET);
        int[] report = new int[3];
        report[0] = decode(value, 0);
        report[1] = decode(value, 1);
        int type = decode(value, 2);
        if (type == 0) {
            return null;
        }
        report[2] = type;
        return report;
    }

    public static void clearWellReport(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(0, 0, 0));
    }

    /**
     * Decodes one array value at an index 0, 1, or 2
     */
    static int decode(int value, int index) {
        // index 0: number % 64 / 1, index 1: number % 64^2 / 64, index 3: number % 64^3 / 64^2
        return (value % (int) Math.pow(64, index + 1) / (int) Math.pow(64, index));
    }

    /**
     * Encodes a variable number of values into a single array value ready to be written
     */
    static int encode(int... fields) {
        int result = 0;
        for (int i = 0; i < fields.length; i++) {
            result += fields[i] * Math.pow(64, i);
        }
        return result;
    }
}
