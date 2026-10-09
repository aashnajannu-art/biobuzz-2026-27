package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="intake testing", group="Linear Opmode")

public class testIntake extends LinearOpMode {

    private static final String MOTOR_NAME = "intake";
    private static final String MOTOR_NAME_2 = "intake2";


    private static final double POWER = 1.0;
    private static final double POWER_2 = 1.0;


    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException{

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        DcMotorEx intake2 = hardwareMap.get(DcMotorEx.class, MOTOR_NAME_2);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        intake2.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Hold A = spin both rollers");
        telemetry.addLine("Hold X = spin only " + MOTOR_NAME);
        telemetry.addLine("Hold B = spin only " + MOTOR_NAME_2);
        telemetry.update();


        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                intake.setPower(POWER);
                intake2.setPower(POWER_2);
            } else if (gamepad1.x) {
                intake.setPower(POWER);
                intake2.setPower(0);
            } else if (gamepad1.b) {
                intake.setPower(0);
                intake2.setPower(POWER_2);
            } else {
                intake.setPower(0);
                intake2.setPower(0);
            }

            telemetry.addData(MOTOR_NAME + " Power", intake.getPower());
            telemetry.addData(MOTOR_NAME_2 + " Power", intake2.getPower());
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.update();
        }
    }
}
