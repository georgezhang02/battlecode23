package EB_test;

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

    static boolean[] rotHQsExplored;

    static boolean[] horizHQsExplored;
    static boolean[] vertHQsExplored;
    static boolean[]curExploring;

    static boolean exploreRot = false;
    static boolean exploreHor = false;
    static boolean exploreVrt = false;
    static int curHQExploreIndex = -1;
    static int numHQs = 0;

    static MapLocation target;


    public static void resetHQsExplored(RobotController rc){
        curHQExploreIndex = -1;
        rotHQsExplored = new boolean[numHQs];
        horizHQsExplored = new boolean[numHQs];
        vertHQsExplored = new boolean[numHQs];
    }


    public static void getHQExploreTarget(RobotController rc) throws GameActionException {

        if(!HQInit){
            numHQs = Comms.getNumHQs(rc);
            HQLoc1 = Comms.getTeamHQLocation(rc, 0);
            if(numHQs <= 2)  HQLoc2 = Comms.getTeamHQLocation(rc, 1);
            if(numHQs <= 3)  HQLoc3 = Comms.getTeamHQLocation(rc, 2);
            if(numHQs <= 4)  HQLoc4 = Comms.getTeamHQLocation(rc, 3);

            rotHQsExplored = new boolean[numHQs];
            horizHQsExplored = new boolean[numHQs];
            vertHQsExplored = new boolean[numHQs];


        }

        target = null;


        if(Database.rotational){
            target = getNearestUnexploredHQ(rc, Database.rotationalEnemyHQs, rotHQsExplored);
            curExploring = rotHQsExplored;
            exploreRot = true;
            exploreHor = false;
            exploreVrt = false;
        } else if(Database.horizontal){
            target = getNearestUnexploredHQ(rc, Database.horizontalEnemyHQs, horizHQsExplored);
            curExploring = horizHQsExplored;
            exploreRot = false;
            exploreHor = true;
            exploreVrt = false;
        } else if(Database.vertical){
            target = getNearestUnexploredHQ(rc, Database.verticalEnemyHQs, vertHQsExplored);
            curExploring = vertHQsExplored;
            exploreRot = false;
            exploreHor = false;
            exploreVrt = true;
        }

        if(target == null){
            getExploreTargetRandom(rc, rc.getMapWidth(), rc.getMapHeight());
        }
    }

    public static MapLocation getNearestUnexploredHQ(RobotController rc, Database.SymmetryCheck[]enemyHQs, boolean[]HQsExplored){



        int maxRange = 100000;
        for(int i = 0; i< enemyHQs.length; i++){
            if(enemyHQs[i] != null){
                MapLocation enemyHQLoc = enemyHQs[i].location;
                int range = rc.getLocation().distanceSquaredTo(enemyHQLoc);

                if(!HQsExplored[i]){
                    if(rc.getLocation().distanceSquaredTo(enemyHQLoc) > rc.getType().visionRadiusSquared){
                        if(range < maxRange){
                            maxRange = range;
                            target = enemyHQLoc;
                            curHQExploreIndex = i;
                        }
                    } else{
                        HQsExplored[i] = true;
                    }
                }
            }



        }

        return target;
    }

    // Choose random unvisited location through visited array, if can't find in tries moves
    //chooses random on radius
    public static void getExploreTarget(RobotController rc, int tries, int mapWidth, int mapHeight) throws GameActionException {
        curHQExploreIndex = -1;
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