package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@Autonomous(name = "BioBuzzRedSafeAuto")
public class BioBuzzRedSafeAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        telemetry.addLine("start pressed");

        drive.driveInches(0, 0.6, this);
        drive.strafeInches(0, 0.6, this);

        drive.drive(0, 0, 0);

        telemetry.addLine("Done");
        telemetry.update();
    }
}
