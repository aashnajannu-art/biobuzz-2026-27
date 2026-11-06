package org.firstinspires.ftc.teamcode.Robot.motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Outtake {
    //goBILDA 6000 RPM motor encoder
    private static final double TICKS_PER_REV = 28.0;

    public static double TARGET_RPM = 850;
    public static double RPM_TOLERANCE = 100;

    public static double P = 70;
    public static double I = 2.0;
    public static double D = 0.0;
    public static double F = 32767.0 / 2800.0;

    private DcMotorEx outtake;
    private double targetRpm = 0;

    public void init(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, "outtake_top");
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(P, I, D, F));

        stop();
    }

    public void run() {
        run(TARGET_RPM);
    }

    public void run(double rpm) {
        targetRpm = rpm;
        outtake.setVelocity(rpm / 60.0 * TICKS_PER_REV);
    }

    public void stop() {
        targetRpm = 0;
        outtake.setVelocity(0);
    }

    public boolean isAtSpeed() {
        return targetRpm > 0 && Math.abs(Math.abs(getRpm()) - targetRpm) <= RPM_TOLERANCE;
    }

    public double getRpm() {
        return outtake.getVelocity() / TICKS_PER_REV * 60.0;
    }
}
