package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@Autonomous(name = "BioBuzzRedSafeAuto") //drives to parking
public class BioBuzzRedSafeAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();

    // flip the sign of turn and strafe
    public static double PARK_FORWARD_IN = 24;    // + = forward
    public static double PARK_STRAFE_IN = 0;      // + = right
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
        drive.strafeInches(PARK_STRAFE_IN, PARK_POWER, this);

        drive.drive(0, 0, 0);

        telemetry.addLine("Done");
        telemetry.update();
    }
}
