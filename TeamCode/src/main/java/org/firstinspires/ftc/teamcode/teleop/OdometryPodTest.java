package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Odometry Pod Test", group = "Test")
public class OdometryPodTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotorEx podForward = hardwareMap.get(DcMotorEx.class, "podforward");
        DcMotorEx podStrafe = hardwareMap.get(DcMotorEx.class, "podstrafe");

        resetPods(podForward, podStrafe);

        telemetry.addLine("Press START, then push the robot by hand.");
        telemetry.addLine("Press A to reset both counts to 0.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                resetPods(podForward, podStrafe);
            }

            telemetry.addData("podforward (port 1)", podForward.getCurrentPosition());
            telemetry.addData("podstrafe  (port 0)", podStrafe.getCurrentPosition());
            telemetry.addLine();
            telemetry.addLine("Push forward: podforward changes, podstrafe stays near 0");
            telemetry.addLine("Push sideways: podstrafe changes, podforward stays near 0");
            telemetry.addLine("A = reset to 0");
            telemetry.update();
        }
    }

    private void resetPods(DcMotorEx... pods) {
        for (DcMotorEx pod : pods) {
            pod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            pod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }
}
