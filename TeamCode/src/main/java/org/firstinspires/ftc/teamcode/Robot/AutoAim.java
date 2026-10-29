package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

// Turns the robot to face an AprilTag using the Limelight's tx (how far left/right
// the tag is from the center of the camera, in degrees).
public class AutoAim {

    public Limelight3A limelight = null;

    //PID memory between loops
    private final ElapsedTime pidTimer = new ElapsedTime();
    private double integral = 0;
    private double lastError = Double.NaN;

    // Only aim at these tag IDs.Empty=aim at any tag the Limelight sees.
    private int[] targetTags = new int[0];

    //TUNING

    //PID + F for turning toward the tag (error = degrees the tag is off center)
    // P: turn power per degree of error. Raise if aiming is slow, lower if it overshoots/wobbles.
    public static double AIM_KP = 0.02;
    // I: builds up while the robot is stuck just off target. Raise slowly if it stops a few degrees short.
    public static double AIM_KI = 0.0;
    // D: slows the turn as it closes in. Raise if it overshoots and swings back and forth.
    public static double AIM_KD = 0.001;
    // F: small push added in the turn direction so the robot can still turn when it's close to the tag (overcomes friction).
    public static double AIM_MIN_POWER = 0.05;
    // Cap on how much the I term can add, so it can't wind up and spin the robot.
    public static double AIM_MAX_INTEGRAL_POWER = 0.15;

    //Never turn faster than this while aiming.
    public static double AIM_MAX_POWER = 0.5;

    // Counts as "aimed" when the tag is within this many degrees of center.
    public static double AIM_TOLERANCE_DEG = 1.0;

    // Add this to tx if the camera/shooter isn't centered on the robot (degrees, + = aim more right).
    public static double AIM_OFFSET_DEG = 0.0;

    // Ignore Limelight results older than this (ms) so we don't chase an old tag position.
    public static long MAX_STALENESS_MS = 500;

    // Flip this if the robot turns AWAY from the tag.
    public static boolean INVERT_TURN = false;

    // Give up aiming in auton after this long.
    public static double AIM_TIMEOUT_SEC = 2.0;

    // -------------------- INIT --------------------

    public void init(HardwareMap hardwareMap, int... targetTags) {
        this.targetTags = targetTags;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void setTargetTags(int... targetTags) {
        this.targetTags = targetTags;
    }

    // Pipeline the Limelight is actually running right now (should be 0).
    public int getPipelineIndex() {
        return limelight.getStatus().getPipelineIndex();
    }

    public String getPipelineType() {
        return limelight.getStatus().getPipelineType();
    }

    // Raw Limelight output with NO filtering (no staleness check, no tag ID filter).
    // Use this to figure out why hasTarget() is false.
    public String getDebugInfo() {
        LLResult result = limelight.getLatestResult();
        if (result == null) {
            return "result = null (Limelight not sending data)";
        }
        if (result.getFiducialResults() == null) {
            return String.format("valid=%b stale=%dms tags=none", result.isValid(), result.getStaleness());
        }
        StringBuilder ids = new StringBuilder();
        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
            ids.append(tag.getFiducialId()).append(String.format(" (tx %.1f) ", tag.getTargetXDegrees()));
        }
        return String.format("valid=%b stale=%dms tags=[%s]",
                result.isValid(), result.getStaleness(), ids.toString().trim());
    }

    public void stop() {
        limelight.stop();
    }

    // -------------------- VISION --------------------

    // Returns the tag we should aim at (the one closest to center), or null if none is visible.
    public LLResultTypes.FiducialResult getTargetTag() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid() || result.getStaleness() > MAX_STALENESS_MS) {
            return null;
        }
        if (result.getFiducialResults() == null) {
            return null;
        }

        LLResultTypes.FiducialResult best = null;
        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
            if (!isTargetTag(tag.getFiducialId())) {
                continue;
            }
            if (best == null || Math.abs(tag.getTargetXDegrees()) < Math.abs(best.getTargetXDegrees())) {
                best = tag;
            }
        }
        return best;
    }

    private boolean isTargetTag(int id) {
        if (targetTags.length == 0) {
            return true;
        }
        for (int t : targetTags) {
            if (t == id) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTarget() {
        return getTargetTag() != null;
    }

    // Degrees the tag is from where we want to aim (+ = tag is to the right). NaN if no tag.
    public double getAimError() {
        LLResultTypes.FiducialResult tag = getTargetTag();
        if (tag == null) {
            return Double.NaN;
        }
        return tag.getTargetXDegrees() + AIM_OFFSET_DEG;
    }

    public boolean isAimed() {
        double error = getAimError();
        return !Double.isNaN(error) && Math.abs(error) <= AIM_TOLERANCE_DEG;
    }

    // -------------------- AIMING --------------------

    // Yaw power to pass into MecanumDrivetrain.drive(axial, lateral, yaw).
    // Returns 0 when no tag is visible or we're already on target.
    // Call this every loop while aiming. Call resetAim() whenever you stop aiming.
    public double getTurnPower() {
        double error = getAimError();
        if (Double.isNaN(error)) {
            //lost the tag: forget the old error so the D term doesn't jump when it comes back
            resetAim();
            return 0;
        }

        double dt = pidTimer.seconds();
        pidTimer.reset();

        double derivative = 0;
        if (!Double.isNaN(lastError) && dt > 0 && dt < 0.5) {
            derivative = (error - lastError) / dt;
            integral += error * dt;
            //keep the I term from winding up
            if (AIM_KI != 0) {
                double maxIntegral = AIM_MAX_INTEGRAL_POWER / AIM_KI;
                integral = Math.max(-maxIntegral, Math.min(maxIntegral, integral));
            }
        }
        lastError = error;

        if (Math.abs(error) <= AIM_TOLERANCE_DEG) {
            //on target: stop, and clear the I term so it doesn't push past next time
            integral = 0;
            return 0;
        }

        double power = AIM_KP * error
                + AIM_KI * integral
                + AIM_KD * derivative
                + Math.copySign(AIM_MIN_POWER, error);
        power = Math.max(-AIM_MAX_POWER, Math.min(AIM_MAX_POWER, power));
        return INVERT_TURN ? -power : power;
    }

    // Clears the PID memory. Call when the aim button is let go or before a new aim in auton.
    public void resetAim() {
        integral = 0;
        lastError = Double.NaN;
        pidTimer.reset();
    }

    // Auton: turn in place until aimed at the tag (or timeout). Returns true if it got aimed.
    public boolean aim(MecanumDrivetrain drive, LinearOpMode opMode) {
        ElapsedTime timer = new ElapsedTime();
        resetAim();

        while (opMode.opModeIsActive() && timer.seconds() < AIM_TIMEOUT_SEC) {
            if (isAimed()) {
                drive.drive(0, 0, 0);
                return true;
            }
            drive.drive(0, 0, getTurnPower());
            opMode.idle();
        }

        drive.drive(0, 0, 0);
        return false;
    }
}
