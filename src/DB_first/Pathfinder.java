package DB_first;

import battlecode.common.*;
public strictfp class Pathfinder {

    static boolean currentlyBug;
    static boolean rotating;
    static boolean rotateLeft;
    static Direction lastBugDir;
    static int rotationCount;
    static double lowestDist;

    static double

    public static Direction pathBF(RobotController rc, MapLocation target) throws GameActionException {


        double currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
         if (currentlyBug || (rotating && currentDist>=lowestDist && rotationCount<=8) ){

             return pathBug(rc, target);
         } else{
             currentlyBug = false;
             rotating = false;
             Direction moveDir = BFPathing20.bfPathToTarget(rc, target);

             if(moveDir ==null || moveDir == Direction.CENTER){
                 currentlyBug = true;
                  return pathBug(rc, target);
             }
             return moveDir;
         }
    }


    public static Direction pathGreedy(RobotController rc, MapLocation target)throws GameActionException {
        if (currentlyBug){
            return pathBug(rc, target);
        } else{
            return pathGreedyDepth(rc,  target, 2);
        }

    }

    public static Direction pathGreedyDepth(RobotController rc, MapLocation target, int depth)throws GameActionException {
        if (currentlyBug){
            return pathBug(rc, target);
        }
        return pathBug(rc, target);

    }

    public static Direction pathBug(RobotController rc, MapLocation target)throws GameActionException {
        if(rotating ){
            if(rotateLeft){
                if(rc.canMove(lastBugDir.rotateLeft())){
                    lastBugDir=lastBugDir.rotateLeft();
                    rotationCount++;
                }
                else if(rc.canMove(lastBugDir.rotateLeft().rotateLeft()) && rotationCount< 7){
                    lastBugDir=lastBugDir.rotateLeft().rotateLeft();
                    rotationCount+=2;
                }
            } else{
                if(rc.canMove(lastBugDir.rotateRight()) && rotationCount <8){
                    lastBugDir=lastBugDir.rotateRight();
                    rotationCount++;
                }else if(rc.canMove(lastBugDir.rotateRight().rotateRight())){
                    lastBugDir=lastBugDir.rotateRight().rotateRight();
                    rotationCount+=2;
                }
            }
            return lastBugDir;

        } else{
            Direction moveDir = rc.getLocation().directionTo(target);
            if(rc.canMove(moveDir)){
                return moveDir;
            } else{
                Direction left = moveDir.rotateLeft();
                Direction right = moveDir.rotateRight();

                rotationCount = 1;
                while(!rc.canMove(left) && !rc.canMove(right) && rotationCount < 3){
                    left = left.rotateLeft();
                    right = right.rotateRight();
                    rotationCount++;
                }
                if(rotationCount != 3){
                    lowestDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
                    rotating = true;
                    currentlyBug = false;
                    if(rc.canMove(left)){
                        rotateLeft = false;
                        lastBugDir= left;
                        return left;
                    } else{
                        rotateLeft = true;
                        lastBugDir = right;
                        return right;

                    }

                } else{
                    return Direction.CENTER;
                }
            }
        }
    }




}
