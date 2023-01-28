package FB_merged;

import battlecode.common.*;


public strictfp class Launcher {

    static final int ATTACKDMG = 20;
    public enum LauncherState {
        Combat, Pursuing, Exploring, Fallback, FollowingCommand, Camping
    }

    static RobotInfo[] enemies;

    static RobotInfo nearestEnemyMil;
    static RobotInfo[] allies;
    static WellInfo[]wells;

    static int[]islands;

    static MapLocation fallbackIsland;

    static boolean initialized = false;

    static int combatCD = 0;

    static MapLocation pursuitLocation;

    static boolean moveFirst = false;

    static int numEnemyMil;
    static int numAllyMil;

    static int numNearbyAllyMil;

    static Direction dirChange;

    static RobotInfo[] nearbyAllyMil = new RobotInfo[10];

    static RobotInfo nearestAllyMil;

    static RobotInfo furthestAllyMil;

    static RobotInfo[] alliesPrevious = new RobotInfo[10];;

    static int numAlliesPrevious =0;

    static boolean movementChange;

    static RobotInfo followBot;

    static RobotInfo nearestNonAdjacentAllyMil;

    static boolean canExplore = false;

    static int cooldownTurn = 0;

    static int detachCD = 0;

    static Comms.Attack attackCommand;

    static MapLocation closestEnemyHQ;

    static float diagonal = 1000;

    static boolean attackSent;

    static MapLocation[]clouds;


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
        updateDatabase(rc);
        getFallback(rc);
        selectState(rc);

        if(state == LauncherState.Exploring && alliesPrevious != null ){
            checkMovement(rc);

        }// sense if other bots have moved



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
            case Fallback:
                fallback(rc);
                break;
            case FollowingCommand:
                followCommand(rc);
                break;
            case Camping:
                campHQ(rc);
                break;
        }

        writeComms(rc);
        Database.checkSymmetries(rc);

        cloudAttack(rc);



    }

    static void onUnitInit(RobotController rc) throws GameActionException{
        state = LauncherState.Exploring;
        diagonal= (float) Math.sqrt(rc.getMapHeight()* rc.getMapHeight()+rc.getMapWidth()* rc.getMapHeight());
        fallbackIsland = null;
    }

    static void onTurnStart(RobotController rc) throws GameActionException{
        dirChange = null;
        movementChange = false;
        combatCD--;
        detachCD--;
        attackSent = false;

        if(rc.getRoundNum()%2 == 0){
            for(int i = 0; i< numNearbyAllyMil; i++){
                alliesPrevious[i] = nearbyAllyMil[i];
            }
            numAlliesPrevious = numNearbyAllyMil;
        }
    }

    static void readComms(RobotController rc)throws GameActionException{
        Database.init(rc);
        Database.downloadSymmetry(rc);
        Database.downloadLocations(rc);
    }
    static void sense(RobotController rc) throws GameActionException{
        int minRange = RobotType.LAUNCHER.visionRadiusSquared+1;
        enemies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();

        allies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam());

        numEnemyMil = 0;
        nearestEnemyMil = null;
        closestEnemyHQ = null;
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

        islands  = rc.senseNearbyIslands();
        boolean commandSent = false;
        for(int i = 0; i < islands.length; i++){

            if(rc.senseTeamOccupyingIsland(islands[i]) == rc.getTeam() && fallbackIsland == null){
                fallbackIsland = rc.senseNearbyIslandLocations(islands[i])[0];
            } else if(numEnemyMil == 0 && !commandSent && rc.canWriteSharedArray(0,0)){
                commandSent = true;
                Comms.setAnchorCommand(rc, rc.senseNearbyIslandLocations(islands[i])[0]);
            }
        }

        clouds = rc.senseNearbyCloudLocations();


        if((numAllyMil >=2 && cooldownTurn >=2) || numEnemyMil >= 1 || rc.getRoundNum() >30){

            canExplore = true;
        }
        if(numAllyMil >=2 && cooldownTurn <2){
            cooldownTurn++;
        }
    }

    static void selectState(RobotController rc) throws GameActionException{
        // select combat state if you see an enemy robot that is not HQ
        // combatCD will be necessary in the future for avoiding high-cd multiplier squares

        boolean enemiesFound = false;

        if(enemies.length > 0){
            int i = 0;
            while(i< enemies.length && enemies[i].getType() == RobotType.HEADQUARTERS){
                closestEnemyHQ = enemies[i].getLocation();
                i++;
            }
            if(i != enemies.length){
                enemiesFound = true;
            }
        }

        if (enemiesFound){
            attackCommand = null;
            pursuitLocation = null;
            combatCD =5;
            state = LauncherState.Combat;
        } else if(state == LauncherState.Fallback) {
            attackCommand = null;
            pursuitLocation = null;
            if(fallbackIsland == null){
                state = LauncherState.Exploring;
            } else if (rc.getLocation().distanceSquaredTo(fallbackIsland) <= RobotType.LAUNCHER.visionRadiusSquared) {
                if (rc.canSenseLocation(fallbackIsland)) {
                    if (rc.senseTeamOccupyingIsland(rc.senseIsland(fallbackIsland)) != rc.getTeam()) {
                        if(rc.canWriteSharedArray(0,0)){
                            Comms.reportIslandLocation(rc, fallbackIsland, null);
                        }
                        state = LauncherState.Exploring;
                    }
                }
            }
            if (rc.getHealth() == RobotType.LAUNCHER.getMaxHealth()) {
                state = LauncherState.Exploring;
            }
        } else if (fallbackIsland != null && rc.getHealth() < RobotType.LAUNCHER.getMaxHealth()/2
            && Math.sqrt(rc.getLocation().distanceSquaredTo(fallbackIsland)) <= diagonal/2){
            attackCommand = null;
            pursuitLocation = null;
            state = LauncherState.Fallback;
        }
        else if( combatCD >0 && pursuitLocation!=null &&
                rc.getLocation().distanceSquaredTo(pursuitLocation) > 5 ){
            attackCommand = null;
            state = LauncherState.Pursuing;
        } else if(state == LauncherState.FollowingCommand && attackCommand != null){
            if(rc.getLocation().distanceSquaredTo(attackCommand.location) <= rc.getType().actionRadiusSquared
                    && rc.canSenseLocation(attackCommand.location)){
                attackCommand = null;
                if(getAttackCommand(rc) != null){
                    combatCD = 0;
                    pursuitLocation = null;
                    state = LauncherState.FollowingCommand;
                } else{
                    pursuitLocation = null;
                    state = LauncherState.Exploring;
                }
            }
        } else if(getAttackCommand(rc) != null){
            combatCD = 0;
            pursuitLocation = null;
            state = LauncherState.FollowingCommand;
        } /*else if(closestEnemyHQ != null){
            attackCommand = null;
            pursuitLocation = null;
            state = LauncherState.Camping;
        }*/else{
            combatCD = 0;
            pursuitLocation = null;
            state = LauncherState.Exploring;
        }
    }

    static void fallback(RobotController rc) throws GameActionException{

        //if its on an island tile, just stay there, otherwise if its close to an island tile but its occupied,
        //path to an open spot
        if(rc.senseIsland(rc.getLocation()) > 0 && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == rc.getTeam()){
            fallbackIsland = rc.getLocation();
        }
        //move towards the fallbackIsland spot
        Direction dir = Pathfinder.pathGreedy(rc, fallbackIsland);
        if(rc.canMove(dir)) {
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
            if(nearestEnemyMil != null &&
                    (nearestEnemyMil.getLocation().distanceSquaredTo(rc.getLocation()) <= 16 && rc.getActionCooldownTurns()<=1)){

                return Pathfinder.pathAwayFrom(rc, nearestEnemyMil.getLocation());
            }  else if(attackRobot!= null){

                pursuitLocation = attackRobot.getLocation();
                pursue(rc);
            }
        } else{
            // action ready
            if(attackRobot!= null){

                MapLocation attackLoc = attackRobot.getLocation();

                if(rc.canAttack(attackLoc)){
                    // if attack already in radius, attack and kite
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
                        if(attackRobot.getHealth() <=ATTACKDMG){
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

                                    }
                                }



                            }
                            if(alliesCanSee >= numEnemyMil || alliesCanSee >=2){
                                moveToAttack = true;
                            }

                        }

                    } else if(!rc.canAttack(attackLoc)){
                        int alliesCanSee = 0;
                        for(RobotInfo ally: allies){

                            if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.DESTABILIZER){
                                int range = ally.getLocation().distanceSquaredTo(attackLoc);
                                if(range <= ally.getType().visionRadiusSquared ||
                                        (rc.senseMapInfo(ally.getLocation()).getCooldownMultiplier(rc.getTeam()) >= 1 && range <= 4)){
                                    alliesCanSee++;

                                }
                            }
                        }
                        if(alliesCanSee >= 1){
                            moveToAttack = true;
                        }


                    }


                    if(moveToAttack){

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
                    attackValue += 200;

                } else if(enemy.getType() == RobotType.AMPLIFIER){
                    attackValue +=150;
                } else if(enemy.getType() == RobotType.CARRIER){
                    attackValue += 100;
                }

                //add value for every hp point attacked
                attackValue+= Math.min(enemy.getHealth(), ATTACKDMG);


                //if you can kill the unit, add 10 value
                if(enemy.getHealth() - ATTACKDMG <= 0){
                    attackValue+=100;
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
        if(!attackSent && enemyToAttack != null){
            if(rc.canWriteSharedArray(0,0)){
                Comms.setAttackCommand(rc, enemyToAttack.getLocation(), enemyToAttack.getType());
                attackSent = true;
            }
        }

        return enemyToAttack;
    }

    static void cloudAttack(RobotController rc) throws GameActionException {
        int minDist = 10000;
        MapLocation attackLoc = null;
        if (rc.isActionReady() && rc.senseMapInfo(rc.getLocation()).getCooldownMultiplier(rc.getTeam()) <= 1){
            for (int i = 0; i < clouds.length && Clock.getBytecodesLeft() > 500; i++) {
                int range = rc.getLocation().distanceSquaredTo(clouds[i]);
                if (rc.canAttack(clouds[i]) && range > 4 && range < minDist) {
                    minDist = range;
                    attackLoc = clouds[i];
                }
            }

            if(attackLoc!= null){
                rc.attack(attackLoc);
            }
        }




    }

    static void pursue(RobotController rc) throws GameActionException{

        if(rc.isMovementReady()){
            Direction moveDir = Pathfinder.pathGreedy(rc, pursuitLocation);

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


    static void followCommand(RobotController rc) throws GameActionException{


        MapLocation target= attackCommand.location;

        if(!rc.canSenseLocation(target) || rc.getLocation().distanceSquaredTo(target) > rc.getType().actionRadiusSquared){
            if(rc.isMovementReady()){
                Direction moveDir = Pathfinder.pathGreedy(rc, target);
                if(canMove(rc, moveDir)){
                    rc.move(moveDir);
                }
                sense(rc);
                if(enemies.length > 0){
                    RobotInfo enemy = findAttack(rc);
                    if(enemy!= null && rc.canAttack(enemy.getLocation())){
                        rc.attack(enemy.getLocation());
                    }
                }

            }
        } else{
            attackCommand = null;
            state = LauncherState.Exploring;
            explore(rc);
        }
    }

    static Comms.Attack getAttackCommand(RobotController rc) throws GameActionException {
        Comms.Attack[] attackCommands = Comms.getAllAttackCommands(rc);
        int maxPrio = (attackCommand==null) ? 0 : Comms.getCommPrio(attackCommand.type);
        int minRange = 10000;

        for(int i = 0; i< attackCommands.length; i++){
            MapLocation loc = attackCommands[i].location;
            int prio = Comms.getCommPrio(attackCommands[i].type);
            int range = rc.getLocation().distanceSquaredTo(attackCommands[i].location);
            if(range > rc.getType().actionRadiusSquared &&
                    (loc.distanceSquaredTo(rc.getLocation()) <=50 ||
                            Math.sqrt(loc.distanceSquaredTo(rc.getLocation()))< diagonal/3)){

                if(prio > maxPrio){
                    attackCommand = attackCommands[i];
                    maxPrio = prio;
                    minRange = range;
                } else if(prio== maxPrio && minRange < range){
                    attackCommand = attackCommands[i];
                    minRange = range;
                }
            }

        }
        return attackCommand;
    }

    static void explore(RobotController rc) throws GameActionException{
        Direction dir = Direction.CENTER;

        if(RobotPlayer.turnCount < 2 && numAllyMil > 0 &&
                nearestAllyMil.getLocation().distanceSquaredTo(rc.getLocation()) >=2){
            canExplore = true;
            dir = Pathfinder.pathGreedy(rc, nearestAllyMil.getLocation());
            if(canMoveToExplore(rc, dir)) {
                rc.move(dir);
            }
        } else if(numAllyMil <2){
            canExplore = true;
        }

        if (canExplore && (rc.getRoundNum()%2 == 0 ||
                rc.senseMapInfo(rc.getLocation()).getCooldownMultiplier(rc.getTeam()) != 1)) {
            if(movementChange){
                detachCD =4;
            }
            if((movementChange || detachCD > 0)  && numNearbyAllyMil < 5 &&
                    rc.getLocation().distanceSquaredTo(followBot.getLocation()) >2
                        && (!Pathfinder.rotatingBug && !Pathfinder.directBug)){

                dir = Pathfinder.pathGreedy(rc, followBot.getLocation());

            } else{
                dir = Pathfinder.pathToExploreHQ(rc);

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

    static void campHQ(RobotController rc) throws GameActionException{
        Direction dir = Pathfinder.pathBug(rc, closestEnemyHQ);
        if(rc.getLocation().isWithinDistanceSquared(closestEnemyHQ,9)){
            dir = Pathfinder.pathAwayFrom(rc,closestEnemyHQ);
        } else if(rc.getLocation().add(dir).isWithinDistanceSquared(closestEnemyHQ,9)){
            return;
        }
        if(rc.canMove(dir)){
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

    static void checkMovement(RobotController rc){

        String checked ="";

        String previous = "alliesPrevious ";


        for(RobotInfo ally:allies){
            if(ally.getType() == RobotType.LAUNCHER || ally.getType() == RobotType.DESTABILIZER){

                int ID = ally.getID();
                MapLocation loc = ally.getLocation();



                for(int j = 0; j < numAlliesPrevious; j++){

                    //If the ids match up
                    if(ID == alliesPrevious[j].getID()){
                        previous  = previous + ally.getID()+" "+ally.getLocation();
                        //If the previous ally location is different from before


                        if(loc != alliesPrevious[j].getLocation()){
                            movementChange = true;
                            dirChange = alliesPrevious[j].getLocation().directionTo(loc);
                            followBot = ally;

                            return;
                        }
                    }
                }
            }
        }
    }

    //Find closest capped island
    static MapLocation getFallback(RobotController rc) throws GameActionException{
        Comms.Island[] islands = Comms.getAllIslands(rc);
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < islands.length; i++){
                int dist = rc.getLocation().distanceSquaredTo(islands[i].location);
                if(dist < min){
                    fallbackIsland = islands[i].location;
                    min = dist;
                }
        }
        return fallbackIsland;
    }

    static void updateDatabase(RobotController rc) throws GameActionException{
        for(RobotInfo enemy: enemies){
            if(enemy.getType().equals(RobotType.HEADQUARTERS)){
                Database.addEnemyHQ(rc, enemy);
            }
        }
        for(WellInfo well: wells){
            Database.addWell(rc, well);
        }

    }

    static void writeComms(RobotController rc) throws GameActionException {

        if(rc.canWriteSharedArray(0,0)){
            Database.uploadSymmetry(rc);
            Database.uploadLocations(rc);
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
