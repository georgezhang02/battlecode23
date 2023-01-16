package BB_followfirstandcarrier;

import battlecode.common.*;

import java.util.Arrays;

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

    static int[] PERWELL = {3, 3, 3}; // {AD, MN, EX}
    static MapLocation[] wellsDiscoveredNearby = new MapLocation[288];
    static int wellsDiscoveredCount = 0;
    static int[] wellsDiscoveredType = new int[288]; // 0 = AD, 1 = MN, 2 = EX
    static int[] wellsAssigned = new int[288];
    static boolean assigning = false;
    static MapLocation carrierBuildTarget;

    static int totalAnchorCount = 0;

    static RobotInfo[] enemies;
    static boolean enemiesFound;

    static MapLocation[] explorationTargets = new MapLocation[12];
    static int targetCount = 0;

    public static void run(RobotController rc) throws GameActionException {

        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        // sense part
        sense(rc);

        // If first HQ and 2nd round, check map symmetries for exploration targets
        if (HQIndex == 0 && rc.getRoundNum() == 2) {
            findExplorationTargets(rc);
            System.out.println(Arrays.toString(explorationTargets));
        }


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
        width = rc.getMapWidth();
        height = rc.getMapHeight();
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

    static void findExplorationTargets(RobotController rc) throws GameActionException {
        int HQCount = Comms.getNumHQs(rc);
        int EnemyCount = Comms.getNumEnemyHQs(rc);
        // If only 1 HQ
        if (HQCount == 1) {
            // If we see an enemy, set that as the exploration target
            if (EnemyCount > 0) {
                explorationTargets[targetCount++] = Comms.getEnemyHQLocation(rc, 0);
            }
            // Otherwise, assume rotational then reflectional symmetry
            else {
                explorationTargets[targetCount++] = rotate(location);
                explorationTargets[targetCount++] = reflectAcrossVertical(location);
                explorationTargets[targetCount++] = reflectAcrossHorizontal(location);
            }
        }
        // If multiple HQs
        else {
            MapLocation[] allHQs = Comms.getAllHQs(rc);
            MapLocation[] enemyHQs = Comms.getAllEnemyHQs(rc);
            // If we see an enemy, find the symmetry
            if (EnemyCount > 0) {
                for (MapLocation HQ : allHQs) {
                    if (HQ.equals(rotate(enemyHQs[0]))) {
                        for (MapLocation loc: allHQs) {
                            explorationTargets[targetCount++] = rotate(loc);
                        }
                        break;
                    } else if (HQ.equals(reflectAcrossVertical(enemyHQs[0]))) {
                        for (MapLocation loc: allHQs) {
                            explorationTargets[targetCount++] = reflectAcrossVertical(loc);
                        }
                        break;
                    } else if (HQ.equals(reflectAcrossHorizontal(enemyHQs[0]))) {
                        for (MapLocation loc: allHQs) {
                            explorationTargets[targetCount++] = reflectAcrossHorizontal(loc);
                        }
                        break;
                    }
                }
            }
            // Otherwise, if we're on the same side, assume rotational then reflectional symmetry
            else {
                boolean xLeft = false;
                boolean xRight = false;
                boolean yTop = false;
                boolean yBottom = false;
                for (MapLocation HQ : allHQs) {
                    if (HQ.x < width / 2) {
                        xLeft = true;
                    } else {
                        xRight = true;
                    }
                    if (HQ.y < height / 2) {
                        yBottom = true;
                    } else {
                        yTop = true;
                    }
                }
                boolean entireWidth = xLeft && xRight;
                boolean entireHeight = yBottom && yTop;
                if (entireWidth && !entireHeight) {
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = rotate(loc);
                    }
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossHorizontal(loc);
                    }
                    /*
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossVertical(loc);
                    }
                    */
                } else if (entireHeight && !entireWidth) {
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = rotate(loc);
                    }
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossVertical(loc);
                    }
                    /*
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossHorizontal(loc);
                    }*/
                } else if (entireWidth && entireHeight) {
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossVertical(loc);
                    }
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossHorizontal(loc);
                    }
                    /*
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = rotate(loc);
                    }
                    */
                } else {
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = rotate(loc);
                    }
                    /*
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossVertical(loc);
                    }
                    for (MapLocation loc: allHQs) {
                        explorationTargets[targetCount++] = reflectAcrossHorizontal(loc);
                    }
                     */
                }
            }
        }
        for (int i = 0; i < targetCount; i++) {
            Comms.writeExplorationTarget(rc, explorationTargets[i]);
        }
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();
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
            int unassignedIndex = getUnassignedWell();
            if (unassignedIndex == -1 && PERWELL[1] == 3) {
                PERWELL[1] = 6;
                unassignedIndex = getUnassignedWell();
            }
            if (unassignedIndex != -1){
                Comms.writeWellCommand(rc, HQIndex, wellsDiscoveredNearby[unassignedIndex]);
                wellsAssigned[unassignedIndex]++;
                indicatorString = "Assigning towards: " + wellsDiscoveredNearby[unassignedIndex].x + ", "
                        + wellsDiscoveredNearby[unassignedIndex].y
                        + ", assigned: " + wellsAssigned[unassignedIndex];
                assigning = true;
                int newIndex = getUnassignedWell();
                if (newIndex == -1) {
                    carrierBuildTarget = center;
                } else {
                    carrierBuildTarget = wellsDiscoveredNearby[newIndex];
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
            } else if (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
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

    private static int getUnassignedWell() {
        for (int i = 0; i < wellsDiscoveredCount; i++) {
            if (wellsAssigned[i] < PERWELL[wellsDiscoveredType[i]]) {
                return i;
            }
        }
        return -1;
    }

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
}