package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@TeleOp(name = "Wheel Test", group = "Test")
public class WheelTest extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);

        telemetry.addLine("Lift the robot so the wheels are off the ground.");
        telemetry.addLine("Hold X = lf, Y = rf, B = rr, A = lr (each should spin FORWARD)");
        telemetry.addLine("Hold dpad up = all four forward");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double power = 0.3;

            drive.lfMotor.setPower(gamepad1.x || gamepad1.dpad_up ? power : 0);
            drive.rfMotor.setPower(gamepad1.y || gamepad1.dpad_up ? power : 0);
            drive.rbMotor.setPower(gamepad1.b || gamepad1.dpad_up ? power : 0);
            drive.lbMotor.setPower(gamepad1.a || gamepad1.dpad_up ? power : 0);

            drive.pinpoint.update();
            telemetry.addLine("X = lf (front left), Y = rf (front right)");
            telemetry.addLine("A = lr (back left),  B = rr (back right)");
            telemetry.addLine("Dpad up = all four");
            telemetry.addLine();
            telemetry.addData("pinpoint forward (in)", "%.1f", drive.getX());
            telemetry.addData("pinpoint sideways (in)", "%.1f", drive.getY());
            telemetry.addData("pinpoint heading (deg)", "%.1f", drive.getHeadingDegrees());
            telemetry.update();
        }
    }
}
