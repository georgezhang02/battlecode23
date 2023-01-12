package BB_explorebetter;

import battlecode.common.*;

public class Explorer {

    static MapLocation target;




    // Choose random unvisited location through visited array, if can't find in tries moves
    //chooses random on radius
    public static void getExploreTarget(RobotController rc, int tries, int mapWidth, int mapHeight){
        int count = 0;
        boolean found = false;
        while(!found && count < tries){
            getExploreTargetRandom(rc, mapWidth, mapHeight);
            if(rc.getLocation().distanceSquaredTo(target) > rc.getType().visionRadiusSquared){
                found = true;
            }
            count++;
        }



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
        x = Math.min(mapWidth-1, x);
        y = Math.min(mapHeight-1, y);

        target = new MapLocation(x, y);



    }




}