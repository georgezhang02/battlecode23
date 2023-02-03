package FB_ZZZ;

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
    static boolean robotWall = false;

    static boolean turnedCurrentCorner = false;




    public static Direction pathBF(RobotController rc, MapLocation target) throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }

        if(!target.equals(lastTarget)){
            directBug = false;
            rotatingBug = false;
            turnedCurrentCorner = false;
        }
        lastTarget=target;

        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
        if (directBug || (rotatingBug && currentDist>=lowestDist) ){
            return pathBug(rc, target);
        } else{
            directBug = false;
            rotatingBug = false;
            turnedCurrentCorner = false;
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
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        Direction dir = pathGreedy(rc, Explorer.target);
        exploring = true;
        return dir;
    }

    public static Direction pathToExploreHQ(RobotController rc) throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }

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
        Direction dir = pathGreedy(rc, Explorer.target);

        exploring = true;
        return dir;
    }

    public static Direction pathToExploreBug(RobotController rc) throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        //rc.setIndicatorLine(rc.getLocation(), Explorer.target, 0, 255, 255);
        Direction dir = pathBug(rc, Explorer.target);


        exploring = true;
        return dir;
    }

    public static Direction pathToExplore(RobotController rc, RobotInfo[]allies) throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
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
        Direction dir = pathGreedy(rc, Explorer.target);
        exploring = true;
        return dir;
    }



    public static Direction pathAwayFrom(RobotController rc, MapLocation target) throws GameActionException{
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
        exploring = false;
        MapLocation curLoc = rc.getLocation();
        int distXToTarget = target.x - curLoc.x;
        int distYToTarget = target.y - curLoc.y;

        MapLocation runawayTarget = new MapLocation(curLoc.x-distXToTarget, curLoc.y-distYToTarget);
        return pathGreedy(rc, runawayTarget);

    }
    public static Direction pathGreedy(RobotController rc, MapLocation target)throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
        exploring = false;
        return pathGreedyDepth(rc, rc.getLocation(),  target, 2);

    }

    public static double pathGreedyHelper(RobotController rc, MapLocation loc, MapLocation target, int depth) throws GameActionException {

        //rc.setIndicatorLine(rc.getLocation(), target, 255, 255, 0);
        double lowestCost = 10000;
        Direction moveDir = loc.directionTo(target);
        Direction left = moveDir.rotateLeft();
        Direction right = moveDir.rotateRight();
        Direction left_left = left.rotateLeft();
        Direction right_right = right.rotateRight();
        if(depth == 1){


            if(canMoveThrough(rc, moveDir, loc.add(moveDir) )){
                double cost = 10 * rc.senseMapInfo(loc.add(moveDir)).getCooldownMultiplier(rc.getTeam());
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, left, loc.add(left) )){
                double cost = 10 * rc.senseMapInfo(loc.add(left)).getCooldownMultiplier(rc.getTeam());
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }

            if(canMoveThrough(rc, right, loc.add(right) )){
                double cost = 10 * rc.senseMapInfo(loc.add(right)).getCooldownMultiplier(rc.getTeam());
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, right, loc.add(left_left) )){
                double cost = 10 * rc.senseMapInfo(loc.add(left_left)).getCooldownMultiplier(rc.getTeam());
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, right, loc.add(right_right) )){
                double cost = 10 * rc.senseMapInfo(loc.add(right_right)).getCooldownMultiplier(rc.getTeam());
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }

        } else{

            if(canMoveThrough(rc, moveDir, loc.add(moveDir) )){
                double cost = 10 * rc.senseMapInfo(loc.add(moveDir)).getCooldownMultiplier(rc.getTeam());
                cost += pathGreedyHelper(rc, loc.add(moveDir), target, depth-1);
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, left, loc.add(left) )){
                double cost = 10 * rc.senseMapInfo(loc.add(left)).getCooldownMultiplier(rc.getTeam());
                cost += pathGreedyHelper(rc, loc.add(left), target, depth-1);
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }

            if(canMoveThrough(rc, right, loc.add(right) )){
                double cost = 10 * rc.senseMapInfo(loc.add(right)).getCooldownMultiplier(rc.getTeam());
                cost += pathGreedyHelper(rc, loc.add(right), target, depth-1);
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, right, loc.add(left_left) )){
                double cost = 10 * rc.senseMapInfo(loc.add(left_left)).getCooldownMultiplier(rc.getTeam());
                cost += pathGreedyHelper(rc, loc.add(left_left), target, depth-1);
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
            if(canMoveThrough(rc, right, loc.add(right_right) )){
                double cost = 10 * rc.senseMapInfo(loc.add(right_right)).getCooldownMultiplier(rc.getTeam());
                cost += pathGreedyHelper(rc, loc.add(right_right), target, depth-1);
                if(cost < lowestCost){
                    lowestCost = cost;
                }
            }
        }
        return lowestCost;

    }

    public static Direction pathGreedyDepth(RobotController rc, MapLocation loc, MapLocation target, int depth)throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
        exploring = false;
        if(!target.equals(lastTarget)){
            rotatingBug = false;
            directBug = false;
            turnedCurrentCorner = false;
        }
        lastTarget=target;

        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));


        if(directBug || (rotatingBug && currentDist>=lowestDist)){
            rc.setIndicatorString("pathing bug, alreadypathing bug " + target);
            Direction moveDir = pathBugHelper(rc, target);
            if(directBug || rotatingBug){
                return moveDir;
            }

        }

        if(rotatingBug && currentDist < lowestDist){
            if(wallLeft){
                Direction leftWallDir = lastBugDir.rotateLeft().rotateLeft();
                if(!currentPassable(rc, lastBugDir, rc.getLocation().add(leftWallDir))){
                    lowestDist = currentDist;
                    Direction moveDir = pathBugHelper(rc, target);
                    if(directBug || rotatingBug){
                        return moveDir;
                    }
                }

            } else {
                Direction rightWallDir = lastBugDir.rotateRight().rotateRight();
                if(!currentPassable(rc, lastBugDir, rc.getLocation().add(rightWallDir)) ){
                    lowestDist = currentDist;
                    Direction moveDir = pathBugHelper(rc, target);
                    if(directBug || rotatingBug){
                        return moveDir;
                    }
                }

            }
        }


        double lowestCost = 10000;

        Direction dir = null;

        Direction moveDir = loc.directionTo(target);
        Direction left = moveDir.rotateLeft();
        Direction right = moveDir.rotateRight();

        if(canMoveThrough(rc, moveDir, loc.add(moveDir) )){
            double cost = 10 * rc.senseMapInfo(loc.add(moveDir)).getCooldownMultiplier(rc.getTeam());
            cost += pathGreedyHelper(rc, loc.add(moveDir), target, depth-1);

            if(cost < lowestCost){
                dir = moveDir;
                lowestCost = cost;
            }
        }
        if(canMoveThrough(rc, left, loc.add(left) )){
            double cost = 10 * rc.senseMapInfo(loc.add(left)).getCooldownMultiplier(rc.getTeam());
            cost += pathGreedyHelper(rc, loc.add(left), target, depth-1);

            if(cost < lowestCost){
                dir = left;
                lowestCost = cost;
            }
        }
        if(canMoveThrough(rc, right, loc.add(right) )){
            double cost = 10 * rc.senseMapInfo(loc.add(right)).getCooldownMultiplier(rc.getTeam());
            cost += pathGreedyHelper(rc, loc.add(right), target, depth-1);

            if(cost < lowestCost){
                dir = right;
                lowestCost = cost;
            }
        }


        if(dir!= null && lowestCost <1000){
            return dir;
        } else{
            directBug = true;
            rotatingBug = false;
            turnedCurrentCorner = false;
            rc.setIndicatorString("greedy fail, pathing bug" + target);
            return pathBugHelper(rc, target);
        }

    }

    public static Direction pathBug(RobotController rc, MapLocation target)throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
        exploring = false;
        directBug = true;

        if(!target.equals(lastTarget)){
            rotatingBug = false;
            turnedCurrentCorner = false;
        }
        lastTarget=target;

        return pathBugHelper(rc, target);


    }

    public static Direction pathBugHelper(RobotController rc, MapLocation target) throws GameActionException {
        if(!rc.isMovementReady()){
            return Direction.CENTER;
        }
        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));

        if(rotatingBug && rotationCount>=4){
            rotationCount = 0;
            rotatingBug = false;
            turnedCurrentCorner = false;
            directBug = false;

        }
        if(rotatingBug){
            Direction moveDir = rc.getLocation().directionTo(target);
            if(wallLeft){
                Direction leftWallDir = lastBugDir.rotateLeft().rotateLeft();
                if(
                        canMoveThrough(rc, lastBugDir, rc.getLocation().add(leftWallDir) )
                        && canMoveThrough(rc, leftWallDir, rc.getLocation().add(leftWallDir)) &&
                        (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateRight().rotateRight(), rc.getLocation().add(leftWallDir)))){
                    if(!rc.onTheMap(rc.getLocation().add(leftWallDir))){
                        rotatingBug = false;
                        turnedCurrentCorner = false;
                        directBug = false;
                       // rc.setIndicatorString("off the map");
                    }
                    else if(robotWall){
                        robotWall = false;
                        return leftWallDir;
                    } else{
                        rotatingBug = false;
                        turnedCurrentCorner = false;
                    }
                    //rc.setIndicatorString("can move through now");
                }

                if(turnedCurrentCorner && currentPassable(rc, lastBugDir.rotateRight().rotateRight(), rc.getLocation().add(leftWallDir))){
                    turnedCurrentCorner = false;
                }
            } else{
                Direction rightWallDir = lastBugDir.rotateRight().rotateRight();
                if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(rightWallDir)) &&
                        canMoveThrough(rc, rightWallDir, rc.getLocation().add(rightWallDir)) &&
                        (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateLeft().rotateLeft(), rc.getLocation().add(rightWallDir))) ) {
                    if(!rc.onTheMap(rc.getLocation().add(rightWallDir))){
                        rotatingBug = false;
                        turnedCurrentCorner = false;
                        directBug = false;
                        //rc.setIndicatorString("off the map");
                    } else if(robotWall){
                        robotWall = false;
                        return rightWallDir;
                    } else{
                        rotatingBug = false;
                        turnedCurrentCorner = false;
                    }
                    //rc.setIndicatorString("can move through now");
                }

                if(turnedCurrentCorner && currentPassable(rc, lastBugDir.rotateLeft().rotateLeft(), rc.getLocation().add(rightWallDir))){
                    turnedCurrentCorner = false;
                }
            }
        }








        if(rotatingBug){
            if(wallLeft){

               // rc.setIndicatorDot(rc.getLocation().add(lastBugDir.rotateLeft().rotateLeft()), 255, 255, 0);
                int count = 0;
                while( count <4){
                    MapLocation moveTo = rc.getLocation().add(lastBugDir);
                    if(!rc.onTheMap(moveTo)){
                       // rc.setIndicatorString("off the map, swapping to wall right");
                        lastBugDir = lastBugDir.rotateRight().rotateRight().rotateRight().rotateRight();
                        rotationCount = 0;
                        wallLeft = false;
                        break;
                    }else if(canMoveThrough(rc, lastBugDir.rotateLeft(), rc.getLocation().add(lastBugDir.rotateLeft()))
                            && (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateRight().rotateRight(), rc.getLocation().add(lastBugDir.rotateLeft())))){
                        Direction ans = lastBugDir.rotateLeft();
                        Direction leftWallDir = lastBugDir.rotateLeft().rotateLeft();
                        if(!currentPassable(rc, lastBugDir, rc.getLocation().add(leftWallDir))){
                            turnedCurrentCorner = true;
                        }


                        lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                        rotationCount ++;
                        return ans;
                    } else if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(lastBugDir)) &&
                            (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateRight().rotateRight(), rc.getLocation().add(lastBugDir)))){
                        return lastBugDir;
                    }
                    lastBugDir = lastBugDir.rotateRight().rotateRight();
                    count++;
                }
            } else{

                rc.setIndicatorDot(rc.getLocation().add(lastBugDir.rotateRight().rotateRight()), 255, 255, 0);
                int count = 0;
                while( count <4){
                    MapLocation moveTo = rc.getLocation().add(lastBugDir);
                    if(!rc.onTheMap(moveTo)){
                    //    rc.setIndicatorString("off the map, swapping to wall left");
                        lastBugDir = lastBugDir.rotateRight().rotateRight().rotateRight().rotateRight();
                        rotationCount = 0;
                        wallLeft = true;
                        break;
                    } else if(canMoveThrough(rc, lastBugDir.rotateRight(), rc.getLocation().add(lastBugDir.rotateRight())) &&
                            (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateLeft().rotateLeft(), rc.getLocation().add(lastBugDir.rotateRight())))){

                        Direction ans = lastBugDir.rotateRight();

                        Direction rightWallDir = lastBugDir.rotateRight().rotateRight();

                        if(!currentPassable(rc, lastBugDir, rc.getLocation().add(rightWallDir))){
                            turnedCurrentCorner = true;
                        }

                        lastBugDir = lastBugDir.rotateRight().rotateRight();
                        rotationCount ++;
                        return ans;
                    } else if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(lastBugDir)) &&
                            (!turnedCurrentCorner || canMoveThrough(rc, lastBugDir.rotateLeft().rotateLeft(), rc.getLocation().add(lastBugDir)))){
                        return lastBugDir;
                    }
                    lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                    count++;
                }

            }
            return Direction.CENTER;

        } else if (directBug){
            robotWall = false;
            Direction moveDir = rc.getLocation().directionTo(target);
            directBug = true;
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
                        //rc.setIndicatorString("starting rotation wall right"+rc.getLocation()+" "+(lowestDist-currentDist));



                        return left;
                    } else{
                        if(right==Direction.NORTHEAST||right==Direction.NORTHWEST||right==Direction.SOUTHEAST||
                                right==Direction.SOUTHWEST){
                            lastBugDir= right.rotateLeft();
                        } else{
                            lastBugDir= right;
                        }

                      //  rc.setIndicatorString("starting rotation wall left"+rc.getLocation() +" "+(lowestDist-currentDist));


                        return right;

                    }

                } else{
                    return Direction.CENTER;
                }
            }
        }
        return Direction.CENTER;
    }

    static boolean canMoveThrough(RobotController rc, Direction dir, MapLocation loc) throws GameActionException{
        if(rc.canSenseLocation(loc) && rc.isLocationOccupied(loc)){
            robotWall = true;
        }
        if(rc.canSenseLocation(loc) && rc.sensePassability(loc) && !rc.isLocationOccupied(loc)){

            return currentPassable(rc, dir, loc);

        }
        return false;

    }

    static boolean currentPassable(RobotController rc, Direction dir, MapLocation loc) throws GameActionException {
        if(dir == null){
            return true;
        }

        if(rc.onTheMap(loc)){
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
