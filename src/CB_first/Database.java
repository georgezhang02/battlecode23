package CB_first;

import battlecode.common.*;

public class Database {

    static boolean rotational; // available symmetries
    static boolean horizontal;
    static boolean vertical;
    static MapLocation[]localADWells = new MapLocation[8]; // adamantium wells
    static MapLocation[]globalADWells = new MapLocation[8];

    static MapLocation[]unprocessedADWells = new MapLocation[16];
    static SymmetryCheck[]rotationalADWells = new SymmetryCheck[16];
    static SymmetryCheck[]horizontalADWells = new SymmetryCheck[16];
    static SymmetryCheck[]verticalADWells = new SymmetryCheck[16];
    static MapLocation[]localManaWells = new MapLocation[8];
    static MapLocation[]globalManaWells = new MapLocation[8];
    static SymmetryCheck[]rotationalManaWells = new SymmetryCheck[16];
    static SymmetryCheck[]horizontalManaWells = new SymmetryCheck[16];
    static SymmetryCheck[]verticalManaWells= new SymmetryCheck[16];

    static MapLocation[]unprocessedManaWells = new MapLocation[16];
    static MapLocation[]allyHQs; // HQ locations
    static MapLocation[]globalEnemyHQs;
    static RobotInfo[]localEnemyHQs = new RobotInfo[4];
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

    static HashSet<MapLocation> globalKnownLocations;
    static HashSet<MapLocation> localKnownLocations;

    public static void init(RobotController rc) throws GameActionException {
        globalKnownLocations = new HashSet<>();
        localKnownLocations = new HashSet<>();
        allyHQs = Comms.getAllHQs(rc);

    }
    public static void downloadLocations(RobotController rc) throws GameActionException {
        int numCommEnemyHQ = Comms.getNumEnemyHQs(rc);
        int numCommAD = Comms.getNumADWells(rc);
        int numCommMana = Comms.getNumManaWells(rc);

        if(numCommEnemyHQ > numGlobalEnemyHQs){
            for(;numGlobalEnemyHQs < numCommEnemyHQ; numGlobalEnemyHQs++){
                globalEnemyHQs[numGlobalEnemyHQs] = Comms.getEnemyHQLocation(rc, numGlobalEnemyHQs);
                globalKnownLocations.add(globalEnemyHQs[numGlobalEnemyHQs] );
            }
        }

        if(numCommAD > numGlobalAD){
            for(;numGlobalAD < numCommAD; numGlobalAD++){
                globalADWells[numGlobalAD] = Comms.getADWell(rc, numGlobalAD);
                globalKnownLocations.add(globalADWells[numGlobalAD] );
            }
        }

        if(numCommMana > numGlobalMana){
            for(;numGlobalMana < numCommMana; numGlobalMana++){
                globalManaWells[numGlobalMana] = Comms.getManaWell(rc, numGlobalMana);
                globalKnownLocations.add(globalManaWells[numGlobalMana]);
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

    public static void uploadLocations(RobotController rc) throws GameActionException {
        if(numLocalHQs >0){
            for(int i = 0; i < 4; i++){
                if(localEnemyHQs[i]!= null){

                    if(globalKnownLocations.contains(localEnemyHQs[i].getLocation())){
                        localEnemyHQs[i] = null;
                        numLocalHQs--;
                    } else{
                        boolean added = Comms.setEnemyHQLocation(rc, localEnemyHQs[i].getLocation(), localEnemyHQs[i].getID());

                        if(added){
                            globalEnemyHQs[numGlobalEnemyHQs] = localEnemyHQs[i].getLocation();
                            globalKnownLocations.add(localEnemyHQs[i].getLocation());
                            localEnemyHQs[i] = null;
                            numLocalHQs--;
                            numGlobalEnemyHQs++;
                        }
                    }

                }
            }
        }
        if(numLocalADWells >0 ){
            for(int i = 0; i < 8; i++){
                if(localADWells[i]!= null){
                    if(globalKnownLocations.contains(localADWells[i])){
                        localADWells[i] = null;
                        numLocalADWells--;
                    } else{
                        boolean added = Comms.setADWell(rc, localADWells[i]);
                        if(added){
                            globalADWells[numGlobalAD] = localADWells[i];
                            globalKnownLocations.add(localADWells[i]);
                            localADWells[i] = null;
                            numLocalADWells--;
                            numGlobalAD++;
                        }
                    }

                }
            }
        }
        if(numLocalManaWells >0){
            for(int i = 0; i < 8; i++){
                if(localManaWells[i]!= null){
                    if(globalKnownLocations.contains(localManaWells[i])){
                        localManaWells[i] = null;
                        numLocalManaWells--;
                    } else{
                        boolean added = Comms.setManaWell(rc, localManaWells[i]);

                        if(added){
                            globalManaWells[numGlobalMana] = localManaWells[i];
                            globalKnownLocations.add(localManaWells[i]);
                            localManaWells[i] = null;
                            numLocalManaWells--;
                            numGlobalMana++;
                        }
                    }

                }
            }
        }
    }

    public static void addWell(RobotController rc, WellInfo info) throws GameActionException{
        MapLocation loc = info.getMapLocation();
        if(!globalKnownLocations.contains(loc) && !localKnownLocations.contains(loc)){
            if(rc.canWriteSharedArray(0,0)){
                if(info.getResourceType().equals(ResourceType.ADAMANTIUM)){
                    boolean added = Comms.setADWell(rc, info.getMapLocation());
                    if(added){
                        globalADWells[numGlobalAD] = info.getMapLocation();
                        globalKnownLocations.add(info.getMapLocation());
                        numGlobalAD++;
                    }

                } else if (info.getResourceType().equals(ResourceType.MANA)){
                    boolean added = Comms.setManaWell(rc, info.getMapLocation());
                    if(added){
                        globalManaWells[numGlobalMana] = info.getMapLocation();
                        globalKnownLocations.add(info.getMapLocation());
                        numGlobalMana++;
                    }

                }
            } else{

                if(info.getResourceType().equals(ResourceType.ADAMANTIUM)){
                    int index = findFirstNullLocation(localADWells);
                    if(index != -1){
                        localADWells[index] = info.getMapLocation();
                        localKnownLocations.add(info.getMapLocation());
                    }

                } else{
                    int index = findFirstNullLocation(localManaWells);
                    if(index != -1){
                        localManaWells[index] = info.getMapLocation();
                        localKnownLocations.add(info.getMapLocation());
                    }
                }

            }
        }
    }

    public static int findFirstNullLocation(Object[]arr){
        for(int i = 0; i< arr.length; i++){
            if(arr[i] == null){
                return i;
            }
        }
        return -1;

    }

    public static void addEnemyHQ(RobotController rc, RobotInfo info) throws GameActionException{
        MapLocation loc = info.getLocation();
        if(!globalKnownLocations.contains(loc) && !localKnownLocations.contains(loc)){
            if(rc.canWriteSharedArray(0,0)){

                boolean added = Comms.setEnemyHQLocation(rc, info.getLocation(), info.getID());
                if(added){
                    globalEnemyHQs[numGlobalEnemyHQs] = info.getLocation();
                    globalKnownLocations.add(info.getLocation());
                    numGlobalEnemyHQs++;
                }


            } else{
                localKnownLocations.add(info.getLocation());
                int index = findFirstNullLocation(localEnemyHQs);
                if(index != -1){
                    localEnemyHQs[index] = info;
                    localKnownLocations.add(info.getLocation());
                }
            }
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
