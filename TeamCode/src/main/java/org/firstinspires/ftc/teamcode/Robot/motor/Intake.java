package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;


public class Intake {
    public static final String MOTOR_NAME = "intake";

    private DcMotorEx intake;


    public void init(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);

        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake.setPower(0);
    }


    public void goForward(double power) {
        intake.setPower(Math.abs(power));
    }


    public void goBackward(double power) {
        intake.setPower(-Math.abs(power));
    }


    public void stop() {
        intake.setPower(0);
    }


    public double getPower() {
        return intake.getPower();
    }


    public double getCurrentAmps() {
        return intake.getCurrent(CurrentUnit.AMPS);
    }
}
