package CB_launcherheal;

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

    static Comms.Attack attackCommand;

    static void run(RobotController rc) throws GameActionException {
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        onTurnStart(rc); // cleanup for when the turn starts

        // interpret overall macro state
        readComms(rc);

        // sense part
        sense(rc);

        selectState(rc);



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


    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = AmpState.Exploring;
    }

    static void onTurnStart(RobotController rc) throws GameActionException{
        attackCommand = null;
    }

    static void readComms(RobotController rc)throws GameActionException {
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

    static void sense(RobotController rc) throws GameActionException{
        int minRange = 21;
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
            if(ally.getType() == RobotType.AMPLIFIER && loc.isWithinDistanceSquared(ally.getLocation(),16)){
                ampInRange = true;
                if(closestAmp == null || loc.distanceSquaredTo(closestAmp) > loc.distanceSquaredTo(ally.getLocation())){
                    closestAmp = ally.getLocation();
                }
            }
        }
    }
    static boolean enemiesFound(RobotController rc) throws GameActionException {
        enemies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, rc.getTeam().opponent());
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
            if(rc.getLocation().distanceSquaredTo(nearestEnemyMil.getLocation()) <=
                    nearestEnemyMil.getType().visionRadiusSquared){
                if(rc.isMovementReady()){
                    Direction moveDir = Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
                    if(moveDir != null && rc.canMove(moveDir)){
                        rc.move(moveDir);
                    }
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
