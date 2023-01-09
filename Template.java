package currentpackage;

import battlecode.common.*;

public strictfp class Template {
    public enum SoldierState {

    }

    public enum SoldierAction{

    }
    public enum SoldierMovement{

    }
    static SoldierState currentState = SoldierState.Exploring;
    static SoldierAction selectedAction = SoldierAction.Attack;
    static SoldierMovement selectedMovement = SoldierMovement.Kite;

    static Pathfinder pathfinder;
    static RobotInfo[] enemies;
    static RobotInfo[] allies;
    static RobotController rc;
    static boolean initialized = false;
    static MapLocation target;

    static String indicatorString;

    static boolean moveThenAct;


    public static void run() throws GameActionException {
        if(!initialized){
            onUnitInit(); // first time starting the bot, do some setup
            initialized = true;
        }

        initTurn(); // cleanup for when the turn starts

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
        executeMovementAndAction();

        debug();
        // prints the indicator string

    }

    static void onUnitInit(){

    }

    static void initTurn(){
        indicatorString = "";
    }

    static void readComms(){

    }

    static void sense(){

    }

    static void perEnemy(){
        for(Enemy enemy : enemies){

        }
    }

    static void perAlly(){
        for(Ally ally : allies){

        }
    }

    static void perObjective(){

    }

    static void executeMovementAndAction(){
        if(rc.isMovementReady() && rc.isActionReady()){
            if(moveThenAct){
                move();
                act();
            } else {
                act();
                move();
            }
        } else if(rc.isActionReady()){
            act();
        } else if(rc.isMovementReady()){
            move();
        }

    }

    static void act(){
        switch(selectedAction){
            case Attack:
                rc.attack();

                break;
        }
    }

    static void move(){
        switch(selectedMovement){
            case PathTowardBF:
                bfPathTo(x);
            break;
        }

    }

    static void debug(){
        rc.setIndicatorString(indicatorString);
    }

}
