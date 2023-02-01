package FB_VS_old;

import battlecode.common.*;

public class Database {

    static final int BYTECODE_LIMIT = 500;
    static boolean rotational = true; // available symmetries
    static boolean horizontal = true;
    static boolean vertical = true;
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
    static int horizontalManaWellsCount = 0;
    static SymmetryCheck[]verticalManaWells= new SymmetryCheck[16];
    static int verticalManaWellsCount = 0;
    static MapLocation[]unprocessedManaWells = new MapLocation[16];
    public static MapLocation[]allyHQs; // HQ locations
    static MapLocation[]globalEnemyHQs = new MapLocation[4];;
    static RobotInfo[]localEnemyHQs = new RobotInfo[4];
    public static SymmetryCheck[]rotationalEnemyHQs;
    public static SymmetryCheck[]horizontalEnemyHQs;
    public static SymmetryCheck[]verticalEnemyHQs;
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

    public static boolean uploadedAllLocations = false;

    static int globalSymmetryCount = 3;
    static HashSet<MapLocation> globalKnownLocations;
    static HashSet<MapLocation> localKnownLocations;

    private static int width;
    private static int height;

    static boolean initialized = false;

    public static void init(RobotController rc) throws GameActionException {
        if(!initialized){

            rotational = true;
            horizontal = true;
            vertical = true;

            globalKnownLocations = new HashSet<>();
            localKnownLocations = new HashSet<>();
            allyHQs = Comms.getAllHQs(rc);


            width = rc.getMapWidth();
            height = rc.getMapHeight();
            initialized = true;

            processHQSymmetries(rc);

        }

    }

    public static MapLocation[] getKnownADLocations(){
        int wellIndex = 0;
        MapLocation[]wells = new MapLocation[numLocalADWells+numGlobalAD];
        for(int i = 0; i<localADWells.length; i++){
            if(localADWells[i]!= null){
                wells[wellIndex] = localADWells[i];
                wellIndex++;
            }
        }

        for(int i = 0; i<numGlobalAD; i++){
            wells[wellIndex] = globalADWells[i];
            wellIndex++;
        }
        return wells;
    }

    public static MapLocation[] getKnownManaLocations(){
        int wellIndex = 0;
        MapLocation[]wells = new MapLocation[numLocalManaWells+numGlobalMana];
        for(int i = 0; i<localManaWells.length; i++){
            if(localManaWells[i]!= null){
                wells[wellIndex] = localManaWells[i];
                wellIndex++;
            }
        }

        for(int i = 0; i<numGlobalMana; i++){
            wells[wellIndex] = globalManaWells[i];
            wellIndex++;
        }

        return wells;

    }

    public static MapLocation[] getKnownEnemyHQLocations(){
        int index = 0;
        MapLocation[]hqs = new MapLocation[numGlobalEnemyHQs+numLocalHQs];
        for(int i = 0; i<localEnemyHQs.length; i++){
            if(localEnemyHQs[i]!= null){
                hqs[index] = localEnemyHQs[i].location;
                index++;
            }
        }

        for(int i = 0; i< numGlobalEnemyHQs; i++){
            hqs[i] = globalEnemyHQs[i];
            index++;
        }

        return hqs;

    }

    public static void downloadLocations(RobotController rc) throws GameActionException {
        if(allyHQs == null || allyHQs.length < Comms.getNumHQs(rc)){
            allyHQs = Comms.getAllHQs(rc);
            processHQSymmetries(rc);
        }
        int numCommEnemyHQ = Comms.getNumEnemyHQs(rc);
        int numCommAD = Comms.getNumADWells(rc);
        int numCommMana = Comms.getNumManaWells(rc);

        if(numCommEnemyHQ > numGlobalEnemyHQs){
            for(;numGlobalEnemyHQs < numCommEnemyHQ; numGlobalEnemyHQs++){
                globalEnemyHQs[numGlobalEnemyHQs] = Comms.getEnemyHQLocation(rc, numGlobalEnemyHQs);
                globalKnownLocations.add(globalEnemyHQs[numGlobalEnemyHQs] );

                if(!localKnownLocations.contains(globalEnemyHQs[numGlobalEnemyHQs])) {
                    uncheckedEnemyHQs[numUncheckedHQs] = globalEnemyHQs[numGlobalEnemyHQs];
                    numUncheckedHQs++;
                }
            }
        }

        if(numCommAD > numGlobalAD){
            for(;numGlobalAD < numCommAD; numGlobalAD++){
                globalADWells[numGlobalAD] = Comms.getADWell(rc, numGlobalAD);
                globalKnownLocations.add(globalADWells[numGlobalAD] );

                if(!localKnownLocations.contains(globalADWells[numGlobalAD])) {
                    unprocessedADWells[genADSymmetries] = globalADWells[numGlobalAD];
                    genADSymmetries++;
                }
            }
        }

        if(numCommMana > numGlobalMana){
            for(;numGlobalMana < numCommMana; numGlobalMana++){
                globalManaWells[numGlobalMana] = Comms.getManaWell(rc, numGlobalMana);
                globalKnownLocations.add(globalManaWells[numGlobalMana]);

                if(!localKnownLocations.contains(globalManaWells[numGlobalMana])){
                    unprocessedManaWells[genManaSymmetries] = globalManaWells[numGlobalMana];
                    genManaSymmetries++;
                }

            }
        }
    }

    public static void downloadSymmetry(RobotController rc) throws GameActionException {
        boolean[]symmetries = Comms.getSymmetries(rc);

        if(symmetries[0])globalSymmetryCount ++;
        if(symmetries[1])globalSymmetryCount ++;
        if(symmetries[2])globalSymmetryCount ++;

        rotational =  symmetries[0] && rotational;
        horizontal =  symmetries[1] && horizontal;
        vertical =  symmetries[2] && vertical;

        checkSymmetryFound();
    }

    static void checkSymmetryFound(){
        int newCount = 0;
        if(rotational)newCount ++;
        if(horizontal)newCount ++;
        if(vertical)newCount ++;

        if(newCount < globalSymmetryCount && newCount>0){
            symmetryUpload = true;
        }
        if(newCount ==1 && !symmetryFound) {
            symmetryFound = true; // could perform some actions on symmetry found
        }
    }

    public static void uploadSymmetry(RobotController rc) throws GameActionException {
        if(symmetryUpload && rc.canWriteSharedArray(0,0) && Clock.getBytecodesLeft() > BYTECODE_LIMIT) {
            Comms.setSymmetries(rc, rotational, horizontal, vertical);
            symmetryUpload = false;
        }
    }

    public static void uploadLocations(RobotController rc) throws GameActionException {
        if(numLocalHQs >0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            for(int i = 0; i < 4 && Clock.getBytecodesLeft() > BYTECODE_LIMIT; i++){
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
        if(numLocalADWells >0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            for(int i = 0; i < 8   && Clock.getBytecodesLeft() >BYTECODE_LIMIT; i++){
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
        if(numLocalManaWells >0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            for(int i = 0; i < 8  && Clock.getBytecodesLeft() >BYTECODE_LIMIT; i++){
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
        if(Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            uploadedAllLocations = true;
        }
    }

    public static void addWell(RobotController rc, WellInfo info) throws GameActionException{
        if(Clock.getBytecodesLeft() >BYTECODE_LIMIT){
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

                    if(info.getResourceType().equals(ResourceType.ADAMANTIUM) && numLocalADWells < 8){
                        int index = findFirstNullLocation(localADWells);
                        if(index != -1){
                            localADWells[index] = info.getMapLocation();
                            localKnownLocations.add(info.getMapLocation());
                            numLocalADWells++;

                            unprocessedADWells[genADSymmetries] = info.getMapLocation();
                            genADSymmetries++;

                            uploadedAllLocations = false;

                        }

                    } else if (info.getResourceType().equals(ResourceType.MANA) && numLocalManaWells < 8){
                        int index = findFirstNullLocation(localManaWells);
                        if(index != -1){
                            localManaWells[index] = info.getMapLocation();
                            localKnownLocations.add(info.getMapLocation());
                            numLocalManaWells++;

                            uploadedAllLocations = false;

                            unprocessedManaWells[genManaSymmetries] = info.getMapLocation();
                            genManaSymmetries++;
                        }
                    }

                }
            }
        }

    }

    private static int findFirstNullLocation(Object[]arr){
        for(int i = 0; i< arr.length; i++){
            if(arr[i] == null){
                return i;
            }
        }
        return -1;

    }

    public static void addEnemyHQ(RobotController rc, RobotInfo info) throws GameActionException{

        if(Clock.getBytecodesLeft() >BYTECODE_LIMIT) {
            MapLocation loc = info.getLocation();
            if (!globalKnownLocations.contains(loc) && !localKnownLocations.contains(loc)) {
                if (rc.canWriteSharedArray(0, 0)) {
                    boolean added = Comms.setEnemyHQLocation(rc, info.getLocation(), info.getID());
                    if (added) {
                        globalEnemyHQs[numGlobalEnemyHQs] = info.getLocation();
                        globalKnownLocations.add(info.getLocation());
                        numGlobalEnemyHQs++;
                    }
                } else {
                    int index = findFirstNullLocation(localEnemyHQs);
                    if (index != -1) {
                        localEnemyHQs[index] = info;
                        localKnownLocations.add(info.getLocation());
                        numLocalHQs++;

                        uploadedAllLocations = false;
                    }
                }
                uncheckedEnemyHQs[numUncheckedHQs] = info.getLocation();
                numUncheckedHQs++;
            }
        }
    }
    public static void processHQSymmetries(RobotController rc){ // checked
        rotationalEnemyHQs = new SymmetryCheck[allyHQs.length];
        horizontalEnemyHQs = new SymmetryCheck[allyHQs.length];
        verticalEnemyHQs = new SymmetryCheck[allyHQs.length];
        for(int i = 0; i< allyHQs.length; i++){
            MapLocation rotation = rotate(allyHQs[i]);
            rotationalEnemyHQs[i] = new SymmetryCheck(rotation);

            MapLocation horizontalReflection = reflectAcrossHorizontal(allyHQs[i]);
            horizontalEnemyHQs[i] = new SymmetryCheck(horizontalReflection);

            MapLocation verticalReflection = reflectAcrossVertical(allyHQs[i]);
            verticalEnemyHQs[i] = new SymmetryCheck(verticalReflection);

        }
    }

    public static void processWellSymmetries(){

        for(; genADSymmetries>0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT; genADSymmetries--){
            rotationalADWells[rotationalADWellsCount] = new SymmetryCheck(rotate(unprocessedADWells[genADSymmetries-1]));
            rotationalADWellsCount++;

            horizontalADWells[horizontalADWellsCount] = new SymmetryCheck(reflectAcrossHorizontal(unprocessedADWells[genADSymmetries-1]));
            horizontalADWellsCount++;

            verticalADWells[verticalADWellsCount] = new SymmetryCheck(reflectAcrossVertical(unprocessedADWells[genADSymmetries-1]));
            verticalADWellsCount++;
        }

        for(; genManaSymmetries>0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT; genManaSymmetries--){
            rotationalManaWells[rotationalManaWellsCount] = new SymmetryCheck(rotate(unprocessedManaWells[genManaSymmetries-1]));
            rotationalManaWellsCount++;

            horizontalManaWells[horizontalManaWellsCount] = new SymmetryCheck(reflectAcrossHorizontal(unprocessedManaWells[genManaSymmetries-1]));
            horizontalManaWellsCount++;

            verticalManaWells[verticalManaWellsCount] = new SymmetryCheck(reflectAcrossVertical(unprocessedManaWells[genManaSymmetries-1]));
            verticalManaWellsCount++;
        }
    }

    public static void checkSymmetries(RobotController rc) throws GameActionException{
        // if you've found unchecked Enemy HQs, run them through all other known HQs to make sure they fit
        if(rc.getRoundNum() <= 1){
            return;
        }
        searchHQAllowed(rc);
        checkSymmetryFound();
        if(!symmetryFound && rotational && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            rotational = checkHQSymmetry(rc, rotationalEnemyHQs);
        }
        if(!symmetryFound && horizontal && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            horizontal = checkHQSymmetry(rc, horizontalEnemyHQs);
        }
        if(!symmetryFound && vertical && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            vertical = checkHQSymmetry(rc, verticalEnemyHQs);
        } // do your enemy HQ checks first*/

        processWellSymmetries();
        if(!symmetryFound && rotational && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            rotational = checkWellSymmetry(rc, rotationalADWells, rotationalManaWells);
        }
        if(!symmetryFound && horizontal && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            horizontal = checkWellSymmetry(rc, horizontalADWells, horizontalManaWells);
        }
        if(!symmetryFound && vertical && Clock.getBytecodesLeft() >BYTECODE_LIMIT){
            vertical = checkWellSymmetry(rc, verticalADWells, verticalManaWells);
        }
        checkSymmetryFound();
        uploadSymmetry(rc);


    }



    public static void searchHQAllowed(RobotController rc){

        if(!symmetryFound && numUncheckedHQs >0 && Clock.getBytecodesLeft()>BYTECODE_LIMIT
                && rc.getRoundNum() >1){
            for(;numUncheckedHQs>0 && Clock.getBytecodesLeft() >BYTECODE_LIMIT; numUncheckedHQs--){
                MapLocation loc = uncheckedEnemyHQs[numUncheckedHQs-1];
                if(rotational && rotationalEnemyHQs.length >0){
                    rotational = false;
                    for(int i = 0; i<rotationalEnemyHQs.length; i++){
                        if(rotationalEnemyHQs[i] != null &&rotationalEnemyHQs[i].location.equals(loc)){
                            rotational = true;
                            break;
                        }
                    }
                }
                if(horizontal && horizontalEnemyHQs.length >0){
                    horizontal = false;
                    for(int i = 0; i<horizontalEnemyHQs.length; i++){
                        if(horizontalEnemyHQs[i] != null && horizontalEnemyHQs[i].location.equals(loc)){
                            horizontal = true;
                            break;
                        }
                    }
                }
                if(vertical  && verticalEnemyHQs.length >0){
                    vertical = false;
                    for(int i = 0; i<verticalEnemyHQs.length; i++){
                        if(verticalEnemyHQs[i] != null && verticalEnemyHQs[i].location.equals(loc)){
                            vertical = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    static boolean checkHQSymmetry(RobotController rc, SymmetryCheck[]enemyHQs) throws GameActionException {


        if(rc.getRoundNum() >1) {

            for (int i = 0; i < enemyHQs.length && Clock.getBytecodesLeft() > BYTECODE_LIMIT; i++) {
                if (enemyHQs[i] == null) {
                    break;
                }

                if (!enemyHQs[i].checked) {
                    if (rc.canSenseLocation(enemyHQs[i].location)) {


                        RobotInfo info = rc.senseRobotAtLocation(enemyHQs[i].location);
                        if (info == null || !(info.getType().equals(RobotType.HEADQUARTERS) &&
                                info.getTeam().equals(rc.getTeam().opponent()))) {

                            return false;
                        } else {
                            enemyHQs[i].checked = true;
                        }
                    }
                }
            }
        }
        return true;
    }


    static boolean checkWellSymmetry(RobotController rc, SymmetryCheck[]adWells, SymmetryCheck[]manaWells) throws GameActionException {
        for(int i = 0; i<adWells.length && Clock.getBytecodesLeft() >BYTECODE_LIMIT; i++){
            if(adWells[i] == null){
                break;
            }
            if(!adWells[i].checked){
                if(rc.canSenseLocation(adWells[i].location)){
                    WellInfo info = rc.senseWell(adWells[i].location);
                    if(info == null ||
                            !(info.getResourceType().equals(ResourceType.ADAMANTIUM) || info.getResourceType().equals(ResourceType.ELIXIR)) ){
                        return false;
                    }
                    else{
                        adWells[i].checked = true;
                    }
                }
            }
        }

        for(int i = 0; i<manaWells.length && Clock.getBytecodesLeft() >BYTECODE_LIMIT; i++){
            if(manaWells[i] == null){
                break;
            }
            if(manaWells[i].checked){
                if(rc.canSenseLocation(manaWells[i].location)){
                    WellInfo info = rc.senseWell(manaWells[i].location);
                    if(info == null ||
                            !(info.getResourceType().equals(ResourceType.MANA) || info.getResourceType().equals(ResourceType.ELIXIR)) ){
                        return false;
                    }
                    else{
                        manaWells[i].checked = true;
                    }
                }
            }
        }
        return true;
    }

    public static MapLocation rotate(MapLocation loc) {
        return new MapLocation(width - loc.x - 1, height - loc.y - 1);
    }

    public static MapLocation reflectAcrossVertical(MapLocation loc) {
        int newX = width - loc.x - 1;
        return new MapLocation(newX, loc.y);
    }

    public static MapLocation reflectAcrossHorizontal(MapLocation loc) {
        int newY = height - loc.y - 1;
        return new MapLocation(loc.x, newY);
    }

    static class SymmetryCheck{
        public MapLocation location;
        public boolean checked;
        public SymmetryCheck(MapLocation location){
            this.location = location;
            this.checked = false;
        }
    }



}
