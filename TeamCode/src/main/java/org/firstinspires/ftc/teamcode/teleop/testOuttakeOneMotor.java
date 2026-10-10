package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="testing outtake (one motor)", group="Linear Opmode")

public class testOuttakeOneMotor extends LinearOpMode {

    
    private static final String MOTOR_NAME = "outtake_top";

    private static final double POWER = 1.0;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException{

        DcMotorEx outtake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Hold A = spin outtake");
        telemetry.update();


        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                outtake.setPower(POWER);
            } else {
                outtake.setPower(0);
            }

            telemetry.addData("Power", outtake.getPower());
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.update();
        }
    }
}
