package FB_carriers2;

import battlecode.common.*;

import java.util.Objects;

public class Comms {
    private static final int COUNT_OFFSET_1 = 0;
    private static final int COUNT_OFFSET_2 = 1;
    private static final int COUNT_OFFSET_3 = 2;
    private static final int COUNT_OFFSET_4 = 3;
    private static final int ALLY_HQ_OFFSET = 6;
    private static final int ENEMY_HQ_OFFSET = 10;

    private static final int HQ_COMM_OFFSET = 14;
    private static final int WELL_COMM_OFFSET = 18;
    private static final int AD_WELL_OFFSET = 20;
    private static final int MANA_WELL_OFFSET = 28;
    //private static final int WELL_REPORT_OFFSET = 28;
    private static final int ISLAND_OFFSET = 36;
    private static final int ISLAND_REPORT_OFFSET = 44;
    private static final int ANCHOR_OFFSET_EVEN = 48;

    private static final int ANCHOR_OFFSET_ODD = 50;
    private static final int ATTACK_OFFSET_EVEN= 52;
    private static final int ATTACK_OFFSET_ODD = 58;

    private static final int ALLY_HQ_MAXCOUNT = 4;
    private static final int ENEMY_HQ_MAXCOUNT = 4;
    private static final int WELL_COMM_MAXCOUNT = 2;
    private static final int WELL_MAXCOUNT = 8;
    //private static final int WELL_REP_MAXCOUNT = 4;
    private static final int ISLAND_MAXCOUNT = 8;
    private static final int ISLAND_REP_MAXCOUNT = 4;
    private static final int ANCHOR_MAXCOUNT = 2;
    private static final int ATTACK_MAXCOUNT = 6;

    // Team Count
    /**
     *
     * @return the number of HQs
     */
    public static int getNumHQs(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_1), 0);
    }

    /**
     *
     * @return Number of odd attack commands
     */
    public static int getNumACEven(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_1), 1);
    }


    /**
     *
     * @return Number of odd attack commands
     */
    public static int getNumACOdd(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_1), 2);
    }


    /**
     *
     * @return Number of [HQs, Odd ACs, Even ACs]
     */
    public static int[] getAllCount1(RobotController rc) throws GameActionException {
        int val = rc.readSharedArray(COUNT_OFFSET_1);
        int HQCount = decode(val, 0);
        int ACOddCount = decode(val, 1);
        int ACEvenCount = decode(val, 2);
        return new int[]{HQCount, ACOddCount, ACEvenCount};
    }

    /**
     * @return Number of Well Commands
     */
    public static int getNumWellComms(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_2), 0);
    }

    /**
     * @return Number of Permanent Wells
     */
    public static int getNumADWells(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_2), 1);
    }

    /**
     * @return Number of Well Reports
     */
    public static int getNumManaWells(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_2), 2);
    }

    /**
     * @return Number of [HQ Comms, Permanent Wells, Well Reports]
     */
    public static int[] getAllCount2(RobotController rc) throws GameActionException {
        int val = rc.readSharedArray(COUNT_OFFSET_2);
        int WellCommCount = decode(val, 0);
        int PermWellCount = decode(val, 1);
        int WellRepCount = decode(val, 2);
        return new int[]{WellCommCount, PermWellCount, WellRepCount};
    }
    /**
     * @return Number of Enemy HQs
     */
    public static int getNumEnemyHQs(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_3), 0);
    }

    /**
     * @return Number of Islands in comms
     */
    public static int getNumIslands(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_3), 1);
    }

    /**
     * @return Number of Island Reports
     */
    public static int getNumIslandReps(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_3), 2);
    }

    /**
     * @return Number of [Enemy HQ, Team Island, Island Report]
     */
    public static int[] getAllCount3(RobotController rc) throws GameActionException {
        int val = rc.readSharedArray(COUNT_OFFSET_3);
        int HQCount = decode(val, 0);
        int IslandCount = decode(val, 1);
        int IslandRepCount = decode(val, 2);
        return new int[]{HQCount, IslandCount, IslandRepCount};
    }

    /**
     * @return Number of Enemy HQs Found
     */
    public static int getNumAnchorEven(RobotController rc) throws GameActionException{
        return decode(rc.readSharedArray(COUNT_OFFSET_4), 0);
    }

    public static int getNumAnchorOdd(RobotController rc) throws GameActionException{
        return decode(rc.readSharedArray(COUNT_OFFSET_4), 1);
    }


    /**
     * @return Symmetries found and viable symmetries
     * symmetries are in order of [symmetryFound, rotational, horizontal, vertical]
     */
    public static boolean[] getSymmetries(RobotController rc) throws GameActionException{
        int val = decode(rc.readSharedArray(COUNT_OFFSET_4), 2);
        boolean rotational = (val%8)/4 == 0;
        boolean horizontal = (val%4)/2 == 0;
        boolean vertical = val%2 == 0;
        return new boolean[]{rotational, horizontal, vertical};
    }
    public static void setSymmetries(RobotController rc, boolean rotational,
                                     boolean horizontal, boolean vertical) throws GameActionException{
        int[]count4 = getAllCount4(rc);
        int val = count4[2];
        int bits = (val/8) *8; // extract commscleaned
        if(!rotational) bits += 4;
        if(!horizontal) bits += 2;
        if(!vertical) bits += 1;

        rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], count4[1], bits));
    }

    /**
     * @return If the array was wiped on this turn
     */
    public static boolean isCommsCleaned(RobotController rc) throws GameActionException{
        int val = decode(rc.readSharedArray(COUNT_OFFSET_4), 2);
        return (val/8) == (rc.getRoundNum() % 2);
    }


    public static void setCommsCleaned(RobotController rc) throws GameActionException{
        int[]count4 = getAllCount4(rc);
        int val = count4[2];
        val = (val %8) + (rc.getRoundNum() % 2) * 8;
        rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], count4[1], val));
    }

    /**
     * @return The number of [Anchor command even, Anchor command odd, and bits for symmetry and tracking array clean]
     */
    public static int[] getAllCount4(RobotController rc) throws GameActionException {
        int val = rc.readSharedArray(COUNT_OFFSET_4);
        int AnchorCountEven = decode(val, 0);
        int AnchorCountOdd  = decode(val, 1);
        int bits = decode(val, 2);
        return new int[]{AnchorCountEven, AnchorCountOdd, bits};
    }


    // HQ
    /**
     * Sets the team HQ location in the next available location
     * @return the index corresponding to the HQ
     */
    public static int setTeamHQLocation(RobotController rc, MapLocation HQLocation, int id) throws GameActionException {
        int[] count = getAllCount1(rc);
        rc.writeSharedArray(count[0] + ALLY_HQ_OFFSET, encode(HQLocation.x, HQLocation.y, id));
        rc.writeSharedArray(COUNT_OFFSET_1, encode(count[0] + 1, count[1], count[2]));
        return count[0];
    }

    public static int getHQIndexByID(RobotController rc, int HQID) throws GameActionException {
        for (int i = 0; i < getNumHQs(rc); i++) {
            int id = decode(rc.readSharedArray(i + ALLY_HQ_OFFSET), 2);
            if (HQID == id) {
                return i;
            }
        }
        return -1;
    }

    public static int getHQIndexByLocation(RobotController rc, MapLocation location) throws GameActionException {
        for (int i = 0; i < getNumHQs(rc); i++) {
            int x = decode(rc.readSharedArray(i + ALLY_HQ_OFFSET), 0);
            int y = decode(rc.readSharedArray(i + ALLY_HQ_OFFSET), 1);
            if (location.x == x && location.y == y) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the location of the closest team HQ location
     */
    public static MapLocation getTeamHQLocation(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + ALLY_HQ_OFFSET);
        int x = decode(value, 0);
        int y = decode(value, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation[] getAllHQs(RobotController rc) throws GameActionException {
        int count = getNumHQs(rc);
        MapLocation[] allHQs = new MapLocation[count];
        for (int i = 0; i < count; i ++) {
            allHQs[i] = getTeamHQLocation(rc, i);
        }
        return allHQs;
    }

    public static MapLocation getClosestTeamHQLocation(RobotController rc, MapLocation current) throws GameActionException {
        int lowest_dist = 10000;
        int numHQs = getNumHQs(rc);
        MapLocation closest = null;
        for (int i = 0; i < numHQs; i++) {
            int value = rc.readSharedArray(i + ALLY_HQ_OFFSET);
            int x = decode(value, 0);
            int y = decode(value, 1);
            int distance = current.distanceSquaredTo(new MapLocation(x, y));
            if (distance < lowest_dist) {
                lowest_dist = distance;
                closest = new MapLocation(x, y);
            }
        }
        return closest;
    }

    public static boolean setEnemyHQLocation(RobotController rc, MapLocation HQLocation, int id) throws GameActionException {
        int[] count3 = getAllCount3(rc);
        int enemyHQCount = count3[0];

        if(enemyHQCount < ENEMY_HQ_MAXCOUNT){
            for(int i = 0; i<enemyHQCount; i++){
                if(getEnemyHQLocation(rc, i).equals(HQLocation)){
                    return false;
                }
            }
            rc.writeSharedArray(enemyHQCount + ENEMY_HQ_OFFSET, encode(HQLocation.x, HQLocation.y, id));
            rc.writeSharedArray(COUNT_OFFSET_3, encode(enemyHQCount+1, count3[1], count3[2]));
            return true;
        }
        return false;

    }

    public static MapLocation getEnemyHQLocation(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + ENEMY_HQ_OFFSET);
        int x = decode(value, 0);
        int y = decode(value, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation[] getAllEnemyHQs(RobotController rc) throws GameActionException {
        int count = getNumEnemyHQs(rc);
        MapLocation[] allEnemyHQs = new MapLocation[count];
        for (int i = 0; i < count; i++) {
            allEnemyHQs[i] = getEnemyHQLocation(rc, i);
        }
        return allEnemyHQs;
    }

    public static Command getHQCommand(RobotController rc, int HQIndex) throws GameActionException {
        int value = rc.readSharedArray(HQ_COMM_OFFSET + HQIndex);
        int x = decode(value, 0);
        int y = decode(value, 1);
        int num = decode(value, 2);

        return new Command(new MapLocation(x, y), num);
    }
    public static void writeHQCommand(RobotController rc, int HQIndex, MapLocation loc, int num) throws GameActionException {
        rc.writeSharedArray(HQIndex + HQ_COMM_OFFSET, encode(loc.x, loc.y, num));
    }
    public static void clearHQCommand(RobotController rc, int HQIndex) throws GameActionException {
        rc.writeSharedArray(HQIndex + HQ_COMM_OFFSET, encode(0, 0, 0));
    }
    public static boolean setWellCommand(RobotController rc, MapLocation loc, ResourceType type) throws GameActionException {
        int[] count2 = getAllCount2(rc);
        int wellCommCount = count2[0];

        if(wellCommCount < WELL_COMM_MAXCOUNT){
            for(int i = 0; i<wellCommCount; i++){
                if(getWellCommand(rc, i).location.equals(loc)){
                    return false;
                }
            }
            rc.writeSharedArray(wellCommCount+ WELL_COMM_OFFSET,
                    encode(loc.x, loc.y, type.resourceID));
            rc.writeSharedArray(COUNT_OFFSET_2, encode(wellCommCount+1, count2[1], count2[2]));
            return true;
        }
        return false;
    }

    public static Well getWellCommand(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(WELL_COMM_OFFSET+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        ResourceType type = ResourceType.values()[decode(val, 2)];
        return new Well(new MapLocation(x, y), type);
    }

    /**
     * Returns the location of the closest team HQ location
     */
    public static Well[] getAllWellCommands(RobotController rc) throws GameActionException {
        int count = getNumWellComms(rc);
        Well[] allComms = new Well[count];
        for (int i = 0; i < count; i ++) {
            allComms[i] = getWellCommand(rc, i);
        }
        return allComms;
    }

    /**
     * NEED TO IMPLEMENT; check getWellPerm and getAllWellPerm
     */
    public static boolean setADWell(RobotController rc, MapLocation loc) throws GameActionException {
        int[] count2 = getAllCount2(rc);
        int adWellCount = count2[1];

        if(adWellCount < WELL_MAXCOUNT){
            for(int i = 0; i<adWellCount; i++){
                if(getADWell(rc, i).equals(loc)){
                    return false;
                }
            }
            rc.writeSharedArray(adWellCount+ AD_WELL_OFFSET,
                    encode(loc.x, loc.y, 0));
            rc.writeSharedArray(COUNT_OFFSET_2, encode(count2[0], adWellCount+1, count2[2]));
            return true;
        }
        return false;
    }
    public static MapLocation getADWell(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(AD_WELL_OFFSET +index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new MapLocation(x, y);
    }
    public static MapLocation[] getAllADWells(RobotController rc) throws GameActionException{
        int num = getNumADWells(rc);
        MapLocation[] allWells= new MapLocation[num];
        for (int i = 0; i < num; i ++) {
            allWells[i] = getADWell(rc, i);
        }
        return allWells;
    }


    public static boolean setManaWell(RobotController rc, MapLocation loc) throws GameActionException {
        int[] count2 = getAllCount2(rc);
        int manaWellCount = count2[2];

        if(manaWellCount < WELL_MAXCOUNT){
            for(int i = 0; i<manaWellCount; i++){
                if(getManaWell(rc, i).equals(loc)){
                    return false;
                }
            }
            rc.writeSharedArray(manaWellCount+MANA_WELL_OFFSET,
                    encode(loc.x, loc.y, 0));
            rc.writeSharedArray(COUNT_OFFSET_2, encode(count2[0], count2[1], manaWellCount+1));
            return true;
        }
        return false;
    }
    public static MapLocation getManaWell(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(MANA_WELL_OFFSET +index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        ResourceType type = ResourceType.values()[decode(val, 2)];
        return new MapLocation(x, y);
    }
    public static MapLocation[] getAllManaWells(RobotController rc) throws GameActionException{
        int num = getNumManaWells(rc);
        MapLocation[] allWells= new MapLocation[num];
        for (int i = 0; i < num; i ++) {
            allWells[i] = getManaWell(rc, i);
        }
        return allWells;
    }
   /* public static boolean reportWellLocation(RobotController rc, int index, WellInfo well) throws GameActionException {
        int[] count2 = getAllCount2(rc);
        int repCount = getNumWellsRep(rc);
        if (repCount < WELL_REP_MAXCOUNT) {
            for(int i = 0; i<repCount; i++){
                if(readWellReport(rc, i).location.equals(well.getMapLocation())){
                    return false;
                }
            }
            MapLocation loc = well.getMapLocation();
            rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(loc.x, loc.y, well.getResourceType().resourceID));
            rc.writeSharedArray(COUNT_OFFSET_2, encode(count2[0], count2[1], repCount+1));
            return true;
        }
        return false;
    }

    public static Well readWellReport(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_REPORT_OFFSET);
        int type = decode(value, 2);
        Well report = new Well(new MapLocation(decode(value, 0),  decode(value, 1)),
                ResourceType.values()[type]);

        return report;
    }

    public static Well[] getAllWellReports(RobotController rc) throws GameActionException {
        int count = getNumWellsRep(rc);
        Well[] allWells = new Well[count];
        for (int i = 0; i < count; i ++) {
            allWells[i] = readWellReport(rc, i);
        }
        return allWells;
    }
    public static void clearWellReport(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(0, 0, 0));
    }*/

    public static Island getIsland(RobotController rc, int index) throws GameActionException{
        int val = rc.readSharedArray(ISLAND_OFFSET+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new Island(new MapLocation(x, y));
    }

    public static Island[] getAllIslands(RobotController rc) throws GameActionException{
        int islandCount = getNumIslands(rc);
        Island[] islands = new Island[islandCount];
        for (int i = 0; i < islandCount; i ++) {
            islands[i] = getIsland(rc, i);
        }
        return islands;
    }

    /**
     * Pass in null for team if no team currently owns it
     * @param rc
     * @param loc
     * @param team
     * @throws GameActionException
     */
    public static void setIsland(RobotController rc, MapLocation loc, Team team) throws GameActionException {
        int[] count3 = getAllCount3(rc);
        int islandCount = count3[1];


        int teamVal;
        if (team == null){
            teamVal = 0;
        }
        else if(team.equals(rc.getTeam())){
            teamVal = 1;
        } else {
            teamVal = 2;
        }

        if(islandCount < ISLAND_MAXCOUNT){
            for(int i = 0; i<islandCount; i++){
                if(getIsland(rc, i).location.distanceSquaredTo(loc) <=8){
                    return;
                }
            }

            rc.writeSharedArray(islandCount+ ISLAND_OFFSET,
                    encode(loc.x, loc.y, teamVal));
            rc.writeSharedArray(COUNT_OFFSET_3, encode(count3[0], islandCount +1, count3[2]));
        }
    }
    public static void clearIslandReport(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + ISLAND_REPORT_OFFSET, encode(0, 0, 0));
    }

    public static Island readIslandReport(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + ISLAND_REPORT_OFFSET);
        int team = decode(value, 2);

        Team owner = null;

        switch(team)
        {
            case 1:
                owner = rc.getTeam();
                break;
            case 2:
                owner = rc.getTeam().opponent();
                break;
            default:
                break;
        }

        return new Island(new MapLocation(decode(value, 0),  decode(value, 1)), owner);
    }

    public static Island[] getAllIslandReports(RobotController rc) throws GameActionException {
        int count = getNumIslandReps(rc);
        Island[] allReps = new Island[count];
        for (int i = 0; i < count; i ++) {
            allReps[i] = readIslandReport(rc, i);
        }
        return allReps;
    }

    /**
     * Pass in null for team if no team currently owns it
     * @param rc
     * @param loc
     * @param owner
     * @throws GameActionException
     */
    public static boolean reportIslandLocation(RobotController rc, MapLocation loc, Team owner) throws GameActionException{
        int[] count3 = getAllCount3(rc);
        int repCount = getNumIslandReps(rc);
        if (repCount < ISLAND_REP_MAXCOUNT) {
            for(int i = 0; i<repCount; i++){
                if(readIslandReport(rc, i).location.distanceSquaredTo(loc) <= 8){
                    return false;
                }
            }

            int teamVal;
            if (owner == null){
                teamVal = 0;
            }
            else if(owner.equals(rc.getTeam())){
                teamVal = 1;
            } else {
                teamVal = 2;
            }

            rc.writeSharedArray(repCount + ISLAND_REPORT_OFFSET, encode(loc.x, loc.y, teamVal));
            rc.writeSharedArray(COUNT_OFFSET_3, encode(count3[0], count3[1], repCount+1));
            return true;
        }
        return false;
    }

    public static MapLocation getAnchorCommandEven(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ANCHOR_OFFSET_EVEN+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation getAnchorCommandOdd(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ANCHOR_OFFSET_ODD+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation[] getAllAnchorCommands(RobotController rc) throws GameActionException{
        MapLocation[] anchorLocations;
        if(readEven(rc)){
            int numAnchorCommands = getNumAnchorEven(rc);
            anchorLocations = new MapLocation[numAnchorCommands];
            for(int i = 0; i < numAnchorCommands; i++){
                anchorLocations[i] = getAnchorCommandEven(rc, i);
            }
        } else{
            int numAnchorCommands = getNumAnchorOdd(rc);
            anchorLocations = new MapLocation[numAnchorCommands];
            for(int i = 0; i < numAnchorCommands; i++){
                anchorLocations[i] = getAnchorCommandOdd(rc, i);
            }
        }

        return anchorLocations;
    }

    public static boolean setAnchorCommand(RobotController rc, MapLocation loc) throws GameActionException {
        if(writeEven(rc)){
            // write evens
            int[] count4 = getAllCount4(rc);
            int anchorCount = count4[0];
            if (anchorCount < ANCHOR_MAXCOUNT) {
                for(int i = 0; i<anchorCount; i++){
                    if(getAnchorCommandEven(rc, i).distanceSquaredTo(loc) <=8){
                        return false;
                    }
                }
                rc.writeSharedArray(anchorCount + ANCHOR_OFFSET_EVEN, encode(loc.x, loc.y, 0));
                rc.writeSharedArray(COUNT_OFFSET_4, encode(anchorCount+1, count4[1], count4[2]));
                return true;
            }
            return false;

        } else{
            //write odds
            int[] count4 = getAllCount4(rc);
            int anchorCount = count4[1];
            if (anchorCount < ANCHOR_MAXCOUNT) {
                for(int i = 0; i<anchorCount; i++){
                    if(getAnchorCommandOdd(rc, i).distanceSquaredTo(loc) <=8){
                        return false;
                    }
                }
                rc.writeSharedArray(anchorCount + ANCHOR_OFFSET_ODD, encode(loc.x, loc.y, 0));
                rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], anchorCount+1, count4[2]));
                return true;
            }
            return false;
        }
    }



    public static Attack getAttackCommandEven(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ATTACK_OFFSET_EVEN+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        int type = decode(val, 2);
        return new Attack(new MapLocation(x, y), RobotType.values()[type]);
    }

    public static Attack getAttackCommandOdd(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ATTACK_OFFSET_ODD+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        int type = decode(val, 2);
        return new Attack(new MapLocation(x, y), RobotType.values()[type]);
    }

    public static Attack[] getAllAttackCommands(RobotController rc) throws GameActionException {
        Attack[] attackCommands;
        if(readEven(rc)){
            int numAttackCommands = getNumACEven(rc);
            attackCommands = new Attack[numAttackCommands];
            for(int i = 0; i < numAttackCommands; i++){
                attackCommands[i] = getAttackCommandEven(rc, i);
            }
        } else{
            int numAttackCommands = getNumACOdd(rc);
            attackCommands = new Attack[numAttackCommands];
            for(int i = 0; i < numAttackCommands; i++){
                attackCommands[i] = getAttackCommandOdd(rc, i);
            }
        }

        return attackCommands;
    }

    public static boolean setAttackCommand(RobotController rc, MapLocation loc, RobotType type) throws GameActionException {
        if(writeEven(rc)){
            // write evens
            int[] count1 = getAllCount1(rc);
            int attackCount = count1[1];
            if (attackCount < ATTACK_MAXCOUNT) {
                for(int i = 0; i<attackCount; i++){
                    Attack attack = getAttackCommandEven(rc, i);
                    if(attack.location.distanceSquaredTo(loc) <=8
                            && attack.type.equals(type)){
                        return false;
                    }
                }
                rc.writeSharedArray(attackCount + ATTACK_OFFSET_EVEN, encode(loc.x, loc.y, type.ordinal()));
                rc.writeSharedArray(COUNT_OFFSET_1, encode(count1[0], attackCount+1, count1[2]));
                return true;
            }
            return false;

        } else{
            //write odds
            int[] count1 = getAllCount1(rc);
            int attackCount = count1[2];
            if (attackCount < ATTACK_MAXCOUNT) {
                for(int i = 0; i<attackCount; i++){
                    Attack attack = getAttackCommandOdd(rc, i);
                    if(attack.location.distanceSquaredTo(loc) <=8
                            && attack.type.equals(type)){
                        return false;
                    }
                }
                rc.writeSharedArray(attackCount + ATTACK_OFFSET_ODD, encode(loc.x, loc.y, type.ordinal()));
                rc.writeSharedArray(COUNT_OFFSET_1, encode(count1[0], count1[1], attackCount+1));
                return true;
            }
            return false;
        }
    }

    public static boolean readEven(RobotController rc){
        return rc.getRoundNum()%2 != 0;
    }

    public static boolean writeEven(RobotController rc){
        return rc.getRoundNum()%2 == 0;
    }

    //non-garbage collecting version
    public static void wipeComms(RobotController rc) throws GameActionException{
        int[] count1 = getAllCount1(rc);
        int[] count2 = getAllCount2(rc);
        int[] count3 = getAllCount3(rc);
        int[] count4 = getAllCount4(rc);
        //wipe last round Attacks and Anchors
        if(writeEven(rc)){
            // if we're now writing to evens, clear evens
            count1[1] = 0;
            count4[0] = 0;

        } else{
            // if we're now writing to odds, clear odds
            count1[2] = 0;
            count4[1] = 0;
        }
        // wipe reports
        count3[2] = 0; // island reports


        rc.writeSharedArray(COUNT_OFFSET_1, encode(count1[0], count1[1], count1[2]));
        rc.writeSharedArray(COUNT_OFFSET_2, encode(count2[0], count2[1], count2[2]));
        rc.writeSharedArray(COUNT_OFFSET_3, encode(count3[0], count3[1], count3[2]));
        rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], count4[1], count4[2]));

        setCommsCleaned(rc);


    }
    //non-garbage collecting version

    /**
     * wipes the given sections in addition to updates if booleans set to true
     * @param rc
     * @param wellComms
     * @param permaWells
     * @param teamIslands
     * @throws GameActionException
     */
    public static void wipeComms(RobotController rc, boolean wellComms, boolean permaWells, boolean teamIslands)
            throws GameActionException{

        int[] count1 = getAllCount1(rc);
        int[] count2 = getAllCount2(rc);
        int[] count3 = getAllCount3(rc);
        int[] count4 = getAllCount4(rc);
        //wipe last round Attacks and Anchors
        if(writeEven(rc)){
            // if we're now writing to evens, clear evens
            count1[1] = 0;
            count4[0] = 0;

        } else{
            // if we're now writing to odds, clear odds
            count1[2] = 0;
            count4[1] = 0;
        }
        // wipe reports
        count3[2] = 0;

        if(wellComms) count2[0] = 0;
        if(permaWells){
            count2[1] = 0;
            count2[2] = 0;
        }
        if(teamIslands) count3[1] = 0;

        rc.writeSharedArray(COUNT_OFFSET_1, encode(count1[0], count1[1], count1[2]));
        rc.writeSharedArray(COUNT_OFFSET_2, encode(count2[0], count2[1], count2[2]));
        rc.writeSharedArray(COUNT_OFFSET_3, encode(count3[0], count3[1], count3[2]));
        rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], count4[1], count4[2]));

        setCommsCleaned(rc);

    }



    /**
     * Decodes one array value at an index 0, 1, or 2
     */
    static int decode(int value, int index) {
        // index 0: number % 64 / 1, index 1: number % 64^2 / 64, index 3: number % 64^3 / 64^2
        return (value % (int) Math.pow(64, index + 1) / (int) Math.pow(64, index));
    }

    /**
     * Encodes a variable number of values into a single array value ready to be written
     */
    static int encode(int... fields) {
        int result = 0;
        for (int i = 0; i < fields.length; i++) {
            result += fields[i] * Math.pow(64, i);
        }
        return result;
    }
    public static class Attack {
        public MapLocation location;
        public RobotType type;
        public int num;

        public Attack(MapLocation location, RobotType type) {
            this.location = location;
            this.type = type;
        }

        public Attack(MapLocation location, int num) {
            this.location = location;
            this.num = num;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            Attack command = (Attack) o;
            return location.equals(command.location) && type == command.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, type);
        }

        public int priority() {
            return getCommPrio(type);
        }
    };

    public static class Well{
        public MapLocation location;
        public ResourceType type;


        public Well(MapLocation location, ResourceType type) {
            this.location = location;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            Well w = (Well) o;
            return location.equals(w.location) && type == w.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, type);
        }
    }

    public static class Island{
        public MapLocation location;
        public Team owner; // note: null equals no team owned

        public Island(MapLocation location){
            this.location = location;
            owner = null;
        }

        public Island(MapLocation location, int team, RobotController rc){
            this.location = location;
            if(team == 1){
                owner = rc.getTeam();
            } else if (team == 2){
                owner = rc.getTeam().opponent();
            } else{
                owner = null;
            }
        }

        public Island(MapLocation location, Team team){
            this.location = location;
            this.owner = team;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            Island island = (Island) o;
            return location.equals(island.location) && owner.equals(island.owner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, owner);
        }

    }

    public static class Command {
        public MapLocation location;
        public int num;

        public Command(MapLocation location, int num) {
            this.location = location;
            this.num = num;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            Command command = (Command) o;
            return location.equals(command.location) && command.num == num;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, num);
        }

    };
    public static int getCommPrio(RobotType type) {
        if (type == null) {
            return 0;
        }
        switch (type) {
            case HEADQUARTERS:
                return 10;
            case DESTABILIZER:
                return 8;
            case BOOSTER:
                return 8;
            case AMPLIFIER:
                return 7;
            case LAUNCHER:
                return 6;
            case CARRIER:
                return 4;


            default:
                break;
        }

        return -1;
    }
}
