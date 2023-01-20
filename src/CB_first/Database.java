package CB_first;

import battlecode.common.*;

public class Database {

    static boolean rotational; // available symmetries
    static boolean horizontal;
    static boolean vertical;
    static MapLocation[]localADWells = new MapLocation[8]; // adamantium wells
    static MapLocation[]globalADWells;

    static MapLocation[]unprocessedADWells = new MapLocation[16];
    static SymmetryCheck[]rotationalADWells = new SymmetryCheck[16];
    static SymmetryCheck[]horizontalADWells = new SymmetryCheck[16];
    static SymmetryCheck[]verticalADWells = new SymmetryCheck[16];
    static MapLocation[]localManaWells = new MapLocation[8];
    static MapLocation[]globalManaWells;
    static SymmetryCheck[]rotationalManaWells = new SymmetryCheck[16];
    static SymmetryCheck[]horizontalManaWells = new SymmetryCheck[16];
    static SymmetryCheck[]verticalManaWells= new SymmetryCheck[16];

    static MapLocation[]unprocessedManaWells = new MapLocation[16];
    static MapLocation[]allyHQs; // HQ locations
    static MapLocation[]globalEnemyHQs;
    static MapLocation[]localEnemyHQs = new MapLocation[4];
    static SymmetryCheck[]rotationalEnemyHQs = new SymmetryCheck[4];
    static SymmetryCheck[]horizontalEnemyHQs= new SymmetryCheck[4];
    static SymmetryCheck[]verticalEnemyHQs = new SymmetryCheck[4];
    static boolean[] rotationalHQExplored = new boolean[4];
    static boolean[] horizontalHQExplored = new boolean[4];
    static boolean[] verticalHQExplored = new boolean[4];

    static MapLocation[]unprocessedEnemyHQs = new MapLocation[4];

    static int numGlobalEnemyHQs = 0;
    static int numGlobalAD = 0;
    static int numGlobalMana = 0;

    static int numLocalADWells = 0;
    static int numLocalManaWells = 0;
    static int numLocalHQs = 0;

    static boolean symmetryUpload = false;

    static boolean symmetryFound = false;

    public static void init(RobotController rc) throws GameActionException {
        allyHQs = Comms.getAllHQs(rc);

    }
    public static void downloadLocations(RobotController rc) throws GameActionException {
        int numCommEnemyHQ = Comms.getNumEnemyHQs(rc);
        int numCommAD = Comms.getNumADWells(rc);
        int numCommMana = Comms.getNumManaWells(rc);

        if(numCommEnemyHQ > numGlobalEnemyHQs){
            for(;numGlobalEnemyHQs < numCommEnemyHQ; numGlobalEnemyHQs++){
                globalEnemyHQs[numGlobalEnemyHQs] = Comms.getEnemyHQLocation(rc, numGlobalEnemyHQs);
            }
        }

        if(numCommAD > numGlobalAD){
            for(;numGlobalAD < numCommAD; numGlobalAD++){
                globalADWells[numGlobalAD] = Comms.getADWell(rc, numGlobalAD);
            }
        }

        if(numCommMana > numGlobalMana){
            for(;numGlobalMana < numCommMana; numGlobalMana++){
                globalManaWells[numGlobalMana] = Comms.getManaWell(rc, numGlobalMana);
            }
        }
    }

    public static void downloadSymmetry(RobotController rc) throws GameActionException {
        boolean[]symmetries = Comms.getSymmetries(rc);

        int globalCount = 0;
        if(symmetries[0])globalCount ++;
        if(symmetries[1])globalCount ++;
        if(symmetries[2])globalCount ++;

        int newCount =0;
        rotational =  symmetries[0] && rotational;
        horizontal =  symmetries[1] && horizontal;
        vertical =  symmetries[2] && vertical;

        if(rotational)newCount ++;
        if(horizontal)newCount ++;
        if(vertical)newCount ++;

        if(newCount < globalCount){
            symmetryUpload = true;
        }

        if(newCount ==1 && !symmetryFound){
            symmetryFound = true; // could perform some actions on symmetry found
            onSymmetryFound();
        }
    }

    static void onSymmetryFound(){

    }

    public static void uploadSymmetry(RobotController rc) throws GameActionException {
        if(rc.canWriteSharedArray(0,0) && symmetryUpload) {
            Comms.setSymmetries(rc, rotational, horizontal, vertical);
        }
    }

    public static void uploadLocations()throws GameActionException {
        if(numLocalADWells >0 ){

        }
        if(numLocalManaWells >0){

        }
        if(numLocalHQs >0){

        }


    }

    public static void checkSymmetries() throws GameActionException{

    }

    static class SymmetryCheck{
        public MapLocation location;
        public boolean checked;
        public SymmetryCheck(MapLocation location, boolean checked){
            this.location = location;
            this.checked = checked;
        }
    }



}
