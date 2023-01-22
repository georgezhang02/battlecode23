package CB_commstest;

import battlecode.common.*;
import battlecode.world.Inventory;

import javax.xml.crypto.Data;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;


    public static void run(RobotController rc) throws GameActionException {
        

        while (true) {

            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                String toPrint = "";
                if(!Comms.isCommsCleaned(rc)){
                    Comms.wipeComms(rc);
                }
                if(turnCount==1){
                    Database.init(rc);
                    if(rc.getType().equals(RobotType.HEADQUARTERS)){
                        Comms.setTeamHQLocation(rc, rc.getLocation(), rc.getID());
                    }
                }

                if(turnCount>1){
                    Database.downloadLocations(rc);

                    toPrint = toPrint + " "+Database.vertical;
                    toPrint = toPrint + " "+Database.horizontal;
                    toPrint = toPrint + " "+Database.rotational;

                }

                if(turnCount >1){
                    RobotInfo[]enemies = rc.senseNearbyRobots(RobotType.HEADQUARTERS.visionRadiusSquared, rc.getTeam().opponent());

                    for(RobotInfo enemy : enemies ){
                        if(enemy.getType().equals(RobotType.HEADQUARTERS)){
                            Database.addEnemyHQ(rc, enemy);
                        }
                    }
                }
                rc.setIndicatorString(toPrint);







            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }
}
