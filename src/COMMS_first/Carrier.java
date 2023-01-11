package COMMS_first;

import battlecode.common.*;

import java.util.Random;

public strictfp class Carrier {

    static MapLocation location;
    static CarrierType carrierType = CarrierType.None;

    private static enum CarrierType {
        None, ExploreCarrier, ReturnCarrier, AnchorCarrier, WellCarrier;
    }

    static MapLocation HQ_LOCATION = null;

    static void run(RobotController rc) throws GameActionException {

        location = rc.getLocation();

        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBF(rc, new MapLocation(25, 25));

            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }

        System.out.println(Comms.getClosestTeamHQLocation(rc, location));
        rc.setIndicatorLine(location, Comms.getClosestTeamHQLocation(rc, location),255, 0, 0);
    }


}
