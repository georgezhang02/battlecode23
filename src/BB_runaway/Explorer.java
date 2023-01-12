package BB_runaway;

import battlecode.common.*;
public class Explorer {

    static MapLocation target;




    // Choose random unvisited location through visited array, if can't find in tries moves
    //chooses random on radius
    public static void getExploreTarget(int tries, int mapWidth, int mapHeight){



    }

    // returns random target towards the edges a larger distance away
    public static void getExploreTargetRandom(int mapWidth, int mapHeight){
        int x = (int) (10000* Math.random()) %mapWidth;
        int y = (int) (10000 * Math.random()) % mapHeight;

        target = new MapLocation(x, y);



    }




}