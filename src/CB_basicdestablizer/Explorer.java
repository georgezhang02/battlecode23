package CB_basicdestablizer;

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

    static int numExplorationTargets = 0;
    static int curHQExploreIndex = 0;
    static boolean[] HQsExplored;

    static int numHQs = 0;

    static MapLocation target;


    public static void getHQExploreTarget(RobotController rc) throws GameActionException {

        if(!HQInit){
            int numHQs = Comms.getNumHQs(rc);
            HQLoc1 = Comms.getTeamHQLocation(rc, 0);
            if(numHQs <= 2)  HQLoc2 = Comms.getTeamHQLocation(rc, 1);
            if(numHQs <= 3)  HQLoc3 = Comms.getTeamHQLocation(rc, 2);
            if(numHQs <= 4)  HQLoc4 = Comms.getTeamHQLocation(rc, 3);
        }

        if(numExplorationTargets == 0){
            numExplorationTargets = Comms.getNumExploration(rc);
            HQsExplored = new boolean[numExplorationTargets];
        }
        int maxRange = 100000;
        target = null;
        for(int i = 0; i<numExplorationTargets; i++){
            MapLocation enemyHQLoc = Comms.getExplorationTarget(rc, i);
            int range = rc.getLocation().distanceSquaredTo(enemyHQLoc);

            if(!HQsExplored[i]){
                if(rc.getLocation().distanceSquaredTo(enemyHQLoc) > rc.getType().visionRadiusSquared){
                    // check not in vision
                 /*   if(targetNotHQ(HQLoc1) &&
                            (numHQs>=2 || targetNotHQ(HQLoc2)) &&
                            (numHQs>=3 || targetNotHQ(HQLoc3)) &&
                            (numHQs>=4 || targetNotHQ(HQLoc4))
                    ) {*/
                        //check not in HQ range
                        if(range < maxRange){
                            maxRange = range;
                            target = enemyHQLoc;
                            curHQExploreIndex = i;
                        }
                    } else{
                        // inside ally HQ range
                        HQsExplored[i] = true;
                    }
                /*}else  {
                    //inside vision range
                    HQsExplored[i] = true;
                }*/
            }


        }

        if(target == null){
            getExploreTargetRandom(rc, rc.getMapWidth(), rc.getMapHeight());
        }
    }

    // Choose random unvisited location through visited array, if can't find in tries moves
    //chooses random on radius
    public static void getExploreTarget(RobotController rc, int tries, int mapWidth, int mapHeight) throws GameActionException {
        if(!HQInit){
            int numHQs = Comms.getNumHQs(rc);
            HQLoc1 = Comms.getTeamHQLocation(rc, 0);
            if(numHQs <= 2)  HQLoc2 = Comms.getTeamHQLocation(rc, 1);
            if(numHQs <= 3)  HQLoc3 = Comms.getTeamHQLocation(rc, 2);
            if(numHQs <= 4)  HQLoc4 = Comms.getTeamHQLocation(rc, 3);
        }


        int count = 0;
        boolean found = false;
        while(!found && count < tries){
            getExploreTargetRandom(rc, mapWidth, mapHeight);
            if(rc.getLocation().distanceSquaredTo(target) > rc.getType().visionRadiusSquared){
               /* if(targetNotHQ(HQLoc1) &&
                        (numHQs>=2 || targetNotHQ(HQLoc2)) &&
                        (numHQs>=3 || targetNotHQ(HQLoc3)) &&
                        (numHQs>=4 || targetNotHQ(HQLoc4))
                ) {*/
                    found = true;
              //  }


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