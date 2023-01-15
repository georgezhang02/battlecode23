package BB_betterlauncherv2;

import battlecode.common.*;

import java.awt.*;

public strictfp class Launcher {

    public enum LauncherState {
        Combat, Pursuing, Exploring
    }

    static RobotInfo[] enemies;

    static RobotInfo nearestEnemyMil;
    static RobotInfo[] allies;

    static WellInfo[]wells;
    static boolean initialized = false;

    static int combatCD = 0;

    static MapLocation pursuitLocation;

    static boolean moveFirst = false;

    static int numEnemyMil;
    static int numAllyMil;

    static int numNearbyAllyMil;

    static RobotInfo[] nearbyAllyMil = new RobotInfo[8];

    static RobotInfo nearestAllyMil;

    static RobotInfo furthestAllyMil;

    static RobotInfo nearestNonAdjacentAllyMil;

    static boolean canExplore = false;

    static String allyString;

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

        rc.setIndicatorString(state.name());

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
        int minRange = RobotType.LAUNCHER.visionRadiusSquared+1;
        enemies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();
        allies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam());

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
        minRange = RobotType.LAUNCHER.visionRadiusSquared+1;
        int minRangeNonAdj = RobotType.LAUNCHER.visionRadiusSquared+1;;
        int maxRange = 0;
        numAllyMil = 0;
        numNearbyAllyMil = 0;
        nearestAllyMil = null;
        furthestAllyMil = null;
        nearestNonAdjacentAllyMil = null;

        String allyString="";


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

                if(range<=2){
                    nearbyAllyMil[numNearbyAllyMil] = ally;
                    numNearbyAllyMil++;
                }
                numAllyMil++;

            }
        }



        if(numAllyMil >=2 || numEnemyMil >= 1 || rc.getRoundNum() >20){
            canExplore = true;
        }
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
                rc.getLocation().distanceSquaredTo(pursuitLocation) > 5 ){
            state = LauncherState.Pursuing;
        }else{
            combatCD = 0;
            pursuitLocation = null;
            state = LauncherState.Exploring;
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
            if(attackRobot!= null &&  rc.canAttack(attackRobot.getLocation())){
                rc.attack(attackRobot.getLocation());
            }

            if(!moveFirst){
                if(canMove(rc, moveDir)){
                    rc.move(moveDir);
                }
            }
        }
    }
    static Direction findMovementCombat(RobotController rc, RobotInfo attackRobot) throws GameActionException {

        if(!rc.isActionReady()){
            // no action available, run from enemies
            if(nearestEnemyMil != null){
                rc.setIndicatorString("no actions, run");
                return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
            }  else if(attackRobot!= null){
                rc.setIndicatorString("pursue");
                pursuitLocation = attackRobot.getLocation();
                pursue(rc);
            }
        } else{
            // action ready
            //rc.setIndicatorString(attackRobot+"");
            if(attackRobot!= null){

                MapLocation attackLoc = attackRobot.getLocation();

                if(rc.canAttack(attackLoc)){
                    // if attack already in radius, attack and kite
                    rc.setIndicatorString("attack and kite");
                    rc.attack(attackLoc);
                    moveFirst = false;
                    if(nearestEnemyMil != null){
                        pursuitLocation = nearestEnemyMil.getLocation();
                        return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
                    }
                } else {
                    // attack not in radius
                    // only move forward to hit if the enemy is killable or you have significant man advantage
                    boolean moveToAttack = false;
                    if(numEnemyMil >0){
                        if(attackRobot.getHealth() <=6){
                            moveToAttack = true;
                        } else{
                            for(RobotInfo ally: allies){
                                if(ally.getLocation().distanceSquaredTo(attackLoc) <= attackRobot.getType().actionRadiusSquared){
                                    moveToAttack = true;
                                }
                            }
                        }



                        if(!moveToAttack && rc.getHealth() == rc.getType().getMaxHealth()){
                            int alliesCanSee = 0;
                            for(RobotInfo ally: allies){

                                if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.DESTABILIZER){
                                    int range = ally.getLocation().distanceSquaredTo(attackLoc);
                                    if(range <= ally.getType().visionRadiusSquared ||
                                            (rc.senseMapInfo(ally.getLocation()).getCooldownMultiplier(rc.getTeam()) >= 1 && range <= 4)){
                                            alliesCanSee++;

                                            // using cooldown here as heuristic for being in cloud
                                    }
                                }



                            }
                            if(alliesCanSee > numEnemyMil || alliesCanSee >=3){
                                moveToAttack = true;
                            }

                        }

                    } else if (!rc.canAttack(attackLoc)){
                        moveToAttack = true;
                    }


                    if(moveToAttack){
                        rc.setIndicatorString("move and hit to kill");
                        moveFirst = true;
                        return Pathfinder.pathBug(rc, attackLoc);
                    }

                }
            }


        }
        return null;


    }





    static RobotInfo findAttack(RobotController rc) throws GameActionException{
        int maxValue = 0;
        int minRange = 100;
        boolean inRange = false;
        boolean canAttack = false;

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
                } else{
                    // focus low health targets
                    attackValue+= (enemy.getType().getMaxHealth() - enemy.getHealth())/2;
                }

                if (rc.canAttack(enemy.getLocation())){
                    // prefer targets already in action radius, and targets that are closer
                    if(attackValue> maxValue || !inRange){
                        maxValue = attackValue;
                        enemyToAttack = enemy;
                    } else if(attackValue == maxValue && range < minRange ){
                        minRange = range;
                        enemyToAttack = enemy;
                    }
                    inRange = true;
                    canAttack = true;
                } else if(!inRange && rc.isMovementReady()){
                    // if your movement is ready, you can attack targets out of radius

                    if(!canAttack || attackValue> maxValue){
                        maxValue = attackValue;
                        enemyToAttack = enemy;
                    } else if(attackValue == maxValue && range < minRange){
                        minRange = range;
                        enemyToAttack = enemy;
                    }
                    canAttack = true;

                } else if (!inRange && !canAttack){
                    // if you cant reach the opponent, set pursuitlocation
                    if(attackValue> maxValue && (enemy.getType()
                            == RobotType.AMPLIFIER) || (enemy.getType()==RobotType.CARRIER)){
                        maxValue = attackValue;
                        pursuitLocation =  enemy.getLocation();
                        enemyToAttack = enemy;

                    }

                }

            }
        }
        return enemyToAttack;
    }

    static void pursue(RobotController rc) throws GameActionException{

        if(rc.isMovementReady()){
            rc.setIndicatorString(pursuitLocation+"");
            Direction moveDir = Pathfinder.pathBug(rc, pursuitLocation);

            if(canMove(rc, moveDir)){
                rc.move(moveDir);
                sense(rc);
                if(enemies.length > 0){
                    RobotInfo enemy = findAttack(rc);
                    if(enemy!= null && rc.canAttack(enemy.getLocation())){
                        rc.attack(enemy.getLocation());
                    }
                }
            }
        }
    }

    static void explore(RobotController rc) throws GameActionException{

        if (canExplore) {
            Direction dir;
            if(numNearbyAllyMil>=2){
                // 2 adjacent allies
                dir = Pathfinder.pathToExploreBug(rc);
                rc.setIndicatorString("2 adj, path to explore");
            } else if(numNearbyAllyMil == 1){
                // only one adjacent ally

                if(nearestNonAdjacentAllyMil != null
                        ){
                    dir = Pathfinder.pathBug(rc, nearestNonAdjacentAllyMil.getLocation());
                    if(rc.getLocation().add(dir).distanceSquaredTo(nearestNonAdjacentAllyMil.getLocation()) <= 2){
                        rc.setIndicatorString("one adjacent, path tonearest nonadj");
                    } else{
                        dir = Pathfinder.pathToExploreBug(rc);
                        rc.setIndicatorString("1 adj, too far to nonadj, path to explore");
                    }

                } else if(numAllyMil <2){
                    dir = Pathfinder.pathToExploreBug(rc);
                    rc.setIndicatorString("1 adj, 0 else sensed, path to explore");
                } else{
                    dir = Pathfinder.pathToExploreBug(rc);
                    rc.setIndicatorString("1 adj, others sensed, path to explore");
                }
            }else{
                // no adjacent allies
                if(numAllyMil == 0){
                    dir = Pathfinder.pathToExploreBug(rc);
                    rc.setIndicatorString("0 adj, path to explore");
                } else{
                    dir = Pathfinder.pathBug(rc, nearestAllyMil.getLocation());
                    rc.setIndicatorString(numAllyMil + " 0 adj, path to nearest");
                }

            }
            if(canMoveToExplore(rc, dir)){
                rc.move(dir);
                sense(rc);
                if(enemies.length > 0){
                    RobotInfo enemy = findAttack(rc);
                    if(enemy!= null && rc.canAttack(enemy.getLocation())){
                        rc.attack(enemy.getLocation());
                    }
                }
            }

        }

    }


    static boolean canMove(RobotController rc, Direction dir) throws GameActionException{
        return dir != null && rc.canMove(dir);


    }

    static boolean canMoveToExplore(RobotController rc, Direction dir) throws GameActionException{
        return dir != null && rc.canMove(dir) &&
                (rc.getRoundNum()%2 ==0 || rc.senseMapInfo(rc.getLocation()).getCooldownMultiplier(rc.getTeam()) != 1);


    }



}
