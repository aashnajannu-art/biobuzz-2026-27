package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;


@TeleOp(name="testing outtake", group="Linear Opmode")

public class testOuttake extends LinearOpMode {


    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    Outtake motor= new Outtake();

    @Override
    public void runOpMode() throws InterruptedException{

        //connect software to hardware
        motor.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();


        waitForStart(); //wait for user to press play
        runtime.reset();

        double targetRpm = Outtake.FAST_RPM;
        boolean dpadUpPrev = false;
        boolean dpadDownPrev = false;

        while (opModeIsActive()) {
            //dpad up/down bumps the target RPM by 50 so we can test any speed
            boolean dpadUpNow = gamepad1.dpad_up;
            if (dpadUpNow && !dpadUpPrev) {
                targetRpm+=50;
            }
            dpadUpPrev = dpadUpNow;

            boolean dpadDownNow = gamepad1.dpad_down;
            if (dpadDownNow && !dpadDownPrev) {
                targetRpm -= 50;
            }
            dpadDownPrev = dpadDownNow;

            if (gamepad1.a) {
                motor.spinUp(targetRpm);
            } else {
                motor.stop();
            }
            motor.update();

            double[] outtakeVelocity = motor.getVelocityOuttake();
            double voltage = hardwareMap.voltageSensor.iterator().next().getVoltage();

            telemetry.addData("Target RPM", targetRpm);
            telemetry.addData("Bottom RPM", outtakeVelocity[0]);
            telemetry.addData("Top RPM", outtakeVelocity[1]);
            telemetry.addData("Error Bottom", targetRpm - outtakeVelocity[0]);
            telemetry.addData("Error Top", targetRpm - outtakeVelocity[1]);
            telemetry.addData("Voltage", voltage);
            telemetry.addData("At Target Velocity", motor.isAtTargetVelocity());
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.update();
        }
    }
}

