package org.firstinspires.ftc.teamcode.Robot;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrivetrain {

    public DcMotor lfMotor = null;
    public DcMotor lbMotor = null;
    public DcMotor rfMotor = null;
    public DcMotor rbMotor = null;

    // -------------------- CALIBRATION CONSTANTS --------------------

    // goBILDA 5203, 19.2:1, 312 RPM
    public static final double TICKS_PER_REV = 537.6;

    // Assumed wheel diameter; update if you change wheels
    public static final double WHEEL_DIAMETER_IN = 4.0; //

    // Gear ratio: wheel revs per motor rev.
    // If you add external gearing, update this.
    public static double GEAR_RATIO = 1.0; //

    // Distance per wheel rev = π * D * GEAR_RATIO
    public static final double TICKS_PER_INCH = 28.06;
            //(TICKS_PER_REV * GEAR_RATIO) / (Math.PI * WHEEL_DIAMETER_IN);

    // Approximate turning constant (encoder ticks per degree of robot rotation).
    // You MUST tune this on your real robot.
    public static double TICKS_PER_DEGREE =  7.0389;

    // Max drive power used in teleop normalization
    public static final double TELEOP_MAX_POWER = 0.7;

    // -------------------- INIT --------------------

    public void init(HardwareMap hardwareMap) {
        lfMotor = hardwareMap.get(DcMotor.class, "lf");
        lbMotor = hardwareMap.get(DcMotor.class, "lr");
        rfMotor = hardwareMap.get(DcMotor.class, "rf");
        rbMotor = hardwareMap.get(DcMotor.class, "rr");

        // Adjust directions as needed for your build
        lfMotor.setDirection(DcMotor.Direction.REVERSE);
        lbMotor.setDirection(DcMotor.Direction.REVERSE);
        rfMotor.setDirection(DcMotor.Direction.FORWARD);
        rbMotor.setDirection(DcMotor.Direction.FORWARD);

        lfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use encoders for precise movement
        lfMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lbMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rfMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rbMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        stopMotors();
    }

    // -------------------- TELEOP DRIVE --------------------

    // Standard Mecanum drive: axial = forward, lateral = strafe, yaw = rotate
    public void drive(double axial, double lateral, double yaw) {
        // Optional small scaling for feel; change or remove if desired
        axial   *= 1.1;
        lateral *= 1.1;
        yaw     *= 1.1;

        double rightFrontPower = -axial + lateral - yaw;
        double leftFrontPower  = -axial - lateral + yaw;
        double rightBackPower  = -axial - lateral - yaw;
        double leftBackPower   = -axial + lateral + yaw;

        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > TELEOP_MAX_POWER) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }

        lfMotor.setPower(leftFrontPower);
        rfMotor.setPower(rightFrontPower);
        lbMotor.setPower(leftBackPower);
        rbMotor.setPower(rightBackPower);
    }

    // -------------------- COMMON ENCODER HELPER --------------------

    private void setRunToPosition() {
        lfMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lbMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rfMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rbMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void setRunUsingEncoder() {
        lfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void stopMotors() {
        lfMotor.setPower(0);
        lbMotor.setPower(0);
        rfMotor.setPower(0);
        rbMotor.setPower(0);
    }

    private boolean anyBusy() {
        return lfMotor.isBusy() || lbMotor.isBusy() || rfMotor.isBusy() || rbMotor.isBusy();
    }

    // -------------------- DRIVE STRAIGHT (INCHES) --------------------

    // Non-blocking: set targets, start motion, return immediately.
    public void driveInchesAsync(double inches, double power) {
        int moveCounts = (int) Math.round(inches * TICKS_PER_INCH);

        lfMotor.setTargetPosition(lfMotor.getCurrentPosition() + moveCounts);
        lbMotor.setTargetPosition(lbMotor.getCurrentPosition() + moveCounts);
        rfMotor.setTargetPosition(rfMotor.getCurrentPosition() + moveCounts);
        rbMotor.setTargetPosition(rbMotor.getCurrentPosition() + moveCounts);

        setRunToPosition();

        lfMotor.setPower(Math.abs(power));
        lbMotor.setPower(Math.abs(power));
        rfMotor.setPower(Math.abs(power));
        rbMotor.setPower(Math.abs(power));
    }

    // Blocking: waits until motion completes or opmode stops.
    public void driveInches(double inches, double power, LinearOpMode opMode) {
        driveInchesAsync(inches, power);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();

        }

        stopMotors();
        setRunUsingEncoder();
    }

    // -------------------- STRAFE (INCHES) --------------------

    // Non-blocking strafe: +inches = right, -inches = left
    public void strafeInchesAsync(double inches, double power) {
        int moveCounts = (int) Math.round(inches * TICKS_PER_INCH);

        // For mecanum, strafe pattern:
        // LF +, LR -, RF -, RR +
        lfMotor.setTargetPosition(lfMotor.getCurrentPosition() + moveCounts);
        lbMotor.setTargetPosition(lbMotor.getCurrentPosition() - moveCounts);
        rfMotor.setTargetPosition(rfMotor.getCurrentPosition() - moveCounts);
        rbMotor.setTargetPosition(rbMotor.getCurrentPosition() + moveCounts);

        setRunToPosition();

        lfMotor.setPower(Math.abs(power));
        lbMotor.setPower(Math.abs(power));
        rfMotor.setPower(Math.abs(power));
        rbMotor.setPower(Math.abs(power));
    }

    public void strafeInches(double inches, double power, LinearOpMode opMode) {
        strafeInchesAsync(inches, power);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();
        }

        stopMotors();
        setRunUsingEncoder();
    }

    // -------------------- TURN (DEGREES) --------------------

    // Non-blocking turn: +degrees = turn left (CCW), -degrees = right (CW)
    public void turnDegreesAsync(double degrees, double power) {
        int turnCounts = (int) Math.round(degrees * TICKS_PER_DEGREE);

        // Left side backwards, right side forwards for positive turn
        lfMotor.setTargetPosition(lfMotor.getCurrentPosition() - turnCounts);
        lbMotor.setTargetPosition(lbMotor.getCurrentPosition() - turnCounts);
        rfMotor.setTargetPosition(rfMotor.getCurrentPosition() + turnCounts);
        rbMotor.setTargetPosition(rbMotor.getCurrentPosition() + turnCounts);

        setRunToPosition();

        double p = Math.abs(power);
        lfMotor.setPower(p);
        lbMotor.setPower(p);
        rfMotor.setPower(p);
        rbMotor.setPower(p);
    }

    public void turnDegrees(double degrees, double power, LinearOpMode opMode) {
        turnDegreesAsync(degrees, power);

        while (opMode.opModeIsActive() && anyBusy()) {
            opMode.idle();
        }

        stopMotors();
        setRunUsingEncoder();
    }
}
