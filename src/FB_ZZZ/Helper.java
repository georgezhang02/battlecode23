package FB_ZZZ;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

import java.util.Random;
import java.util.Set;

public class Helper {
    static final Random rng = new Random(6147);

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    static int distanceTo(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    static MapLocation getClosest(MapLocation[] options, MapLocation location) {
        int lowestDist = 10000;
        MapLocation closest = null;
        for (MapLocation option: options) {
            int distance = location.distanceSquaredTo(option);
            if (distance < lowestDist) {
                lowestDist = distance;
                closest = new MapLocation(option.x, option.y);
            }
        }
        return closest;
    }

    static MapLocation getClosest(MapLocation[] options, MapLocation location, Set<MapLocation> visitedWells) {
        int lowestDist = 10000;
        MapLocation closest = null;
        for (MapLocation option: options) {
            if (!visitedWells.contains(option)) {
                int distance = location.distanceSquaredTo(option);
                if (distance < lowestDist) {
                    lowestDist = distance;
                    closest = new MapLocation(option.x, option.y);
                }
            }
        }
        return closest;
    }

    /*
    private static MapLocation rotate(MapLocation loc) {
        return new MapLocation(width - loc.x - 1, height - loc.y - 1);
    }

    private static MapLocation reflectAcrossVertical(MapLocation loc) {
        int newX = width - loc.x - 1;
        return new MapLocation(newX, loc.y);
    }

    private static MapLocation reflectAcrossHorizontal(MapLocation loc) {
        int newY = height - loc.y - 1;
        return new MapLocation(loc.x, newY);
    }

     */
}
