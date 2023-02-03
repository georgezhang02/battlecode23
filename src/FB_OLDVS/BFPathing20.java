package FB_OLDVS;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public strictfp class BFPathing20 {
    static Direction dir9;
    static Direction dir10;
    static Direction dir11;
    static Direction dir12;
    static Direction dir13;
    static Direction dir14;
    static Direction dir15;
    static Direction dir16;
    static Direction dir17;
    static Direction dir18;
    static Direction dir19;
    static Direction dir20;
    static Direction dir21;
    static Direction dir22;
    static Direction dir23;
    static Direction dir24;
    static Direction dir25;
    static Direction dir26;
    static Direction dir27;
    static Direction dir28;
    static Direction dir29;
    static Direction dir30;
    static Direction dir31;
    static Direction dir32;
    static Direction dir33;
    static Direction dir34;
    static Direction dir35;
    static Direction dir36;
    static Direction dir37;
    static Direction dir38;
    static Direction dir39;
    static Direction dir40;
    static Direction dir41;
    static Direction dir42;
    static Direction dir43;
    static Direction dir44;
    static Direction dir45;
    static Direction dir46;
    static Direction dir47;
    static Direction dir48;
    static Direction dir49;
    static Direction dir50;
    static Direction dir51;
    static Direction dir52;
    static Direction dir53;
    static Direction dir54;
    static Direction dir55;
    static Direction dir56;
    static Direction dir57;
    static Direction dir58;
    static Direction dir59;
    static Direction dir60;
    static Direction dir61;
    static Direction dir62;
    static Direction dir63;
    static Direction dir64;
    static Direction dir65;
    static Direction dir66;
    static Direction dir67;
    static Direction dir68;
    static double dist1;
    static double dist2;
    static double dist3;
    static double dist4;
    static double dist5;
    static double dist6;
    static double dist7;
    static double dist8;
    static double dist9;
    static double dist10;
    static double dist11;
    static double dist12;
    static double dist13;
    static double dist14;
    static double dist15;
    static double dist16;
    static double dist17;
    static double dist18;
    static double dist19;
    static double dist20;
    static double dist21;
    static double dist22;
    static double dist23;
    static double dist24;
    static double dist25;
    static double dist26;
    static double dist27;
    static double dist28;
    static double dist29;
    static double dist30;
    static double dist31;
    static double dist32;
    static double dist33;
    static double dist34;
    static double dist35;
    static double dist36;
    static double dist37;
    static double dist38;
    static double dist39;
    static double dist40;
    static double dist41;
    static double dist42;
    static double dist43;
    static double dist44;
    static double dist45;
    static double dist46;
    static double dist47;
    static double dist48;
    static double dist49;
    static double dist50;
    static double dist51;
    static double dist52;
    static double dist53;
    static double dist54;
    static double dist55;
    static double dist56;
    static double dist57;
    static double dist58;
    static double dist59;
    static double dist60;
    static double dist61;
    static double dist62;
    static double dist63;
    static double dist64;
    static double dist65;
    static double dist66;
    static double dist67;
    static double dist68;
    static double cost9;
    static double cost10;
    static double cost11;
    static double cost12;
    static double cost13;
    static double cost14;
    static double cost15;
    static double cost16;
    static double cost17;
    static double cost18;
    static double cost19;
    static double cost20;
    static double cost21;
    static double cost22;
    static double cost23;
    static double cost24;
    static double cost25;
    static double cost26;
    static double cost27;
    static double cost28;
    static double cost29;
    static double cost30;
    static double cost31;
    static double cost32;
    static double cost33;
    static double cost34;
    static double cost35;
    static double cost36;
    static double cost37;
    static double cost38;
    static double cost39;
    static double cost40;
    static double cost41;
    static double cost42;
    static double cost43;
    static double cost44;
    static double cost45;
    static double cost46;
    static double cost47;
    static double cost48;
    static double cost49;
    static double cost50;
    static double cost51;
    static double cost52;
    static double cost53;
    static double cost54;
    static double cost55;
    static double cost56;
    static double cost57;
    static double cost58;
    static double cost59;
    static double cost60;
    static double cost61;
    static double cost62;
    static double cost63;
    static double cost64;
    static double cost65;
    static double cost66;
    static double cost67;
    static double cost68;
    static MapLocation ml1;
    static MapLocation ml2;
    static MapLocation ml3;
    static MapLocation ml4;
    static MapLocation ml5;
    static MapLocation ml6;
    static MapLocation ml7;
    static MapLocation ml8;
    static MapLocation ml9;
    static MapLocation ml10;
    static MapLocation ml11;
    static MapLocation ml12;
    static MapLocation ml13;
    static MapLocation ml14;
    static MapLocation ml15;
    static MapLocation ml16;
    static MapLocation ml17;
    static MapLocation ml18;
    static MapLocation ml19;
    static MapLocation ml20;
    static MapLocation ml21;
    static MapLocation ml22;
    static MapLocation ml23;
    static MapLocation ml24;
    static MapLocation ml25;
    static MapLocation ml26;
    static MapLocation ml27;
    static MapLocation ml28;
    static MapLocation ml29;
    static MapLocation ml30;
    static MapLocation ml31;
    static MapLocation ml32;
    static MapLocation ml33;
    static MapLocation ml34;
    static MapLocation ml35;
    static MapLocation ml36;
    static MapLocation ml37;
    static MapLocation ml38;
    static MapLocation ml39;
    static MapLocation ml40;
    static MapLocation ml41;
    static MapLocation ml42;
    static MapLocation ml43;
    static MapLocation ml44;
    static MapLocation ml45;
    static MapLocation ml46;
    static MapLocation ml47;
    static MapLocation ml48;
    static MapLocation ml49;
    static MapLocation ml50;
    static MapLocation ml51;
    static MapLocation ml52;
    static MapLocation ml53;
    static MapLocation ml54;
    static MapLocation ml55;
    static MapLocation ml56;
    static MapLocation ml57;
    static MapLocation ml58;
    static MapLocation ml59;
    static MapLocation ml60;
    static MapLocation ml61;
    static MapLocation ml62;
    static MapLocation ml63;
    static MapLocation ml64;
    static MapLocation ml65;
    static MapLocation ml66;
    static MapLocation ml67;
    static MapLocation ml68;
    static Direction dir1 = Direction.SOUTH;
    static Direction dir2 = Direction.SOUTHEAST;
    static Direction dir3 = Direction.EAST;
    static Direction dir4 = Direction.NORTHEAST;
    static Direction dir5 = Direction.NORTH;
    static Direction dir6 = Direction.NORTHWEST;
    static Direction dir7 = Direction.WEST;
    static Direction dir8 = Direction.SOUTHWEST;


    static Direction
    bfPathToTarget(RobotController rc, MapLocation target) throws GameActionException {
        dist1 = 10000;
        dist2 = 10000;
        dist3 = 10000;
        dist4 = 10000;
        dist5 = 10000;
        dist6 = 10000;
        dist7 = 10000;
        dist8 = 10000;
        dist9 = 10000;
        dist10 = 10000;
        dist11 = 10000;
        dist12 = 10000;
        dist13 = 10000;
        dist14 = 10000;
        dist15 = 10000;
        dist16 = 10000;
        dist17 = 10000;
        dist18 = 10000;
        dist19 = 10000;
        dist20 = 10000;
        dist21 = 10000;
        dist22 = 10000;
        dist23 = 10000;
        dist24 = 10000;
        dist25 = 10000;
        dist26 = 10000;
        dist27 = 10000;
        dist28 = 10000;
        dist29 = 10000;
        dist30 = 10000;
        dist31 = 10000;
        dist32 = 10000;
        dist33 = 10000;
        dist34 = 10000;
        dist35 = 10000;
        dist36 = 10000;
        dist37 = 10000;
        dist38 = 10000;
        dist39 = 10000;
        dist40 = 10000;
        dist41 = 10000;
        dist42 = 10000;
        dist43 = 10000;
        dist44 = 10000;
        dist45 = 10000;
        dist46 = 10000;
        dist47 = 10000;
        dist48 = 10000;
        dist49 = 10000;
        dist50 = 10000;
        dist51 = 10000;
        dist52 = 10000;
        dist53 = 10000;
        dist54 = 10000;
        dist55 = 10000;
        dist56 = 10000;
        dist57 = 10000;
        dist58 = 10000;
        dist59 = 10000;
        dist60 = 10000;
        dist61 = 10000;
        dist62 = 10000;
        dist63 = 10000;
        dist64 = 10000;
        dist65 = 10000;
        dist66 = 10000;
        dist67 = 10000;
        dist68 = 10000;
        cost9 = 0;
        cost10 = 0;
        cost11 = 0;
        cost12 = 0;
        cost13 = 0;
        cost14 = 0;
        cost15 = 0;
        cost16 = 0;
        cost17 = 0;
        cost18 = 0;
        cost19 = 0;
        cost20 = 0;
        cost21 = 0;
        cost22 = 0;
        cost23 = 0;
        cost24 = 0;
        cost25 = 0;
        cost26 = 0;
        cost27 = 0;
        cost28 = 0;
        cost29 = 0;
        cost30 = 0;
        cost31 = 0;
        cost32 = 0;
        cost33 = 0;
        cost34 = 0;
        cost35 = 0;
        cost36 = 0;
        cost37 = 0;
        cost38 = 0;
        cost39 = 0;
        cost40 = 0;
        cost41 = 0;
        cost42 = 0;
        cost43 = 0;
        cost44 = 0;
        cost45 = 0;
        cost46 = 0;
        cost47 = 0;
        cost48 = 0;
        cost49 = 0;
        cost50 = 0;
        cost51 = 0;
        cost52 = 0;
        cost53 = 0;
        cost54 = 0;
        cost55 = 0;
        cost56 = 0;
        cost57 = 0;
        cost58 = 0;
        cost59 = 0;
        cost60 = 0;
        cost61 = 0;
        cost62 = 0;
        cost63 = 0;
        cost64 = 0;
        cost65 = 0;
        cost66 = 0;
        cost67 = 0;
        cost68 = 0;


        MapLocation ml0 = rc.getLocation();
        if (ml0.equals(target)) return Direction.CENTER;



        ml1 = ml0.add(Direction.SOUTH);
        ml2 = ml1.add(Direction.EAST);
        ml3 = ml2.add(Direction.NORTH);
        ml4 = ml3.add(Direction.NORTH);
        ml5 = ml4.add(Direction.WEST);
        ml6 = ml5.add(Direction.WEST);
        ml7 = ml6.add(Direction.SOUTH);
        ml8 = ml7.add(Direction.SOUTH);
        ml9 = ml8.add(Direction.SOUTH);
        ml10 = ml9.add(Direction.EAST);
        ml11 = ml10.add(Direction.EAST);
        ml12 = ml11.add(Direction.EAST);
        ml13 = ml12.add(Direction.NORTH);
        ml14 = ml13.add(Direction.NORTH);
        ml15 = ml14.add(Direction.NORTH);
        ml16 = ml15.add(Direction.NORTH);
        ml17 = ml16.add(Direction.WEST);
        ml18 = ml17.add(Direction.WEST);
        ml19 = ml18.add(Direction.WEST);
        ml20 = ml19.add(Direction.WEST);
        ml21 = ml20.add(Direction.SOUTH);
        ml22 = ml21.add(Direction.SOUTH);
        ml23 = ml22.add(Direction.SOUTH);
        ml24 = ml23.add(Direction.SOUTH);
        ml25 = ml24.add(Direction.SOUTH);
        ml26 = ml25.add(Direction.EAST);
        ml27 = ml26.add(Direction.EAST);
        ml28 = ml27.add(Direction.EAST);
        ml29 = ml28.add(Direction.EAST);
        ml30 = ml29.add(Direction.NORTHEAST);
        ml31 = ml30.add(Direction.NORTH);
        ml32 = ml31.add(Direction.NORTH);
        ml33 = ml32.add(Direction.NORTH);
        ml34 = ml33.add(Direction.NORTH);
        ml35 = ml34.add(Direction.NORTHWEST);
        ml36 = ml35.add(Direction.WEST);
        ml37 = ml36.add(Direction.WEST);
        ml38 = ml37.add(Direction.WEST);
        ml39 = ml38.add(Direction.WEST);
        ml40 = ml39.add(Direction.SOUTHWEST);
        ml41 = ml40.add(Direction.SOUTH);
        ml42 = ml41.add(Direction.SOUTH);
        ml43 = ml42.add(Direction.SOUTH);
        ml44 = ml43.add(Direction.SOUTH);
        ml45 = ml44.add(Direction.SOUTH);
        ml46 = ml45.add(Direction.SOUTHEAST);
        ml47 = ml46.add(Direction.EAST);
        ml48 = ml47.add(Direction.EAST);
        ml49 = ml48.add(Direction.EAST);
        ml50 = ml49.add(Direction.EAST);
        ml51 = ml50.add(Direction.NORTHEAST);
        ml52 = ml51.add(Direction.NORTHEAST);
        ml53 = ml52.add(Direction.NORTH);
        ml54 = ml53.add(Direction.NORTH);
        ml55 = ml54.add(Direction.NORTH);
        ml56 = ml55.add(Direction.NORTH);
        ml57 = ml56.add(Direction.NORTHWEST);
        ml58 = ml57.add(Direction.NORTHWEST);
        ml59 = ml58.add(Direction.WEST);
        ml60 = ml59.add(Direction.WEST);
        ml61 = ml60.add(Direction.WEST);
        ml62 = ml61.add(Direction.WEST);
        ml63 = ml62.add(Direction.SOUTHWEST);
        ml64 = ml63.add(Direction.SOUTHWEST);
        ml65 = ml64.add(Direction.SOUTH);
        ml66 = ml65.add(Direction.SOUTH);
        ml67 = ml66.add(Direction.SOUTH);
        ml68 = ml67.add(Direction.SOUTH);
        if(rc.canSenseLocation(ml1) && rc.sensePassability(ml1)){
            if(!rc.isLocationOccupied(ml1)){
                dist1 = rc.senseMapInfo(ml1).getCooldownMultiplier(rc.getTeam());;
            }
        }
        if(rc.canSenseLocation(ml2) && rc.sensePassability(ml2)){
            if(!rc.isLocationOccupied(ml2)){
                dist2 = rc.senseMapInfo(ml2).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml3) && rc.sensePassability(ml3)){
            if(!rc.isLocationOccupied(ml3)){
                dist3 = rc.senseMapInfo(ml3).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml4) && rc.sensePassability(ml4)){
            if(!rc.isLocationOccupied(ml4)){
                dist4 = rc.senseMapInfo(ml4).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml5) && rc.sensePassability(ml5)){
            if(!rc.isLocationOccupied(ml5)){
                dist5 = rc.senseMapInfo(ml5).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml6) && rc.sensePassability(ml6)){
            if(!rc.isLocationOccupied(ml6)){
                dist6 = rc.senseMapInfo(ml6).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml7) && rc.sensePassability(ml7)){
            if(!rc.isLocationOccupied(ml7)){
                dist7 = rc.senseMapInfo(ml7).getCooldownMultiplier(rc.getTeam());
            }
        }
        if(rc.canSenseLocation(ml8) && rc.sensePassability(ml8)){
            if(!rc.isLocationOccupied(ml8)){
                dist8 = rc.senseMapInfo(ml8).getCooldownMultiplier(rc.getTeam());
            }
        }

        if(rc.onTheMap(ml9) && (rc.senseCloud(ml9) || rc.sensePassability(ml9))){
            cost9 = 10;
            if(cost9 + dist8 < dist9){
                dist9 = cost9 + dist8;
                dir9 = dir8;
            }
            if(cost9 + dist1 < dist9){
                dist9 = cost9 + dist1;
                dir9 = dir1;
            }

        }
        if(rc.onTheMap(ml10) && (rc.senseCloud(ml10) || rc.sensePassability(ml10))){
            cost10 = 10;
            if(cost10 + dist8 < dist10){
                dist10 = cost10 + dist8;
                dir10 = dir8;
            }
            if(cost10 + dist1 < dist10){
                dist10 = cost10 + dist1;
                dir10 = dir1;
            }
            if(cost10 + dist2 < dist10){
                dist10 = cost10 + dist2;
                dir10 = dir2;
            }

        }
        if(rc.onTheMap(ml11) && (rc.senseCloud(ml11) || rc.sensePassability(ml11))){
            cost11 = 10;
            if(cost11 + dist1 < dist11){
                dist11 = cost11 + dist1;
                dir11 = dir1;
            }
            if(cost11 + dist2 < dist11){
                dist11 = cost11 + dist2;
                dir11 = dir2;
            }
            if(cost11 + dist10 < dist11){
                dist11 = cost11 + dist10;
                dir11 = dir10;
            }

        }
        if(rc.onTheMap(ml12) && (rc.senseCloud(ml12) || rc.sensePassability(ml12))){
            cost12 = 10;
            if(cost12 + dist2 < dist12){
                dist12 = cost12 + dist2;
                dir12 = dir2;
            }
            if(cost12 + dist11 < dist12){
                dist12 = cost12 + dist11;
                dir12 = dir11;
            }

        }
        if(rc.onTheMap(ml13) && (rc.senseCloud(ml13) || rc.sensePassability(ml13))){
            cost13 = 10;
            if(cost13 + dist3 < dist13){
                dist13 = cost13 + dist3;
                dir13 = dir3;
            }
            if(cost13 + dist2 < dist13){
                dist13 = cost13 + dist2;
                dir13 = dir2;
            }
            if(cost13 + dist11 < dist13){
                dist13 = cost13 + dist11;
                dir13 = dir11;
            }

        }
        if(rc.onTheMap(ml14) && (rc.senseCloud(ml14) || rc.sensePassability(ml14))){
            cost14 = 10;
            if(cost14 + dist4 < dist14){
                dist14 = cost14 + dist4;
                dir14 = dir4;
            }
            if(cost14 + dist3 < dist14){
                dist14 = cost14 + dist3;
                dir14 = dir3;
            }
            if(cost14 + dist2 < dist14){
                dist14 = cost14 + dist2;
                dir14 = dir2;
            }

        }
        if(rc.onTheMap(ml15) && (rc.senseCloud(ml15) || rc.sensePassability(ml15))){
            cost15 = 10;
            if(cost15 + dist4 < dist15){
                dist15 = cost15 + dist4;
                dir15 = dir4;
            }
            if(cost15 + dist3 < dist15){
                dist15 = cost15 + dist3;
                dir15 = dir3;
            }
            if(cost15 + dist14 < dist15){
                dist15 = cost15 + dist14;
                dir15 = dir14;
            }

        }
        if(rc.onTheMap(ml16) && (rc.senseCloud(ml16) || rc.sensePassability(ml16))){
            cost16 = 10;
            if(cost16 + dist4 < dist16){
                dist16 = cost16 + dist4;
                dir16 = dir4;
            }
            if(cost16 + dist15 < dist16){
                dist16 = cost16 + dist15;
                dir16 = dir15;
            }

        }
        if(rc.onTheMap(ml17) && (rc.senseCloud(ml17) || rc.sensePassability(ml17))){
            cost17 = 10;
            if(cost17 + dist5 < dist17){
                dist17 = cost17 + dist5;
                dir17 = dir5;
            }
            if(cost17 + dist4 < dist17){
                dist17 = cost17 + dist4;
                dir17 = dir4;
            }
            if(cost17 + dist15 < dist17){
                dist17 = cost17 + dist15;
                dir17 = dir15;
            }

        }
        if(rc.onTheMap(ml18) && (rc.senseCloud(ml18) || rc.sensePassability(ml18))){
            cost18 = 10;
            if(cost18 + dist6 < dist18){
                dist18 = cost18 + dist6;
                dir18 = dir6;
            }
            if(cost18 + dist5 < dist18){
                dist18 = cost18 + dist5;
                dir18 = dir5;
            }
            if(cost18 + dist4 < dist18){
                dist18 = cost18 + dist4;
                dir18 = dir4;
            }

        }
        if(rc.onTheMap(ml19) && (rc.senseCloud(ml19) || rc.sensePassability(ml19))){
            cost19 = 10;
            if(cost19 + dist18 < dist19){
                dist19 = cost19 + dist18;
                dir19 = dir18;
            }
            if(cost19 + dist6 < dist19){
                dist19 = cost19 + dist6;
                dir19 = dir6;
            }
            if(cost19 + dist5 < dist19){
                dist19 = cost19 + dist5;
                dir19 = dir5;
            }

        }
        if(rc.onTheMap(ml20) && (rc.senseCloud(ml20) || rc.sensePassability(ml20))){
            cost20 = 10;
            if(cost20 + dist19 < dist20){
                dist20 = cost20 + dist19;
                dir20 = dir19;
            }
            if(cost20 + dist6 < dist20){
                dist20 = cost20 + dist6;
                dir20 = dir6;
            }

        }
        if(rc.onTheMap(ml21) && (rc.senseCloud(ml21) || rc.sensePassability(ml21))){
            cost21 = 10;
            if(cost21 + dist19 < dist21){
                dist21 = cost21 + dist19;
                dir21 = dir19;
            }
            if(cost21 + dist6 < dist21){
                dist21 = cost21 + dist6;
                dir21 = dir6;
            }
            if(cost21 + dist7 < dist21){
                dist21 = cost21 + dist7;
                dir21 = dir7;
            }

        }
        if(rc.onTheMap(ml22) && (rc.senseCloud(ml22) || rc.sensePassability(ml22))){
            cost22 = 10;
            if(cost22 + dist6 < dist22){
                dist22 = cost22 + dist6;
                dir22 = dir6;
            }
            if(cost22 + dist7 < dist22){
                dist22 = cost22 + dist7;
                dir22 = dir7;
            }
            if(cost22 + dist8 < dist22){
                dist22 = cost22 + dist8;
                dir22 = dir8;
            }

        }
        if(rc.onTheMap(ml23) && (rc.senseCloud(ml23) || rc.sensePassability(ml23))){
            cost23 = 10;
            if(cost23 + dist22 < dist23){
                dist23 = cost23 + dist22;
                dir23 = dir22;
            }
            if(cost23 + dist7 < dist23){
                dist23 = cost23 + dist7;
                dir23 = dir7;
            }
            if(cost23 + dist8 < dist23){
                dist23 = cost23 + dist8;
                dir23 = dir8;
            }
            if(cost23 + dist9 < dist23){
                dist23 = cost23 + dist9;
                dir23 = dir9;
            }

        }
        if(rc.onTheMap(ml24) && (rc.senseCloud(ml24) || rc.sensePassability(ml24))){
            cost24 = 10;
            if(cost24 + dist23 < dist24){
                dist24 = cost24 + dist23;
                dir24 = dir23;
            }
            if(cost24 + dist8 < dist24){
                dist24 = cost24 + dist8;
                dir24 = dir8;
            }
            if(cost24 + dist9 < dist24){
                dist24 = cost24 + dist9;
                dir24 = dir9;
            }

        }
        if(rc.onTheMap(ml9) && (rc.senseCloud(ml9) || rc.sensePassability(ml9))){
            if(cost9 + dist23 < dist9){
                dist9 = cost9 + dist23;
                dir9 = dir23;
            }
            if(cost9 + dist10 < dist9){
                dist9 = cost9 + dist10;
                dir9 = dir10;
            }

        }
        if(rc.onTheMap(ml11) && (rc.senseCloud(ml11) || rc.sensePassability(ml11))){
            if(cost11 + dist13 < dist11){
                dist11 = cost11 + dist13;
                dir11 = dir13;
            }

        }
        if(rc.onTheMap(ml12) && (rc.senseCloud(ml12) || rc.sensePassability(ml12))){
            if(cost12 + dist13 < dist12){
                dist12 = cost12 + dist13;
                dir12 = dir13;
            }

        }
        if(rc.onTheMap(ml13) && (rc.senseCloud(ml13) || rc.sensePassability(ml13))){
            if(cost13 + dist14 < dist13){
                dist13 = cost13 + dist14;
                dir13 = dir14;
            }

        }
        if(rc.onTheMap(ml15) && (rc.senseCloud(ml15) || rc.sensePassability(ml15))){
            if(cost15 + dist17 < dist15){
                dist15 = cost15 + dist17;
                dir15 = dir17;
            }

        }
        if(rc.onTheMap(ml16) && (rc.senseCloud(ml16) || rc.sensePassability(ml16))){
            if(cost16 + dist17 < dist16){
                dist16 = cost16 + dist17;
                dir16 = dir17;
            }

        }
        if(rc.onTheMap(ml17) && (rc.senseCloud(ml17) || rc.sensePassability(ml17))){
            if(cost17 + dist18 < dist17){
                dist17 = cost17 + dist18;
                dir17 = dir18;
            }

        }
        if(rc.onTheMap(ml19) && (rc.senseCloud(ml19) || rc.sensePassability(ml19))){
            if(cost19 + dist21 < dist19){
                dist19 = cost19 + dist21;
                dir19 = dir21;
            }

        }
        if(rc.onTheMap(ml20) && (rc.senseCloud(ml20) || rc.sensePassability(ml20))){
            if(cost20 + dist21 < dist20){
                dist20 = cost20 + dist21;
                dir20 = dir21;
            }

        }
        if(rc.onTheMap(ml21) && (rc.senseCloud(ml21) || rc.sensePassability(ml21))){
            if(cost21 + dist22 < dist21){
                dist21 = cost21 + dist22;
                dir21 = dir22;
            }

        }
        if(rc.onTheMap(ml25) && (rc.senseCloud(ml25) || rc.sensePassability(ml25))){
            cost25 = 10;
            if(cost25 + dist24 < dist25){
                dist25 = cost25 + dist24;
                dir25 = dir24;
            }
            if(cost25 + dist9 < dist25){
                dist25 = cost25 + dist9;
                dir25 = dir9;
            }

        }
        if(rc.onTheMap(ml26) && (rc.senseCloud(ml26) || rc.sensePassability(ml26))){
            cost26 = 10;
            if(cost26 + dist24 < dist26){
                dist26 = cost26 + dist24;
                dir26 = dir24;
            }
            if(cost26 + dist9 < dist26){
                dist26 = cost26 + dist9;
                dir26 = dir9;
            }
            if(cost26 + dist10 < dist26){
                dist26 = cost26 + dist10;
                dir26 = dir10;
            }

        }
        if(rc.onTheMap(ml27) && (rc.senseCloud(ml27) || rc.sensePassability(ml27))){
            cost27 = 10;
            if(cost27 + dist9 < dist27){
                dist27 = cost27 + dist9;
                dir27 = dir9;
            }
            if(cost27 + dist10 < dist27){
                dist27 = cost27 + dist10;
                dir27 = dir10;
            }
            if(cost27 + dist11 < dist27){
                dist27 = cost27 + dist11;
                dir27 = dir11;
            }

        }
        if(rc.onTheMap(ml28) && (rc.senseCloud(ml28) || rc.sensePassability(ml28))){
            cost28 = 10;
            if(cost28 + dist10 < dist28){
                dist28 = cost28 + dist10;
                dir28 = dir10;
            }
            if(cost28 + dist11 < dist28){
                dist28 = cost28 + dist11;
                dir28 = dir11;
            }
            if(cost28 + dist12 < dist28){
                dist28 = cost28 + dist12;
                dir28 = dir12;
            }
            if(cost28 + dist27 < dist28){
                dist28 = cost28 + dist27;
                dir28 = dir27;
            }

        }
        if(rc.onTheMap(ml29) && (rc.senseCloud(ml29) || rc.sensePassability(ml29))){
            cost29 = 10;
            if(cost29 + dist11 < dist29){
                dist29 = cost29 + dist11;
                dir29 = dir11;
            }
            if(cost29 + dist12 < dist29){
                dist29 = cost29 + dist12;
                dir29 = dir12;
            }
            if(cost29 + dist28 < dist29){
                dist29 = cost29 + dist28;
                dir29 = dir28;
            }

        }
        if(rc.onTheMap(ml30) && (rc.senseCloud(ml30) || rc.sensePassability(ml30))){
            cost30 = 10;
            if(cost30 + dist13 < dist30){
                dist30 = cost30 + dist13;
                dir30 = dir13;
            }
            if(cost30 + dist12 < dist30){
                dist30 = cost30 + dist12;
                dir30 = dir12;
            }
            if(cost30 + dist29 < dist30){
                dist30 = cost30 + dist29;
                dir30 = dir29;
            }

        }
        if(rc.onTheMap(ml31) && (rc.senseCloud(ml31) || rc.sensePassability(ml31))){
            cost31 = 10;
            if(cost31 + dist14 < dist31){
                dist31 = cost31 + dist14;
                dir31 = dir14;
            }
            if(cost31 + dist13 < dist31){
                dist31 = cost31 + dist13;
                dir31 = dir13;
            }
            if(cost31 + dist12 < dist31){
                dist31 = cost31 + dist12;
                dir31 = dir12;
            }

        }
        if(rc.onTheMap(ml32) && (rc.senseCloud(ml32) || rc.sensePassability(ml32))){
            cost32 = 10;
            if(cost32 + dist15 < dist32){
                dist32 = cost32 + dist15;
                dir32 = dir15;
            }
            if(cost32 + dist14 < dist32){
                dist32 = cost32 + dist14;
                dir32 = dir14;
            }
            if(cost32 + dist13 < dist32){
                dist32 = cost32 + dist13;
                dir32 = dir13;
            }

        }
        if(rc.onTheMap(ml33) && (rc.senseCloud(ml33) || rc.sensePassability(ml33))){
            cost33 = 10;
            if(cost33 + dist16 < dist33){
                dist33 = cost33 + dist16;
                dir33 = dir16;
            }
            if(cost33 + dist15 < dist33){
                dist33 = cost33 + dist15;
                dir33 = dir15;
            }
            if(cost33 + dist14 < dist33){
                dist33 = cost33 + dist14;
                dir33 = dir14;
            }
            if(cost33 + dist32 < dist33){
                dist33 = cost33 + dist32;
                dir33 = dir32;
            }

        }
        if(rc.onTheMap(ml34) && (rc.senseCloud(ml34) || rc.sensePassability(ml34))){
            cost34 = 10;
            if(cost34 + dist16 < dist34){
                dist34 = cost34 + dist16;
                dir34 = dir16;
            }
            if(cost34 + dist15 < dist34){
                dist34 = cost34 + dist15;
                dir34 = dir15;
            }
            if(cost34 + dist33 < dist34){
                dist34 = cost34 + dist33;
                dir34 = dir33;
            }

        }
        if(rc.onTheMap(ml35) && (rc.senseCloud(ml35) || rc.sensePassability(ml35))){
            cost35 = 10;
            if(cost35 + dist17 < dist35){
                dist35 = cost35 + dist17;
                dir35 = dir17;
            }
            if(cost35 + dist16 < dist35){
                dist35 = cost35 + dist16;
                dir35 = dir16;
            }
            if(cost35 + dist34 < dist35){
                dist35 = cost35 + dist34;
                dir35 = dir34;
            }

        }
        if(rc.onTheMap(ml36) && (rc.senseCloud(ml36) || rc.sensePassability(ml36))){
            cost36 = 10;
            if(cost36 + dist18 < dist36){
                dist36 = cost36 + dist18;
                dir36 = dir18;
            }
            if(cost36 + dist17 < dist36){
                dist36 = cost36 + dist17;
                dir36 = dir17;
            }
            if(cost36 + dist16 < dist36){
                dist36 = cost36 + dist16;
                dir36 = dir16;
            }

        }
        if(rc.onTheMap(ml37) && (rc.senseCloud(ml37) || rc.sensePassability(ml37))){
            cost37 = 10;
            if(cost37 + dist19 < dist37){
                dist37 = cost37 + dist19;
                dir37 = dir19;
            }
            if(cost37 + dist18 < dist37){
                dist37 = cost37 + dist18;
                dir37 = dir18;
            }
            if(cost37 + dist17 < dist37){
                dist37 = cost37 + dist17;
                dir37 = dir17;
            }

        }
        if(rc.onTheMap(ml38) && (rc.senseCloud(ml38) || rc.sensePassability(ml38))){
            cost38 = 10;
            if(cost38 + dist37 < dist38){
                dist38 = cost38 + dist37;
                dir38 = dir37;
            }
            if(cost38 + dist20 < dist38){
                dist38 = cost38 + dist20;
                dir38 = dir20;
            }
            if(cost38 + dist19 < dist38){
                dist38 = cost38 + dist19;
                dir38 = dir19;
            }
            if(cost38 + dist18 < dist38){
                dist38 = cost38 + dist18;
                dir38 = dir18;
            }

        }
        if(rc.onTheMap(ml39) && (rc.senseCloud(ml39) || rc.sensePassability(ml39))){
            cost39 = 10;
            if(cost39 + dist38 < dist39){
                dist39 = cost39 + dist38;
                dir39 = dir38;
            }
            if(cost39 + dist20 < dist39){
                dist39 = cost39 + dist20;
                dir39 = dir20;
            }
            if(cost39 + dist19 < dist39){
                dist39 = cost39 + dist19;
                dir39 = dir19;
            }

        }
        if(rc.onTheMap(ml40) && (rc.senseCloud(ml40) || rc.sensePassability(ml40))){
            cost40 = 10;
            if(cost40 + dist39 < dist40){
                dist40 = cost40 + dist39;
                dir40 = dir39;
            }
            if(cost40 + dist20 < dist40){
                dist40 = cost40 + dist20;
                dir40 = dir20;
            }
            if(cost40 + dist21 < dist40){
                dist40 = cost40 + dist21;
                dir40 = dir21;
            }

        }
        if(rc.onTheMap(ml41) && (rc.senseCloud(ml41) || rc.sensePassability(ml41))){
            cost41 = 10;
            if(cost41 + dist20 < dist41){
                dist41 = cost41 + dist20;
                dir41 = dir20;
            }
            if(cost41 + dist21 < dist41){
                dist41 = cost41 + dist21;
                dir41 = dir21;
            }
            if(cost41 + dist22 < dist41){
                dist41 = cost41 + dist22;
                dir41 = dir22;
            }

        }
        if(rc.onTheMap(ml42) && (rc.senseCloud(ml42) || rc.sensePassability(ml42))){
            cost42 = 10;
            if(cost42 + dist21 < dist42){
                dist42 = cost42 + dist21;
                dir42 = dir21;
            }
            if(cost42 + dist22 < dist42){
                dist42 = cost42 + dist22;
                dir42 = dir22;
            }
            if(cost42 + dist23 < dist42){
                dist42 = cost42 + dist23;
                dir42 = dir23;
            }

        }
        if(rc.onTheMap(ml43) && (rc.senseCloud(ml43) || rc.sensePassability(ml43))){
            cost43 = 10;
            if(cost43 + dist42 < dist43){
                dist43 = cost43 + dist42;
                dir43 = dir42;
            }
            if(cost43 + dist22 < dist43){
                dist43 = cost43 + dist22;
                dir43 = dir22;
            }
            if(cost43 + dist23 < dist43){
                dist43 = cost43 + dist23;
                dir43 = dir23;
            }
            if(cost43 + dist24 < dist43){
                dist43 = cost43 + dist24;
                dir43 = dir24;
            }

        }
        if(rc.onTheMap(ml44) && (rc.senseCloud(ml44) || rc.sensePassability(ml44))){
            cost44 = 10;
            if(cost44 + dist43 < dist44){
                dist44 = cost44 + dist43;
                dir44 = dir43;
            }
            if(cost44 + dist23 < dist44){
                dist44 = cost44 + dist23;
                dir44 = dir23;
            }
            if(cost44 + dist24 < dist44){
                dist44 = cost44 + dist24;
                dir44 = dir24;
            }
            if(cost44 + dist25 < dist44){
                dist44 = cost44 + dist25;
                dir44 = dir25;
            }

        }
        if(rc.onTheMap(ml25) && (rc.senseCloud(ml25) || rc.sensePassability(ml25))){
            if(cost25 + dist44 < dist25){
                dist25 = cost25 + dist44;
                dir25 = dir44;
            }
            if(cost25 + dist26 < dist25){
                dist25 = cost25 + dist26;
                dir25 = dir26;
            }

        }
        if(rc.onTheMap(ml26) && (rc.senseCloud(ml26) || rc.sensePassability(ml26))){
            if(cost26 + dist27 < dist26){
                dist26 = cost26 + dist27;
                dir26 = dir27;
            }

        }
        if(rc.onTheMap(ml29) && (rc.senseCloud(ml29) || rc.sensePassability(ml29))){
            if(cost29 + dist30 < dist29){
                dist29 = cost29 + dist30;
                dir29 = dir30;
            }

        }
        if(rc.onTheMap(ml30) && (rc.senseCloud(ml30) || rc.sensePassability(ml30))){
            if(cost30 + dist31 < dist30){
                dist30 = cost30 + dist31;
                dir30 = dir31;
            }

        }
        if(rc.onTheMap(ml31) && (rc.senseCloud(ml31) || rc.sensePassability(ml31))){
            if(cost31 + dist32 < dist31){
                dist31 = cost31 + dist32;
                dir31 = dir32;
            }

        }
        if(rc.onTheMap(ml34) && (rc.senseCloud(ml34) || rc.sensePassability(ml34))){
            if(cost34 + dist35 < dist34){
                dist34 = cost34 + dist35;
                dir34 = dir35;
            }

        }
        if(rc.onTheMap(ml35) && (rc.senseCloud(ml35) || rc.sensePassability(ml35))){
            if(cost35 + dist36 < dist35){
                dist35 = cost35 + dist36;
                dir35 = dir36;
            }

        }
        if(rc.onTheMap(ml36) && (rc.senseCloud(ml36) || rc.sensePassability(ml36))){
            if(cost36 + dist37 < dist36){
                dist36 = cost36 + dist37;
                dir36 = dir37;
            }

        }
        if(rc.onTheMap(ml39) && (rc.senseCloud(ml39) || rc.sensePassability(ml39))){
            if(cost39 + dist40 < dist39){
                dist39 = cost39 + dist40;
                dir39 = dir40;
            }

        }
        if(rc.onTheMap(ml40) && (rc.senseCloud(ml40) || rc.sensePassability(ml40))){
            if(cost40 + dist41 < dist40){
                dist40 = cost40 + dist41;
                dir40 = dir41;
            }

        }
        if(rc.onTheMap(ml41) && (rc.senseCloud(ml41) || rc.sensePassability(ml41))){
            if(cost41 + dist42 < dist41){
                dist41 = cost41 + dist42;
                dir41 = dir42;
            }

        }
        if(rc.onTheMap(ml45) && (rc.senseCloud(ml45) || rc.sensePassability(ml45))){
            cost45 = 10;
            if(cost45 + dist44 < dist45){
                dist45 = cost45 + dist44;
                dir45 = dir44;
            }
            if(cost45 + dist24 < dist45){
                dist45 = cost45 + dist24;
                dir45 = dir24;
            }
            if(cost45 + dist25 < dist45){
                dist45 = cost45 + dist25;
                dir45 = dir25;
            }

        }
        if(rc.onTheMap(ml46) && (rc.senseCloud(ml46) || rc.sensePassability(ml46))){
            cost46 = 10;
            if(cost46 + dist45 < dist46){
                dist46 = cost46 + dist45;
                dir46 = dir45;
            }
            if(cost46 + dist25 < dist46){
                dist46 = cost46 + dist25;
                dir46 = dir25;
            }
            if(cost46 + dist26 < dist46){
                dist46 = cost46 + dist26;
                dir46 = dir26;
            }

        }
        if(rc.onTheMap(ml47) && (rc.senseCloud(ml47) || rc.sensePassability(ml47))){
            cost47 = 10;
            if(cost47 + dist25 < dist47){
                dist47 = cost47 + dist25;
                dir47 = dir25;
            }
            if(cost47 + dist26 < dist47){
                dist47 = cost47 + dist26;
                dir47 = dir26;
            }
            if(cost47 + dist27 < dist47){
                dist47 = cost47 + dist27;
                dir47 = dir27;
            }

        }
        if(rc.onTheMap(ml48) && (rc.senseCloud(ml48) || rc.sensePassability(ml48))){
            cost48 = 10;
            if(cost48 + dist26 < dist48){
                dist48 = cost48 + dist26;
                dir48 = dir26;
            }
            if(cost48 + dist27 < dist48){
                dist48 = cost48 + dist27;
                dir48 = dir27;
            }
            if(cost48 + dist28 < dist48){
                dist48 = cost48 + dist28;
                dir48 = dir28;
            }

        }
        if(rc.onTheMap(ml49) && (rc.senseCloud(ml49) || rc.sensePassability(ml49))){
            cost49 = 10;
            if(cost49 + dist27 < dist49){
                dist49 = cost49 + dist27;
                dir49 = dir27;
            }
            if(cost49 + dist28 < dist49){
                dist49 = cost49 + dist28;
                dir49 = dir28;
            }
            if(cost49 + dist29 < dist49){
                dist49 = cost49 + dist29;
                dir49 = dir29;
            }
            if(cost49 + dist48 < dist49){
                dist49 = cost49 + dist48;
                dir49 = dir48;
            }

        }
        if(rc.onTheMap(ml50) && (rc.senseCloud(ml50) || rc.sensePassability(ml50))){
            cost50 = 10;
            if(cost50 + dist28 < dist50){
                dist50 = cost50 + dist28;
                dir50 = dir28;
            }
            if(cost50 + dist29 < dist50){
                dist50 = cost50 + dist29;
                dir50 = dir29;
            }
            if(cost50 + dist49 < dist50){
                dist50 = cost50 + dist49;
                dir50 = dir49;
            }

        }
        if(rc.onTheMap(ml51) && (rc.senseCloud(ml51) || rc.sensePassability(ml51))){
            cost51 = 10;
            if(cost51 + dist12 < dist51){
                dist51 = cost51 + dist12;
                dir51 = dir12;
            }
            if(cost51 + dist30 < dist51){
                dist51 = cost51 + dist30;
                dir51 = dir30;
            }
            if(cost51 + dist29 < dist51){
                dist51 = cost51 + dist29;
                dir51 = dir29;
            }
            if(cost51 + dist50 < dist51){
                dist51 = cost51 + dist50;
                dir51 = dir50;
            }

        }
        if(rc.onTheMap(ml52) && (rc.senseCloud(ml52) || rc.sensePassability(ml52))){
            cost52 = 10;
            if(cost52 + dist31 < dist52){
                dist52 = cost52 + dist31;
                dir52 = dir31;
            }
            if(cost52 + dist30 < dist52){
                dist52 = cost52 + dist30;
                dir52 = dir30;
            }
            if(cost52 + dist51 < dist52){
                dist52 = cost52 + dist51;
                dir52 = dir51;
            }

        }
        if(rc.onTheMap(ml53) && (rc.senseCloud(ml53) || rc.sensePassability(ml53))){
            cost53 = 10;
            if(cost53 + dist32 < dist53){
                dist53 = cost53 + dist32;
                dir53 = dir32;
            }
            if(cost53 + dist31 < dist53){
                dist53 = cost53 + dist31;
                dir53 = dir31;
            }
            if(cost53 + dist30 < dist53){
                dist53 = cost53 + dist30;
                dir53 = dir30;
            }

        }
        if(rc.onTheMap(ml54) && (rc.senseCloud(ml54) || rc.sensePassability(ml54))){
            cost54 = 10;
            if(cost54 + dist33 < dist54){
                dist54 = cost54 + dist33;
                dir54 = dir33;
            }
            if(cost54 + dist32 < dist54){
                dist54 = cost54 + dist32;
                dir54 = dir32;
            }
            if(cost54 + dist31 < dist54){
                dist54 = cost54 + dist31;
                dir54 = dir31;
            }

        }
        if(rc.onTheMap(ml55) && (rc.senseCloud(ml55) || rc.sensePassability(ml55))){
            cost55 = 10;
            if(cost55 + dist34 < dist55){
                dist55 = cost55 + dist34;
                dir55 = dir34;
            }
            if(cost55 + dist33 < dist55){
                dist55 = cost55 + dist33;
                dir55 = dir33;
            }
            if(cost55 + dist32 < dist55){
                dist55 = cost55 + dist32;
                dir55 = dir32;
            }
            if(cost55 + dist54 < dist55){
                dist55 = cost55 + dist54;
                dir55 = dir54;
            }

        }
        if(rc.onTheMap(ml56) && (rc.senseCloud(ml56) || rc.sensePassability(ml56))){
            cost56 = 10;
            if(cost56 + dist34 < dist56){
                dist56 = cost56 + dist34;
                dir56 = dir34;
            }
            if(cost56 + dist33 < dist56){
                dist56 = cost56 + dist33;
                dir56 = dir33;
            }
            if(cost56 + dist55 < dist56){
                dist56 = cost56 + dist55;
                dir56 = dir55;
            }

        }
        if(rc.onTheMap(ml57) && (rc.senseCloud(ml57) || rc.sensePassability(ml57))){
            cost57 = 10;
            if(cost57 + dist35 < dist57){
                dist57 = cost57 + dist35;
                dir57 = dir35;
            }
            if(cost57 + dist16 < dist57){
                dist57 = cost57 + dist16;
                dir57 = dir16;
            }
            if(cost57 + dist34 < dist57){
                dist57 = cost57 + dist34;
                dir57 = dir34;
            }
            if(cost57 + dist56 < dist57){
                dist57 = cost57 + dist56;
                dir57 = dir56;
            }

        }
        if(rc.onTheMap(ml58) && (rc.senseCloud(ml58) || rc.sensePassability(ml58))){
            cost58 = 10;
            if(cost58 + dist36 < dist58){
                dist58 = cost58 + dist36;
                dir58 = dir36;
            }
            if(cost58 + dist35 < dist58){
                dist58 = cost58 + dist35;
                dir58 = dir35;
            }
            if(cost58 + dist57 < dist58){
                dist58 = cost58 + dist57;
                dir58 = dir57;
            }

        }
        if(rc.onTheMap(ml59) && (rc.senseCloud(ml59) || rc.sensePassability(ml59))){
            cost59 = 10;
            if(cost59 + dist37 < dist59){
                dist59 = cost59 + dist37;
                dir59 = dir37;
            }
            if(cost59 + dist36 < dist59){
                dist59 = cost59 + dist36;
                dir59 = dir36;
            }
            if(cost59 + dist35 < dist59){
                dist59 = cost59 + dist35;
                dir59 = dir35;
            }

        }
        if(rc.onTheMap(ml60) && (rc.senseCloud(ml60) || rc.sensePassability(ml60))){
            cost60 = 10;
            if(cost60 + dist38 < dist60){
                dist60 = cost60 + dist38;
                dir60 = dir38;
            }
            if(cost60 + dist37 < dist60){
                dist60 = cost60 + dist37;
                dir60 = dir37;
            }
            if(cost60 + dist36 < dist60){
                dist60 = cost60 + dist36;
                dir60 = dir36;
            }

        }
        if(rc.onTheMap(ml61) && (rc.senseCloud(ml61) || rc.sensePassability(ml61))){
            cost61 = 10;
            if(cost61 + dist60 < dist61){
                dist61 = cost61 + dist60;
                dir61 = dir60;
            }
            if(cost61 + dist39 < dist61){
                dist61 = cost61 + dist39;
                dir61 = dir39;
            }
            if(cost61 + dist38 < dist61){
                dist61 = cost61 + dist38;
                dir61 = dir38;
            }
            if(cost61 + dist37 < dist61){
                dist61 = cost61 + dist37;
                dir61 = dir37;
            }

        }
        if(rc.onTheMap(ml62) && (rc.senseCloud(ml62) || rc.sensePassability(ml62))){
            cost62 = 10;
            if(cost62 + dist61 < dist62){
                dist62 = cost62 + dist61;
                dir62 = dir61;
            }
            if(cost62 + dist39 < dist62){
                dist62 = cost62 + dist39;
                dir62 = dir39;
            }
            if(cost62 + dist38 < dist62){
                dist62 = cost62 + dist38;
                dir62 = dir38;
            }

        }
        if(rc.onTheMap(ml63) && (rc.senseCloud(ml63) || rc.sensePassability(ml63))){
            cost63 = 10;
            if(cost63 + dist62 < dist63){
                dist63 = cost63 + dist62;
                dir63 = dir62;
            }
            if(cost63 + dist39 < dist63){
                dist63 = cost63 + dist39;
                dir63 = dir39;
            }
            if(cost63 + dist40 < dist63){
                dist63 = cost63 + dist40;
                dir63 = dir40;
            }
            if(cost63 + dist20 < dist63){
                dist63 = cost63 + dist20;
                dir63 = dir20;
            }

        }
        if(rc.onTheMap(ml64) && (rc.senseCloud(ml64) || rc.sensePassability(ml64))){
            cost64 = 10;
            if(cost64 + dist63 < dist64){
                dist64 = cost64 + dist63;
                dir64 = dir63;
            }
            if(cost64 + dist40 < dist64){
                dist64 = cost64 + dist40;
                dir64 = dir40;
            }
            if(cost64 + dist41 < dist64){
                dist64 = cost64 + dist41;
                dir64 = dir41;
            }

        }
        if(rc.onTheMap(ml65) && (rc.senseCloud(ml65) || rc.sensePassability(ml65))){
            cost65 = 10;
            if(cost65 + dist40 < dist65){
                dist65 = cost65 + dist40;
                dir65 = dir40;
            }
            if(cost65 + dist41 < dist65){
                dist65 = cost65 + dist41;
                dir65 = dir41;
            }
            if(cost65 + dist42 < dist65){
                dist65 = cost65 + dist42;
                dir65 = dir42;
            }

        }
        if(rc.onTheMap(ml66) && (rc.senseCloud(ml66) || rc.sensePassability(ml66))){
            cost66 = 10;
            if(cost66 + dist41 < dist66){
                dist66 = cost66 + dist41;
                dir66 = dir41;
            }
            if(cost66 + dist42 < dist66){
                dist66 = cost66 + dist42;
                dir66 = dir42;
            }
            if(cost66 + dist43 < dist66){
                dist66 = cost66 + dist43;
                dir66 = dir43;
            }

        }
        if(rc.onTheMap(ml67) && (rc.senseCloud(ml67) || rc.sensePassability(ml67))){
            cost67 = 10;
            if(cost67 + dist66 < dist67){
                dist67 = cost67 + dist66;
                dir67 = dir66;
            }
            if(cost67 + dist42 < dist67){
                dist67 = cost67 + dist42;
                dir67 = dir42;
            }
            if(cost67 + dist43 < dist67){
                dist67 = cost67 + dist43;
                dir67 = dir43;
            }
            if(cost67 + dist44 < dist67){
                dist67 = cost67 + dist44;
                dir67 = dir44;
            }

        }
        if(rc.onTheMap(ml68) && (rc.senseCloud(ml68) || rc.sensePassability(ml68))){
            cost68 = 10;
            if(cost68 + dist67 < dist68){
                dist68 = cost68 + dist67;
                dir68 = dir67;
            }
            if(cost68 + dist43 < dist68){
                dist68 = cost68 + dist43;
                dir68 = dir43;
            }
            if(cost68 + dist44 < dist68){
                dist68 = cost68 + dist44;
                dir68 = dir44;
            }
            if(cost68 + dist45 < dist68){
                dist68 = cost68 + dist45;
                dir68 = dir45;
            }

        }
        if(rc.onTheMap(ml45) && (rc.senseCloud(ml45) || rc.sensePassability(ml45))){
            if(cost45 + dist68 < dist45){
                dist45 = cost45 + dist68;
                dir45 = dir68;
            }
            if(cost45 + dist46 < dist45){
                dist45 = cost45 + dist46;
                dir45 = dir46;
            }

        }
        if(rc.onTheMap(ml46) && (rc.senseCloud(ml46) || rc.sensePassability(ml46))){
            if(cost46 + dist47 < dist46){
                dist46 = cost46 + dist47;
                dir46 = dir47;
            }

        }
        if(rc.onTheMap(ml47) && (rc.senseCloud(ml47) || rc.sensePassability(ml47))){
            if(cost47 + dist48 < dist47){
                dist47 = cost47 + dist48;
                dir47 = dir48;
            }

        }
        if(rc.onTheMap(ml50) && (rc.senseCloud(ml50) || rc.sensePassability(ml50))){
            if(cost50 + dist51 < dist50){
                dist50 = cost50 + dist51;
                dir50 = dir51;
            }

        }
        if(rc.onTheMap(ml51) && (rc.senseCloud(ml51) || rc.sensePassability(ml51))){
            if(cost51 + dist52 < dist51){
                dist51 = cost51 + dist52;
                dir51 = dir52;
            }

        }
        if(rc.onTheMap(ml52) && (rc.senseCloud(ml52) || rc.sensePassability(ml52))){
            if(cost52 + dist53 < dist52){
                dist52 = cost52 + dist53;
                dir52 = dir53;
            }

        }
        if(rc.onTheMap(ml53) && (rc.senseCloud(ml53) || rc.sensePassability(ml53))){
            if(cost53 + dist54 < dist53){
                dist53 = cost53 + dist54;
                dir53 = dir54;
            }

        }
        if(rc.onTheMap(ml56) && (rc.senseCloud(ml56) || rc.sensePassability(ml56))){
            if(cost56 + dist57 < dist56){
                dist56 = cost56 + dist57;
                dir56 = dir57;
            }

        }
        if(rc.onTheMap(ml57) && (rc.senseCloud(ml57) || rc.sensePassability(ml57))){
            if(cost57 + dist58 < dist57){
                dist57 = cost57 + dist58;
                dir57 = dir58;
            }

        }
        if(rc.onTheMap(ml58) && (rc.senseCloud(ml58) || rc.sensePassability(ml58))){
            if(cost58 + dist59 < dist58){
                dist58 = cost58 + dist59;
                dir58 = dir59;
            }

        }
        if(rc.onTheMap(ml59) && (rc.senseCloud(ml59) || rc.sensePassability(ml59))){
            if(cost59 + dist60 < dist59){
                dist59 = cost59 + dist60;
                dir59 = dir60;
            }

        }
        if(rc.onTheMap(ml62) && (rc.senseCloud(ml62) || rc.sensePassability(ml62))){
            if(cost62 + dist63 < dist62){
                dist62 = cost62 + dist63;
                dir62 = dir63;
            }

        }
        if(rc.onTheMap(ml63) && (rc.senseCloud(ml63) || rc.sensePassability(ml63))){
            if(cost63 + dist64 < dist63){
                dist63 = cost63 + dist64;
                dir63 = dir64;
            }

        }
        if(rc.onTheMap(ml64) && (rc.senseCloud(ml64) || rc.sensePassability(ml64))){
            if(cost64 + dist65 < dist64){
                dist64 = cost64 + dist65;
                dir64 = dir65;
            }

        }
        if(rc.onTheMap(ml65) && (rc.senseCloud(ml65) || rc.sensePassability(ml65))){
            if(cost65 + dist66 < dist65){
                dist65 = cost65 + dist66;
                dir65 = dir66;
            }

        }



        int xDiff = target.x - ml0.x;
        int yDiff = target.y - ml0.y;
        switch (xDiff){
            case -4:
                switch (yDiff){
                    case -2:
                        if(dist68  <5000) return dir68 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist67  <5000) return dir67 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist66  <5000) return dir66 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist65  <5000) return dir65 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist64  <5000) return dir64 ;
                        else return Direction.CENTER;
                }
                break;
            case -3:
                switch (yDiff){
                    case -3:
                        if(dist45  <5000) return dir45 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist44  <5000) return dir44 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist43  <5000) return dir43 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist42  <5000) return dir42 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist41  <5000) return dir41 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist40  <5000) return dir40 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist63  <5000) return dir63 ;
                        else return Direction.CENTER;
                }
                break;
            case -2:
                switch (yDiff){
                    case -4:
                        if(dist46  <5000) return dir46 ;
                        else return Direction.CENTER;
                    case -3:
                        if(dist25  <5000) return dir25 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist24  <5000) return dir24 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist23  <5000) return dir23 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist22  <5000) return dir22 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist21  <5000) return dir21 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist20  <5000) return dir20 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist39  <5000) return dir39 ;
                        else return Direction.CENTER;
                    case 4:
                        if(dist62  <5000) return dir62 ;
                        else return Direction.CENTER;
                }
                break;
            case -1:
                switch (yDiff){
                    case -4:
                        if(dist47  <5000) return dir47 ;
                        else return Direction.CENTER;
                    case -3:
                        if(dist26  <5000) return dir26 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist9  <5000) return dir9 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist8  <5000) return dir8 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist7  <5000) return dir7 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist6  <5000) return dir6 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist19  <5000) return dir19 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist38  <5000) return dir38 ;
                        else return Direction.CENTER;
                    case 4:
                        if(dist61  <5000) return dir61 ;
                        else return Direction.CENTER;
                }
                break;
            case 0:
                switch (yDiff){
                    case -4:
                        if(dist48  <5000) return dir48 ;
                        else return Direction.CENTER;
                    case -3:
                        if(dist27  <5000) return dir27 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist10  <5000) return dir10 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist1  <5000) return dir1 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist5  <5000) return dir5 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist18  <5000) return dir18 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist37  <5000) return dir37 ;
                        else return Direction.CENTER;
                    case 4:
                        if(dist60  <5000) return dir60 ;
                        else return Direction.CENTER;
                }
                break;
            case 1:
                switch (yDiff){
                    case -4:
                        if(dist49  <5000) return dir49 ;
                        else return Direction.CENTER;
                    case -3:
                        if(dist28  <5000) return dir28 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist11  <5000) return dir11 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist2  <5000) return dir2 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist3  <5000) return dir3 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist4  <5000) return dir4 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist17  <5000) return dir17 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist36  <5000) return dir36 ;
                        else return Direction.CENTER;
                    case 4:
                        if(dist59  <5000) return dir59 ;
                        else return Direction.CENTER;
                }
                break;
            case 2:
                switch (yDiff){
                    case -4:
                        if(dist50  <5000) return dir50 ;
                        else return Direction.CENTER;
                    case -3:
                        if(dist29  <5000) return dir29 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist12  <5000) return dir12 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist13  <5000) return dir13 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist14  <5000) return dir14 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist15  <5000) return dir15 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist16  <5000) return dir16 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist35  <5000) return dir35 ;
                        else return Direction.CENTER;
                    case 4:
                        if(dist58  <5000) return dir58 ;
                        else return Direction.CENTER;
                }
                break;
            case 3:
                switch (yDiff){
                    case -3:
                        if(dist51  <5000) return dir51 ;
                        else return Direction.CENTER;
                    case -2:
                        if(dist30  <5000) return dir30 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist31  <5000) return dir31 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist32  <5000) return dir32 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist33  <5000) return dir33 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist34  <5000) return dir34 ;
                        else return Direction.CENTER;
                    case 3:
                        if(dist57  <5000) return dir57 ;
                        else return Direction.CENTER;
                }
                break;
            case 4:
                switch (yDiff){
                    case -2:
                        if(dist52  <5000) return dir52 ;
                        else return Direction.CENTER;
                    case -1:
                        if(dist53  <5000) return dir53 ;
                        else return Direction.CENTER;
                    case 0:
                        if(dist54  <5000) return dir54 ;
                        else return Direction.CENTER;
                    case 1:
                        if(dist55  <5000) return dir55 ;
                        else return Direction.CENTER;
                    case 2:
                        if(dist56  <5000) return dir56 ;
                        else return Direction.CENTER;
                }
                break;
        }

        double gain;
        Direction ans = Direction.CENTER;
        double initDist = Math.sqrt(ml0.distanceSquaredTo(target));
        double maxGainPerCost = 0;
        gain = (initDist - Math.sqrt(ml45.distanceSquaredTo(target))) / dist45;
        if(gain > maxGainPerCost && dist45 < 5000){
            maxGainPerCost = gain;
            ans = dir45;
        }
        gain = (initDist - Math.sqrt(ml46.distanceSquaredTo(target))) / dist46;
        if(gain > maxGainPerCost && dist46 < 5000){
            maxGainPerCost = gain;
            ans = dir46;
        }
        gain = (initDist - Math.sqrt(ml47.distanceSquaredTo(target))) / dist47;
        if(gain > maxGainPerCost && dist47 < 5000){
            maxGainPerCost = gain;
            ans = dir47;
        }
        gain = (initDist - Math.sqrt(ml48.distanceSquaredTo(target))) / dist48;
        if(gain > maxGainPerCost && dist48 < 5000){
            maxGainPerCost = gain;
            ans = dir48;
        }
        gain = (initDist - Math.sqrt(ml49.distanceSquaredTo(target))) / dist49;
        if(gain > maxGainPerCost && dist49 < 5000){
            maxGainPerCost = gain;
            ans = dir49;
        }
        gain = (initDist - Math.sqrt(ml50.distanceSquaredTo(target))) / dist50;
        if(gain > maxGainPerCost && dist50 < 5000){
            maxGainPerCost = gain;
            ans = dir50;
        }
        gain = (initDist - Math.sqrt(ml51.distanceSquaredTo(target))) / dist51;
        if(gain > maxGainPerCost && dist51 < 5000){
            maxGainPerCost = gain;
            ans = dir51;
        }
        gain = (initDist - Math.sqrt(ml52.distanceSquaredTo(target))) / dist52;
        if(gain > maxGainPerCost && dist52 < 5000){
            maxGainPerCost = gain;
            ans = dir52;
        }
        gain = (initDist - Math.sqrt(ml53.distanceSquaredTo(target))) / dist53;
        if(gain > maxGainPerCost && dist53 < 5000){
            maxGainPerCost = gain;
            ans = dir53;
        }
        gain = (initDist - Math.sqrt(ml54.distanceSquaredTo(target))) / dist54;
        if(gain > maxGainPerCost && dist54 < 5000){
            maxGainPerCost = gain;
            ans = dir54;
        }
        gain = (initDist - Math.sqrt(ml55.distanceSquaredTo(target))) / dist55;
        if(gain > maxGainPerCost && dist55 < 5000){
            maxGainPerCost = gain;
            ans = dir55;
        }
        gain = (initDist - Math.sqrt(ml56.distanceSquaredTo(target))) / dist56;
        if(gain > maxGainPerCost && dist56 < 5000){
            maxGainPerCost = gain;
            ans = dir56;
        }
        gain = (initDist - Math.sqrt(ml57.distanceSquaredTo(target))) / dist57;
        if(gain > maxGainPerCost && dist57 < 5000){
            maxGainPerCost = gain;
            ans = dir57;
        }
        gain = (initDist - Math.sqrt(ml58.distanceSquaredTo(target))) / dist58;
        if(gain > maxGainPerCost && dist58 < 5000){
            maxGainPerCost = gain;
            ans = dir58;
        }
        gain = (initDist - Math.sqrt(ml59.distanceSquaredTo(target))) / dist59;
        if(gain > maxGainPerCost && dist59 < 5000){
            maxGainPerCost = gain;
            ans = dir59;
        }
        gain = (initDist - Math.sqrt(ml60.distanceSquaredTo(target))) / dist60;
        if(gain > maxGainPerCost && dist60 < 5000){
            maxGainPerCost = gain;
            ans = dir60;
        }
        gain = (initDist - Math.sqrt(ml61.distanceSquaredTo(target))) / dist61;
        if(gain > maxGainPerCost && dist61 < 5000){
            maxGainPerCost = gain;
            ans = dir61;
        }
        gain = (initDist - Math.sqrt(ml62.distanceSquaredTo(target))) / dist62;
        if(gain > maxGainPerCost && dist62 < 5000){
            maxGainPerCost = gain;
            ans = dir62;
        }
        gain = (initDist - Math.sqrt(ml63.distanceSquaredTo(target))) / dist63;
        if(gain > maxGainPerCost && dist63 < 5000){
            maxGainPerCost = gain;
            ans = dir63;
        }
        gain = (initDist - Math.sqrt(ml64.distanceSquaredTo(target))) / dist64;
        if(gain > maxGainPerCost && dist64 < 5000){
            maxGainPerCost = gain;
            ans = dir64;
        }
        gain = (initDist - Math.sqrt(ml65.distanceSquaredTo(target))) / dist65;
        if(gain > maxGainPerCost && dist65 < 5000){
            maxGainPerCost = gain;
            ans = dir65;
        }
        gain = (initDist - Math.sqrt(ml66.distanceSquaredTo(target))) / dist66;
        if(gain > maxGainPerCost && dist66 < 5000){
            maxGainPerCost = gain;
            ans = dir66;
        }
        gain = (initDist - Math.sqrt(ml67.distanceSquaredTo(target))) / dist67;
        if(gain > maxGainPerCost && dist67 < 5000){
            maxGainPerCost = gain;
            ans = dir67;
        }
        gain = (initDist - Math.sqrt(ml68.distanceSquaredTo(target))) / dist68;
        if(gain > maxGainPerCost && dist68 < 5000){
            maxGainPerCost = gain;
            ans = dir68;
        }
        gain = (initDist - Math.sqrt(ml44.distanceSquaredTo(target))) / dist44;
        if(gain > maxGainPerCost && dist44 < 5000){
            maxGainPerCost = gain;
            ans = dir44;
        }
        gain = (initDist - Math.sqrt(ml25.distanceSquaredTo(target))) / dist25;
        if(gain > maxGainPerCost && dist25 < 5000){
            maxGainPerCost = gain;
            ans = dir25;
        }
        gain = (initDist - Math.sqrt(ml29.distanceSquaredTo(target))) / dist29;
        if(gain > maxGainPerCost && dist29 < 5000){
            maxGainPerCost = gain;
            ans = dir29;
        }
        gain = (initDist - Math.sqrt(ml30.distanceSquaredTo(target))) / dist30;
        if(gain > maxGainPerCost && dist30 < 5000){
            maxGainPerCost = gain;
            ans = dir30;
        }
        gain = (initDist - Math.sqrt(ml34.distanceSquaredTo(target))) / dist34;
        if(gain > maxGainPerCost && dist34 < 5000){
            maxGainPerCost = gain;
            ans = dir34;
        }
        gain = (initDist - Math.sqrt(ml35.distanceSquaredTo(target))) / dist35;
        if(gain > maxGainPerCost && dist35 < 5000){
            maxGainPerCost = gain;
            ans = dir35;
        }
        gain = (initDist - Math.sqrt(ml39.distanceSquaredTo(target))) / dist39;
        if(gain > maxGainPerCost && dist39 < 5000){
            maxGainPerCost = gain;
            ans = dir39;
        }
        gain = (initDist - Math.sqrt(ml40.distanceSquaredTo(target))) / dist40;
        if(gain > maxGainPerCost && dist40 < 5000){
            maxGainPerCost = gain;
            ans = dir40;
        }
        return ans;



    }


}
