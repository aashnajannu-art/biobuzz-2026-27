/*intake (3 intake wheels, driven by 2 motors)*/
package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Intake {
    private static final String MOTOR_NAME = "intake";
    private static final String MOTOR_NAME_2 = "intake2";

    private static final double POWER = 1.0;
    private static final double POWER_2 = 1.0;

    //initialize all variables
    private DcMotorEx intake;
    private DcMotorEx intake2;


    //initialize motors
    public void init(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        intake2 = hardwareMap.get(DcMotorEx.class, MOTOR_NAME_2);

        //if a roller pushes balls out instead of pulling them in, flip its direction to REVERSE
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake2.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        stop();
    }

    //pull balls in (both rollers)
    public void goForward() {
        intake.setPower(POWER);
        intake2.setPower(POWER_2);
    }

    //push balls back out (both rollers)
    public void goBackward() {
        intake.setPower(-POWER);
        intake2.setPower(-POWER_2);
    }

    //spin only the first intake motor
    public void runIntakeOnly() {
        intake.setPower(POWER);
        intake2.setPower(0);
    }

    //spin only the second intake motor
    public void runIntake2Only() {
        intake.setPower(0);
        intake2.setPower(POWER_2);
    }

    public void stop() {
        intake.setPower(0.0);
        intake2.setPower(0.0);
    }

    public double getPower() {
        return intake.getPower();
    }

    public double getPower2() {
        return intake2.getPower();
    }
}
