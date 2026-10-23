package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.motor.Intake;


@TeleOp(name="intake testing", group="Linear Opmode")

public class testIntake extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    Intake intake = new Intake();

    @Override
    public void runOpMode() throws InterruptedException{

        intake.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Hold A = intake in, Hold B = push out");
        telemetry.update();


        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                intake.goForward();
            } else if (gamepad1.b) {
                intake.goBackward();
            } else {
                intake.stop();
            }

            telemetry.addData("Run Time", runtime.seconds());
            telemetry.update();
        }
    }
}
