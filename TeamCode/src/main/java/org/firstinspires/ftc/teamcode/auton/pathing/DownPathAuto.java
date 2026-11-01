package org.firstinspires.ftc.teamcode.auton.pathing;

//"Down" auto (RedDownAuto / BlueDownAuto), built like the safe autos: shoot the preloaded pollen, then park.
//
//Start: against the top wall at the Down start spot, intake facing the wall, outtake facing the hive.
//"face" angles are from the starting direction: 0 = facing the top wall like at the start,
//90 = turned left to face the left wall, 180 = facing down the field.
public abstract class DownPathAuto extends SimplePathAuto {

    //TUNING (inches) - change these on the field like the safe auto numbers
    // Back away from the top wall before turning (also clears the flower on the top wall).
    public static double OFF_WALL_IN = 6;
    // Parking: drive left across the top, down the left side, then into the box.
    public static double PARK_ACROSS_IN = 35;
    public static double PARK_DOWN_IN = 22;
    public static double PARK_INTO_BOX_IN = 15;

    protected DownPathAuto(boolean blue) {
        super(blue);
    }

    @Override
    protected String placement() {
        return "against the top wall at the Down start spot, intake facing the wall, outtake facing the hive";
    }

    @Override
    protected void runPath() {
        //1. shoot the preloaded pollen
        shoot();

        //2. back off the top wall, turn left, drive across toward the left wall
        forward(-OFF_WALL_IN);
        face(90);
        forward(PARK_ACROSS_IN);

        //3. turn left to face down the field, drive down to the box, turn right and drive into it
        face(180);
        forward(PARK_DOWN_IN);
        face(90);
        forward(PARK_INTO_BOX_IN);
    }
}
