package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class intake2motor {
    //two motors driving the intake, they always get the same power
    private DcMotorEx intake;
    private DcMotorEx intake2;

    private double speed = 1;

    //initialize motors
    public void init(HardwareMap hardwareMap) {
        intake  = hardwareMap.get(DcMotorEx.class, "intake");
        intake2 = hardwareMap.get(DcMotorEx.class, "intake2");

        //both motors run the same direction
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake2.setDirection(DcMotorSimple.Direction.FORWARD);

        //stop fast when power is cut so balls don't roll out
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stop();
    }

    //spin intake inward
    public void goForward() {
        setPower(speed);
    }

    //spin intake outward
    public void goBackward() {
        setPower(-speed);
    }

    public void stop() {
        setPower(0.0);
    }

    //one place that sets both motors, so they never get out of sync
    private void setPower(double power) {
        intake.setPower(power);
        intake2.setPower(power);
    }
}

