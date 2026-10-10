package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;

@Autonomous(name = "BioBuzzBlueAuto")
public class BioBuzzBlueAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();
    private Limelight3A limelight;
    private DcMotor outtake;
    private DcMotor intake;

    private final int[] ourTags = {38, 39, 40, 41};

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        outtake = hardwareMap.get(DcMotor.class, "outtake_top");
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        telemetry.addLine("start pressed");

        outtake.setPower(1.0);
        sleep(1000);

        while (!seeOurTag() && getRuntime() < 23 && !isStopRequested()) {
            idle();
        }

        intake.setPower(1.0);
        sleep(2000);
        intake.setPower(0);
        outtake.setPower(0);

        drive.turnDegrees(0, 0.6, this);
        drive.driveInches(0, 0.6, this);
        drive.strafeInches(0, 0.6, this);

        drive.drive(0, 0, 0);

        telemetry.addLine("Done");
        telemetry.update();
    }

    private boolean seeOurTag() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return false;
        }
        for (LLResultTypes.FiducialResult tag : result.getFiducialResults()) {
            for (int id : ourTags) {
                if (tag.getFiducialId() == id) {
                    return true;
                }
            }
        }
        return false;
    }
}
