package DB_VERY_DB;

import DB_first.Pathfinder;
import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public strictfp class Carrier {

    static final Random rng = new Random(6147);

    static CarrierState state = CarrierState.None;
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    private static enum CarrierState {
        None, Exploring, Returning, Anchoring, Gathering, AnchorReturnCarrier;
    }

    static MapLocation WELL_LOCATION = null;
    static MapLocation HQ_LOCATION = null;

    static void run(RobotController rc) throws GameActionException {
        if(state == CarrierState.None){
            assign(rc);
        }
        switch (state) {
            case Exploring:
                explore(rc);
            case Anchoring:
                anchor(rc);
            case Returning:
                returnToHQ(rc);
            case Gathering:
                gather(rc);
                break;
        }
    }
        /*
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }


        // If we can see a well, move towards it
        WellInfo[] wells = rc.senseNearbyWells();
        if (wells.length > 1 && rng.nextInt(3) == 1) {
            WellInfo well_one = wells[1];
            Direction dir = me.directionTo(well_one.getMapLocation());
            if (rc.canMove(dir))
                rc.move(dir);
        }
         */

    static void assign(RobotController rc) throws GameActionException{
        RobotInfo nearbyRobots[] = rc.senseNearbyRobots();
        for(int i = 0; i < nearbyRobots.length; i++){
            if(nearbyRobots[i].getType() == RobotType.HEADQUARTERS){
                HQ_LOCATION = nearbyRobots[i].getLocation();
                //If it can take an anchor then get the anchor
                if(rc.canTakeAnchor(nearbyRobots[i].getLocation(), Anchor.STANDARD) && rc.getAnchor() == null){
                    rc.takeAnchor(nearbyRobots[i].getLocation(), Anchor.STANDARD);
                    state = CarrierState.Anchoring;
                }
                else{
                    state = CarrierState.Exploring;
                }
            }
        }
    }
    static void explore(RobotController rc) throws GameActionException{
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

        //If there is a well nearby, become a well carrier
        WellInfo[] wells = rc.senseNearbyWells();
        if (wells.length > 0) {
            if(wells[0].getMapLocation().distanceSquaredTo(rc.getLocation()) < 16){
                WELL_LOCATION = wells[0].getMapLocation();
                state = CarrierState.Gathering;
            }
        }
    }

    static void returnToHQ(RobotController rc) throws GameActionException{
        //If the hq location is in action range, deposit resources to HQ
        if(HQ_LOCATION.distanceSquaredTo(rc.getLocation()) < 5){
            int manaAmount = rc.getResourceAmount(ResourceType.MANA);
            int adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
            int elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);
            if(rc.canTransferResource(HQ_LOCATION, ResourceType.MANA, manaAmount) && manaAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.MANA, manaAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.ADAMANTIUM, adAmount) && adAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ADAMANTIUM, adAmount);
            }
            else if(rc.canTransferResource(HQ_LOCATION, ResourceType.ELIXIR, elixirAmount) && elixirAmount != 0){
                rc.transferResource(HQ_LOCATION,ResourceType.ELIXIR, elixirAmount);
            }
            else{
                state = state.Exploring;
            }
        }

        //move back to HQ
        else{
            Direction moveDir = Pathfinder.pathBF(rc, HQ_LOCATION);

            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }

    static void anchor(RobotController rc) throws GameActionException{
        //If the robot has an anchor, move towards an island to place the anchor
        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            if (islands.length > 0) {
                for (int id : islands) {
                    MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                    islandLocs.addAll(Arrays.asList(thisIslandLocs));
                }
                if (islandLocs.size() > 0) {
                    MapLocation islandLocation = islandLocs.iterator().next();
                    rc.setIndicatorString("Moving my anchor towards " + islandLocation);
                    while (!rc.getLocation().equals(islandLocation)) {
                        Direction dir = rc.getLocation().directionTo(islandLocation);
                        if (rc.canMove(dir)) {
                            rc.move(dir);
                        }
                    }
                    if (rc.canPlaceAnchor()) {
                        rc.setIndicatorString("Huzzah, placed anchor!");
                        rc.placeAnchor();
                        state = state.Returning;
                    }
                }
            }
            //Move randomly until you find an island
            else{
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }
            }
        }
    }

    static void gather(RobotController rc) throws GameActionException{
        if(WELL_LOCATION.distanceSquaredTo(rc.getLocation()) < 5){
            MapLocation me = rc.getLocation();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                    if (rc.canCollectResource(wellLocation, -1)) {
                        rc.collectResource(wellLocation, -1);
                        rc.setIndicatorString("Collecting, now have, AD:" +
                                rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                                " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                                " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                    }
                    //if a carrier cannot get anymore resources, return to base
                    if(rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA)
                            + rc.getResourceAmount(ResourceType.ELIXIR) > 38)
                        state = state.Returning;
                }
            }
        }
        if(rc.isMovementReady()) {
            Direction moveDir = Pathfinder.pathBF(rc, WELL_LOCATION);

            if(moveDir != null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }
}
