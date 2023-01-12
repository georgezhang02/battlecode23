package BB_runaway;

import battlecode.common.*;

public strictfp class HQ {

    static final int PERWELL = 3;

    static boolean initialized = false;
    static MapLocation location;
    static int HQIndex;

    static MapLocation[] starterADWell = new MapLocation[144];
    static int starterADWellCount = 0;
    static int starterADWellAssigned = 0;
    static MapLocation[] starterMNWell = new MapLocation[144];
    static int starterMNWellCount = 0;
    static int starterMNWellAssigned = 0;

    static String indicatorString;

    static enum HQState{
        STARTERAD, STARTERMN, EXPLORER
    }
    static HQState state;
    static int id;

    static RobotType buildType;
    static int totalAnchorCount = 0;
    static int anchorsProduced = 0;
    static int robotsProduced = 0;

    public static void run(RobotController rc) throws GameActionException {

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        initTurn(rc); // cleanup for when the turn starts

        // sense part
        sense(rc);

        // interpret overall macro state
        readComms();

        think(rc);

        //act part should be triggered by think part, see methods below
        build(rc);

        // prints the indicator string
        debug(rc);
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        state = HQState.STARTERAD;
        location = rc.getLocation();
        HQIndex = Comms.setTeamHQLocation(rc, location);
        id = rc.getID();
        for (WellInfo well : rc.senseNearbyWells()) {
            if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                starterADWell[starterADWellCount++] = well.getMapLocation();
            } else if (well.getResourceType() == ResourceType.MANA) {
                starterMNWell[starterMNWellCount++] = well.getMapLocation();
            }
            int wellIndex = Comms.addWellLocation(rc, well.getMapLocation());
            Comms.setWellStatus(rc, wellIndex, 5);
        }
    }

    static void initTurn(RobotController rc) throws GameActionException {
        indicatorString = "";
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();
    }

    static void readComms(){

    }

    static void think(RobotController rc) {
        if (state == HQState.STARTERAD) {
            if (starterADWellCount == 0 || starterADWellAssigned >= PERWELL * starterADWellCount) {
                state = HQState.STARTERMN;
            } else {
                MapLocation selectedWell = starterADWell[starterADWellAssigned / PERWELL];
                //Comms.setCommand(rc, HQIndex, selectedWell);
                buildType = RobotType.CARRIER;
            }
        }
        if (state == HQState.STARTERMN) {
            if (starterMNWellCount == 0 || starterMNWellAssigned >= PERWELL * starterMNWellCount) {
                state = HQState.EXPLORER;
            } else {
                MapLocation selectedWell = starterMNWell[starterMNWellAssigned / PERWELL];
                //Comms.setCommand(rc, HQIndex, selectedWell);
                buildType = RobotType.CARRIER;
            }
        }
        indicatorString = state.toString();
    }

    static void build(RobotController rc) throws GameActionException{
        MapLocation carrierBuildLoc;
        MapLocation launcherBuildLoc;
        switch(state) {
            case STARTERAD:
                carrierBuildLoc = buildTowards(rc, starterADWell[starterADWellAssigned / PERWELL]);
                if (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
                    rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                    starterADWellAssigned++;
                    robotsProduced++;
                }
                break;
            case STARTERMN:
                carrierBuildLoc = buildTowards(rc, starterMNWell[starterMNWellAssigned / PERWELL]);
                if (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
                    rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                    starterMNWellAssigned++;
                    robotsProduced++;
                }
                break;
            case EXPLORER:
                Direction dir = Helper.directions[Helper.rng.nextInt(Helper.directions.length)];
                launcherBuildLoc = rc.getLocation().add(dir);
                carrierBuildLoc = rc.getLocation().add(dir);
                if (totalAnchorCount == 0 && rc.canBuildAnchor(Anchor.STANDARD)) {
                    rc.buildAnchor(Anchor.STANDARD);
                    anchorsProduced++;
                } else if (robotsProduced < 25 * (anchorsProduced+1)) {
                    if (rc.canBuildRobot(RobotType.LAUNCHER, launcherBuildLoc)) {
                        rc.buildRobot(RobotType.LAUNCHER, launcherBuildLoc);
                        robotsProduced++;
                    } else if (rc.canBuildRobot(RobotType.CARRIER, launcherBuildLoc)) {
                        rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                        robotsProduced++;
                    }
                }
                break;
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