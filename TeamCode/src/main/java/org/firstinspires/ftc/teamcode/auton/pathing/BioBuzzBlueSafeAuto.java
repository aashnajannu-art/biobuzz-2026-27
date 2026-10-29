package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@Autonomous(name = "BioBuzzBlueSafeAuto") //drives to parking
public class BioBuzzBlueSafeAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();

    // drive forward, turn right, then drive forward again to park
    // Same moves as BioBuzzRedSafeAuto: the BioBuzz field is rotated 180 degrees for blue (not mirrored),
    // so turning right still parks in the blue box when the robot starts in the blue starting spot.
    public static double PARK_FORWARD_IN = 15;     // first move, + = forward
    public static double PARK_TURN_DEG = -90;     // + = turn left, - = turn right
    public static double PARK_FORWARD_2_IN = 36;  // after the turn, + = forward
    public static double PARK_POWER = 0.6;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        telemetry.addLine("start pressed");

        drive.driveInches(PARK_FORWARD_IN, PARK_POWER, this);
        drive.turnDegrees(PARK_TURN_DEG, PARK_POWER, this);
        drive.driveInches(PARK_FORWARD_2_IN, PARK_POWER, this);

        drive.drive(0, 0, 0);

        telemetry.addLine("Done");
        telemetry.update();
    }
}
