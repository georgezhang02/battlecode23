package BB_kiteexplore;

import battlecode.common.*;

public strictfp class Launcher {

    public enum LauncherState {
        Combat, Pursuing, Exploring
    }

    static RobotInfo[] enemies;

    static WellInfo[]wells;
    static boolean initialized = false;

    static int combatCD = 0;

    static MapLocation pursuitLocation;

    static boolean inRange = false;

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

    static LauncherState state;

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
            case Pursuing:
                pursue(rc);
                break;
            case Exploring:
                explore(rc);
                break;
        }


    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = LauncherState.Exploring;
    }

    static void onTurnStart(RobotController rc) throws GameActionException{
        combatCD--;
    }

    static void readComms(RobotController rc)throws GameActionException{

    }

    static void sense(RobotController rc) throws GameActionException{
        enemies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();
    }

    static void selectState(RobotController rc) throws GameActionException{
        // select combat state if you see an enemy robot that is not HQ
        // combatCD will be necessary in the future for avoiding high-cd multiplier squares
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
            pursuitLocation = null;
            combatCD =5;
            state = LauncherState.Combat;
        } else if( combatCD >0 && pursuitLocation!=null &&
                rc.getLocation().distanceSquaredTo(pursuitLocation)>rc.getType().actionRadiusSquared ){
            state = LauncherState.Pursuing;
        }else{
            combatCD = 0;
            pursuitLocation = null;
            state = LauncherState.Exploring;
        }



    }

    static void combat(RobotController rc) throws GameActionException{
        RobotInfo attackRobot = findAttack(rc);
        if(attackRobot!=null){
            MapLocation attackLoc = attackRobot.getLocation();
            if(rc.canAttack(attackLoc)){
                // attack inside radius
                rc.attack(attackLoc);
                if(rc.isMovementReady() &&
                        (rc.getType() != RobotType.AMPLIFIER) ){
                    Direction moveDir = Pathfinder.pathAwayFrom(rc, attackLoc);
                    if(moveDir!= null && rc.canMove(moveDir)){
                        rc.move(moveDir);

                        if(rc.getLocation().distanceSquaredTo(attackLoc) > 13){
                            // if there is potential for them to step outside of your range
                            pursuitLocation = attackLoc;
                        }
                    }
                }
            } else if(rc.isMovementReady()){
                // attack outside radius
                Direction moveDir = Pathfinder.pathBug(rc, attackLoc);
                if(moveDir!= null && rc.canMove(moveDir)){
                    rc.move(moveDir);
                }
                rc.attack(attackLoc);

            }

        } else if(rc.isMovementReady() && pursuitLocation != null){
            // can't attack any target
            pursue(rc);
        }
    }

    static RobotInfo findAttack(RobotController rc) throws GameActionException{
        int maxValue = 0;
        int minRange = 0;
        inRange = false;

        RobotInfo enemyToAttack = null;
        for(RobotInfo enemy : enemies){
            if(enemy.getType() != RobotType.HEADQUARTERS){
                int range = enemy.getLocation().distanceSquaredTo(rc.getLocation());
                int attackValue = 0;

                // weight value based on unit attacked
                if(enemy.getType() == RobotType.LAUNCHER || enemy.getType()==RobotType.BOOSTER ||
                        enemy.getType()==RobotType.DESTABILIZER){
                    attackValue += 20 ;

                } else if(enemy.getType() == RobotType.AMPLIFIER){
                    attackValue +=15;
                } else if(enemy.getType() == RobotType.CARRIER){
                    attackValue += 10;
                }

                //add value for every hp point attacked
                attackValue+= Math.min(enemy.getHealth(), 6);


                //if you can kill the unit, add 10 value
                if(enemy.getHealth() - 6 <= 0){
                    attackValue+=10;
                }


                if (range < rc.getType().actionRadiusSquared){
                    // prefer targets already in action radius, and targets that are closer
                     inRange = true;

                    if(attackValue> maxValue){
                        maxValue = attackValue;
                        enemyToAttack = enemy;
                    } else if(attackValue == maxValue && range < minRange){
                        minRange = range;
                        enemyToAttack = enemy;
                    }
                } else if(!inRange && rc.isMovementReady()
                        && rc.canMove(rc.getLocation().directionTo(enemy.getLocation())) ){
                    // if your movement is ready, you can attack targets out of radius

                    if(attackValue> maxValue){
                        maxValue = attackValue;
                        enemyToAttack = enemy;
                    } else if(attackValue == maxValue && range < minRange){
                        minRange = range;
                        enemyToAttack = enemy;
                    }

                } else{
                    // if you cant reach the opponent, set pursuitlocation
                    if(attackValue> maxValue){
                        maxValue = attackValue;
                        pursuitLocation = enemy.getLocation();
                    }

                }

            }
        }
        return enemyToAttack;
    }

    static void pursue(RobotController rc) throws GameActionException{
        Direction moveDir = Pathfinder.pathBug(rc, pursuitLocation);
        if(rc.isMovementReady()){
            if(moveDir!= null && rc.canMove(moveDir)){
                rc.move(moveDir);
            }
        }
    }

    static void explore(RobotController rc) throws GameActionException{

        Direction dir = Pathfinder.pathToExplore(rc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }




}
