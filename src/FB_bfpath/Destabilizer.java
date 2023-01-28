package FB_bfpath;

import battlecode.common.*;

public strictfp class Destabilizer {

    public enum DestabilizerState {
        Combat, Exploring, Hover
    }

    static RobotInfo[] enemies;

    static RobotInfo nearestEnemyMil;

    static RobotInfo[] allies;

    static WellInfo[]wells;

    static boolean initialized = false;

    static boolean moveFirst = false;

    static int numEnemyMil;

    static int numAllyMil;

    static int numNearbyAllyMil;

    static RobotInfo[] nearbyAllyMil = new RobotInfo[10];

    static RobotInfo nearestAllyMil;

    static RobotInfo furthestAllyMil;

    static RobotInfo nearestNonAdjacentAllyMil;

    static DestabilizerState state;

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
        }

    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = DestabilizerState.Exploring;
    }

    static void readComms(RobotController rc)throws GameActionException{

    }

    //Sense nearby allies / enemies
    static void sense(RobotController rc) throws GameActionException{
        int minRange = RobotType.DESTABILIZER.visionRadiusSquared+1;
        enemies = rc.senseNearbyRobots(RobotType.DESTABILIZER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();

        allies = rc.senseNearbyRobots(RobotType.DESTABILIZER.visionRadiusSquared, rc.getTeam());
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
        minRange = RobotType.DESTABILIZER.visionRadiusSquared+1;
        int minRangeNonAdj = RobotType.DESTABILIZER.visionRadiusSquared+1;;
        int maxRange = 0;
        numAllyMil = 0;
        numNearbyAllyMil = 0;
        nearestAllyMil = null;
        furthestAllyMil = null;
        nearestNonAdjacentAllyMil = null;

        for(RobotInfo ally: allies){
            if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.DESTABILIZER){
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
            state = DestabilizerState.Combat;
        } else if(numAllyMil >= 1){
            state = DestabilizerState.Hover;
        }else{
            state = DestabilizerState.Exploring;
        }
    }

    static void combat(RobotController rc) throws GameActionException{
        RobotInfo attackRobot = findAttack(rc);
        Direction moveDir = findMovementCombat(rc, attackRobot);

        if(attackRobot!=null){
            if(moveFirst){
                if(canMove(rc, moveDir)){
                    rc.move(moveDir);
                    sense(rc);
                    if(enemies.length > 0){
                        RobotInfo enemy = findAttack(rc);
                        attackRobot = enemy;
                    }
                }
            }
            //rc.setIndicatorString(attackRobot.getLocation().toString() + " " + rc.canAttack(attackRobot.getLocation()) + " " + rc.getActionCooldownTurns());
            if(attackRobot != null &&  rc.canDestabilize(attackRobot.getLocation())){
                rc.destabilize(attackRobot.getLocation());
            }

            if(!moveFirst){
                if(canMove(rc, moveDir)){
                    rc.move(moveDir);
                }
            }
        }
    }

    //Hover around the nearest ally
    static void hover(RobotController rc) throws GameActionException{
        Direction dir = Pathfinder.pathBug(rc, nearestAllyMil.getLocation());
        if(rc.canMove(dir)){
            rc.move(dir);
        }
    }

    //Probably find some way to optimize to attack a group of enemies, for now it just attacks the closest
    static RobotInfo findAttack(RobotController rc) throws GameActionException{
        int minRange = 100;
        int maxRange = 0;

        RobotInfo enemyToAttack = null;
        for(RobotInfo enemy : enemies){
            if(enemy.getType() != RobotType.HEADQUARTERS){
                int range = enemy.getLocation().distanceSquaredTo(rc.getLocation());

                if(range < RobotType.DESTABILIZER.actionRadiusSquared){
                    if(range > maxRange){
                        maxRange = range;
                    }
                    if(range < minRange){
                        enemyToAttack = enemy;
                        minRange = range;
                    }
                }
            }
        }
        return enemyToAttack;
    }

    static Direction findMovementCombat(RobotController rc, RobotInfo attackRobot) throws GameActionException {
        moveFirst = false;
        if(!rc.isActionReady()){
            // no action available, run from enemies; you're a destablizier not much you can do
            rc.setIndicatorString("no actions, run");
            if(nearestEnemyMil != null){
                return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
            }
            return Pathfinder.pathAwayFrom(rc,enemies[0].getLocation());
        } else{
            // action ready
            //rc.setIndicatorString(attackRobot+"");
            if(attackRobot!= null){

                MapLocation attackLoc = attackRobot.getLocation();

                if(rc.canAttack(attackLoc)){
                    // if attack already in radius, attack and kite
                    rc.setIndicatorString("attack and kite");
                    rc.attack(attackLoc);
                    return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
                } else {
                    // attack not in radius
                    // only move forward to hit if the enemy is killable or you have nearby allies
                    int alliesCanSee = 0;
                    if(numEnemyMil > 0) {
                        for (RobotInfo ally : allies) {
                            if (ally.getLocation().distanceSquaredTo(attackLoc) <= attackRobot.getType().actionRadiusSquared) {
                                alliesCanSee++;
                            }
                        }
                        if(alliesCanSee > 2){
                            moveFirst = true;
                            return Pathfinder.pathBug(rc, attackLoc);
                        }
                    }
                }
            }
        }
        return null;

    }

    //Explore if there are no military units nearby
    static void explore(RobotController rc) throws GameActionException{
        Direction dir = Pathfinder.pathToExploreHQ(rc);
        if(rc.canMove(dir)){
            rc.move(dir);
        }
    }

    static boolean canMove(RobotController rc, Direction dir) throws GameActionException{
        return dir != null && rc.canMove(dir);
    }
}
