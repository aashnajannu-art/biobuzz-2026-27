package org.firstinspires.ftc.teamcode.Robot;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class AutoAim {

    public Limelight3A limelight = null;

    private final ElapsedTime pidTimer = new ElapsedTime();
    private double integral = 0;
    private double lastError = Double.NaN;

    //empty = aim at any tag
    private int[] targetTags = new int[0];

    public static double AIM_KP = 0.02;
    public static double AIM_KI = 0.0;
    public static double AIM_KD = 0.001;
    public static double AIM_MIN_POWER = 0.05;
    public static double AIM_MAX_INTEGRAL_POWER = 0.15;
    public static double AIM_MAX_POWER = 0.5;
    public static double AIM_TOLERANCE_DEG = 1.0;
    //+ = aim more right
    public static double AIM_OFFSET_DEG = 0.0;
    public static long MAX_STALENESS_MS = 500;
    public static boolean INVERT_TURN = false;
    public static double AIM_TIMEOUT_SEC = 2.0;

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

    public void stop() {
        limelight.stop();
    }

    //the visible target tag closest to center, or null
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

    //degrees off target (+ = tag is to the right), NaN if no tag
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

    //yaw power for drive(); call every loop while aiming and resetAim() when you stop
    public double getTurnPower() {
        double error = getAimError();
        if (Double.isNaN(error)) {
            resetAim();
            return 0;
        }

        double dt = pidTimer.seconds();
        pidTimer.reset();

        double derivative = 0;
        if (!Double.isNaN(lastError) && dt > 0 && dt < 0.5) {
            derivative = (error - lastError) / dt;
            integral += error * dt;
            if (AIM_KI != 0) {
                double maxIntegral = AIM_MAX_INTEGRAL_POWER / AIM_KI;
                integral = Math.max(-maxIntegral, Math.min(maxIntegral, integral));
            }
        }
        lastError = error;

        if (Math.abs(error) <= AIM_TOLERANCE_DEG) {
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

    public void resetAim() {
        integral = 0;
        lastError = Double.NaN;
        pidTimer.reset();
    }

    //auton: turn in place until aimed or timed out
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
