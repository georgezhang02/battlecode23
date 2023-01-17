package obselete.DB_comms;

import battlecode.common.*;

public strictfp class HQ {

    static Pathfinder pathfinder;
    static RobotInfo[] enemies;
    static RobotInfo[] allies;
    static boolean initialized = false;
    static MapLocation target;
    static String indicatorString;

    static MapLocation location;
    static boolean firstHQ;

    public static void run(RobotController rc) throws GameActionException {

        if(!initialized){
            onUnitInit(rc); // first time starting the bot, do some setup
            initialized = true;
        }

        initTurn(rc); // cleanup for when the turn starts

        // sense part
        sense();

        //think part, NOTE -> later one we might want to t
        // take into account whether your action and movement are available,
        //and what order you want to do them in
        //select your next state, movement, and action based on comms, sensor data, and state

        // interpret overall macro state
        readComms();

        // optimize selected action/movement over all sensed objects
        // you should be writing to comms as you detect important information
        perEnemy();
        perAlly();
        perObjective();

        //act part should be triggered by think part, see methods below
        act(rc);

        debug(rc);
        // prints the indicator string

    }

    static void onUnitInit(RobotController rc) throws GameActionException {
        location = rc.getLocation();
        firstHQ = Comms.setTeamHQLocation(rc, location);
    }

    static void initTurn(RobotController rc) throws GameActionException {
        indicatorString = "";
    }

    static void readComms(){

    }

    static void sense(){

    }

    static void perEnemy(){

    }

    static void perAlly(){

    }

    static void perObjective(){

    }

    static void act(RobotController rc) throws GameActionException{
        // Pick a direction to build in.
        Direction dir = Helper.directions[Helper.rng.nextInt(Helper.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);

        //If there are less than 15 bots, or one of mana or ad is booming
        if (rc.getRobotCount() <= 15 || rc.getResourceAmount(ResourceType.MANA) > 150 || rc.getResourceAmount(ResourceType.ADAMANTIUM) > 150) {

            //Build Carrier
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }

            //Build Launcher
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }

        //Otherwise, build anchors!
        else if (rc.canBuildAnchor(Anchor.STANDARD)) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }
    }

    static void debug(RobotController rc){
        rc.setIndicatorString(indicatorString);
    }
}
