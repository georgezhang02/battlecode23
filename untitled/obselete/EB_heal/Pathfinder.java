package EB_heal;

import battlecode.common.*;

public strictfp class Pathfinder {

    static boolean directBug;
    static boolean rotatingBug;
    static boolean wallLeft;
    static Direction lastBugDir;
    static int rotationCount;
    static double lowestDist;

    static double currentDist;

    static MapLocation lastLocation;

    static MapLocation lastTarget;
    static boolean exploring;



    public static Direction pathBF(RobotController rc, MapLocation target) throws GameActionException {

        if(!target.equals(lastTarget)){
            directBug = false;
            rotatingBug = false;
        }
        lastTarget=target;

        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
        if (directBug || (rotatingBug && currentDist>=lowestDist) ){
            return pathBug(rc, target);
        } else{
            directBug = false;
            rotatingBug = false;
            Direction moveDir = BFPathing20.bfPathToTarget(rc, target);

            if(moveDir ==null || moveDir == Direction.CENTER || rc.getLocation().add(moveDir).equals(lastLocation)){
                directBug = true;

                return pathBug(rc, target);
            }


            lastLocation = rc.getLocation();

            return moveDir;
        }

    }

    public static MapLocation locationToExplore(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        exploring = true;
        return Explorer.target;
    }

    public static Direction pathToExplore(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        Direction dir = pathBF(rc, Explorer.target);
        exploring = true;
        return dir;
    }

    public static Direction pathToExploreHQ(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            if(Explorer.curExploring != null && Explorer.curHQExploreIndex!=-1){
                Explorer.curExploring[Explorer.curHQExploreIndex] = true;
            }
            Explorer.getHQExploreTarget(rc);
        }

        if(Explorer.exploreRot && !Database.rotational){
            Explorer.getHQExploreTarget(rc);
        } else if(Explorer.exploreHor && !Database.horizontal){
            Explorer.getHQExploreTarget(rc);
        }  else if(Explorer.exploreVrt && !Database.vertical){
            Explorer.getHQExploreTarget(rc);
        }
        //rc.setIndicatorString(Explorer.target+"");
        Direction dir = pathBug(rc, Explorer.target);

        exploring = true;
        return dir;
    }

    public static Direction pathToExploreBug(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        //rc.setIndicatorString(Explorer.target+"");
        Direction dir = pathBug(rc, Explorer.target);


        exploring = true;
        return dir;
    }

    public static Direction pathToExplore(RobotController rc, RobotInfo[]allies) throws GameActionException {
        int lowestID = rc.getID();
        int lowestHealth = rc.getHealth();
        RobotInfo pathToAlly = null;
        for(RobotInfo ally: allies){
            if((ally.getType()== RobotType.LAUNCHER ||ally.getType()== RobotType.DESTABILIZER )){
                if(ally.getID() < lowestID){
                    pathToAlly = ally;
                    lowestID = ally.getID();
                }

            }
        }
        //(pathToAlly+" ");
        if(pathToAlly != null){
            if(pathToAlly.getLocation().distanceSquaredTo(rc.getLocation()) <=1){
                return Direction.CENTER;
            }
            return pathBug(rc, pathToAlly.getLocation());
        }

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 4){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        Direction dir = pathBF(rc, Explorer.target);
        exploring = true;
        return dir;
    }



    public static Direction pathAwayFrom(RobotController rc, MapLocation target) throws GameActionException{
        exploring = false;
        MapLocation curLoc = rc.getLocation();
        int distXToTarget = target.x - curLoc.x;
        int distYToTarget = target.y - curLoc.y;

        MapLocation runawayTarget = new MapLocation(curLoc.x-distXToTarget, curLoc.y-distYToTarget);
        return pathBug(rc, runawayTarget);

    }
    public static Direction pathGreedy(RobotController rc, MapLocation target)throws GameActionException {
        exploring = false;
        if (directBug){
            return pathBug(rc, target);
        } else{
            return pathGreedyDepth(rc,  target, 2);
        }

    }

    public static Direction pathGreedyDepth(RobotController rc, MapLocation target, int depth)throws GameActionException {
        exploring = false;
        if (directBug){
            return pathBug(rc, target);
        }
        return pathBug(rc, target);

    }

    public static Direction pathBug(RobotController rc, MapLocation target)throws GameActionException {
        exploring = false;
        boolean found = false;
        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));

        if(!target.equals(lastTarget)){
            rotatingBug = false;
        }
        lastTarget=target;

        if(currentDist < lowestDist){
            rotatingBug = false;
        }

        if(rotatingBug){
            if(wallLeft){
                Direction leftWallDir = lastBugDir.rotateLeft().rotateLeft();
                if((canMoveThrough(rc, lastBugDir, rc.getLocation().add(leftWallDir) )
                        && canMoveThrough(rc, leftWallDir, rc.getLocation().add(leftWallDir)))
                        || !rc.onTheMap(rc.getLocation().add(leftWallDir)))
                {
                    rotatingBug = false;
                    //rc.setIndicatorString("can move through now");
                } else if(currentDist < lowestDist && canMoveThrough(rc, lastBugDir, rc.getLocation().add(leftWallDir))){
                    //rc.setIndicatorString("lower than max dist");
                    rotatingBug = false;
                }
            } else{
                Direction rightWallDir = lastBugDir.rotateRight().rotateRight();
                if((canMoveThrough(rc, lastBugDir, rc.getLocation().add(rightWallDir)) &&
                        canMoveThrough(rc, rightWallDir, rc.getLocation().add(rightWallDir)))
                        || !rc.onTheMap(rc.getLocation().add(rightWallDir))) {
                    rotatingBug = false;
                    //rc.setIndicatorString("can move through now");
                }
            }
        }

        if(rotatingBug){
            if(wallLeft){
                rc.setIndicatorDot(rc.getLocation().add(lastBugDir.rotateLeft().rotateLeft()), 255, 255, 0);
                int count = 0;
                while( count <4){
                    if(canMoveThrough(rc, lastBugDir.rotateLeft(), rc.getLocation().add(lastBugDir.rotateLeft()))){
                        Direction ans = lastBugDir.rotateLeft();
                        lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                        return ans;
                    } else if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(lastBugDir))){
                        return lastBugDir;
                    }
                    lastBugDir = lastBugDir.rotateRight().rotateRight();
                    count++;
                }
            } else{
                rc.setIndicatorDot(rc.getLocation().add(lastBugDir.rotateRight().rotateRight()), 255, 255, 0);
                int count = 0;
                while( count <4){
                    if(canMoveThrough(rc, lastBugDir.rotateRight(), rc.getLocation().add(lastBugDir.rotateRight()))){
                        Direction ans = lastBugDir.rotateRight();
                        lastBugDir = lastBugDir.rotateRight().rotateRight();
                        return ans;
                    } else if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(lastBugDir))){
                        return lastBugDir;
                    }
                    lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                    count++;
                }

            }


            return Direction.CENTER;

        } else{

            Direction moveDir = rc.getLocation().directionTo(target);
            if(canMoveThrough(rc,moveDir, rc.getLocation().add(moveDir))){
                //rc.setIndicatorString("i can move to "+target.toString() + " "+moveDir.toString());
                return moveDir;
            } else{

                Direction left = moveDir.rotateLeft();
                Direction right = moveDir.rotateRight();
                int i;
                for(i = 1; i<= 4; i++){
                    if(!canMoveThrough(rc,left, rc.getLocation().add(left))){
                        left = left.rotateLeft();
                    } else{
                        wallLeft = false;
                        break;
                    }

                    if(!canMoveThrough(rc,right, rc.getLocation().add(right))){
                        right = right.rotateRight();
                    } else{
                        wallLeft = true;
                        break;
                    }
                }


                if(i <=4){
                    lowestDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
                    rotatingBug = true;
                    directBug = false;
                    rotationCount=0;
                    if(!wallLeft){

                        if(left==Direction.NORTHEAST||left==Direction.NORTHWEST||left==Direction.SOUTHEAST||
                                left==Direction.SOUTHWEST){
                            lastBugDir= left.rotateRight();
                        } else{
                            lastBugDir= left;
                        }
                        /*rc.setIndicatorString("starting rotation left"+target.toString() +" "+lastBugDir.toString()+
                                " ");*/



                        return left;
                    } else{
                        if(right==Direction.NORTHEAST||right==Direction.NORTHWEST||right==Direction.SOUTHEAST||
                                right==Direction.SOUTHWEST){
                            lastBugDir= right.rotateLeft();
                        } else{
                            lastBugDir= right;
                        }

                       /* rc.setIndicatorString("starting rotation right"+target.toString() +" "+lastBugDir.toString()+
                                " ");*/


                        return right;

                    }

                } else{
                    return Direction.CENTER;
                }
            }
        }

    }

    static boolean canMoveThrough(RobotController rc, Direction dir, MapLocation loc) throws GameActionException{

        if(rc.canSenseLocation(loc) && rc.sensePassability(loc) && !rc.isLocationOccupied(loc)){
            MapInfo info = rc.senseMapInfo(loc);
            Direction current = info.getCurrentDirection();
            if(current != Direction.CENTER){

                Direction opposite = dir.opposite();
                Direction oppositeLeft = opposite.rotateLeft();
                Direction oppositeRight = opposite.rotateRight();
                if(current == opposite || current == oppositeLeft || current == oppositeRight){
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;

    }
}
