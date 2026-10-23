package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Drive Encoder Test", group = "Test")
public class DriveEncoderTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor lf = hardwareMap.get(DcMotor.class, "lf");
        DcMotor rf = hardwareMap.get(DcMotor.class, "rf");
        DcMotor rr = hardwareMap.get(DcMotor.class, "rr");
        DcMotor lr = hardwareMap.get(DcMotor.class, "lr");

        lf.setDirection(DcMotor.Direction.REVERSE);
        lr.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.FORWARD);
        rr.setDirection(DcMotor.Direction.FORWARD);

        for (DcMotor m : new DcMotor[]{lf, rf, rr, lr}) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        telemetry.addLine("Press START, then push the robot forward by hand.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("lf (port 0)", lf.getCurrentPosition());
            telemetry.addData("rf (port 1)", rf.getCurrentPosition());
            telemetry.addData("rr (port 2)", rr.getCurrentPosition());
            telemetry.addData("lr (port 3)", lr.getCurrentPosition());
            telemetry.addLine();
            telemetry.addLine("Push forward: all four should go UP by about the same amount.");
            telemetry.addLine("Stuck at 0 = encoder cable not plugged in.");
            telemetry.addLine("Going DOWN = that motor's direction is flipped.");
            telemetry.update();
        }
    }
}
