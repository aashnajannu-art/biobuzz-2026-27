package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.Robot.servo.PollenServo;

@Autonomous(name = "BioBuzzRedSafeAuto")
public class BioBuzzRedSafeAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();
    private Intake intake = new Intake();
    private Outtake outtake = new Outtake();
    private PollenServo pollenServo = new PollenServo();

    //+ = turn left, - = turn right
    public static double PARK_TURN_DEG = 28;
    //+ = forward
    public static double PARK_FORWARD_2_IN = -101;
    public static double PARK_POWER = 0.6;

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        pollenServo.init(hardwareMap);

        waitForStart();

        shoot();

        drive.turnDegrees(PARK_TURN_DEG, PARK_POWER, this);
        drive.driveInches(PARK_FORWARD_2_IN, PARK_POWER, this);
        drive.turnDegrees(28, 0.6, this);
        drive.driveInches(-101, 0.6, this);
        drive.turnDegrees(-28, 0.6, this);

        drive.drive(0, 0, 0);
    }

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
