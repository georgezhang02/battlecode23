package CB_merged;

import battlecode.common.*;

public strictfp class HQ {

    static int ANCHOR_BUILD_THRESHOLD = 30;
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
    static boolean launchersFirst = false;

    static int launchersBuilt = 0;
    static int carriersBuilt = 0;
    static int anchorsBuilt = 0;
    static int amplifiersBuilt = 0;

    static MapLocation[] ADWells;
    static MapLocation closestAD;
    static MapLocation[] MNWells;
    static MapLocation closestMN;

    static int carrierCounter = 0;

    static boolean buildAnchor = false;

    public static void run(RobotController rc) throws GameActionException {

        checkEnemies(rc);

        readComms(rc);

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        // sense part
        sense(rc);

        //act part should be triggered by think part, see methods below
        build(rc);

        rc.setIndicatorString(Database.rotational+" "+Database.horizontal+" "+Database.vertical);

        writeComms(rc);
        Database.checkSymmetries(rc);

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
        smallMap = width <= 30 || height <= 30;
        center = new MapLocation(width / 2, height / 2);  // Get map center

        // Find any enemy HQs
        for (RobotInfo enemy : enemies) {
            if (enemy.getType() == RobotType.HEADQUARTERS) {
                Database.addEnemyHQ(rc, enemy);
                smallMap = true;
                launchersFirst = true;
            }
        }

        // Initialize all the wells within vision range
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            Database.addWell(rc, well);
        }

        ADWells = Comms.getAllADWells(rc);
        closestAD = Helper.getClosest(ADWells, location);
        MNWells = Comms.getAllManaWells(rc);
        closestMN = Helper.getClosest(MNWells, location);

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
        ADWells = Database.getKnownADLocations();
        closestAD = Helper.getClosest(ADWells, location);
        MNWells = Database.getKnownManaLocations();
        closestMN = Helper.getClosest(MNWells, location);
    }


    static void readComms(RobotController rc) throws GameActionException {
        Database.init(rc);
        Database.downloadLocations(rc);
        Database.downloadSymmetry(rc);

        wipeComms(rc);

    }

    static void writeComms(RobotController rc) throws GameActionException {
        Database.uploadSymmetry(rc);
        Database.uploadLocations(rc);
    }

    static void wipeComms(RobotController rc) throws GameActionException{
        Comms.Island[] teamIslands = Comms.getAllIslands(rc);

        Comms.Island[]reports = Comms.getAllIslandReports(rc);

        boolean removed = false;
        boolean added = false;

        //reading island report comms
        for(int j = 0; j<reports.length; j++){
            Comms.Island report = reports[j];
            if(report.owner == null || !report.owner.equals(rc.getTeam())){
                // the owner is not us
                boolean toRemove = false;
                for(int i = 0; i< teamIslands.length; i++){
                    if(report.location.distanceSquaredTo(teamIslands[i].location) <= 5){
                        teamIslands[i] = null;
                        removed = true;
                        toRemove = true;
                    }
                }
                if(toRemove) reports[j] = null;
                // check for removal of islands based off of losing reports
            } else {
                for(int i = 0; i< teamIslands.length; i++){
                    if(report.location.distanceSquaredTo(teamIslands[i].location) <= 5){
                        reports[j]  = null;
                    } else{
                        added = true;
                    }
                }
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

    static void build(RobotController rc) throws GameActionException{
        if (rc.getRoundNum() == 1) {
            if (launchersFirst || smallMap) {
                initialLauncherBuild(rc);
            }
            else {
                initialCarrierBuild(rc);
            }
        } else if (rc.getRoundNum() == 2) {
            if (launchersFirst || smallMap) {
                initialCarrierBuild(rc);
            }
            else {
                initialLauncherBuild(rc);
            }
        } else {
            MapLocation centerBuildLoc = buildTowards(rc, center);
            MapLocation carrierBuildTarget;
            if (smallMap) {
                carrierBuildTarget = closestMN;
            } else {
                carrierBuildTarget = closestAD;
            }

            if(totalAnchorCount == 0 &&
                    !buildAnchor  && rc.getRobotCount() > 5 * Comms.getNumHQs(rc) && anchorsBuilt < 20 * carriersBuilt
                        && rc.getRobotCount() >= ANCHOR_BUILD_THRESHOLD && carrierCounter > 20){
                buildAnchor = true;
                carrierCounter = 0;
            }

            if (!enemiesFound) {
                if(buildAnchor){
                    if(rc.canBuildAnchor(Anchor.STANDARD)){
                        rc.buildAnchor(Anchor.STANDARD);
                        buildAnchor = false;
                    }
                    MapLocation carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
                    while (rc.getResourceAmount(ResourceType.ADAMANTIUM) > 150 &&
                            rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
                        rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                        carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
                        carriersBuilt++;
                    }

                    MapLocation launcherBuildLoc = buildTowards(rc, carrierBuildTarget);
                    while (rc.getResourceAmount(ResourceType.MANA) > 160 && rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                        rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
                        centerBuildLoc = buildTowards(rc, center);
                        launchersBuilt++;
                    }

                } else{
                     /*if (rc.getRobotCount() > 5 * Comms.getNumHQs(rc) && amplifiersBuilt < 30 * launchersBuilt) {
                        if (rc.canBuildRobot(RobotType.AMPLIFIER, buildTowards(rc, center))) {
                            rc.buildRobot(RobotType.AMPLIFIER, centerBuildLoc);
                            amplifiersBuilt++;
                        }
                    } else {*/
                        MapLocation carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
                        while (rc.canBuildRobot(RobotType.CARRIER, carrierBuildLoc)) {
                            rc.buildRobot(RobotType.CARRIER, carrierBuildLoc);
                            carrierBuildLoc = buildTowards(rc, carrierBuildTarget);
                            carriersBuilt++;
                            if(rc.getRoundNum() > 100){
                                carrierCounter++;
                            }
                        }
                        while (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                            rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
                            centerBuildLoc = buildTowards(rc, center);
                            launchersBuilt++;
                   //     }
                    }
                }
            } else {
                while (rc.canBuildRobot(RobotType.LAUNCHER, centerBuildLoc)) {
                    rc.buildRobot(RobotType.LAUNCHER, centerBuildLoc);
                    centerBuildLoc = buildTowards(rc, center);
                    launchersBuilt++;
                }
            }
        }
    }


    private static MapLocation buildTowards(RobotController rc, MapLocation target) throws GameActionException {
        int lowestDist = 10000;
        MapLocation buildSquare = location;
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(location, 9);
        for (MapLocation loc : locs) {
            int distance = loc.distanceSquaredTo(target);
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
            carriersBuilt += 4;
        }
        // Build towards a well
        else {
            rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
            rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
            rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
            rc.buildRobot(RobotType.CARRIER, buildTowards(rc, carrierBuildTarget));
            carriersBuilt += 4;
        }
    }

    private static void initialLauncherBuild(RobotController rc) throws GameActionException {
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        rc.buildRobot(RobotType.LAUNCHER, buildTowards(rc, center));
        launchersBuilt += 3;
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