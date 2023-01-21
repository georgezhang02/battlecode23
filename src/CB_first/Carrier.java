package CB_first;

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
    static MapLocation[] knownADWells;
    static MapLocation[] knownMNWells;
    static WellInfo[] discoveredWells = new WellInfo[288];
    static int discoveredWellCount = 0;
    static int reportedWellCount = 0;
    static Comms.Command command;
    static int exploreCounter = 0;

    static void run(RobotController rc) throws GameActionException {
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }
        comms(rc);
        sense(rc);
        updateState(rc);
        runState(rc);
        rc.setIndicatorString(String.valueOf(state));
    }

    static void comms(RobotController rc) throws GameActionException {
        knownADWells = Comms.getAllADWells(rc);
        knownMNWells = Comms.getAllManaWells(rc);
        command = Comms.getHQCommand(rc, HQIndex);
    }
    static void sense(RobotController rc) throws GameActionException{
        location = rc.getLocation();
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
        adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        manaAmount = rc.getResourceAmount(ResourceType.MANA);
        elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);
        for (WellInfo well : rc.senseNearbyWells()) {
            boolean known = false;
            MapLocation wellLocation = well.getMapLocation();
            for (MapLocation adWell : knownADWells) {
                if (adWell.equals(wellLocation)) {
                    known = true;
                    break;
                }
            }
            if (!known) {
                for (MapLocation adWell : knownMNWells) {
                    if (adWell.equals(wellLocation)) {
                        known = true;
                        break;
                    }
                }
            }
            if (!known) {
                discoveredWells[discoveredWellCount++] = well;
            }
        }
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
        for (RobotInfo ally : allies) {
            if (ally.getType() == RobotType.HEADQUARTERS) {
                HQ_LOCATION = ally.getLocation();
                HQIndex = Comms.getHQIndexByID(rc, ally.getID());
                command = Comms.getHQCommand(rc, HQIndex);
                if (command.num == 0) {
                    state = CarrierState.Exploring;
                } else if (command.num == 1) {
                    state = CarrierState.Gathering;
                    assignedWell = command.location;
                }
            }
        }
    }

    static boolean enemiesFound(RobotController rc) throws GameActionException {
        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());
        boolean enemiesFound = false;
        for (RobotInfo enemy : enemies) {
            if (!(enemy.getType() == RobotType.HEADQUARTERS || enemy.getType() == RobotType.CARRIER)) {
                enemiesFound = true;
                break;
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
                    gatherUpdate();
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

    static void gatherUpdate() {
        //if a carrier cannot get anymore resources, return to base
        if(adAmount + manaAmount + elixirAmount == 40){
            state = CarrierState.Returning;
        }
    }

    static void gather(RobotController rc) throws GameActionException {
        if(assignedWell.distanceSquaredTo(location) <= 2){
            if (rc.canCollectResource(assignedWell, -1)) {
                rc.collectResource(assignedWell, -1);
            }
        } else {
            pathTowards(rc, assignedWell);
        }
    }

    static void exploreUpdate(RobotController rc) {
        if (discoveredWellCount > 0) {
            state = CarrierState.Returning;
        } else if (command.num == 1){

        }
    }

    static void explore(RobotController rc) throws GameActionException{
        Direction away = location.directionTo(HQ_LOCATION).opposite();
        MapLocation firstStep = location.add(away).add(away);
        Direction turn = away.rotateLeft().rotateLeft();
        MapLocation secondStep = firstStep.add(turn).add(turn).add(turn).add(turn);
        if (exploreCounter == 0) {
            pathTowards(rc, firstStep);
            exploreCounter++;
        } else if (exploreCounter <= 2) {
            exploreCounter++;
            pathTowards(rc, secondStep);
        }
        pathExplore(rc);
    }

    static void returnUpdate(RobotController rc) throws GameActionException {
        //HQ_LOCATION.distanceSquaredTo(location) <= 2 &&
        if (reportedWellCount == discoveredWellCount && adAmount == 0 && manaAmount == 0 && elixirAmount == 0) {
            // Get command or anchor if available
            if (command.num == 1) {
                assignedWell = command.location;
                state = CarrierState.Gathering;
            } else if (rc.canTakeAnchor(HQ_LOCATION, Anchor.STANDARD)) {
                rc.takeAnchor(HQ_LOCATION, Anchor.STANDARD);
                state = CarrierState.Anchoring;
            } else {
                state = CarrierState.Exploring;
            }
        }
    }

    static void returnToHQ(RobotController rc) throws GameActionException{
        // If lower than maxHealth and can throw, throw
        if(rc.getHealth() < RobotType.CARRIER.getMaxHealth()) {
            for (RobotInfo enemy : enemies) {
                if (rc.canAttack(enemy.getLocation())) {
                    rc.attack(enemy.getLocation());
                    break;
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
            while (reportedWellCount < discoveredWellCount) {
                //AD ID = 1
                if(discoveredWells[reportedWellCount].getResourceType().resourceID == 1){
                    Comms.setADWell(rc, discoveredWells[reportedWellCount++].getMapLocation());
                }
                //MN ID = 2
                else if(discoveredWells[reportedWellCount].getResourceType().resourceID == 2){
                    Comms.setManaWell(rc, discoveredWells[reportedWellCount++].getMapLocation());
                }
            }
            discoveredWellCount = 0;
            reportedWellCount = 0;
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
                MapLocation islandLocation = islandLocs.iterator().next();
                pathTowards(rc, islandLocation);

                if (rc.canPlaceAnchor()) {
                    rc.placeAnchor();
                    if (discoveredWellCount > 0) {
                        state = CarrierState.Returning;
                    } else {
                        state = CarrierState.Exploring;
                    }
                }
            }
        }

        //Otherwise, Explore until you find an island
        else{
            pathExplore(rc);
        }
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
            Direction moveDir = Pathfinder.pathBF(rc, target);
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
        if(enemiesFound(rc)){
            state = CarrierState.Returning;
            target = HQ_LOCATION;
        }
        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBug(rc, target);
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }
}
