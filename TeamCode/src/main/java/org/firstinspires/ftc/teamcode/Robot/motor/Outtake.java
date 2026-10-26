/*outtake (one motor)*/
package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Outtake {
    private static final String MOTOR_NAME = "outtake_top";

    private static final double POWER = 0.8;

    //goBILDA 6000 RPM (1:1) motor encoder: 28 ticks per revolution
    private static final double TICKS_PER_REV = 28.0;

    private DcMotorEx outtake;


    //initialize motor
    public void init(HardwareMap hardwareMap) {
        outtake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        //FLOAT so the flywheel coasts down instead of slamming to a stop
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        stop();
    }

    //spin the outtake
    public void run() {
        outtake.setPower(POWER);
    }

    public void stop() {
        outtake.setPower(0.0);
    }

    public double getPower() {
        return outtake.getPower();
    }

    //how fast the outtake is actually spinning (needs the encoder cable plugged in)
    public double getRpm() {
        return outtake.getVelocity() / TICKS_PER_REV * 60.0;
    }
}
