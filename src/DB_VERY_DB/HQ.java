package DB_VERY_DB;

import battlecode.common.*;
import java.util.Random;

public strictfp class HQ {

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

    static void run(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        //If mana is above 120
        if (rc.getResourceAmount(ResourceType.MANA) > 120) {
            //Build Launcher
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

        //carrier spam lol
        if(rc.getRobotCount() <= 40){
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        }

        //Otherwise, build anchors!
        else if (rc.canBuildAnchor(Anchor.STANDARD)) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }
    }

}