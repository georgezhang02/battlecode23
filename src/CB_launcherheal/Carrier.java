package CB_launcherheal;

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

    //assignedWell used to be a location, now it's a Well object
    //going to comment this out for now and replace it with a Comms.Well version
    //static MapLocation assignedWell = null;
    static Comms.Well assignedWell = null;
    //make this a list of well objects instead of a list of ints, if it's a list of ints then it's just the encoded information
    static Comms.Well[] knownWells;
    static WellInfo discoveredWell = null;

    //going to do the same thing with these two
    //static MapLocation previousCommand = null;
    //static MapLocation newCommand = null;
    static Comms.Well previousCommand = null;
    static Comms.Well newCommand = null;

    static void run(RobotController rc) throws GameActionException {
        sense(rc);
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }
        comms(rc);
        updateState(rc);
        runState(rc);
        previousCommand = newCommand;
        rc.setIndicatorString(String.valueOf(state));
    }

    static void sense(RobotController rc) throws GameActionException{
        location = rc.getLocation();
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
        adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        manaAmount = rc.getResourceAmount(ResourceType.MANA);
        elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        for (RobotInfo ally : allies) {
            if (ally.getType() == RobotType.HEADQUARTERS) {
                HQ_LOCATION = ally.getLocation();
                HQIndex = Comms.getHQIndexByID(rc, ally.getID());
                if (location.distanceSquaredTo(HQ_LOCATION) <= 9) {
                    assignedWell = Comms.getWellCommand(rc, HQIndex);
                    //well commands are now HQ commands
                    Comms.clearHQCommand(rc, HQIndex);
                    if (assignedWell != null) {
                        state = CarrierState.Gathering;
                    } else {
                        state = CarrierState.Exploring;
                    }
                } else {
                    state = CarrierState.Returning;
                }
            }
        }
    }

    static void comms(RobotController rc) throws GameActionException {
        //knownWells = Comms.getAllWellValues(rc);
        knownWells = Comms.getAllWellCommands(rc);
        newCommand = Comms.getWellCommand(rc, HQIndex);
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
        if(assignedWell.location.distanceSquaredTo(location) <= 2){
            if (rc.canCollectResource(assignedWell.location, -1)) {
                rc.collectResource(assignedWell.location, -1);
            }
        } else {
            pathTowards(rc, assignedWell.location);
        }
    }

    static void exploreUpdate(RobotController rc) {
        //If there is a new well nearby, return to HQ to report
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            MapLocation loc = well.getMapLocation();
            //int newValue = Comms.encode(loc.x, loc.y);
            boolean alreadyKnew = false;
            for (Comms.Well knownWell: knownWells) {
                if (knownWell.location.equals(loc)) {
                    alreadyKnew = true;
                    break;
                }
            }
            if (!alreadyKnew) {
                discoveredWell = well;
                state = CarrierState.Returning;
                break;
            }
        }

        // If the HQ started assigning again, also return
        if (previousCommand == null && newCommand != null) {
            state = CarrierState.Returning;
        }
    }

    static void explore(RobotController rc) throws GameActionException{
        pathExplore(rc);
    }

    static void returnUpdate(RobotController rc) throws GameActionException {
        if (HQ_LOCATION.distanceSquaredTo(location) <= 2 && discoveredWell == null
                && adAmount == 0 && manaAmount == 0 && elixirAmount == 0) {
            // Get command or anchor if available
            if (assignedWell == null) {
                Comms.Well command = Comms.getWellCommand(rc, HQIndex);
                if (command != null) {
                    assignedWell = command;
                    Comms.clearHQCommand(rc, HQIndex);
                    state = CarrierState.Gathering;
                } else if (rc.canTakeAnchor(HQ_LOCATION, Anchor.STANDARD)) {
                    rc.takeAnchor(HQ_LOCATION, Anchor.STANDARD);
                    state = CarrierState.Anchoring;
                } else {
                    state = CarrierState.Exploring;
                }
            } else {
                state = CarrierState.Gathering;
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
        if (discoveredWell != null && rc.canWriteSharedArray(0, 0)) {
            //AD ID = 1
            if(discoveredWell.getResourceType().resourceID == 1){
                Comms.setADWell(rc, discoveredWell.getMapLocation());
            }
            //MN ID = 2
            if(discoveredWell.getResourceType().resourceID == 2){
                Comms.setManaWell(rc, discoveredWell.getMapLocation());
            }

            /*
            if (Comms.reportWellLocation(rc, HQIndex, discoveredWell)) {
                discoveredWell = null;
            }

             */
        }
    }

    static void anchorUpdate(RobotController rc) throws GameActionException {
        //If there is a new well nearby, remember
        if (discoveredWell == null) {
            WellInfo[] wells = rc.senseNearbyWells();
            for (WellInfo well : wells) {
            /*
                MapLocation loc = well.getMapLocation();
                int newValue = Comms.encode(loc.x, loc.y);
                for (int knownValue: knownWells) {
                    if (newValue != knownValue) {
                        discoveredWell = well;
                        break;
                    }
                }
            */
                MapLocation loc = well.getMapLocation();
                for (Comms.Well knownWell: knownWells) {
                    if (!knownWell.location.equals(loc)) {
                        discoveredWell = well;
                        break;
                    }
                }

                if (discoveredWell != null) {
                    break;
                }
            }
        }
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
                    if (discoveredWell != null) {
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
