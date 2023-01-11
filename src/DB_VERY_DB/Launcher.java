package DB_VERY_DB;

import battlecode.common.*;

import java.awt.*;
import java.util.Random;

public strictfp class Launcher {

    public enum LauncherState {
        Combat, Pursuing, Exploring
    }

    static RobotInfo[] enemies;

    static WellInfo[]wells;
    static boolean initialized = false;

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

        initTurn(rc); // cleanup for when the turn starts

        // interpret overall macro state
        readComms(rc);

        // sense part
        sense(rc);


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

    }

    static void initTurn(RobotController rc) throws GameActionException{

    }

    static void readComms(RobotController rc)throws GameActionException{

    }

    static void sense(RobotController rc) throws GameActionException{
        enemies = rc.senseNearbyRobots(RobotType.LAUNCHER.visionRadiusSquared, rc.getTeam().opponent());
        wells = rc.senseNearbyWells();
        if(enemies.length > 0){
            state = LauncherState.Combat;
        }
    }

    static void combat(RobotController rc) throws GameActionException{

    }

    static void pursue(RobotController rc) throws GameActionException{

    }

    static void explore(RobotController rc) throws GameActionException{

        Direction dir = Helper.directions[Helper.rng.nextInt(Helper.directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

    }




}
