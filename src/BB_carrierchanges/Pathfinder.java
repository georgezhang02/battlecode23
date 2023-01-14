package BB_carrierchanges;

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

    public static Direction pathToExplore(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        Direction dir = pathBF(rc, Explorer.target);
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
        rc.setIndicatorString(pathToAlly+" ");
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


        if(rotatingBug){
           /* rc.setIndicatorString("rotating "+target.toString() +
                    " "+currentDist+" "+lowestDist +" "+lastBugDir+" wallleft:"+wallLeft);*/
            if(currentDist < lowestDist){
                rotatingBug = false;
            }

            if(wallLeft){
                if(canMoveThrough(rc, lastBugDir.rotateLeft().rotateLeft(), rc.getLocation())
                        || !rc.onTheMap(rc.getLocation().add(lastBugDir.rotateLeft().rotateLeft()))){
                    rotatingBug = false;
                    //rc.setIndicatorString("canmovethrough");
                } else{

                    int count = 0;
                    while( count <4){
                        if(canMoveThrough(rc, lastBugDir.rotateLeft(), rc.getLocation())){
                            Direction ans = lastBugDir.rotateLeft();
                            lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                            return ans;
                        } else if(canMoveThrough(rc, lastBugDir, rc.getLocation())){
                            return lastBugDir;
                        }
                        lastBugDir = lastBugDir.rotateRight().rotateRight();
                        count++;
                    }

                }

            } else{
                if(canMoveThrough(rc, lastBugDir.rotateRight().rotateRight(), rc.getLocation())
                        || !rc.onTheMap(rc.getLocation().add(lastBugDir.rotateRight().rotateRight()))){
                    rotatingBug = false;
                } else{
                    int count = 0;
                    while( count <4){
                        if(canMoveThrough(rc, lastBugDir.rotateRight(), rc.getLocation())){
                            Direction ans = lastBugDir.rotateRight();
                            lastBugDir = lastBugDir.rotateRight().rotateRight();
                            return ans;
                        } else if(canMoveThrough(rc, lastBugDir, rc.getLocation())){
                            return lastBugDir;
                        }
                        lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                        count++;
                    }
                }
            }


            return Direction.CENTER;

        } else{
            //rc.setIndicatorString("bug "+target.toString());

            Direction moveDir = rc.getLocation().directionTo(target);
            if(canMoveThrough(rc,moveDir, rc.getLocation())){
                //rc.setIndicatorString("i can move to "+target.toString() + " "+moveDir.toString());
                return moveDir;
            } else{

                Direction left = moveDir.rotateLeft();
                Direction right = moveDir.rotateRight();

                int i = 1;
                while((!canMoveThrough(rc,left, rc.getLocation()) && !canMoveThrough(rc,right, rc.getLocation())) && i <=4){
                    left = left.rotateLeft();
                    right = right.rotateRight();
                    i++;
                }

                if(i <=4){
                    lowestDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
                    rotatingBug = true;
                    directBug = false;
                    rotationCount=0;
                    if(canMoveThrough(rc,left, rc.getLocation())){

                        wallLeft = false;

                        if(left==Direction.NORTHEAST||left==Direction.NORTHWEST||left==Direction.SOUTHEAST||
                                left==Direction.SOUTHWEST){
                            lastBugDir= left.rotateRight();
                        } else{
                            lastBugDir= left;
                        }
                        /*rc.setIndicatorString("starting rotation "+target.toString() +" "+lastBugDir.toString()+
                                " "+lowestDist);


*/
                        return left;
                    } else{
                        wallLeft = true;
                        if(right==Direction.NORTHEAST||right==Direction.NORTHWEST||right==Direction.SOUTHEAST||
                                right==Direction.SOUTHWEST){
                            lastBugDir= right.rotateRight();
                        } else{
                            lastBugDir= right;
                        }
                        /*
                        rc.setIndicatorString("starting rotation "+target.toString() +" "+lastBugDir.toString()+
                                " "+lowestDist);
                                */

                        return right;

                    }

                } else{
                    return Direction.CENTER;
                }
            }
        }

    }

    static boolean canMoveThrough(RobotController rc, Direction dir, MapLocation origin) throws GameActionException{

        MapLocation dest = origin.add(dir);

        if(rc.canSenseLocation(dest) && rc.sensePassability(dest) && !rc.isLocationOccupied(dest)){
            MapInfo info = rc.senseMapInfo(dest);
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
