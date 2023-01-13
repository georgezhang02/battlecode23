package BB_kiteexplore;

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
    static CarrierState priorState = CarrierState.None;

    static MapLocation location;
    static RobotInfo[] allies;
    static RobotInfo[] enemies;

    static MapLocation HQ_LOCATION = null;
    static int HQIndex;
    static MapLocation assignedWell = null;
    static int[] knownWells;
    static MapLocation discoveredWell = null;

    static void run(RobotController rc) throws GameActionException {

        sense(rc);

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        checkEnemies();

        rc.setIndicatorString(state.name());
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
            case Runaway:
                runaway(rc);
                break;
        }
    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        for (RobotInfo ally : allies) {
            if (ally.getType() == RobotType.HEADQUARTERS) {
                HQ_LOCATION = ally.getLocation();
                HQIndex = Comms.getHQIndexByID(rc, ally.getID());
                assignedWell = Comms.getWellCommand(rc, HQIndex);
                Comms.clearWellCommand(rc, HQIndex);
                knownWells = Comms.getAllWellValues(rc);
                selectState();
            }
        }
    }

    static void selectState() {
        if (assignedWell != null) {
            updateState(CarrierState.Gathering);
        } else {
            updateState(CarrierState.Exploring);
        }
    }

    static void sense(RobotController rc) throws GameActionException{
        location = rc.getLocation();
        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());
        allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam());
    }

    static void checkEnemies() {
        boolean enemiesFound = false;
        for (RobotInfo enemy : enemies) {
            if (!(enemy.getType() == RobotType.HEADQUARTERS || enemy.getType() == RobotType.CARRIER)) {
                enemiesFound = true;
                break;
            }
        }
        if(enemiesFound){
            state = CarrierState.Runaway;
        }
        else if (state == CarrierState.Runaway) {
            state = priorState;
        }
    }

    static void gather(RobotController rc) throws GameActionException {
        //if a carrier cannot get anymore resources, return to base
        if(rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR) == 40){
            updateState(CarrierState.Returning);
            returnToHQ(rc);
        } else if(assignedWell.distanceSquaredTo(location) <= 2){
            if (rc.canCollectResource(assignedWell, -1)) {
                rc.collectResource(assignedWell, -1);
                /* rc.setIndicatorString("Collecting, now have, AD:" +
                        rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                        " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                        " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                 */
            }
        } else if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBF(rc, assignedWell);
            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }

    static void explore(RobotController rc) throws GameActionException{

        if (HQ_LOCATION.distanceSquaredTo(location) <= 9) {
            // Update known wells
            knownWells = Comms.getAllWellValues(rc);

            MapLocation command = Comms.getWellCommand(rc, HQIndex);
            if (command != null) {
                assignedWell = command;
                Comms.clearWellCommand(rc, HQIndex);
                updateState(CarrierState.Gathering);
                gather(rc);
                return;
            }
            if (HQ_LOCATION.distanceSquaredTo(location) <= 2 && rc.canTakeAnchor(HQ_LOCATION, Anchor.STANDARD)) {
                rc.takeAnchor(HQ_LOCATION, Anchor.STANDARD);
                updateState(CarrierState.Anchoring);
                anchor(rc);
                return;
            }
        }

        Direction dir = Pathfinder.pathToExplore(rc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

        //If there is a new well nearby, return to HQ to report
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            MapLocation loc = well.getMapLocation();
            int newValue = Comms.encode(loc.x, loc.y);
            for (int knownValue: knownWells) {
                if (newValue != knownValue) {
                    discoveredWell = loc;
                    state = CarrierState.Returning;
                    return;
                }
            }
        }
        //rc.setIndicatorString(String.valueOf(Clock.getBytecodesLeft()));
    }

    static void returnToHQ(RobotController rc) throws GameActionException{
        //move back to HQ
        Direction moveDir = Pathfinder.pathBF(rc, HQ_LOCATION);
        int adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int manaAmount = rc.getResourceAmount(ResourceType.MANA);
        int elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);
        if(moveDir != null && rc.canMove(moveDir)){
            rc.move(moveDir);
        }
        // If within comms range and can report well, report well.
        if(discoveredWell != null && HQ_LOCATION.distanceSquaredTo(location) <= 9) {
            if (Comms.reportWellLocation(rc, HQIndex, discoveredWell)) {
                discoveredWell = null;
                if (adAmount == 0 && manaAmount == 0 && elixirAmount == 0) {
                    selectState();
                }
            }
        }
        //If the hq location is in action range, deposit resources to HQ
        else if(HQ_LOCATION.distanceSquaredTo(location) <= 2){
            if(rc.canTransferResource(HQ_LOCATION, ResourceType.ELIXIR, elixirAmount) && elixirAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ELIXIR, elixirAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.MANA, manaAmount) && manaAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.MANA, manaAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.ADAMANTIUM, adAmount) && adAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ADAMANTIUM, adAmount);
            }
            else if (discoveredWell == null){
                selectState();
            }
        }
    }

    static void anchor(RobotController rc) throws GameActionException{
        //If the robot has an anchor, move towards an island to place the anchor
        if (rc.getAnchor() != null) {
            //If there is a new well nearby, remember
            if (discoveredWell == null) {
                WellInfo[] wells = rc.senseNearbyWells();
                for (WellInfo well : wells) {
                    MapLocation loc = well.getMapLocation();
                    int newValue = Comms.encode(loc.x, loc.y);
                    for (int knownValue: knownWells) {
                        if (newValue != knownValue) {
                            discoveredWell = loc;
                            break;
                        }
                    }
                    if (discoveredWell != null) {
                        break;
                    }
                }
            }
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
                    Direction moveDir = Pathfinder.pathBF(rc, islandLocation);

                    if(moveDir != null && rc.canMove(moveDir)){
                        rc.move(moveDir);
                    }

                    if (rc.canPlaceAnchor()) {
                        rc.placeAnchor();
                        if (discoveredWell != null) {
                            updateState(CarrierState.Returning);
                        } else {
                            updateState(CarrierState.Exploring);
                        }
                    }
                }
            }
            //Explore until you find an island
            else{
                Direction dir = Pathfinder.pathToExplore(rc);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }
            }
        } else {
            updateState(CarrierState.Exploring);
            explore(rc);
        }
    }

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

    private static void updateState(CarrierState newState) {
        priorState = newState;
        state = newState;
    }
}
