package DB_VERY_DB;

import DB_VERY_DB.Comms;
import DB_VERY_DB.Helper;
import DB_VERY_DB.Pathfinder;
import battlecode.common.*;

import java.util.Map;

public strictfp class HQ {

    static boolean initialized = false;
    static MapLocation location;
    static boolean firstHQ;
    static String indicatorString;
    static int wellIndex;

    public static void run(RobotController rc) throws GameActionException {

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        initTurn(rc); // cleanup for when the turn starts

        // sense part
        sense();

        //think part, NOTE -> later one we might want to t
        // take into account whether your action and movement are available,
        //and what order you want to do them in
        //select your next state, movement, and action based on comms, sensor data, and state

        // interpret overall macro state
        readComms();

        //act part should be triggered by think part, see methods below
        build(rc);

        debug(rc);
        // prints the indicator string

    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        location = rc.getLocation();
        firstHQ = Comms.setTeamHQLocation(rc, location);
        for (WellInfo well : rc.senseNearbyWells()) {
            Comms.addWellLocation(rc, well.getMapLocation());
        }
        wellIndex = 0;
    }

    static void initTurn(RobotController rc) throws GameActionException {
        indicatorString = "";
    }

    static void readComms(){

    }

    static void sense(){

    }

    static void build(RobotController rc) throws GameActionException{
        // Pick a direction to build in.
        Direction dir = Helper.directions[Helper.rng.nextInt(Helper.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        //If there are less than 15 bots, or one of mana or ad is booming
        if (rc.getRobotCount() <= 15 || rc.getResourceAmount(ResourceType.MANA) > 150 || rc.getResourceAmount(ResourceType.ADAMANTIUM) > 150) {

            //Build Carrier
            MapLocation buildLoc = buildTowards(rc, Comms.getWellLocation(rc, wellIndex));
            if (rc.canBuildRobot(RobotType.CARRIER, buildLoc)) {
                rc.buildRobot(RobotType.CARRIER, buildLoc);
            }

            //Build Launcher
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

        //Otherwise, build anchors!
        else if (rc.canBuildAnchor(Anchor.STANDARD)) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }
    }

    static void debug(RobotController rc){
        rc.setIndicatorString(indicatorString);
    }



    private static MapLocation buildTowards(RobotController rc, MapLocation target) throws GameActionException {
        int lowestDist = 10000;
        MapLocation buildSquare = null;
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(location, 9);
        for (MapLocation loc : locs) {
            int distance = Helper.distanceTo(loc.x, loc.y, target.x, target.y);
            if (distance < lowestDist && rc.sensePassability(loc)) {
                lowestDist = distance;
                buildSquare = new MapLocation(loc.x, loc.y);
            }
        }
        return buildSquare;
    }
}