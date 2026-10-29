/*outtake (one motor)*/
package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


public class Outtake {
    private static final String MOTOR_NAME = "outtake_top";

    //goBILDA 6000 RPM (1:1) motor encoder: 28 ticks per revolution
    private static final double TICKS_PER_REV = 28.0;
    private static final double MAX_RPM = 6000.0;
    private static final double MAX_TICKS_PER_SEC = MAX_RPM / 60.0 * TICKS_PER_REV;

    //TUNING
    //Speed the outtake holds while shooting. Change this to change how far it shoots.
    public static double TARGET_RPM = 4500;

    //Counts as "up to speed" within this many RPM of the target.
    public static double RPM_TOLERANCE = 100;

    //PIDF for the hub's velocity control (same starting values as our old outtake code).
    //F = feedforward: the power needed to hold a speed. Starts at 32767 / max ticks per second.
    //P = how hard it pushes back when it's below the target (raise if it's slow to recover after a shot).
    //I = fixes a speed that's always a little low. D = usually 0 for a flywheel.
    public static double P = 70;
    public static double I = 2.0;
    public static double D = 0.0;
    public static double F = 32767.0 / MAX_TICKS_PER_SEC;

    private DcMotorEx outtake;
    private double targetRpm = 0;


    //initialize motor
    public void init(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        //FLOAT so the flywheel coasts down instead of slamming to a stop
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(P, I, D, F));

        stop();
    }

    //spin the outtake at TARGET_RPM
    public void run() {
        run(TARGET_RPM);
    }

    //spin the outtake at a specific RPM
    public void run(double rpm) {
        targetRpm = rpm;
        outtake.setVelocity(rpm / 60.0 * TICKS_PER_REV);
    }

    public void stop() {
        targetRpm = 0;
        outtake.setVelocity(0);
    }

    //true once the outtake is within RPM_TOLERANCE of the target (feed balls after this)
    public boolean isAtSpeed() {
        return targetRpm > 0 && Math.abs(Math.abs(getRpm()) - targetRpm) <= RPM_TOLERANCE;
    }

    public double getTargetRpm() {
        return targetRpm;
    }

    public double getPower() {
        return outtake.getPower();
    }

    //how fast the outtake is actually spinning (needs the encoder cable plugged in)
    public double getRpm() {
        return outtake.getVelocity() / TICKS_PER_REV * 60.0;
    }
}
