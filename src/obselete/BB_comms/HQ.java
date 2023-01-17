package obselete.BB_comms;

import battlecode.common.*;

public strictfp class HQ {

    static final int[] PERWELL = {3, 3, 3}; // {AD, MN, EX}
    static final int ANCHOR_BUILD_THRESHOLD = 30;

    static boolean initialized = false;
    static MapLocation location;
    static int HQIndex;
    static int id;
    static MapLocation center;
    static String indicatorString = "";

    static MapLocation[] wellsDiscoveredNearby = new MapLocation[288];
    static int wellsDiscoveredCount = 0;
    static int[] wellsDiscoveredType = new int[288]; // 0 = AD, 1 = MN, 2 = EX
    static int[] wellsAssigned = new int[288];
    static int wellsAssignedCount = 0;
    static boolean assigning = false;
    static MapLocation carrierBuildTarget;

    static int totalAnchorCount = 0;

    static RobotInfo[] enemies;
    static boolean enemiesFound;

    public static void run(RobotController rc) throws GameActionException {

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        initTurn(); // cleanup for when the turn starts

        // sense part
        sense(rc);

        checkEnemies();

        think(rc);

        //act part should be triggered by think part, see methods below
        build(rc);

        // prints the indicator string
        debug(rc);
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        location = rc.getLocation();
        id = rc.getID();
        HQIndex = Comms.setTeamHQLocation(rc, location, id);
        center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);  // Get map center

        // Initialize all the wells within vision range
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            if (well.getResourceType() == ResourceType.MANA) {
                MapLocation loc = well.getMapLocation();
                Comms.addWellLocation(rc, loc);
                wellsDiscoveredNearby[wellsDiscoveredCount] = loc;
                wellsDiscoveredType[wellsDiscoveredCount] = 1;
                wellsDiscoveredCount++;
            }
        }
        for (WellInfo well: wells) {
            if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                MapLocation loc = well.getMapLocation();
                Comms.addWellLocation(rc, loc);
                wellsDiscoveredNearby[wellsDiscoveredCount] = loc;
                wellsDiscoveredType[wellsDiscoveredCount] = 0;
                wellsDiscoveredCount++;
            }
        }
    }

    static void initTurn() {
        //indicatorString = "";
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();
        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());
    }

    static void checkEnemies() {
        enemiesFound = false;
        for (RobotInfo enemy : enemies) {
            if (!(enemy.getType() == RobotType.HEADQUARTERS || enemy.getType() == RobotType.CARRIER)) {
                enemiesFound = true;
                break;
            }
        }
    }

    static void think(RobotController rc) throws GameActionException {
        // Take in report if available
        int[] report = Comms.readWellReport(rc, HQIndex);
        if (report != null) {
            MapLocation loc = new MapLocation(report[0], report[1]);
            boolean duplicate = false;
            for (int i = 0; i < wellsDiscoveredCount; i++) {
                if (loc.equals(wellsDiscoveredNearby[i])) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                Comms.addWellLocation(rc, loc);
                wellsDiscoveredNearby[wellsDiscoveredCount] = loc;
                wellsDiscoveredType[wellsDiscoveredCount] = report[2] - 1;
                wellsDiscoveredCount++;
            }
            Comms.clearWellReport(rc, HQIndex);
        }
        // If currently has an assignment command out, and it's been taken, go back to assigning
        if (assigning && Comms.getWellCommand(rc, HQIndex) == null) {
            assigning = false;
        }
        // If not currently assigning and there are wells available to be assigned, assign
        if (!assigning) {
            if (wellsAssignedCount < wellsDiscoveredCount) {
                Comms.writeWellCommand(rc, HQIndex, wellsDiscoveredNearby[wellsAssignedCount]);
                wellsAssigned[wellsAssignedCount]++;
                indicatorString = "Assigning towards: " + wellsDiscoveredNearby[wellsAssignedCount].x + ", " + wellsDiscoveredNearby[wellsAssignedCount].y
                        + ", assigned: " + wellsAssigned[wellsAssignedCount];
                assigning = true;
                // If finished assigning, increment wellsAssignedCount
                if (wellsAssigned[wellsAssignedCount] >= PERWELL[wellsDiscoveredType[wellsAssignedCount]]) {
                    wellsAssignedCount++;
                }
                if (wellsAssignedCount < wellsDiscoveredCount) {
                    carrierBuildTarget = wellsDiscoveredNearby[wellsAssignedCount];
                } else {
                    carrierBuildTarget = center;
                }
            } else {
                carrierBuildTarget = center;
            }
        }
    }

    static void build(RobotController rc) throws GameActionException{
        MapLocation centerBuildLoc = buildTowards(rc, center);
        MapLocation carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
        if (!enemiesFound) {
            if (totalAnchorCount == 0 && rc.getRobotCount() >= ANCHOR_BUILD_THRESHOLD) {
                if (rc.canBuildAnchor(Anchor.STANDARD)) {
                    rc.buildAnchor(Anchor.STANDARD);
                }
            } else if (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc) && !enemiesFound) {
                rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
            } else if (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
            }
        } else if (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
            rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
        }
    }

    static void debug(RobotController rc){
        rc.setIndicatorString(indicatorString);
    }

    private static MapLocation buildTowards(RobotController rc, MapLocation target) throws GameActionException {
        int lowestDist = 10000;
        MapLocation buildSquare = location;
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(location, 9);
        for (MapLocation loc : locs) {
            int distance = Helper.distanceTo(loc.x, loc.y, target.x, target.y);
            if (distance < lowestDist && rc.senseRobotAtLocation(loc) == null && rc.sensePassability(loc)) {
                lowestDist = distance;
                buildSquare = new MapLocation(loc.x, loc.y);
            }
        }
        return buildSquare;
    }
}