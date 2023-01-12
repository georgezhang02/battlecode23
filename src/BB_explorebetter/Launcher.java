package BB_explorebetter;

import battlecode.common.*;

public strictfp class Launcher {

    public enum LauncherState {
        Combat, Pursuing, Exploring
    }

    static RobotInfo[] enemies;

    static WellInfo[]wells;
    static boolean initialized = false;

    static int combatCD = 0;

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
        if(enemies.length > 0){
            int i = 0;
            while(i< enemies.length && enemies[i].getType() == RobotType.HEADQUARTERS){
                i++;
            }
            if(i != enemies.length){
                combatCD =1;
                state = LauncherState.Combat;
                rc.setIndicatorString("inCombat");
            } else{
                state = LauncherState.Exploring;
            }

        }
        // otherwise explore
        if(state == LauncherState.Combat && combatCD <=0){
            state = LauncherState.Exploring;
            return;
        }


    }

    static void combat(RobotController rc) throws GameActionException{
        RobotInfo attackRobot = findAttack(rc);
        if(attackRobot!=null){
            MapLocation attackLoc = attackRobot.getLocation();
            if(rc.canAttack(attackLoc)){
                rc.attack(attackLoc);
            }


            // chase after attacked person
            if(rc.isMovementReady() && rc.getLocation().distanceSquaredTo(attackLoc) > 10){
                Direction moveDir = Pathfinder.pathBug(rc, attackLoc);
                if(moveDir!= null && rc.canMove(moveDir)){
                    rc.move(moveDir);
                }
            }
        }
    }

    static RobotInfo findAttack(RobotController rc) throws GameActionException{
        int maxValue = 0;
        RobotInfo enemyToAttack = null;
        for(RobotInfo enemy : enemies){
            if(enemy.getType() != RobotType.HEADQUARTERS){
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

                if(attackValue> maxValue){
                    maxValue = attackValue;
                    enemyToAttack = enemy;
                }
            }
        }
        return enemyToAttack;
    }

    static void pursue(RobotController rc) throws GameActionException{

    }

    static void explore(RobotController rc) throws GameActionException{

        Direction dir = Pathfinder.pathToExplore(rc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }




}
