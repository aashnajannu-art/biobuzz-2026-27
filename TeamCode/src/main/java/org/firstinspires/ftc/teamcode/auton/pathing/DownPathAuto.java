package org.firstinspires.ftc.teamcode.auton.pathing;

//face angles: 0 = facing the top wall like at the start, 90 = facing the left wall, 180 = facing down the field
public abstract class DownPathAuto extends SimplePathAuto {

    public static double OFF_WALL_IN = 6;
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
        shoot();

        forward(-OFF_WALL_IN);
        face(90);
        forward(PARK_ACROSS_IN);

        face(180);
        forward(PARK_DOWN_IN);
        face(90);
        forward(PARK_INTO_BOX_IN);
    }
}
