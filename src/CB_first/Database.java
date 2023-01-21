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
    static int rotationalADWellsCount = 0;
    static SymmetryCheck[]horizontalADWells = new SymmetryCheck[16];
    static int horizontalADWellsCount = 0;
    static SymmetryCheck[]verticalADWells = new SymmetryCheck[16];

    static int verticalADWellsCount = 0;
    static MapLocation[]localManaWells = new MapLocation[8];
    static MapLocation[]globalManaWells = new MapLocation[8];
    static SymmetryCheck[]rotationalManaWells = new SymmetryCheck[16];

    static int rotationalManaWellsCount = 0;
    static SymmetryCheck[]horizontalManaWells = new SymmetryCheck[16];
    static SymmetryCheck[]verticalManaWells= new SymmetryCheck[16];
    static MapLocation[]unprocessedManaWells = new MapLocation[16];
    static MapLocation[]allyHQs; // HQ locations
    static MapLocation[]globalEnemyHQs;
    static RobotInfo[]localEnemyHQs = new RobotInfo[4];
    static MapLocation[]rotationalEnemyHQs;
    static MapLocation[]horizontalEnemyHQs;
    static MapLocation[]verticalEnemyHQs;
    static boolean[] rotationalHQExplored = new boolean[4];
    static boolean[] horizontalHQExplored = new boolean[4];
    static boolean[] verticalHQExplored = new boolean[4];

    static MapLocation[] uncheckedEnemyHQs = new MapLocation[4];

    static int numUncheckedHQs = 0;

    static int numGlobalEnemyHQs = 0;
    static int numGlobalAD = 0;
    static int numGlobalMana = 0;

    static int numLocalADWells = 0;
    static int numLocalManaWells = 0;
    static int numLocalHQs = 0;

    static int genADSymmetries = 0;
    static int genManaSymmetries = 0;
    static boolean symmetryUpload = false;
    static boolean symmetryFound = false;
    static HashSet<MapLocation> globalKnownLocations;
    static HashSet<MapLocation> localKnownLocations;

    static int width;
    static int height;

    public static void init(RobotController rc) throws GameActionException {
        globalKnownLocations = new HashSet<>();
        localKnownLocations = new HashSet<>();
        allyHQs = Comms.getAllHQs(rc);

        width = rc.getMapWidth();
        height = rc.getMapHeight();

    }
    public static void downloadLocations(RobotController rc) throws GameActionException {
        if(allyHQs.length < Comms.getNumHQs(rc)){
            allyHQs = Comms.getAllHQs(rc);
        }
        int numCommEnemyHQ = Comms.getNumEnemyHQs(rc);
        int numCommAD = Comms.getNumADWells(rc);
        int numCommMana = Comms.getNumManaWells(rc);

        if(numCommEnemyHQ > numGlobalEnemyHQs){
            for(;numGlobalEnemyHQs < numCommEnemyHQ; numGlobalEnemyHQs++){
                globalEnemyHQs[numGlobalEnemyHQs] = Comms.getEnemyHQLocation(rc, numGlobalEnemyHQs);
                globalKnownLocations.add(globalEnemyHQs[numGlobalEnemyHQs] );

                uncheckedEnemyHQs[numUncheckedHQs] = globalEnemyHQs[numGlobalEnemyHQs];
                numUncheckedHQs++;

            }
        }

        if(numCommAD > numGlobalAD){
            for(;numGlobalAD < numCommAD; numGlobalAD++){
                globalADWells[numGlobalAD] = Comms.getADWell(rc, numGlobalAD);
                globalKnownLocations.add(globalADWells[numGlobalAD] );

                unprocessedADWells[genADSymmetries] = globalADWells[numGlobalAD];
                genADSymmetries++;
            }
        }

        if(numCommMana > numGlobalMana){
            for(;numGlobalMana < numCommMana; numGlobalMana++){
                globalManaWells[numGlobalMana] = Comms.getManaWell(rc, numGlobalMana);
                globalKnownLocations.add(globalManaWells[numGlobalMana]);

                unprocessedManaWells[genManaSymmetries] = globalManaWells[numGlobalMana];
                genManaSymmetries++;
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
                if(info.getResourceType().equals(ResourceType.ADAMANTIUM) && numGlobalAD < 8){
                    boolean added = Comms.setADWell(rc, info.getMapLocation());
                    if(added){
                        globalADWells[numGlobalAD] = info.getMapLocation();
                        globalKnownLocations.add(info.getMapLocation());
                        numGlobalAD++;

                        unprocessedADWells[genADSymmetries] = info.getMapLocation();
                        genADSymmetries++;
                    }

                } else if (info.getResourceType().equals(ResourceType.MANA) && numGlobalMana < 8){
                    boolean added = Comms.setManaWell(rc, info.getMapLocation());
                    if(added){
                        globalManaWells[numGlobalMana] = info.getMapLocation();
                        globalKnownLocations.add(info.getMapLocation());
                        numGlobalMana++;

                        unprocessedManaWells[genManaSymmetries] = info.getMapLocation();
                        genManaSymmetries++;
                    }

                }
            } else{

                if(info.getResourceType().equals(ResourceType.ADAMANTIUM) && numLocalADWells < 16){
                    int index = findFirstNullLocation(localADWells);
                    if(index != -1){
                        localADWells[index] = info.getMapLocation();
                        localKnownLocations.add(info.getMapLocation());

                        unprocessedADWells[genADSymmetries] = info.getMapLocation();
                        genADSymmetries++;
                    }

                } else if (info.getResourceType().equals(ResourceType.MANA) && numLocalManaWells < 16){
                    int index = findFirstNullLocation(localManaWells);
                    if(index != -1){
                        localManaWells[index] = info.getMapLocation();
                        localKnownLocations.add(info.getMapLocation());

                        unprocessedManaWells[genManaSymmetries] = info.getMapLocation();
                        genManaSymmetries++;
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

            uncheckedEnemyHQs[numUncheckedHQs] = info.getLocation();
            numUncheckedHQs++;
        }
    }



    public static void processHQSymmetries(){
        rotationalEnemyHQs = new MapLocation[allyHQs.length];
        horizontalEnemyHQs = new MapLocation[allyHQs.length];
        verticalEnemyHQs = new MapLocation[allyHQs.length];
        for(int i = 0; i< allyHQs.length; i++){
            rotationalEnemyHQs[i] = rotate(allyHQs[i]);
            horizontalEnemyHQs[i] = reflectAcrossHorizontal(allyHQs[i]);
            verticalEnemyHQs[i] = reflectAcrossVertical(allyHQs[i]);
        }
    }

    public static void processWellSymmetries(){

        for(int i = genADSymmetries; i>=0; i--){

        }
        for(int i = genManaSymmetries; i>=0; i--){

        }
    }

    public static void checkSymmetries() throws GameActionException{
        if(numUncheckedHQs >0){

        }
        if(rotational){

        }
        if(horizontal){

        }
        if(vertical){

        }

    }

    private static void checkSymmetries(MapLocation[]enemyHQs, MapLocation[]ADWells, MapLocation[]manaWells ){

    }

    private static MapLocation rotate(MapLocation loc) {
        return new MapLocation(width - loc.x - 1, height - loc.y - 1);
    }

    private static MapLocation reflectAcrossVertical(MapLocation loc) {
        int newX = width - loc.x - 1;
        return new MapLocation(newX, loc.y);
    }

    private static MapLocation reflectAcrossHorizontal(MapLocation loc) {
        int newY = height - loc.y - 1;
        return new MapLocation(loc.x, newY);
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
