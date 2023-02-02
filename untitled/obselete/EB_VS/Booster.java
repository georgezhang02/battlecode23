package EB_VS;

import battlecode.common.*;

public strictfp class Booster {

    public enum BoosterState {
        Exploring, Hover, Combat, Support
    }

    static RobotInfo[] enemies;

    static RobotInfo nearestEnemyMil;

    static RobotInfo[] allies;

    static WellInfo[]wells;

    static boolean initialized = false;

    static int numEnemyMil;

    static int numAllyMil;

    static int numNearbyAllyMil;

    static RobotInfo[] nearbyAllyMil = new RobotInfo[10];

    static RobotInfo nearestAllyMil;

    static RobotInfo furthestAllyMil;

    static RobotInfo nearestNonAdjacentAllyMil;

    static BoosterState state;

    static void run(RobotController rc) throws GameActionException {
        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }
        // interpret overall macro state
        readComms(rc);

        // sense part
        sense(rc);

        //select robot state
        selectState(rc);

        //select action based on state
        switch (state){
            case Combat:
                combat(rc);
                break;
            case Hover:
                hover(rc);
                break;
            case Exploring:
                explore(rc);
                break;
            case Support:
                support(rc);
                break;
        }

    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = BoosterState.Exploring;
    }

    static void readComms(RobotController rc)throws GameActionException{

    }

    //Sense nearby allies / enemies
    static void sense(RobotController rc) throws GameActionException{
        int minRange = RobotType.BOOSTER.visionRadiusSquared+1;
        enemies = rc.senseNearbyRobots(RobotType.BOOSTER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();

        allies = rc.senseNearbyRobots(RobotType.BOOSTER.visionRadiusSquared, rc.getTeam());
        numEnemyMil = 0;
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
        minRange = RobotType.BOOSTER.visionRadiusSquared+1;
        int minRangeNonAdj = RobotType.BOOSTER.visionRadiusSquared+1;;
        int maxRange = 0;
        numAllyMil = 0;
        numNearbyAllyMil = 0;
        nearestAllyMil = null;
        furthestAllyMil = null;
        nearestNonAdjacentAllyMil = null;

        for(RobotInfo ally: allies){
            if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.BOOSTER){
                int range = rc.getLocation().distanceSquaredTo(ally.getLocation());
                if(range > maxRange){
                    furthestAllyMil = ally;
                    maxRange = range;
                }

                if(range < minRange){
                    nearestAllyMil = ally;
                    minRange = range;
                }

                if(range < minRangeNonAdj && range >2){
                    minRangeNonAdj = range;
                    nearestNonAdjacentAllyMil = ally;
                }

                if(range<=8 && numNearbyAllyMil < 10){
                    nearbyAllyMil[numNearbyAllyMil] = ally;
                    numNearbyAllyMil++;
                }
                numAllyMil++;

            }
        }
    }

    //Select state
    static void selectState(RobotController rc) throws GameActionException{
        boolean enemiesFound = false;
        if(enemies.length > 0){
            int i = 0;
            while(i< enemies.length && enemies[i].getType() == RobotType.HEADQUARTERS){
                i++;
            }
            if(i != enemies.length){
                enemiesFound = true;
            }
        }
        if (enemiesFound){
            state = BoosterState.Combat;
        } else if(numAllyMil >= 1){
            state = BoosterState.Hover;
        }else{
            state = BoosterState.Exploring;
        }
    }

    static void combat(RobotController rc) throws GameActionException{
        Direction moveDir = findMovementCombat(rc);
        if(rc.canBoost()){
            rc.boost();
        }
        if(canMove(rc, moveDir)){
            rc.move(moveDir);
        }
    }
    
    //Hover around the nearest ally
    static void hover(RobotController rc) throws GameActionException{
        Direction dir = Pathfinder.pathBug(rc, nearestAllyMil.getLocation());
        if(rc.canMove(dir)){
            rc.move(dir);
        }
    }

    static void explore(RobotController rc) throws GameActionException{
        Direction dir = Pathfinder.pathToExploreHQ(rc);
        if(rc.canMove(dir)){
            rc.move(dir);
        }
    }
    static boolean canMove(RobotController rc, Direction dir) throws GameActionException{
        return dir != null && rc.canMove(dir);
    }

    //The concept for support is to constantly be boosting allies near hq or near island?
    //Non combat utility ig
    static void support(RobotController rc) throws GameActionException{

    }
    static Direction findMovementCombat(RobotController rc) throws GameActionException {
        //As a booster, you just kite no matter what the situation is
        if (nearestEnemyMil != null) {
            return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
        }
        return Pathfinder.pathAwayFrom(rc, enemies[0].getLocation());
    }

}
