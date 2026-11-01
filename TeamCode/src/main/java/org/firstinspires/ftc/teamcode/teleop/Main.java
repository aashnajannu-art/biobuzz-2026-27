package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot.AutoAim;
import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.Robot.servo.PollenServo;

@TeleOp(name = "Main", group = "Linear Opmode")
public class Main extends LinearOpMode {

    //BIOBUZZ tag clusters
    static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45}; //BLUE AUDIENCE 38-41 and BLUE SCORING 42-45
    static final int[] RED_TAGS  = {30, 31, 32, 33, 34, 35, 36, 37}; //RED SCORING 30-33 and RED AUDIENCE 34-37

    private final MecanumDrivetrain drive = new MecanumDrivetrain();
    private final Intake intake = new Intake();
    private final Outtake outtake = new Outtake();
    private final PollenServo pollenServo = new PollenServo();
    private final AutoAim autoAim = new AutoAim();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        pollenServo.init(hardwareMap);
        autoAim.init(hardwareMap, BLUE_TAGS);

        //Pick alliance during INIT:X = blue,B = red (blue and red buttons on controllers)
        boolean red = false;
        while (opModeInInit()) {
            if (gamepad1.x) red = false;
            if (gamepad1.b) red = true;
            telemetry.addLine(">>> DRIVERS: CHOOSE YOUR ALLIANCE <<<");
            telemetry.addLine("GAMEPAD 1: Press X = BLUE, B = RED");
            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addLine();
            showControls();
            telemetry.update();
        }
        autoAim.setTargetTags(red ? RED_TAGS : BLUE_TAGS);

        while (opModeIsActive()) {
            //Drivetrain
            //left stick=drive/strafe,right stick=turn
            double axial   = gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x;
            double yaw     = gamepad1.right_stick_x;

            //Auto aim
            //Gamepad 2 holds right bumper to turn toward the alliance tag (driver keeps drive/strafe)
            boolean aiming = gamepad2.right_bumper;
            if (aiming && autoAim.hasTarget()) {
                yaw = autoAim.getTurnPower();
            } else {
                //clear the PID memory so the next aim starts fresh
                autoAim.resetAim();
            }

            drive.drive(axial, lateral, yaw);

            //Intake
            //Works from either gamepad: LB=reverse (push balls out),A=both rollers in
            String intakeState;
            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                intake.goBackward();
                intakeState = "REVERSE";
            } else if (gamepad1.a || gamepad2.a) {
                intake.goForward();
                intakeState = "IN";
            } else {
                intake.stop();
                intakeState = "OFF";
            }

            //Outtake
            //Gamepad 2 holds Y to spin the outtake (works at the same time as the intake)
            boolean shooting = gamepad2.y;
            if (shooting) {
                outtake.run();
            } else {
                outtake.stop();
            }

            //Pollen gate
            //Closed by default so the balls stop before the outtake. Gamepad 2 holds right trigger to open it, let go to close it again
            if (gamepad2.right_trigger > 0.5) {
                pollenServo.open();
            } else {
                pollenServo.block();
            }

            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addData("Intake", intakeState);
            telemetry.addData("Outtake RPM", "%.0f (target %.0f)", outtake.getRpm(), outtake.getTargetRpm());
            telemetry.addData("Outtake up to speed", outtake.isAtSpeed());
            telemetry.addData("Pollen gate", pollenServo.isOpen() ? "OPEN" : "BLOCKED");
            telemetry.addData("Tag seen", autoAim.hasTarget());
            telemetry.addData("Locked", autoAim.isAimed());
            telemetry.addLine();
            showControls();
            telemetry.update();
        }

        drive.drive(0, 0, 0);
        intake.stop();
        outtake.stop();
        pollenServo.block();
        autoAim.stop();
    }

    //control list shown on telemetry during both init and the match
    private void showControls() {
        telemetry.addLine("GAMEPAD 1: Left stick = drive/strafe, right stick = turn");
        telemetry.addLine("GAMEPAD 2: Hold RIGHT BUMPER = auto aim");
        telemetry.addLine("GAMEPAD 2: Hold Y = spin outtake");
        telemetry.addLine("GAMEPAD 2: Hold RIGHT TRIGGER = open pollen gate (let balls into outtake)");
        telemetry.addLine("EITHER GAMEPAD: Hold A = both intake rollers in");
        telemetry.addLine("EITHER GAMEPAD: Hold LEFT BUMPER = reverse intake (balls out)");
    }
}
