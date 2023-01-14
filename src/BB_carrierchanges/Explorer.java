package BB_carrierchanges;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Explorer {

    static boolean HQInit = false;

    static MapLocation HQLoc1;
    static MapLocation HQLoc2;
    static MapLocation HQLoc3;
    static MapLocation HQLoc4;

    static int numHQs = 0;

    static MapLocation target;

    // Choose random unvisited location through visited array, if can't find in tries moves
    //chooses random on radius
    public static void getExploreTarget(RobotController rc, int tries, int mapWidth, int mapHeight) throws GameActionException {
        if(!HQInit){
            HQLoc1 = Comms.getTeamHQLocation(rc, 0);
            HQLoc2 = Comms.getTeamHQLocation(rc, 1);
            HQLoc3 = Comms.getTeamHQLocation(rc, 2);
            HQLoc4 = Comms.getTeamHQLocation(rc, 3);

            numHQs = Comms.getNumHQs(rc);
            HQInit = true;
        }

        int count = 0;
        boolean found = false;
        while(!found && count < tries){
            getExploreTargetRandom(rc, mapWidth, mapHeight);
            if(rc.getLocation().distanceSquaredTo(target) > rc.getType().visionRadiusSquared){
                if(targetNotHQ(HQLoc1) &&
                        (numHQs>=2 || targetNotHQ(HQLoc2)) &&
                        (numHQs>=3 || targetNotHQ(HQLoc3)) &&
                        (numHQs>=4 || targetNotHQ(HQLoc4))
                ) {
                    found = true;
                }


            }
            count++;
        }



    }

    public static boolean targetNotHQ(MapLocation HQLoc){
        return target.distanceSquaredTo(HQLoc) > RobotType.HEADQUARTERS.visionRadiusSquared;
    }

    // returns random target towards the edges a larger distance away
    public static void getExploreTargetRandom(RobotController rc, int mapWidth, int mapHeight){
        int x = (int) (10000* Math.random()) % 5;
        int y = (int) (10000 * Math.random()) % 5;

        int gridWidth = (mapWidth+4)/5;
        int gridHeight = (mapHeight+4)/5;

        int xGrid = (int) (10000* Math.random()) %gridWidth;
        int yGrid = (int) (10000 * Math.random()) % gridHeight;

        x += xGrid * 5;
        y+= yGrid * 5;
        x = Math.max(Math.min(mapWidth-4, x), 3);
        y = Math.max(Math.min(mapHeight-4, y), 3);

        target = new MapLocation(x, y);



    }




}