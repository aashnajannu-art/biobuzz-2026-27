package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.List;

@TeleOp(name = "Auto Aim TeleOp")
public class AutoAimTeleOp extends LinearOpMode {

    // ---- Tune these ----
    // BIOBUZZ tag clusters (from the SDK's AprilTagGameDatabase), same lists as the autos
    static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45}; // BLUE AUDIENCE 38-41 + BLUE SCORING 42-45
    static final int[] RED_TAGS  = {30, 31, 32, 33, 34, 35, 36, 37}; // RED SCORING 30-33 + RED AUDIENCE 34-37
    static final double OUTTAKE_POWER = 1.0;  // same power the autos shoot with
    static final double KP = 0.02;            // turn power per degree of error
    static final double MIN_TURN = 0.05;      // overcomes friction near center
    static final double MAX_TURN = 0.5;       // caps turn speed
    static final double TOLERANCE_DEG = 1.0;  // "close enough" window
    static final long MAX_STALENESS_MS = 500; // ignore frozen Limelight results older than this

    @Override
    public void runOpMode() {
        // Names match our Driver Station config (same as MecanumDrivetrain)
        DcMotor lf = hardwareMap.get(DcMotor.class, "lf");
        DcMotor lr = hardwareMap.get(DcMotor.class, "lr");
        DcMotor rf = hardwareMap.get(DcMotor.class, "rf");
        DcMotor rr = hardwareMap.get(DcMotor.class, "rr");
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        for (DcMotor m : new DcMotor[]{lf, lr, rf, rr}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Same outtake motor and direction as BioBuzzBlueAuto / BioBuzzRedAuto
        DcMotor outtake = hardwareMap.get(DcMotor.class, "outtake_top");
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        // Drives both intake motors ("intake" and "intake2") together
        Intake intake = new Intake();
        intake.init(hardwareMap);

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);   // AprilTag pipeline
        limelight.start();

        // Pick alliance during INIT: X = blue, B = red
        boolean red = false;
        while (opModeInInit()) {
            if (gamepad1.x) red = false;
            if (gamepad1.b) red = true;
            telemetry.addLine("Press X = BLUE, B = RED");
            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.update();
        }
        int[] targetTags = red ? RED_TAGS : BLUE_TAGS;

        while (opModeIsActive()) {
            double drive  = -gamepad1.left_stick_y;
            double strafe =  gamepad1.left_stick_x;
            double turn   =  gamepad1.right_stick_x;

            Double tx = getTagTx(limelight, targetTags);
            boolean aiming = gamepad1.right_bumper;

            if (aiming && tx != null) {
                if (Math.abs(tx) > TOLERANCE_DEG) {
                    double power = KP * tx;
                    power += Math.copySign(MIN_TURN, power);
                    turn = Math.max(-MAX_TURN, Math.min(MAX_TURN, power));
                } else {
                    turn = 0;  // locked on
                }
            }

            // Hold left bumper to spin up the outtake
            boolean shooting = gamepad1.left_bumper;
            outtake.setPower(shooting ? OUTTAKE_POWER : 0);

            // A = intake in, B = intake out (same buttons as testIntake)
            String intakeState;
            if (gamepad1.a) {
                intake.goForward();
                intakeState = "IN";
            } else if (gamepad1.b) {
                intake.goBackward();
                intakeState = "OUT";
            } else {
                intake.stop();
                intakeState = "OFF";
            }

            // Mecanum mixing
            double lfP = drive + strafe + turn;
            double lrP = drive - strafe + turn;
            double rfP = drive - strafe - turn;
            double rrP = drive + strafe - turn;
            double max = Math.max(1.0, Math.max(Math.abs(lfP),
                    Math.max(Math.abs(lrP), Math.max(Math.abs(rfP), Math.abs(rrP)))));
            lf.setPower(lfP / max);
            lr.setPower(lrP / max);
            rf.setPower(rfP / max);
            rr.setPower(rrP / max);

            telemetry.addData("Alliance", red ? "RED" : "BLUE");
            telemetry.addData("Sticks", "drive %.2f  strafe %.2f  turn %.2f", drive, strafe, turn);
            telemetry.addData("Motors", "lf %.2f  lr %.2f  rf %.2f  rr %.2f",
                    lfP / max, lrP / max, rfP / max, rrP / max);
            telemetry.addData("Aiming", aiming);
            telemetry.addData("Outtake on", shooting);
            telemetry.addData("Intake", intakeState);
            telemetry.addData("Tag seen", tx != null);
            telemetry.addData("tx (deg)", tx == null ? "-" : String.format("%.2f", tx));
            telemetry.addData("Locked", tx != null && Math.abs(tx) <= TOLERANCE_DEG);
            telemetry.update();
        }

        outtake.setPower(0);
        intake.stop();
        limelight.stop();
    }

    /** Horizontal angle to the target tag in degrees, or null if not visible. */
    private Double getTagTx(Limelight3A limelight, int[] targetTags) {
        LLResult result = limelight.getLatestResult();
        // isValid() is the tv check; staleness catches a frozen last frame that still reads valid
        if (result == null || !result.isValid() || result.getStaleness() > MAX_STALENESS_MS) return null;

        List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
        if (tags == null) return null;
        for (LLResultTypes.FiducialResult tag : tags) {
            for (int id : targetTags) {
                if (tag.getFiducialId() == id) {
                    return tag.getTargetXDegrees();
                }
            }
        }
        return null;
    }
}
