package CB_launcherheal;

import battlecode.common.*;

public strictfp class Amplifier {

    public enum AmpState {
        Combat, Following, Exploring
    }

    static RobotInfo[] enemies;

    static RobotInfo[] allies;

    static boolean initialized = false;

    static RobotInfo nearestEnemyMil;

    static int numEnemyMil;



    static AmpState state;

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
            case Exploring:
                explore(rc);
                break;
        }


    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = AmpState.Exploring;
    }

    static void onTurnStart(RobotController rc) throws GameActionException{

    }

    static void readComms(RobotController rc)throws GameActionException{

    }

    static void sense(RobotController rc) throws GameActionException{
        int minRange = 21;
        enemies = rc.senseNearbyRobots(RobotType.AMPLIFIER.visionRadiusSquared, rc.getTeam().opponent());
        allies = rc.senseNearbyRobots(RobotType.AMPLIFIER.visionRadiusSquared, rc.getTeam());

        nearestEnemyMil = null;
        for(RobotInfo enemy: enemies){
            if(enemy.getType() == RobotType.LAUNCHER || enemy.getType() == RobotType.DESTABILIZER){
                numEnemyMil++;

                int range = rc.getLocation().distanceSquaredTo(enemy.getLocation());
                if(range < minRange){
                    nearestEnemyMil = enemy;
                    minRange = range;
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
        }else{
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

    }

    static void explore(RobotController rc) throws GameActionException{

        Direction dir = Pathfinder.pathToExploreBug(rc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }


}
