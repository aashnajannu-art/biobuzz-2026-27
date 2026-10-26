package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot.AutoAim;
import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;

@TeleOp(name = "Main", group = "Linear Opmode")
public class Main extends LinearOpMode {

    //BIOBUZZ tag clusters
    static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45}; //BLUE AUDIENCE 38-41 and BLUE SCORING 42-45
    static final int[] RED_TAGS  = {30, 31, 32, 33, 34, 35, 36, 37}; //RED SCORING 30-33 and RED AUDIENCE 34-37

    private final MecanumDrivetrain drive = new MecanumDrivetrain();
    private final Intake intake = new Intake();
    private final Outtake outtake = new Outtake();
    private final AutoAim autoAim = new AutoAim();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        autoAim.init(hardwareMap, BLUE_TAGS);

        //Pick alliance during INIT:X = blue,B = red (blue and red buttons on controllers)
        boolean red = false;
        while (opModeInInit()) {
            if (gamepad1.x) red = false;
            if (gamepad1.b) red = true;
            telemetry.addLine("Press X = BLUE, B = RED");
            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addLine();
            telemetry.addLine("Left stick = drive/strafe, right stick = turn");
            telemetry.addLine("Hold RIGHT BUMPER = auto aim");
            telemetry.addLine("Hold A = both intake rollers in");
            telemetry.addLine("Hold X = only intake, B = only intake2");
            telemetry.addLine("Hold LEFT BUMPER = reverse intake (balls out)");
            telemetry.addLine("Hold Y = spin outtake");
            telemetry.update();
        }
        autoAim.setTargetTags(red ? RED_TAGS : BLUE_TAGS);

        //highest outtake RPM since the outtake was last turned on
        double peakRpm = 0;

        while (opModeIsActive()) {
            //Drivetrain
            //left stick=drive/strafe,right stick=turn
            double axial   = gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x;
            double yaw     = gamepad1.right_stick_x;

            //Auto aim
            //Hold right bumper to turn toward the alliance tag (driver keeps drive/strafe)
            boolean aiming = gamepad1.right_bumper;
            if (aiming && autoAim.hasTarget()) {
                yaw = autoAim.getTurnPower();
            }

            drive.drive(axial, lateral, yaw);

            //Intake
            //LB=reverse (push balls out),A=both rollers in,X=only intake,B=only intake2
            String intakeState;
            if (gamepad1.left_bumper) {
                intake.goBackward();
                intakeState = "REVERSE";
            } else if (gamepad1.a) {
                intake.goForward();
                intakeState = "IN";
            } else if (gamepad1.x) {
                intake.runIntakeOnly();
                intakeState = "intake only";
            } else if (gamepad1.b) {
                intake.runIntake2Only();
                intakeState = "intake2 only";
            } else {
                intake.stop();
                intakeState = "OFF";
            }

            //Outtake
            //Hold Y to spin the outtake (works at the same time as the intake)
            boolean shooting = gamepad1.y;
            if (shooting) {
                outtake.run();
            } else {
                outtake.stop();
            }

            double outtakeRpm = outtake.getRpm();
            if (shooting) {
                peakRpm = Math.max(peakRpm, Math.abs(outtakeRpm));
            } else if (Math.abs(outtakeRpm) < 50) {
                peakRpm = 0;
            }

            double aimError = autoAim.getAimError();
            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addData("Sticks", "axial %.2f  lateral %.2f  yaw %.2f", axial, lateral, yaw);
            telemetry.addData("Intake", intakeState);
            telemetry.addData("Intake power", "%.2f / %.2f", intake.getPower(), intake.getPower2());
            telemetry.addData("intake2 RPM", "%.0f", intake.getRpm2());
            telemetry.addData("Outtake on", shooting);
            telemetry.addData("Outtake RPM", "%.0f", outtakeRpm);
            telemetry.addData("Outtake peak RPM", "%.0f", peakRpm);
            telemetry.addData("Aiming", aiming);
            telemetry.addData("Tag seen", !Double.isNaN(aimError));
            telemetry.addData("Aim error (deg)", Double.isNaN(aimError) ? "-" : String.format("%.2f", aimError));
            telemetry.addData("Locked", autoAim.isAimed());
            telemetry.update();
        }

        drive.drive(0, 0, 0);
        intake.stop();
        outtake.stop();
        autoAim.stop();
    }
}
