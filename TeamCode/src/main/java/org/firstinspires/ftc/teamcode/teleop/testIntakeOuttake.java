package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="testing intake (3 wheels) + outtake (one motor)", group="Linear Opmode")

public class testIntakeOuttake extends LinearOpMode {

    private static final String INTAKE_NAME = "intake";
    private static final String INTAKE_NAME_2 = "intake2";
    private static final String OUTTAKE_NAME = "outtake_top";


    private static final double INTAKE_POWER = 1.0;
    private static final double INTAKE_POWER_2 = 1.0;
    private static final double OUTTAKE_POWER = 1.0;


    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException{

        DcMotorEx intake = hardwareMap.get(DcMotorEx.class, INTAKE_NAME);
        DcMotorEx intake2 = hardwareMap.get(DcMotorEx.class, INTAKE_NAME_2);
        DcMotorEx outtake = hardwareMap.get(DcMotorEx.class, OUTTAKE_NAME);

        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake2.setDirection(DcMotorSimple.Direction.FORWARD);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Hold A = spin both intake rollers");
        telemetry.addLine("Hold X = spin only " + INTAKE_NAME);
        telemetry.addLine("Hold B = spin only " + INTAKE_NAME_2);
        telemetry.addLine("Hold Y = spin outtake");
        telemetry.update();


        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Intake
            if (gamepad1.a) {
                intake.setPower(INTAKE_POWER);
                intake2.setPower(INTAKE_POWER_2);
            } else if (gamepad1.x) {
                intake.setPower(INTAKE_POWER);
                intake2.setPower(0);
            } else if (gamepad1.b) {
                intake.setPower(0);
                intake2.setPower(INTAKE_POWER_2);
            } else {
                intake.setPower(0);
                intake2.setPower(0);
            }

            // Outtake (independent of intake, so both can run at once)
            if (gamepad1.y) {
                outtake.setPower(OUTTAKE_POWER);
            } else {
                outtake.setPower(0);
            }

            telemetry.addData(INTAKE_NAME + " Power", intake.getPower());
            telemetry.addData(INTAKE_NAME_2 + " Power", intake2.getPower());
            telemetry.addData(OUTTAKE_NAME + " Power", outtake.getPower());
            telemetry.addData("Run Time", runtime.seconds());
            telemetry.update();
        }
    }
}
