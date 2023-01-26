package EB_first;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public strictfp class Carrier {

    static boolean initialized  = false;

    private enum CarrierState {
        None, Exploring, Returning, Anchoring, Gathering, Runaway
    }
    static CarrierState state = CarrierState.None;

    static MapLocation location;
    static RobotInfo[] allies;
    static RobotInfo[] enemies;
    static int adAmount;
    static int manaAmount;
    static int elixirAmount;

    static MapLocation HQ_LOCATION = null;
    static int HQIndex;

    static MapLocation assignedWell = null;
    static int assignedType = -1;
    static MapLocation[] knownADWells;
    static MapLocation[] knownMNWells;
    static int discoveredWellCount = 0;
    static int exploreCounter = 0;
    static boolean smallMap;
    static MapLocation closestAD;
    static boolean ADInRange;
    static MapLocation closestMN;
    static boolean MNInRange;
    static Set<MapLocation> visitedWells = new HashSet<MapLocation>();

    static Direction away;
    static MapLocation firstStep;
    static Direction turn;
    static MapLocation secondStep;

    static MapLocation anchorCommand;

    static void run(RobotController rc) throws GameActionException {
        readComms(rc);
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        sense(rc);
        updateState(rc);
        runState(rc);
        rc.setIndicatorString(state + " " + discoveredWellCount + " " + assignedWell);

        writeComms(rc);
        Database.checkSymmetries(rc);
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
        smallMap = rc.getMapWidth() <= 40 || rc.getMapHeight() <= 40;
        for (RobotInfo ally : allies) {
            if (ally.getType() == RobotType.HEADQUARTERS) {
                HQ_LOCATION = ally.getLocation();
                HQIndex = Comms.getHQIndexByID(rc, ally.getID());
            }
        }
        knownADWells = Database.getKnownADLocations();
        closestAD = Helper.getClosest(knownADWells, HQ_LOCATION);
        knownMNWells = Database.getKnownManaLocations();
        closestMN = Helper.getClosest(knownMNWells, HQ_LOCATION);

        // Don't see any ad wells, explore
        boolean ADInRange = closestAD != null && closestAD.isWithinDistanceSquared(HQ_LOCATION, 34);
        boolean MNInRange = closestMN != null && closestMN.isWithinDistanceSquared(HQ_LOCATION, 34);

        if (!ADInRange && !MNInRange) {
            state = CarrierState.Exploring;
        }
        // Only see mana
        else if (!ADInRange) {
            // Go to mana
            state = CarrierState.Gathering;
            assignedWell = closestMN;
            assignedType = 1;
        }
        // Only see ad
        else if (!MNInRange) {
            // Explore on small map
            if (smallMap) {
                state = CarrierState.Exploring;
            }
            // Go ad on big maps
            else {
                state = CarrierState.Gathering;
                assignedWell = closestAD;
                assignedType = 0;
            }
        }
        // See both wells
        else {
            state = CarrierState.Gathering;
            // Go mn on small maps
            if (smallMap) {
                assignedWell = closestMN;
                assignedType = 1;
            }
            // Go ad on big maps
            else {
                assignedWell = closestAD;
                assignedType = 0;
            }
        }
    }

    static void readComms(RobotController rc) throws GameActionException {
        Database.init(rc);
        Database.downloadSymmetry(rc);
        Database.downloadLocations(rc);

    }

    static void writeComms(RobotController rc) throws GameActionException {

        if(rc.canWriteSharedArray(0,0)){
            Database.uploadSymmetry(rc);
            Database.uploadLocations(rc);
        }
    }



    static void sense(RobotController rc) throws GameActionException{
        location = rc.getLocation();
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
        adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        manaAmount = rc.getResourceAmount(ResourceType.MANA);
        elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);

        HQ_LOCATION = Comms.getClosestTeamHQLocation(rc, location);

        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            //rc.setIndicatorDot(well.getMapLocation(), 255, 255, 255);
            boolean known = Database.globalKnownLocations.contains(well.getMapLocation())
                    || Database.localKnownLocations.contains(well.getMapLocation());

            //otherwise, mark this as known and return to base
            if (!known) {
                Database.addWell(rc, well);
                discoveredWellCount++;
            }
        }

        if (discoveredWellCount > 0) {
            state = CarrierState.Returning;
        }

        knownADWells = Database.getKnownADLocations();
        knownMNWells = Database.getKnownManaLocations();

        int[] islands  = rc.senseNearbyIslands();
        boolean commandSent = false;
        for(int i = 0; i < islands.length; i++){
            if(!enemiesFound(rc) && !commandSent && rc.senseTeamOccupyingIsland(islands[i]) != rc.getTeam()
                    && rc.canWriteSharedArray(0,0)){
                Comms.setAnchorCommand(rc, rc.senseNearbyIslandLocations(islands[i])[0]);
                commandSent = true;
            }
        }
    }


    static boolean enemiesFound(RobotController rc) throws GameActionException {
        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());
        boolean enemiesFound = false;
        boolean attackSent = false;
        for (RobotInfo enemy : enemies) {
            if(enemy.getType() == RobotType.CARRIER){
                if(rc.canWriteSharedArray(0,0) && !attackSent){
                    Comms.setAttackCommand(rc, enemy.getLocation(), enemy.getType());
                    attackSent = true;
                }
            }
            if (!(enemy.getType() == RobotType.HEADQUARTERS || enemy.getType() == RobotType.CARRIER || enemy.getType() == RobotType.AMPLIFIER)) {
                enemiesFound = true;
                if(rc.canWriteSharedArray(0,0) && !attackSent){
                    Comms.setAttackCommand(rc, enemy.getLocation(), enemy.getType());
                    attackSent = true;
                }
            }
            if(enemy.getType()== RobotType.HEADQUARTERS){
                Database.addEnemyHQ(rc, enemy);
            }
        }
        return enemiesFound;
    }

    static void updateState(RobotController rc) throws GameActionException {
        // Check enemies
        if(enemiesFound(rc)){
            state = CarrierState.Returning;
        } else {
            // Update states
            switch (state) {
                case Gathering:
                    gatherUpdate(rc);
                    break;
                case Exploring:
                    exploreUpdate(rc);
                    break;
                case Returning:
                    returnUpdate(rc);
                    break;
                case Anchoring:
                    anchorUpdate(rc);
                    break;
            /* case Runaway:
                runawayUpdate(rc);
                break;
             */
            }
        }
    }

    static void runState(RobotController rc) throws GameActionException {
        switch (state) {
            case Gathering:
                gather(rc);
                break;
            case Exploring:
                explore(rc);
                break;
            case Returning:
                returnToHQ(rc);
                break;
            case Anchoring:
                anchor(rc);
                break;
            /* case Runaway:
                runaway(rc);
                break;
             */
        }
    }

    static void gatherUpdate(RobotController rc) throws GameActionException {
        //if a carrier cannot get anymore resources, return to base
        if(adAmount + manaAmount + elixirAmount == 40){
            state = CarrierState.Returning;
        } else if (location.distanceSquaredTo(assignedWell) <= 10){
            //THIS SECTION IS INTENDED TO MAKE IT SO THAT THE CARRIERS SWITCH THE WELL THEY'RE ASSIGNED TO IF IT'S FULL
            MapLocation[] aroundWell = rc.getAllLocationsWithinRadiusSquared(assignedWell, 2);
            int ADlimit = 3;
            int MNlimit = 4;
            if (smallMap) {
                MNlimit = 8;
            }
            if (rc.getRoundNum() > 200) {
                ADlimit = 8;
                MNlimit = 8;
            }
            int robotCount = 0;
            int availableSquares = 0;
            for (MapLocation loc : aroundWell) {
                if (rc.canSenseRobotAtLocation(loc)) {
                    RobotInfo bot = rc.senseRobotAtLocation(loc);
                    if (bot.team == rc.getTeam() && bot.type == RobotType.CARRIER) {
                        robotCount++;
                    }
                } else if (rc.canSenseLocation(loc) && rc.sensePassability(loc)) {
                    availableSquares++;
                }
            }
            int limit = 8;
            switch(assignedType) {
                case 0:
                    limit = ADlimit;
                    break;
                case 1:
                    limit = MNlimit;
                    break;
            }
            if ((robotCount >= limit || availableSquares <= 0) && location.distanceSquaredTo(assignedWell) > 2) {
                visitedWells.add(assignedWell);
                if (assignedType == 0) {
                    MapLocation nextWell = Helper.getClosest(knownMNWells, HQ_LOCATION, visitedWells);
                    if (nextWell != null) {
                        assignedWell = nextWell;
                        assignedType = 1;
                    } else {
                        nextWell = Helper.getClosest(knownADWells, HQ_LOCATION, visitedWells);
                        if (nextWell != null) {
                            assignedWell = nextWell;
                            assignedType = 0;
                        } else {
                            state = CarrierState.Exploring;
                        }
                    }
                } else {
                    MapLocation nextWell = Helper.getClosest(knownADWells, HQ_LOCATION, visitedWells);
                    if (nextWell != null) {
                        assignedWell = nextWell;
                        assignedType = 0;
                    } else {
                        nextWell = Helper.getClosest(knownMNWells, HQ_LOCATION, visitedWells);
                        if (nextWell != null) {
                            assignedWell = nextWell;
                            assignedType = 1;
                        } else {
                            state = CarrierState.Exploring;
                        }
                    }
                }
            }
        }
    }

    static void gather(RobotController rc) throws GameActionException {
        if(assignedWell.distanceSquaredTo(location) <= 2){
            if (rc.canCollectResource(assignedWell, -1)) {
                rc.collectResource(assignedWell, -1);
            }
            pathWell(rc);
        } else {
            pathTowards(rc, assignedWell);
        }
    }

    static void exploreUpdate(RobotController rc) {
        if (assignedWell == null) {
            assignClosest(rc);
        }
    }

    static void explore(RobotController rc) throws GameActionException{
        if (exploreCounter == 0) {
            away = location.directionTo(HQ_LOCATION).opposite();
            firstStep = location.add(away).add(away);
            turn = away.rotateLeft().rotateLeft();
            secondStep = firstStep.add(turn).add(turn).add(turn).add(turn);
            exploreCounter++;
        }
        if (exploreCounter <= 1) {
            pathTowards(rc, firstStep);
            exploreCounter++;
        } else if (exploreCounter <= 5) {
            exploreCounter++;
            pathTowards(rc, secondStep);
        }
        pathExplore(rc);
    }

    static void returnUpdate(RobotController rc) throws GameActionException {
        //HQ_LOCATION.distanceSquaredTo(location) <= 2 &&
        if (adAmount == 0 && manaAmount == 0 && elixirAmount == 0 && discoveredWellCount == 0) {
            // Get anchor if available
            if (rc.canTakeAnchor(HQ_LOCATION, Anchor.STANDARD)) {
                rc.takeAnchor(HQ_LOCATION, Anchor.STANDARD);
                state = CarrierState.Anchoring;
            } else if (rc.getNumAnchors(Anchor.STANDARD) > 0) {
                state = CarrierState.Anchoring;
            }
            else if (assignedWell == null) {
                assignClosest(rc);
            } else {
                state = CarrierState.Gathering;
            }
        }
    }

    static void returnToHQ(RobotController rc) throws GameActionException{
        // If lower than maxHealth and can throw, throw
        if(rc.getHealth() < RobotType.CARRIER.getMaxHealth()) {
            for (RobotInfo enemy : enemies) {
                if(enemy.getType()!= RobotType.HEADQUARTERS && enemy.getType() != RobotType.AMPLIFIER && enemy.getType() != RobotType.CARRIER){
                    if (rc.canAttack(enemy.getLocation())) {
                        rc.attack(enemy.getLocation());
                        break;
                    }
                }

            }
        }

        //move back to HQ
        if(HQ_LOCATION.distanceSquaredTo(location) > 2){
            pathTowards(rc, HQ_LOCATION);
        }

        //If the hq location is in action range, deposit resources to HQ
        if (HQ_LOCATION.distanceSquaredTo(location) <= 2){
            if(rc.canTransferResource(HQ_LOCATION, ResourceType.ELIXIR, elixirAmount) && elixirAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ELIXIR, elixirAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.MANA, manaAmount) && manaAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.MANA, manaAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.ADAMANTIUM, adAmount) && adAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ADAMANTIUM, adAmount);
            }
        }

        // If within comms range and has discovered well, report well.
        if (rc.canWriteSharedArray(0, 0)) {
            discoveredWellCount = 0;
        }
    }



    static void anchorUpdate(RobotController rc) throws GameActionException {
        //If the robot no longer has an anchor (thrown), explore
        if (rc.getAnchor() == null) {
            state = CarrierState.Exploring;
        }
    }

    static void anchor(RobotController rc) throws GameActionException{
        // If I have an anchor singularly focus on getting it to the first island I see
        int[] islands = rc.senseNearbyIslands();
        int inc = 0;
        for (int id : islands) {
            if(rc.senseTeamOccupyingIsland(id) == rc.getTeam()){
                inc += 1;
            }
        }
        Set<MapLocation> islandLocs = new HashSet<>();
        if (islands.length - inc > 0) {
            for (int id : islands) {
                if(rc.senseTeamOccupyingIsland(id) != rc.getTeam()){
                    MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                    islandLocs.addAll(Arrays.asList(thisIslandLocs));
                }
            }
            if (islandLocs.size() > 0) {
                anchorCommand = null;
                MapLocation islandLocation = islandLocs.iterator().next();
                pathTowards(rc, islandLocation);

                if (rc.canPlaceAnchor()) {
                    rc.placeAnchor();
                    anchorCommand = null;

                    if(rc.canWriteSharedArray(0, 0)){
                        Comms.reportIslandLocation(rc, rc.getLocation(), rc.getTeam());
                    }
                    if (discoveredWellCount > 0) {
                        state = CarrierState.Returning;
                    } else {
                        state = CarrierState.Exploring;
                    }
                }
            }
        } else if(anchorCommand != null || searchAnchorCommands(rc) != null &&
            rc.getLocation().distanceSquaredTo(anchorCommand) <= rc.getType().visionRadiusSquared){
            if(rc.isMovementReady()){
                Direction moveDir = Pathfinder.pathBug(rc, anchorCommand);
                if(moveDir != null && rc.canMove(moveDir)){
                    rc.move(moveDir);
                }
            }
        }
        //Otherwise, Explore until you find an island
        else{
            anchorCommand = null;
            pathExplore(rc);
        }
    }

    static MapLocation searchAnchorCommands(RobotController rc) throws GameActionException {
        MapLocation[] commands = Comms.getAllAnchorCommands(rc);
        int minDist = 10000;

        for(MapLocation command: commands){
            if(rc.getLocation().distanceSquaredTo(command) < minDist){
                anchorCommand = command;
                minDist = rc.getLocation().distanceSquaredTo(anchorCommand);
            }
        }

        return anchorCommand;
    }

    /*
    static void runaway(RobotController rc) throws GameActionException{
        if(rc.getHealth() < RobotType.CARRIER.getMaxHealth()/2 && rc.canAttack(enemies[0].getLocation())){
            rc.attack(enemies[0].getLocation());
        }
        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathAwayFrom(rc, enemies[0].getLocation());
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }
    */

    static void pathExplore(RobotController rc) throws GameActionException {
        MapLocation target = Pathfinder.locationToExplore(rc);
        pathTowards(rc, target);
    }

    static void pathTowards(RobotController rc, MapLocation target) throws GameActionException {
        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBug(rc, target);
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
                run(rc);
            }
        }
    }

    private static void assignClosest(RobotController rc) {
        closestAD = Helper.getClosest(knownADWells, HQ_LOCATION);
        closestMN = Helper.getClosest(knownMNWells, HQ_LOCATION);

        int range;
        int round = rc.getRoundNum();
        if (round <= 4) {
            range = 50;
        } else if (round <= 15){
            range = 100;
        } else {
            range = 10000;
        }
        ADInRange = closestAD != null && closestAD.isWithinDistanceSquared(HQ_LOCATION, range);
        MNInRange = closestMN != null && closestMN.isWithinDistanceSquared(HQ_LOCATION, range);
        if (smallMap && MNInRange){
            state = CarrierState.Gathering;
            assignedWell = closestMN;
            assignedType = 1;
        } else if (!smallMap & ADInRange) {
            state = CarrierState.Gathering;
            assignedWell = closestAD;
            assignedType = 0;
        } else if (MNInRange) {
            state = CarrierState.Gathering;
            assignedWell = closestMN;
            assignedType = 1;
        } else if (ADInRange) {
            state = CarrierState.Gathering;
            assignedWell = closestAD;
            assignedType = 0;
        }
    }

    private static void pathWell(RobotController rc) throws GameActionException {
        if (!location.equals(assignedWell)) {
            Direction toWell = location.directionTo(assignedWell);
            if (rc.canMove(toWell)) {
                rc.move(toWell);
            } else {
                Direction toMove = null;
                switch (toWell) {
                    case NORTH:
                        toMove = Direction.WEST;
                        break;
                    case NORTHEAST:
                        toMove = Direction.NORTH;
                        break;
                    case EAST:
                        toMove = Direction.NORTH;
                        break;
                    case SOUTHEAST:
                        toMove = Direction.EAST;
                        break;
                    case SOUTH:
                        toMove = Direction.EAST;
                        break;
                    case SOUTHWEST:
                        toMove = Direction.SOUTH;
                        break;
                    case WEST:
                        toMove = Direction.SOUTH;
                        break;
                    case NORTHWEST:
                        toMove = Direction.WEST;
                        break;
                }
                if (toMove != null && rc.canMove(toMove) &&
                        rc.canSenseLocation(location.add(toMove)) && rc.senseMapInfo(location.add(toMove)).getCurrentDirection() == Direction.CENTER) {
                    rc.move(toMove);
                }
            }
        }
    }

}
