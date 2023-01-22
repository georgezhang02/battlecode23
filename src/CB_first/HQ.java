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
    static boolean smallMap;
    static String indicatorString = "";

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
                smallMap = true;
            }
        }

        // Initialize all the wells within vision range
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                MapLocation loc = well.getMapLocation();
                Comms.setADWell(rc, loc);
            }
            if (well.getResourceType() == ResourceType.MANA) {
                MapLocation loc = well.getMapLocation();
                Comms.setManaWell(rc, loc);
            }
        }

        MapLocation[] ADWells = Comms.getAllADWells(rc);
        MapLocation closestAD = Helper.getClosest(ADWells, location);
        MapLocation[] MNWells = Comms.getAllManaWells(rc);
        MapLocation closestMN = Helper.getClosest(MNWells, location);

        // Don't see any ad wells, explore
        boolean ADInRange = closestAD != null && closestAD.isWithinDistanceSquared(location, 34);
        boolean MNInRange = closestMN != null && closestMN.isWithinDistanceSquared(location, 34);

        if (!ADInRange && !MNInRange) {
            buildExplore = true;
        }
        // Only see mana
        else if (!ADInRange) {
            // Go to mana
            carrierBuildTarget = closestMN;
        }
        // Only see ad
        else if (!MNInRange) {
            // Explore on small map
            if (smallMap) {
                buildExplore = true;
            }
            // Go ad on big maps
            else {
                carrierBuildTarget = closestAD;
            }
        }
        // See both wells
        else {
            MapLocation closestWell;
            // Mana first on small maps
            if (smallMap) {
                closestWell = closestMN;
            }
            // Ad first on big maps
            else {
                closestWell = closestAD;
            }
            carrierBuildTarget = closestWell;
        }
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();
    }


    static void think(RobotController rc) throws GameActionException {

    }

    static void build(RobotController rc) throws GameActionException{
        MapLocation[] enemyHQs = Comms.getAllEnemyHQs(rc);
        if (rc.getRoundNum() == 1) {
            if (enemyHQs.length > 0) {
                //initialLauncherBuild(rc);
            }
            else {
                initialCarrierBuild(rc);
            }
        } else if (rc.getRoundNum() == 2) {
            if (enemyHQs.length > 0) {
                initialCarrierBuild(rc);
            }
            else {
                //initialLauncherBuild(rc);
            }
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
                    //rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
                }
            } else if (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                //rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
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

    private static void initialCarrierBuild(RobotController rc) throws GameActionException {
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
    }

    private static void initialLauncherBuild(RobotController rc) throws GameActionException {
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
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