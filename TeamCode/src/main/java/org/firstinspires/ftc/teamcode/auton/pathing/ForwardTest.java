package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@Autonomous(name = "ForwardTest")
public class ForwardTest extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        telemetry.addLine("start pressed");

        drive.driveInches(24, 0.5, this);

        drive.drive(0, 0, 0);

        drive.pinpoint.update();
        telemetry.addLine("Done");
        telemetry.addData("forward (in)", "%.1f", drive.getX());
        telemetry.addData("sideways (in)", "%.1f", drive.getY());
        telemetry.addData("heading (deg)", "%.1f", drive.getHeadingDegrees());
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }
}
