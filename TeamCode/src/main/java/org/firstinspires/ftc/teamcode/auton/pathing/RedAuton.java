package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.Robot.servo.PollenServo;


@Autonomous(name = "RedAuton") //shoots, goes through the garden, shoots again, parks
public class RedAuton extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();
    private Intake intake = new Intake();
    private Outtake outtake = new Outtake();
    private PollenServo pollenServo = new PollenServo();

    // Start: against the bottom wall at the Up start spot, intake facing the wall, outtake facing the hive.
    // shoot first, back off the wall, turn 90 degrees right to face the garden, move to the left a tiny bit toward the wall,
    // go forward along the wall with the intake on, go backwards the same exact distance, move right a tiny bit,
    // turn 90 degrees left, shoot, then turn a little left and drive backward straight to the box to park

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        pollenServo.init(hardwareMap);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        telemetry.addLine("start pressed");

        //shoot the preloaded pollen
        shoot();

        //face the garden, move next to the wall
        drive.turnDegrees( 90, 0.6, this);
        drive.strafeInches(-3, 0.6, this);

        //drive through the garden with the intake on
        intake.goForward();
        drive.driveInches(46, 0.6, this);
        intake.stop();

        //go back the same distance, turn the outtake back toward the hive
        drive.driveInches(-40, 0.6, this);
        //drive.strafeInches(3, 0.6, this);
        drive.turnDegrees(90, 0.6, this);

        //shoot the garden pollen
        shoot();

        //park: turn a little left, then drive backward straight to the box
        drive.turnDegrees(28, 0.6, this);
        drive.driveInches(-101, 0.6, this);

        drive.drive(0, 0, 0);

        telemetry.addLine("Done");
        telemetry.update();
    }

    //spin up the outtake, then open the pollen gate and run the intake to push the pollen in
    private void shoot() {
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();
        while (opModeIsActive() && !outtake.isAtSpeed() && spinUp.seconds() < 2) {
            idle();
        }

        pollenServo.open();
        intake.goForward();
        ElapsedTime feed = new ElapsedTime();
        while (opModeIsActive() && feed.seconds() < 2) {
            idle();
        }

        intake.stop();
        pollenServo.block();
        outtake.stop();
    }

}
