package org.firstinspires.ftc.teamcode.auton.pathing;

//face angles: 0 = facing the wall like at the start, -90 = facing the garden, -180 = facing up the field
public abstract class UpPathAuto extends SimplePathAuto {

    public static double OFF_WALL_IN = 2.5;
    public static double TO_WALL_IN = 3;
    public static double GARDEN_IN = 46;
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
        shoot();

        forward(-OFF_WALL_IN);
        face(-90);
        strafe(-TO_WALL_IN);

        intake.goForward();
        forward(GARDEN_IN);
        intake.stop();

        forward(-GARDEN_IN);
        strafe(TO_WALL_IN);
        face(0);

        shootWhenTagVisible();

        face(-90);
        forward(PARK_ACROSS_IN);
        face(-180);
        forward(PARK_UP_IN);
        face(-90);
        forward(PARK_INTO_BOX_IN);
    }
}
