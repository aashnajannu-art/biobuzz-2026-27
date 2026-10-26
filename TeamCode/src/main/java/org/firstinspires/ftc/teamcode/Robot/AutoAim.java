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

    // Only aim at these tag IDs.Empty=aim at any tag the Limelight sees.
    private int[] targetTags = new int[0];

    //TUNING

    // Turn power per degree of tx. Raise if aiming is slow, lower if it overshoots/wobbles.
    public static double AIM_KP = 0.02;

    //Never turn slower than this while still off target, or the robot can stall.
    public static double AIM_MIN_POWER = 0.08;

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
    public double getTurnPower() {
        double error = getAimError();
        if (Double.isNaN(error) || Math.abs(error) <= AIM_TOLERANCE_DEG) {
            return 0;
        }

        double power = AIM_KP * error;
        power = Math.signum(power) * Math.max(AIM_MIN_POWER, Math.min(AIM_MAX_POWER, Math.abs(power)));
        return INVERT_TURN ? -power : power;
    }

    // Auton: turn in place until aimed at the tag (or timeout). Returns true if it got aimed.
    public boolean aim(MecanumDrivetrain drive, LinearOpMode opMode) {
        ElapsedTime timer = new ElapsedTime();

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
