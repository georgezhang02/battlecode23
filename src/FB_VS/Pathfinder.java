package FB_VS;

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




    public static Direction pathBF(RobotController rc, MapLocation target) throws GameActionException {


        exploring = false;
        if(!target.equals(lastTarget)){
            rotatingBug = false;
            directBug = false;
        }
        lastTarget=target;
        rc.setIndicatorString("pathing to target BF");
        if(directBug || (rotatingBug && currentDist>=lowestDist)){
            rc.setIndicatorString("pathing to target bug");
            //rc.setIndicatorString("pathing bug, alreadypathing bug " + target);
            Direction moveDir = pathBugHelper(rc, target);
            if(directBug || rotatingBug){
                return moveDir;
            }

        }
        if(rc.senseCloud(rc.getLocation()) || Clock.getBytecodesLeft() < 7000){
            rc.setIndicatorString("pathing to target greedy");
            return pathGreedy(rc, target);
        }

        directBug = false;
        rotatingBug = false;
        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));
        Direction moveDir = BFPathing20.bfPathToTarget(rc, target);



        if(moveDir ==null || moveDir == Direction.CENTER || rc.getLocation().add(moveDir).equals(lastLocation)){
            directBug = true;
            rotatingBug = false;
            moveDir = pathBugHelper(rc, target);
        }
        lastLocation = rc.getLocation();
        return moveDir;


    }


    public static Direction pathToExplore(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        Direction dir = pathGreedy(rc, Explorer.target);
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
        Direction dir = pathBF(rc, Explorer.target);

        exploring = true;
        return dir;
    }

    public static Direction pathToExploreBug(RobotController rc) throws GameActionException {

        if(!exploring || rc.getLocation().distanceSquaredTo(Explorer.target) <= 16){
            Explorer.getExploreTarget(rc, 10, rc.getMapWidth(), rc.getMapHeight());
        }
        rc.setIndicatorLine(rc.getLocation(), Explorer.target, 0, 255, 255);
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
        Direction dir = pathGreedy(rc, Explorer.target);
        exploring = true;
        return dir;
    }



    public static Direction pathAwayFrom(RobotController rc, MapLocation target) throws GameActionException{
        exploring = false;
        MapLocation curLoc = rc.getLocation();
        int distXToTarget = target.x - curLoc.x;
        int distYToTarget = target.y - curLoc.y;

        MapLocation runawayTarget = new MapLocation(curLoc.x-distXToTarget, curLoc.y-distYToTarget);
        return pathGreedy(rc, runawayTarget);

    }
    public static Direction pathGreedy(RobotController rc, MapLocation target)throws GameActionException {
        exploring = false;
        return pathGreedyDepth(rc, rc.getLocation(),  target, 2);

    }

    public static double pathGreedyHelper(RobotController rc, MapLocation loc, MapLocation target, int depth) throws GameActionException {

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
        exploring = false;
        if(!target.equals(lastTarget)){
            rotatingBug = false;
            directBug = false;
        }
        lastTarget=target;

        if(directBug || (rotatingBug && currentDist>=lowestDist)){
            //rc.setIndicatorString("pathing bug, alreadypathing bug " + target);
            Direction moveDir = pathBugHelper(rc, target);
            if(directBug || rotatingBug){
                return moveDir;
            }

        }

        rc.setIndicatorString("pathing Greedy" + target);

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
            //rc.setIndicatorString("greedy fail, pathing bug" + target);
            return pathBugHelper(rc, target);
        }

    }

    public static Direction pathBug(RobotController rc, MapLocation target)throws GameActionException {
        exploring = false;
        directBug = true;

        if(!target.equals(lastTarget)){
            rotatingBug = false;
        }
        lastTarget=target;

        return pathBugHelper(rc, target);


    }

    public static Direction pathBugHelper(RobotController rc, MapLocation target) throws GameActionException {
        currentDist = Math.sqrt(rc.getLocation().distanceSquaredTo(target));


        if(rotatingBug && rotationCount>=4){
            rotationCount = 0;
            rotatingBug = false;
            directBug = false;

        }

        if(rotatingBug){
            Direction moveDir = rc.getLocation().directionTo(target);
            if(wallLeft){
                Direction leftWallDir = lastBugDir.rotateLeft().rotateLeft();
                if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(leftWallDir) )
                        && canMoveThrough(rc, leftWallDir, rc.getLocation().add(leftWallDir))) {
                    if(!rc.onTheMap(rc.getLocation().add(leftWallDir))){
                        rotatingBug = false;
                        directBug = false;
                        rc.setIndicatorString("off the map");
                    }
                    else if(robotWall){
                        robotWall = false;
                        return leftWallDir;
                    } else{
                        rotatingBug = false;
                    }
                    //rc.setIndicatorString("can move through now");
                }
            } else{
                Direction rightWallDir = lastBugDir.rotateRight().rotateRight();
                if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(rightWallDir)) &&
                        canMoveThrough(rc, rightWallDir, rc.getLocation().add(rightWallDir))) {
                    if(!rc.onTheMap(rc.getLocation().add(rightWallDir))){
                        rotatingBug = false;
                        directBug = false;
                        rc.setIndicatorString("off the map");
                    } else if(robotWall){
                        robotWall = false;
                        return rightWallDir;
                    } else{
                        rotatingBug = false;
                    }
                    //rc.setIndicatorString("can move through now");
                }
            }
        }

        if(rotatingBug){
            if(wallLeft){

                rc.setIndicatorDot(rc.getLocation().add(lastBugDir.rotateLeft().rotateLeft()), 255, 255, 0);
                int count = 0;
                while( count <4){
                    MapLocation moveTo = rc.getLocation().add(lastBugDir);
                    if(!rc.onTheMap(moveTo)){
                        rc.setIndicatorString("off the map, swapping to wall right");
                        lastBugDir = lastBugDir.rotateRight().rotateRight().rotateRight().rotateRight();
                        rotationCount = 0;
                        wallLeft = false;
                        break;
                    }else if(canMoveThrough(rc, lastBugDir.rotateLeft(), rc.getLocation().add(lastBugDir.rotateLeft()))){
                        Direction ans = lastBugDir.rotateLeft();
                        lastBugDir = lastBugDir.rotateLeft().rotateLeft();
                        rc.setIndicatorString("turning corner " + target+" "
                            +currentDist +" "+lowestDist);
                        rotationCount ++;
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
                    MapLocation moveTo = rc.getLocation().add(lastBugDir);
                    if(!rc.onTheMap(moveTo)){
                        rc.setIndicatorString("off the map, swapping to wall left");
                        lastBugDir = lastBugDir.rotateRight().rotateRight().rotateRight().rotateRight();
                        rotationCount = 0;
                        wallLeft = true;
                        break;
                    } else if(canMoveThrough(rc, lastBugDir.rotateRight(), rc.getLocation().add(lastBugDir.rotateRight()))){
                        Direction ans = lastBugDir.rotateRight();
                        lastBugDir = lastBugDir.rotateRight().rotateRight();
                        rotationCount ++;
                        rc.setIndicatorString("turning corner " + target+" "
                                +currentDist);
                        return ans;
                    } else if(canMoveThrough(rc, lastBugDir, rc.getLocation().add(lastBugDir))){
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
                        //rc.setIndicatorString("starting rotation wall right"+target.toString() +" "+lastBugDir.toString()+ " ");



                        return left;
                    } else{
                        if(right==Direction.NORTHEAST||right==Direction.NORTHWEST||right==Direction.SOUTHEAST||
                                right==Direction.SOUTHWEST){
                            lastBugDir= right.rotateLeft();
                        } else{
                            lastBugDir= right;
                        }

                        //rc.setIndicatorString("starting rotation wall left"+target.toString() +" "+lastBugDir.toString()+ " ");


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
            MapInfo info = rc.senseMapInfo(loc);
            Direction current = info.getCurrentDirection();
            if(current != Direction.CENTER){

                Direction opposite = dir.opposite();
                Direction oppositeLeft = opposite.rotateLeft();
                Direction oppositeRight = opposite.rotateRight();
                if(current == opposite || current == oppositeLeft || current == oppositeRight){
                    /*if(rc.getType().equals(RobotType.CARRIER) &&
                            (rc.getResourceAmount(ResourceType.ADAMANTIUM)
                                    + rc.getResourceAmount(ResourceType.MANA)
                                    + rc.getResourceAmount(ResourceType.ELIXIR) <= 8)) {
                        return true;
                    }*/
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;

    }
}
