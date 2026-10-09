package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
public class Outtake {
    private DcMotorEx outtakeBottom;
    private DcMotorEx outtakeTop;


    private static final double TICKS_PER_REV = 28.0;


    private static final double MAX_RPM = 6000.0;
    private static final double MAX_TICKS_PER_SEC = (MAX_RPM / 60.0) * TICKS_PER_REV;


    public static final double FAST_RPM = 4500;
    public static final double MED_RPM = 4000;
    public static final double SlOW_RPM = 3700  ;

    public static final double AUTON_RPM=4500;


    private static final double RPM_TOLERANCE = 100.0;


    private static final double PIDF_P = 70;
    private static final double PIDF_I = 2.0;
    private static final double PIDF_D = 0.0;
    private static final double PIDF_F = 32767.0 / MAX_TICKS_PER_SEC;


    public static double RAMP_RATE_RPM_PER_SEC = 2000.0;


    public static double BURST_HOLDBACK_RPM = 300.0;


    private double targetRpm = 0;
    private double commandedRpm = 0;
    private final ElapsedTime rampTimer = new ElapsedTime();


    private boolean burstActive = false;
    private double burstBaseRpm = 0;
    private final ElapsedTime burstTimer = new ElapsedTime();


    public void init(HardwareMap hardwareMap) {
        outtakeBottom = hardwareMap.get(DcMotorEx.class, "outtake_bottom");
        outtakeTop = hardwareMap.get(DcMotorEx.class, "outtake_top");


        outtakeBottom.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeTop.setDirection(DcMotorSimple.Direction.REVERSE);


        outtakeBottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtakeTop.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        outtakeBottom.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeTop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        outtakeBottom.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        outtakeTop.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);


        outtakeBottom.setVelocity(0);
        outtakeTop.setVelocity(0);


        commandedRpm = 0;
        rampTimer.reset();
        burstActive = false;
    }


    public void spinUp(double rpm) {
        targetRpm = rpm;
    }


    public void runFast() {
        spinUp(FAST_RPM);
    }


    public void runMed() {
        spinUp(MED_RPM);
    }
    public void runSlow(){
        spinUp(SlOW_RPM);
    }

    public void runAuton(){
        spinUp(AUTON_RPM);
    }


    public void stop() {
        targetRpm = 0;
        commandedRpm = 0;
        burstActive = false;
        outtakeBottom.setVelocity(0);
        outtakeTop.setVelocity(0);
    }


    public void setBurstFeeding(boolean feeding) {
        if (feeding && !burstActive) {
            burstActive = true;
            burstBaseRpm = targetRpm;
            burstTimer.reset();
        }
    }


    public void update() {
        double dt = rampTimer.seconds();
        rampTimer.reset();


        if (burstActive) {
            targetRpm = burstBaseRpm - BURST_HOLDBACK_RPM;
            commandedRpm = targetRpm;
        } else {
            double maxStep = RAMP_RATE_RPM_PER_SEC * dt;
            if (commandedRpm < targetRpm) {
                commandedRpm = Math.min(commandedRpm + maxStep, targetRpm);
            } else if (commandedRpm > targetRpm) {
                commandedRpm = Math.max(commandedRpm - maxStep, targetRpm);
            }
        }


        double ticksPerSec = (commandedRpm / 60.0) * TICKS_PER_REV;
        outtakeBottom.setVelocity(ticksPerSec);
        outtakeTop.setVelocity(ticksPerSec);
    }


    public boolean isAtTargetVelocity(double toleranceRpm) {
        if (targetRpm <= 0) return false;
        double[] v = getVelocityOuttake();
        return Math.abs(v[0] - targetRpm) <= toleranceRpm && Math.abs(v[1] - targetRpm) <= toleranceRpm;
    }


    public boolean isAtTargetVelocity() {
        return isAtTargetVelocity(RPM_TOLERANCE);
    }


    public double getTargetRpm() {
        return targetRpm;
    }


    public double[] getVelocityOuttake() {
        double bottomRPM = (outtakeBottom.getVelocity() / TICKS_PER_REV) * 60.0;
        double topRPM = (outtakeTop.getVelocity() / TICKS_PER_REV) * 60.0;
        return new double[]{bottomRPM, topRPM};
    }
}

