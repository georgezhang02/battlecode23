package obselete.DB_first;

import battlecode.common.*;

import java.util.Random;

public strictfp class Carrier {

    static final Random rng = new Random(6147);

    static CarrierType carrierType = CarrierType.None;
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

    private static enum CarrierType {
        None, ExploreCarrier, ReturnCarrier, AnchorCarrier, WellCarrier;
    }

    static MapLocation HQ_LOCATION = null;

    static void run(RobotController rc) throws GameActionException {

        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBF(rc, new MapLocation(25, 25));

            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }


}
