package DB_VERY_DB;

import battlecode.common.*;

import java.util.Random;

public strictfp class Launcher {

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
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

    static void run(RobotController rc) throws GameActionException {
        // Try to attack someone
        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBF(rc, new MapLocation(25, 25));
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }

}
