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

    static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45};
    static final int[] RED_TAGS  = {30, 31, 32, 33, 34, 35, 36, 37};

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

        boolean red = false;
        while (opModeInInit()) {
            if (gamepad1.x) red = false;
            if (gamepad1.b) red = true;
            telemetry.addLine("GAMEPAD 1: Press X = BLUE, B = RED");
            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.update();
        }
        autoAim.setTargetTags(red ? RED_TAGS : BLUE_TAGS);

        while (opModeIsActive()) {
            double axial   = gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x;
            double yaw     = gamepad1.right_stick_x;

            if (gamepad2.right_bumper && autoAim.hasTarget()) {
                yaw = autoAim.getTurnPower();
            } else {
                autoAim.resetAim();
            }

            drive.drive(axial, lateral, yaw);

            if (gamepad1.left_bumper || gamepad2.left_bumper) {
                intake.goBackward();
            } else if (gamepad1.right_trigger > 0.5 || gamepad2.left_trigger > 0.5) {
                intake.goForward();
            } else {
                intake.stop();
            }

            if (gamepad2.y) {
                outtake.run();
            } else {
                outtake.stop();
            }

            if (gamepad2.right_trigger > 0.5) {
                pollenServo.open();
            } else {
                pollenServo.block();
            }

            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addData("Outtake up to speed", outtake.isAtSpeed());
            telemetry.addData("Aimed", autoAim.isAimed());
            telemetry.update();
        }

        drive.drive(0, 0, 0);
        intake.stop();
        outtake.stop();
        pollenServo.block();
        autoAim.stop();
    }
}
