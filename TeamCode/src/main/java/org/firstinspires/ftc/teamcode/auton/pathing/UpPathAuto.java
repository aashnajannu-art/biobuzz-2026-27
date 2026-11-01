package org.firstinspires.ftc.teamcode.auton.pathing;

//"Up" auto (RedUpAuto / BlueUpAuto), built like the safe autos:
//shoot the 4 preloaded pollen -> turn and drive along the wall through the garden with the intake on ->
//back up to the start spot -> shoot (only if the tags are visible) -> park.
//
//Start: against the bottom wall at the Up start spot, intake facing the wall, outtake facing the hive.
//"face" angles are from the starting direction: 0 = facing the wall like at the start,
//-90 = turned right to face the garden, -180 = facing up the field.
public abstract class UpPathAuto extends SimplePathAuto {

    //TUNING (inches) - change these on the field like the safe auto numbers
    // Back away from the wall before turning.
    public static double OFF_WALL_IN = 2.5;
    // After turning toward the garden, strafe left this much toward the wall (the wall is on the robot's left).
    // Bigger = closer to the wall. Too big and the robot rubs the wall and gets stuck.
    public static double TO_WALL_IN = 3;
    // Drive this far through the garden, to the end of the wall.
    public static double GARDEN_IN = 46;
    // Parking: drive left along the bottom, up the left side, then into the box.
    public static double PARK_ACROSS_IN = 32;
    public static double PARK_UP_IN = 90;
    public static double PARK_INTO_BOX_IN = 15;

    protected UpPathAuto(boolean blue) {
        super(blue);
    }

    @Override
    protected String placement() {
        return "against the bottom wall at the Up start spot, intake facing the wall, outtake facing the hive";
    }

    @Override
    protected void runPath() {
        //1. shoot the 4 preloaded pollen
        shoot();

        //2. back off the wall, turn right to face the garden, strafe left toward the wall
        forward(-OFF_WALL_IN);
        face(-90);
        strafe(-TO_WALL_IN);

        //3. drive through the garden with the intake on
        intake.goForward();
        forward(GARDEN_IN);
        intake.stop();

        //4. back up to the start spot, strafe away from the wall, turn the outtake back toward the hive
        forward(-GARDEN_IN);
        strafe(TO_WALL_IN);
        face(0);

        //5. shoot the garden pollen (only if our tags are visible)
        shootWhenTagVisible();

        //6. park on the edge of the box closest to the garden
        face(-90);
        forward(PARK_ACROSS_IN);
        face(-180);
        forward(PARK_UP_IN);
        face(-90);
        forward(PARK_INTO_BOX_IN);
    }
}
