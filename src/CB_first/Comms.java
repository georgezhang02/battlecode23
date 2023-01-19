package CB_first;

import battlecode.common.*;

import java.util.Objects;

public class Comms {
    private static final int COUNT_OFFSET_1 = 0;
    private static final int COUNT_OFFSET_2 = 1;
    private static final int COUNT_OFFSET_3 = 2;
    private static final int COUNT_OFFSET_4 = 3;
    private static final int ALLY_HQ_OFFSET = 4;
    private static final int ENEMY_HQ_OFFSET = 8;

    private static final int HQ_COMM_OFFSET = 12;
    private static final int WELL_COMM_OFFSET = 16;
    private static final int WELL_PERM_OFFSET = 20;
    private static final int WELL_REPORT_OFFSET = 28;
    private static final int ISLAND_OFFSET = 32;
    private static final int ISLAND_REP_OFFSET = 40;
    private static final int ANCHOR_OFFSET_EVEN = 44;

    private static final int ANCHOR_OFFSET_ODD = 44;
    private static final int AC_EVEN_OFFSET = 48;

    private static final int AC_ODD_OFFSET = 56;

    private static final int ALLY_HQ_MAXCOUNT = 4;
    private static final int ENEMY_HQ_MAXCOUNT = 4;

    private static final int WELL_COMM_MAXCOUNT = 4;
    private static final int WELL_PERM_MAXCOUNT = 8;
    private static final int WELL_REP_MAXCOUNT = 4;
    private static final int ISLAND_MAXCOUNT = 8;
    private static final int ISLAND_REP_MAXCOUNT = 4;
    private static final int ANCHOR_MAXCOUNT = 2;
    private static final int AC_MAXCOUNT = 8;

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
    public static int getNumACOdd(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_1), 1);
    }

    /**
     *
     * @return Number of odd attack commands
     */
    public static int getNumACEven(RobotController rc) throws GameActionException {
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
    public static int getNumWellsPerm(RobotController rc) throws GameActionException {
        return decode(rc.readSharedArray(COUNT_OFFSET_2), 1);
    }

    /**
     * @return Number of Well Reports
     */
    public static int getNumWellRep(RobotController rc) throws GameActionException {
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
    public static int getNumIslandRep(RobotController rc) throws GameActionException {
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
        int bits = val/8; // extract commscleaned
        if(rotational) bits += 4;
        if(horizontal) bits += 2;
        if(vertical) bits += 1;

        rc.writeSharedArray(COUNT_OFFSET_4, encode(count4[0], count4[1], bits));
    }

    /**
     * @return If the array was wiped on this turn
     */
    public static boolean isCommsCleaned(RobotController rc) throws GameActionException{
        int val = decode(rc.readSharedArray(COUNT_OFFSET_4), 2);
        return val/8 == rc.getRoundNum() % 2;
    }


    public static void setCommsCleaned(RobotController rc) throws GameActionException{
        int[]count4 = getAllCount4(rc);
        int val = count4[2];
        val = val-(val/8);
        val = val + (rc.getRoundNum() % 2) * 8;
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
        int lowest_dist = 100;
        int numHQs = getNumHQs(rc);
        MapLocation closest = null;
        for (int i = 0; i < numHQs; i++) {
            int value = rc.readSharedArray(i + ALLY_HQ_OFFSET);
            int x = decode(value, 0);
            int y = decode(value, 1);
            int distance = Helper.distanceTo(current.x, current.y, x, y);
            if (distance < lowest_dist) {
                lowest_dist = distance;
                closest = new MapLocation(x, y);
            }

        }
        return closest;
    }

    public static void setEnemyHQLocation(RobotController rc, MapLocation HQLocation, int id) throws GameActionException {
        int[] count3 = getAllCount3(rc);
        int enemyHQCount = count3[0];

        if(enemyHQCount < ENEMY_HQ_MAXCOUNT){
            for(int i = 0; i<enemyHQCount; i++){
                if(getEnemyHQLocation(rc, i).equals(HQLocation)){
                    return;
                }
            }
            rc.writeSharedArray(enemyHQCount + ENEMY_HQ_OFFSET, encode(HQLocation.x, HQLocation.y, id));
            rc.writeSharedArray(COUNT_OFFSET_3, encode(enemyHQCount+1, count3[1], count3[2]));
        }

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

    public static MapLocation getHQCommand(RobotController rc, int HQIndex) throws GameActionException {
        int value = rc.readSharedArray(HQ_COMM_OFFSET + HQIndex);
        int x = decode(value, 0);
        int y = decode(value, 1);
        int assigned = decode(value, 2);
        if (assigned == 1) {
            return new MapLocation(x, y);
        }
        return null;
    }
    public static void writeHQCommand(RobotController rc, int HQIndex, MapLocation well) throws GameActionException {
        rc.writeSharedArray(HQIndex + HQ_COMM_OFFSET, encode(well.x, well.y, 1));
    }
    public static void clearHQCommand(RobotController rc, int HQIndex) throws GameActionException {
        rc.writeSharedArray(HQIndex + HQ_COMM_OFFSET, encode(0, 0, 0));
    }
    public static void setWellCommand(RobotController rc, MapLocation loc, ResourceType type) throws GameActionException {
        int[] count2 = getAllCount2(rc);
        int wellCommCount = count2[0];

        if(wellCommCount < WELL_COMM_MAXCOUNT){
            for(int i = 0; i<wellCommCount; i++){
                if(getWellCommand(rc, i).location.equals(loc)){
                    return;
                }
            }
            rc.writeSharedArray(wellCommCount+ WELL_COMM_OFFSET,
                    encode(loc.x, loc.y, type.resourceID));
            rc.writeSharedArray(COUNT_OFFSET_2, encode(wellCommCount+1, count2[1], count2[2]));
        }
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
    public static void setWellPerm(RobotController rc, MapLocation loc, ResourceType type) throws GameActionException {
    }
    public static Well getWellPerm(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(WELL_PERM_OFFSET+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        ResourceType type = ResourceType.values()[decode(val, 2)];
        return new Well(new MapLocation(x, y), type);
    }

    public static Well[] getAllWellPerm(RobotController rc) throws GameActionException{
        Well[] allPerm = new Well[WELL_PERM_MAXCOUNT];
        for (int i = 0; i < WELL_PERM_MAXCOUNT; i ++) {
            allPerm[i] = getWellPerm(rc, i);
        }
        return allPerm;
    }

    public static boolean reportWellLocation(RobotController rc, int index, WellInfo well) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_REPORT_OFFSET);
        if (decode(value, 2) == 0) {
            MapLocation loc = well.getMapLocation();
            int wellType = 0;
            switch (well.getResourceType()) {
                case ADAMANTIUM:
                    wellType = 1;
                    break;
                case MANA:
                    wellType = 2;
                    break;
                case ELIXIR:
                    wellType = 3;
                    break;
            }
            rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(loc.x, loc.y, wellType));
            return true;
        }
        return false;
    }

    public static int[] readWellReport(RobotController rc, int index) throws GameActionException {
        int value = rc.readSharedArray(index + WELL_REPORT_OFFSET);
        int[] report = new int[3];
        report[0] = decode(value, 0);
        report[1] = decode(value, 1);
        int type = decode(value, 2);
        if (type == 0) {
            return null;
        }
        report[2] = type;
        return report;
    }

    public static void clearWellReport(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + WELL_REPORT_OFFSET, encode(0, 0, 0));
    }

    public static Island getIslandLocation(RobotController rc, int index) throws GameActionException{
        int val = rc.readSharedArray(ISLAND_OFFSET+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        //Island size?
        return new Island(new MapLocation(x, y), 1);
    }

    public static Island[] getIslandLocations(RobotController rc) throws GameActionException{
        Island[] islands = new Island[ISLAND_MAXCOUNT];
        for (int i = 0; i < ISLAND_MAXCOUNT; i ++) {
            islands[i] = getIslandLocation(rc, i);
        }
        return islands;
    }

    public static void setIslandLocation(RobotController rc, MapLocation loc){}


    public static boolean reportIslandLocation(){return false;}

    public static int[] readIslandReport(){return null;}

    public static void clearIslandReport(RobotController rc, int index) throws GameActionException {
        rc.writeSharedArray(index + ISLAND_REP_OFFSET, encode(0, 0, 0));
    }

    public static void setAnchorCommandEven(RobotController rc, MapLocation loc, ResourceType type) throws GameActionException {}

    public static MapLocation getAnchorCommandEven(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ANCHOR_OFFSET_EVEN+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation[] getAllAnchorCommandEven(RobotController rc) throws GameActionException{
        MapLocation[] anchorLocations = new MapLocation[ANCHOR_MAXCOUNT];
        for(int i = 0; i < ANCHOR_MAXCOUNT; i++){
            anchorLocations[i] = getAnchorCommandEven(rc, i);
        }
        return anchorLocations;
    }
    public static void setAnchorCommandOdd(RobotController rc, MapLocation loc, ResourceType type) throws GameActionException {}

    public static MapLocation getAnchorCommandOdd(RobotController rc, int index) throws GameActionException {
        int val = rc.readSharedArray(ANCHOR_OFFSET_ODD+index);
        int x = decode(val, 0);
        int y = decode(val, 1);
        return new MapLocation(x, y);
    }

    public static MapLocation[] getAllAnchorCommandOdd(RobotController rc) throws GameActionException{
        MapLocation[] anchorLocations = new MapLocation[ANCHOR_MAXCOUNT];
        for(int i = 0; i < ANCHOR_MAXCOUNT; i++){
            anchorLocations[i] = getAnchorCommandOdd(rc, i);
        }
        return anchorLocations;
    }

    public static void setAttackCommandEven(RobotController rc, MapLocation loc, RobotType type){}

    public static Attack getAttackCommandEven(RobotController rc, int index){return null;}

    public static Attack[] getAllAttackCommandsEven(RobotController rc){
        Attack[] attacks = new Attack[AC_MAXCOUNT];
        for(int i = 0; i < AC_MAXCOUNT; i++){
            attacks[i] = getAttackCommandEven(rc, i);
        }
        return attacks;
    }

    public static void setAttackCommandOdd(RobotController rc, MapLocation loc, RobotType type){}

    public static Attack getAttackCommandOdd(RobotController rc, int index){return null;}

    public static Attack[] getAllAttackCommandsOdd(RobotController rc){
        Attack[] attacks = new Attack[AC_MAXCOUNT];
        for(int i = 0; i < AC_MAXCOUNT; i++){
            attacks[i] = getAttackCommandOdd(rc, i);
        }
        return attacks;
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
        public int id;
        public Team owner; // note: null equals no team owned
        public Anchor anchorType;

        public Island(MapLocation location, int id){
            this.location = location;
            this.id = id;
            owner = null;
            anchorType = Anchor.STANDARD;
        }

        public Island(MapLocation location, int id, Anchor anchorType){
            this.location = location;
            this.id = id;
            owner = null;
            this.anchorType = anchorType;
        }

        public Island(MapLocation location, int id, Team owner){
            this.location = location;
            this.id = id;
            this.owner = owner;
            anchorType = Anchor.STANDARD;
        }

        public Island(MapLocation location, int id, Team owner, Anchor anchorType){
            this.location = location;
            this.id = id;
            this.owner = owner;
            this.anchorType = anchorType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            Island island = (Island) o;
            return location.equals(island.location) && id == island.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, id);
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
    private static int getCommPrio(RobotType type) {
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
