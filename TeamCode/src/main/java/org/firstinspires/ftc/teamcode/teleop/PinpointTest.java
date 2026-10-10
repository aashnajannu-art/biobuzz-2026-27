package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Pinpoint Test", group = "Test")
public class PinpointTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        telemetry.addLine("Keep the robot still while the Pinpoint calibrates.");
        telemetry.addLine("Press START, then push the robot by hand.");
        telemetry.addLine("Press A to reset to 0.");
        telemetry.update();

        pinpoint.resetPosAndIMU();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                pinpoint.resetPosAndIMU();
            }

            pinpoint.update();

            telemetry.addData("Status", pinpoint.getDeviceStatus());
            telemetry.addData("Encoder X (forward pod)", pinpoint.getEncoderX());
            telemetry.addData("Encoder Y (strafe pod)", pinpoint.getEncoderY());
            telemetry.addData("Heading (deg)", "%.1f", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Update rate (Hz)", "%.0f", pinpoint.getFrequency());
            telemetry.addLine();
            telemetry.addLine("Push forward: X changes, Y stays near 0");
            telemetry.addLine("Push sideways: Y changes, X stays near 0");
            telemetry.addLine("Turn counterclockwise: heading goes up");
            telemetry.addLine("A = reset to 0");
            telemetry.update();
        }
    }
}
