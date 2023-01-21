package CB_first;

import battlecode.common.*;

import java.util.Map;

public strictfp class HQ {

    static final int ANCHOR_BUILD_THRESHOLD = 30;

    static boolean initialized = false;
    static MapLocation location;
    static int HQIndex;
    static int id;
    static int width;
    static int height;
    static MapLocation center;
    static String indicatorString = "";


    static boolean smallMap;
    static MapLocation[] startingAD = new MapLocation[144];
    static int startingADCount = 0;
    static MapLocation[] startingMN = new MapLocation[144];
    static int startingMNCount = 0;

    static boolean buildExplore = false;
    static MapLocation carrierBuildTarget = null;

    static int totalAnchorCount = 0;

    static RobotInfo[] enemies;
    static boolean enemiesFound;

    public static void run(RobotController rc) throws GameActionException {

        checkEnemies(rc);

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        // sense part
        sense(rc);

        think(rc);

        //act part should be triggered by think part, see methods below
        build(rc);

        // prints the indicator string
        debug(rc);
    }

    static void checkEnemies(RobotController rc) throws GameActionException {
        enemies = rc.senseNearbyRobots(RobotType.HEADQUARTERS.visionRadiusSquared, rc.getTeam().opponent());
        enemiesFound = false;
        for (RobotInfo enemy : enemies) {
            if (!(enemy.getType() == RobotType.HEADQUARTERS || enemy.getType() == RobotType.CARRIER)) {
                enemiesFound = true;
                break;
            }
        }
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        location = rc.getLocation();
        id = rc.getID();
        HQIndex = Comms.setTeamHQLocation(rc, location, id);
        width = rc.getMapWidth();
        height = rc.getMapHeight();
        smallMap = width <= 30 && height <= 30;
        center = new MapLocation(width / 2, height / 2);  // Get map center

        // Find any enemy HQs
        for (RobotInfo enemy : enemies) {
            if (enemy.getType() == RobotType.HEADQUARTERS) {
                Comms.setEnemyHQLocation(rc, enemy.getLocation(), enemy.getID());
            }
        }

        // Initialize all the wells within vision range
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                MapLocation loc = well.getMapLocation();
                Comms.setADWell(rc, loc);
                startingAD[startingADCount++] = loc;
            }
            if (well.getResourceType() == ResourceType.MANA) {
                MapLocation loc = well.getMapLocation();
                Comms.setManaWell(rc, loc);
                startingMN[startingMNCount++] = loc;
            }
        }

        // Don't see any ad wells, explore
        if (startingADCount == 0 && startingMNCount == 0) {
            Comms.writeHQCommand(rc, HQIndex, new MapLocation(0, 0), 0);
            buildExplore = true;
        }
        // Only see mana
        else if (startingADCount == 0) {
            // Go to mana on small map
            if (smallMap) {
                MapLocation closestWell = getClosest(startingMN, startingMNCount, location);
                Comms.writeHQCommand(rc, HQIndex, closestWell, 1);
                carrierBuildTarget = closestWell;
                buildExplore = false;
            }
            // Explore on big maps
            else {
                Comms.writeHQCommand(rc, HQIndex, new MapLocation(0, 0), 0);
                buildExplore = true;
            }
        }
        // Only see ad
        else if (startingMNCount == 0) {
            // Explore on small map
            if (smallMap) {
                Comms.writeHQCommand(rc, HQIndex, new MapLocation(0, 0), 0);
                buildExplore = true;
            }
            // Go ad on big maps
            else {
                MapLocation closestWell = getClosest(startingAD, startingADCount, location);
                Comms.writeHQCommand(rc, HQIndex, closestWell, 1);
                carrierBuildTarget = closestWell;
                buildExplore = false;
            }
        }
        // See both wells
        else {
            MapLocation closestWell;
            // Mana first on small maps
            if (smallMap) {
                closestWell = getClosest(startingMN, startingMNCount, location);
            }
            // Ad first on big maps
            else {
                closestWell = getClosest(startingAD, startingADCount, location);
            }
            carrierBuildTarget = closestWell;
            buildExplore = false;
            Comms.writeHQCommand(rc, HQIndex, closestWell, 1);
        }
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();
    }


    static void think(RobotController rc) throws GameActionException {

    }

    static void build(RobotController rc) throws GameActionException{
        if (rc.getRoundNum() == 1) {
            // Build in 4 diagonal directions
            if (buildExplore) {
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, new MapLocation(location.x - 2, location.y - 2)));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, new MapLocation(location.x - 2, location.y + 2)));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, new MapLocation(location.x + 2, location.y - 2)));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, new MapLocation(location.x + 2, location.y + 2)));
            }
            // Build towards a well
            else {
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
                rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
            }
        } else if (rc.getRoundNum() == 2) {
            rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
            rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
            rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        } else {
            MapLocation centerBuildLoc = buildTowards(rc, center);
            MapLocation carrierBuildLoc = buildTowards(rc, center);
            if (!enemiesFound) {
                if (totalAnchorCount == 0 && rc.getRobotCount() >= ANCHOR_BUILD_THRESHOLD) {
                    if (rc.canBuildAnchor(Anchor.STANDARD)) {
                        rc.buildAnchor(Anchor.STANDARD);
                    }
                } else if (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
                    rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                } else if (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                    rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
                }
            } else if (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
            }
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

    static MapLocation getClosest(MapLocation[] options, int count, MapLocation location) {
        int lowestDist = 10000;
        MapLocation closest = null;
        for (int i = 0; i < count; i++) {
            int distance = location.distanceSquaredTo(options[i]);
            if (distance < lowestDist) {
                lowestDist = distance;
                closest = new MapLocation(options[i].x, options[i].y);
            }
        }
        return closest;
    }

    /*
    public static class BuildUnit {
        MapLocation buildTarget;
        RobotType type;
        public BuildUnit(MapLocation buildTarget, RobotType type) {
            this.buildTarget = buildTarget;
            this.type = type;
        }
    }
     */
}