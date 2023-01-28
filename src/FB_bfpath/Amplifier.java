package FB_bfpath;

import battlecode.common.*;

public strictfp class Amplifier {

    public enum AmpState {
        Combat, Following, FollowingCommand, Exploring
    }

    static RobotInfo[] enemies;

    static RobotInfo[] allies;

    static boolean initialized = false;

    static RobotInfo nearestEnemyMil;

    static int numEnemyMil;

    static int numAllyMil;

    static RobotInfo furthestAllyMil;

    static MapLocation closestAmp;

    static boolean ampInRange;
    static AmpState state;

    static int[] islands;

    static Comms.Attack attackCommand;

    static void run(RobotController rc) throws GameActionException {
        // interpret overall macro state
        readComms(rc);
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        onTurnStart(rc); // cleanup for when the turn starts



        // sense part
        sense(rc);

        selectState(rc);

        rc.setIndicatorString(state.toString());

        //select action based on state
        switch (state){
            case Combat:
                combat(rc);
                break;
            case Following:
                follow(rc);
                break;
            case FollowingCommand:
                followingCommand(rc);
                break;
            case Exploring:
                explore(rc);
                break;
        }

        writeComms(rc);
        Database.checkSymmetries(rc);
    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = AmpState.Exploring;
    }

    static void onTurnStart(RobotController rc) throws GameActionException{
        attackCommand = null;
    }

    static void readComms(RobotController rc)throws GameActionException {
        Database.init(rc);
        Database.downloadSymmetry(rc);
        Database.downloadLocations(rc);

        Comms.Attack[] attackCommands = Comms.getAllAttackCommands(rc);
        int maxPrio = (attackCommand==null) ? 0 : Comms.getCommPrio(attackCommand.type);

        for(int i = 0; i< attackCommands.length; i++){
            MapLocation loc = attackCommands[i].location;
            int prio = Comms.getCommPrio(attackCommands[i].type);
            if(prio > maxPrio){
                attackCommand = attackCommands[i];
                maxPrio = prio;
            }
        }



    }


    static void writeComms(RobotController rc) throws GameActionException{

        if(rc.canWriteSharedArray(0,0)){
            if(nearestEnemyMil != null){
                Comms.setAttackCommand(rc, nearestEnemyMil.getLocation(), nearestEnemyMil.getType() );
            }
            Database.uploadSymmetry(rc);
            Database.uploadLocations(rc);
        }
    }

    static void sense(RobotController rc) throws GameActionException{
        int minRange = 200;
        enemies = rc.senseNearbyRobots(RobotType.AMPLIFIER.visionRadiusSquared, rc.getTeam().opponent());
        allies = rc.senseNearbyRobots(RobotType.AMPLIFIER.visionRadiusSquared, rc.getTeam());
        MapLocation loc = rc.getLocation();
        nearestEnemyMil = null;
        for(RobotInfo enemy: enemies){
            if(enemy.getType() == RobotType.LAUNCHER || enemy.getType() == RobotType.DESTABILIZER){
                numEnemyMil++;

                int range = loc.distanceSquaredTo(enemy.getLocation());
                if(range < minRange){
                    nearestEnemyMil = enemy;
                    minRange = range;
                }
            } else if(enemy.getType() == RobotType.HEADQUARTERS){
                Database.addEnemyHQ(rc, enemy);
            }
        }

        int maxRange = 0;
        numAllyMil = 0;
        furthestAllyMil = null;
        closestAmp = null;
        ampInRange = false;
        for(RobotInfo ally: allies){
            if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.DESTABILIZER){
                int range = loc.distanceSquaredTo(ally.getLocation());
                if(range > maxRange){
                    furthestAllyMil = ally;
                    maxRange = range;
                }
                numAllyMil++;
            }
            if(ally.getType() == RobotType.AMPLIFIER && loc.isWithinDistanceSquared(ally.getLocation(),25)){
                ampInRange = true;
                if(closestAmp == null || loc.distanceSquaredTo(closestAmp) > loc.distanceSquaredTo(ally.getLocation())){
                    closestAmp = ally.getLocation();
                }
            }
        }

        islands  = rc.senseNearbyIslands();
        boolean commandSent = false;
        for(int i = 0; i < islands.length; i++){
            if(numEnemyMil == 0 && !commandSent && rc.senseTeamOccupyingIsland(islands[i]) != rc.getTeam()
                    && rc.canWriteSharedArray(0,0)){
                commandSent = true;
                Comms.setAnchorCommand(rc, rc.senseNearbyIslandLocations(islands[i])[0]);
            }
        }

        WellInfo[]wells = rc.senseNearbyWells();
        for(int i =0; i< wells.length; i++){
            Database.addWell(rc, wells[i]);
        }

    }
    static boolean enemiesFound(RobotController rc) throws GameActionException {
        enemies = rc.senseNearbyRobots(RobotType.AMPLIFIER.visionRadiusSquared, rc.getTeam().opponent());
        boolean enemiesFound = false;
        for (RobotInfo enemy : enemies) {
            if (!(enemy.getType() == RobotType.HEADQUARTERS ||
                    enemy.getType() == RobotType.CARRIER || enemy.getType() == RobotType.AMPLIFIER)) {
                enemiesFound = true;
                break;
            }
        }
        return enemiesFound;
    }


    static void selectState(RobotController rc) throws GameActionException{
        // select combat state if you see an enemy robot that is not HQ
        // combatCD will be necessary in the future for avoiding high-cd multiplier squares
        if(enemiesFound(rc)){
            state = AmpState.Combat;
        }else if (attackCommand != null){
            state = AmpState.FollowingCommand;
        } else if(numAllyMil > 0){
            state = AmpState.Following;
        } else{
            state = AmpState.Exploring;
        }


    }

    static void combat(RobotController rc) throws GameActionException{
        if(nearestEnemyMil != null){
            if(rc.canWriteSharedArray(0,0)){
                Comms.setAttackCommand(rc, nearestEnemyMil.getLocation(), nearestEnemyMil.getType());
            }
            if(rc.isMovementReady()){
                Direction moveDir = Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
                if(moveDir != null && rc.canMove(moveDir)){
                    rc.move(moveDir);
                }
            }
        }
    }

    static void follow(RobotController rc) throws GameActionException{
        Direction dir;
        if(ampInRange){
            dir = Pathfinder.pathAwayFrom(rc, closestAmp);
        } else{
            dir = Pathfinder.pathBug(rc,furthestAllyMil.getLocation());
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    static void followingCommand(RobotController rc) throws GameActionException{
        MapLocation target= attackCommand.location;
        Direction dir;
        if(ampInRange){
            dir = Pathfinder.pathAwayFrom(rc, closestAmp);
        } else{
            dir = Pathfinder.pathBug(rc,target);
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    static void explore(RobotController rc) throws GameActionException{
        Direction dir;
        if(ampInRange){
            dir = Pathfinder.pathAwayFrom(rc, closestAmp);
        } else{
            dir = Pathfinder.pathToExploreBug(rc);
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }


}
