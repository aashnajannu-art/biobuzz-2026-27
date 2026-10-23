package org.firstinspires.ftc.teamcode.Robot;


import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class MecanumDrivetrain {

    public DcMotor lfMotor = null;
    public DcMotor lbMotor = null;
    public DcMotor rfMotor = null;
    public DcMotor rbMotor = null;

    public GoBildaPinpointDriver pinpoint = null;

    // -------------------- TUNING --------------------

    // Stop when within this many inches / degrees of the target.
    public static double DISTANCE_TOLERANCE_IN = 0.5;
    public static double TURN_TOLERANCE_DEG = 1.5;

    // Start slowing down this far from the target.
    public static double SLOW_DOWN_IN = 8.0;
    public static double SLOW_DOWN_DEG = 25.0;

    // Never go slower than this while still moving, or the robot can stall short of the target.
    public static double MIN_POWER = 0.15;

    // Give up on a single move after this long, so a stuck robot doesn't push forever.
    public static double MOVE_TIMEOUT_SEC = 5.0;

    // While driving/strafing, steer back to the starting heading and cancel sideways drift.
    // Raise if it still veers, lower if it wiggles side to side.
    public static double HEADING_KP = 0.03;
    public static double DRIFT_KP = 0.08;

    // Ramp power up over this long when starting a move, so one side doesn't grab first.
    public static double RAMP_UP_SEC = 0.3;

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

        // Wheel encoders aren't used - distance comes from the Pinpoint odometry.
        lfMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lbMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rfMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rbMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Pinpoint settings come from the Pinpoint Tuner results saved in pedro/Constants.java
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, Constants.localizerConfig.name.get());
        pinpoint.setEncoderResolution(Constants.localizerConfig.podType.get());
        pinpoint.setOffsets(
                Constants.localizerConfig.xPodOffset.get(),
                Constants.localizerConfig.yPodOffset.get(),
                DistanceUnit.INCH);
        pinpoint.setEncoderDirections(
                Constants.localizerConfig.xPodDirection.get(),
                Constants.localizerConfig.yPodDirection.get());
        // Robot must be still for a moment here while the Pinpoint IMU calibrates.
        pinpoint.resetPosAndIMU();

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

    // -------------------- ODOMETRY --------------------

    public double getX() {
        return pinpoint.getPosX(DistanceUnit.INCH);
    }

    public double getY() {
        return pinpoint.getPosY(DistanceUnit.INCH);
    }

    public double getHeadingDegrees() {
        return pinpoint.getHeading(AngleUnit.DEGREES);
    }

    // forward > 0 = forward, strafe > 0 = right, turn > 0 = left (counterclockwise)
    private void setPowers(double forward, double strafe, double turn) {
        double lf = forward + strafe - turn;
        double rf = forward - strafe + turn;
        double lb = forward - strafe - turn;
        double rb = forward + strafe + turn;

        double max = Math.max(1.0, Math.max(Math.max(Math.abs(lf), Math.abs(rf)), Math.max(Math.abs(lb), Math.abs(rb))));

        lfMotor.setPower(lf / max);
        rfMotor.setPower(rf / max);
        lbMotor.setPower(lb / max);
        rbMotor.setPower(rb / max);
    }

    private void stopMotors() {
        lfMotor.setPower(0);
        lbMotor.setPower(0);
        rfMotor.setPower(0);
        rbMotor.setPower(0);
    }

    // Full power far from the target, slowing down to MIN_POWER as it gets close.
    private static double rampedPower(double power, double remaining, double slowDownZone) {
        double scale = Math.min(1.0, Math.abs(remaining) / slowDownZone);
        return Math.max(MIN_POWER, Math.abs(power) * scale);
    }

    // -------------------- DRIVE STRAIGHT (INCHES) --------------------

    // +inches = forward, -inches = backward
    public void driveInches(double inches, double power, LinearOpMode opMode) {
        moveInches(inches, 0, power, opMode);
    }

    // -------------------- STRAFE (INCHES) --------------------

    // +inches = right, -inches = left
    public void strafeInches(double inches, double power, LinearOpMode opMode) {
        moveInches(0, inches, power, opMode);
    }

    private void moveInches(double forwardInches, double rightInches, double power, LinearOpMode opMode) {
        pinpoint.update();
        double startX = getX();
        double startY = getY();
        double startHeading = Math.toRadians(getHeadingDegrees());

        double target = Math.abs(forwardInches) + Math.abs(rightInches);
        double forwardSign = Math.signum(forwardInches);
        double rightSign = Math.signum(rightInches);

        ElapsedTime timer = new ElapsedTime();

        while (opMode.opModeIsActive() && timer.seconds() < MOVE_TIMEOUT_SEC) {
            pinpoint.update();
            double dx = getX() - startX;
            double dy = getY() - startY;

            // How far we've moved along the robot's starting forward / right directions.
            double movedForward = dx * Math.cos(startHeading) + dy * Math.sin(startHeading);
            double movedRight = dx * Math.sin(startHeading) - dy * Math.cos(startHeading);
            double moved = forwardSign * movedForward + rightSign * movedRight;

            double remaining = target - moved;
            if (remaining <= DISTANCE_TOLERANCE_IN) {
                break;
            }

            // Drift is movement in the direction we are NOT supposed to be going.
            double drift = forwardInches != 0 ? movedRight : movedForward;
            double headingError = AngleUnit.normalizeDegrees(Math.toDegrees(startHeading) - getHeadingDegrees());

            double p = rampedPower(power, remaining, SLOW_DOWN_IN);
            p *= Math.min(1.0, Math.max(MIN_POWER, timer.seconds() / RAMP_UP_SEC));

            double forward = forwardSign * p;
            double strafe = rightSign * p;
            if (forwardInches != 0) {
                strafe = -DRIFT_KP * drift;
            } else {
                forward = -DRIFT_KP * drift;
            }
            double turn = HEADING_KP * headingError;

            setPowers(forward, strafe, turn);
            opMode.idle();
        }

        stopMotors();
    }

    // -------------------- TURN (DEGREES) --------------------

    // +degrees = turn left (CCW), -degrees = right (CW)
    public void turnDegrees(double degrees, double power, LinearOpMode opMode) {
        pinpoint.update();
        double targetHeading = getHeadingDegrees() + degrees;

        ElapsedTime timer = new ElapsedTime();

        while (opMode.opModeIsActive() && timer.seconds() < MOVE_TIMEOUT_SEC) {
            pinpoint.update();
            double error = AngleUnit.normalizeDegrees(targetHeading - getHeadingDegrees());

            if (Math.abs(error) <= TURN_TOLERANCE_DEG) {
                break;
            }

            double p = rampedPower(power, error, SLOW_DOWN_DEG);
            setPowers(0, 0, Math.signum(error) * p);
            opMode.idle();
        }

        stopMotors();
    }
}
