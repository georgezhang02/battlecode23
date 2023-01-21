package CB_launcherheal;

import battlecode.common.*;

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

        readComms(rc);

        // sense part
        sense(rc);

        /*
        // If first HQ check map symmetries for exploration targets
        if (HQIndex == 0) {
            //findExplorationTargets(rc);
        }
        */

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

    static void readComms(RobotController rc) throws GameActionException {

        // reading island comms section
        Comms.Island[] teamIslands = Comms.getAllIslands(rc);

        Comms.Island[]reports = Comms.getAllIslandReports(rc);

        boolean removed = false;
        boolean added = false;

        for(int j = 0; j<reports.length; j++){
            Comms.Island report = reports[j];
            if(report.owner == null || !report.owner.equals(rc.getTeam())){
                // the owner is not us
                boolean toRemove = false;
                for(int i = 0; i< teamIslands.length; i++){
                    if(report.location.distanceSquaredTo(teamIslands[i].location) <= 5){
                        teamIslands[i] = null;
                        toRemove = true;
                    }
                }
                if(toRemove) reports[j] = null;
                removed = toRemove  || removed;
                // check for removal of islands based off of losing reports
            } else {
                boolean toAdd = true;
                for(int i = 0; i< teamIslands.length; i++){
                    if(report.location.distanceSquaredTo(teamIslands[i].location) <= 5){
                        toAdd = false;
                        reports[j]  = null;
                        break;
                    }
                }
                added = added || toAdd;
                // check for islands to add outside of already available team island locations
            }
        }

        if(removed){
            if(!Comms.isCommsCleaned(rc)){
                Comms.wipeComms(rc, false, false, true);
            }
            //wipe team island comms

            for(int i = 0; i< teamIslands.length; i++){
                if(teamIslands[i]!= null){
                    Comms.setIsland(rc, teamIslands[i].location, rc.getTeam());
                }
            } // original island array needs updating due to removals
            for(int i = 0; i< reports.length; i++){
                if(reports[i]!= null){
                    Comms.setIsland(rc, reports[i].location, rc.getTeam());
                }
            } // add in all reports
        } else if(added){
            if(!Comms.isCommsCleaned(rc)){
                Comms.wipeComms(rc);
            }
            //wipe comms regularly
            for(int i = 0; i< reports.length; i++){
                if(reports[i]!= null){
                    Comms.setIsland(rc, reports[i].location, rc.getTeam());
                }
            } // add in team reports
        } else{
            if(!Comms.isCommsCleaned(rc)){
                Comms.wipeComms(rc);
            }
            // wipe comms
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
            if (well.getResourceType() == ResourceType.MANA) {
                MapLocation loc = well.getMapLocation();
                Comms.setManaWell(rc, loc);
            }
            if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                MapLocation loc = well.getMapLocation();
                Comms.setADWell(rc, loc);
            }
        }
    }

    static void sense(RobotController rc) throws GameActionException {
        totalAnchorCount = rc.senseRobot(id).getTotalAnchors();

    }


    static void think(RobotController rc) throws GameActionException {
        // Take in report if available
        MapLocation[] ADWells = Comms.getAllADWells(rc);
        MapLocation[] MNWells = Comms.getAllManaWells(rc);
        // Don't see any ad wells, explore
        if (ADWells.length == 0 && MNWells.length == 0) {
            Comms.writeHQCommand(rc, HQIndex, new MapLocation(0, 0), 0);
            buildExplore = true;
        }
        // Only see mana
        else if (ADWells.length == 0) {
            // Go to mana on small map
            if (smallMap) {
                MapLocation closestWell = Helper.getClosest(MNWells, location);
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
        else if (MNWells.length == 0) {
            // Explore on small map
            if (smallMap) {
                Comms.writeHQCommand(rc, HQIndex, new MapLocation(0, 0), 0);
                buildExplore = true;
            }
            // Go ad on big maps
            else {
                MapLocation closestWell = Helper.getClosest(ADWells, location);
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
                closestWell = Helper.getClosest(MNWells, location);
                carrierBuildTarget = closestWell;
                buildExplore = false;
            }
            // Ad first on big maps
            else {
                closestWell = Helper.getClosest(ADWells, location);
                carrierBuildTarget = closestWell;
                buildExplore = false;
            }
            Comms.writeHQCommand(rc, HQIndex, closestWell, 1);
        }
    }

    static void build(RobotController rc) throws GameActionException{
        MapLocation centerBuildLoc = buildTowards(rc, center);
        MapLocation carrierBuildLoc = null;
        // Build in 4 diagonal directions
        if (buildExplore) {

        }
        // Build towards a well
        else {
            carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
        }
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
}