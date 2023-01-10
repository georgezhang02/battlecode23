package DB_VERY_DB;

import battlecode.common.*;
public strictfp class Pathfinder {

    static boolean directBug;
    static boolean rotatingBug;
    static boolean rotateLeft;
    static Direction lastBugDir;
    static int rotationCount;
    static double lowestDist;

    static double currentDist;

    static MapLocation lastLocation;

    static MapLocation lastTarget;

    public static Direction pathBF(RobotController rc, MapLocation target) throws GameActionException {

        if(!target.equals(lastTarget)){
            directBug = false;
            rotatingBug = false;
        }
        lastTarget=target;

        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
        if (directBug || (rotatingBug && currentDist>=lowestDist && rotationCount<8) ){
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


    public static Direction pathGreedy(RobotController rc, MapLocation target)throws GameActionException {
        if (directBug){
            return pathBug(rc, target);
        } else{
            return pathGreedyDepth(rc,  target, 2);
        }

    }

    public static Direction pathGreedyDepth(RobotController rc, MapLocation target, int depth)throws GameActionException {
        if (directBug){
            return pathBug(rc, target);
        }
        return pathBug(rc, target);

    }

    public static Direction pathBug(RobotController rc, MapLocation target)throws GameActionException {

        boolean found = false;
        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
        if(rotatingBug &&  (currentDist <= lowestDist)){
            rotatingBug = false;
        }

        if(rotatingBug){
            //rc.setIndicatorString(lowestDist+" "+ currentDist+" "+target);

            if(rotateLeft){
                MapLocation wall = rc.getLocation().add(lastBugDir.rotateLeft().rotateLeft());
                if(!rc.canSenseLocation(wall) || rc.sensePassability(wall)){
                    rotatingBug = false;
                } else{
                    if(rc.canMove(lastBugDir.rotateLeft())){
                        lastBugDir=lastBugDir.rotateLeft();
                        rotationCount++;
                    }
                    else if(rc.canMove(lastBugDir.rotateLeft().rotateLeft())){
                        lastBugDir=lastBugDir.rotateLeft().rotateLeft();
                        rotationCount+=2;
                    }
                }

            } else{
                MapLocation wall = rc.getLocation().add(lastBugDir.rotateRight().rotateRight());
                if(!rc.canSenseLocation(wall) || rc.sensePassability(wall)){
                    rotatingBug = false;
                } else {
                    if (rc.canMove(lastBugDir.rotateRight())) {
                        lastBugDir = lastBugDir.rotateRight();
                        rotationCount++;
                    } else if (rc.canMove(lastBugDir.rotateRight().rotateRight())) {
                        lastBugDir = lastBugDir.rotateRight().rotateRight();
                        rotationCount += 2;
                    }
                }
            }


            return lastBugDir;

        } else{
            //rc.setIndicatorString("bug "+target.toString());

            Direction moveDir = rc.getLocation().directionTo(target);
            if(rc.canMove(moveDir)){
                //rc.setIndicatorString("i can move to "+target.toString() + " "+moveDir.toString());
                return moveDir;
            } else{

                Direction left = moveDir.rotateLeft();
                Direction right = moveDir.rotateRight();

                int i = 1;
                while((!rc.canMove(left) && !rc.canMove(right)) && i <=4){
                    left = left.rotateLeft();
                    right = right.rotateRight();
                    i++;
                }
                //rc.setIndicatorString("starting rotation "+target.toString() +" "+left.toString());
                if(i <=4){
                    lowestDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
                    rotatingBug = true;
                    directBug = false;
                    rotationCount=0;
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
