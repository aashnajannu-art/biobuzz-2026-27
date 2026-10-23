package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;

@Autonomous(name = "BioBuzzRedAuto")
public class BioBuzzRedAuto extends LinearOpMode {
    private MecanumDrivetrain drive = new MecanumDrivetrain();
    private Intake intake = new Intake();
    private Limelight3A limelight;
    private DcMotor outtake;

    private final int[] ourTags = {30, 31, 32, 33, 34, 35, 36, 37}; // RED SCORING 30-33 + RED AUDIENCE 34-37

    //TUNING
    //flip the sign of turn and strafe.
    public static double SPIN_UP_SEC = 1.0;
    // Stop waiting for a tag after this long (counted from START) and shoot anyway.
    public static double TAG_WAIT_TIMEOUT_SEC = 5.0;
    // Ignore Limelight results older than this (ms) so a frozen last frame doesn't count as seeing the tag.
    public static long MAX_STALENESS_MS = 500;
    public static double FEED_SEC = 2.0;

    public static double PARK_TURN_DEG = 0;       // + means left
    public static double PARK_FORWARD_IN = 24;    // + means forward
    public static double PARK_STRAFE_IN = 0;      // + means right
    public static double PARK_POWER = 0.6;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.setAutoClear(false);

        drive.init(hardwareMap);
        intake.init(hardwareMap);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        outtake = hardwareMap.get(DcMotor.class, "outtake_top");
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();
        //
        ElapsedTime autoTimer = new ElapsedTime();
        telemetry.addLine("start pressed");
        telemetry.update();

        outtake.setPower(1.0);
        sleep((long) (SPIN_UP_SEC * 1000));

        while (!seeOurTag() && autoTimer.seconds() < TAG_WAIT_TIMEOUT_SEC && opModeIsActive()) {
            idle();
        }
        telemetry.addData("tag seen", seeOurTag());
        telemetry.update();

        //both intake rollers feed the balls
        intake.goForward();
        sleep((long) (FEED_SEC * 1000));
        intake.stop();
        outtake.setPower(0);

        drive.turnDegrees(PARK_TURN_DEG, PARK_POWER, this);
        drive.driveInches(PARK_FORWARD_IN, PARK_POWER, this);
        drive.strafeInches(PARK_STRAFE_IN, PARK_POWER, this);

        drive.drive(0, 0, 0);
        limelight.stop();

        telemetry.addLine("Done");
        telemetry.update();
    }

    private boolean seeOurTag() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid() || result.getStaleness() > MAX_STALENESS_MS) {
            return false;
        }
        if (result.getFiducialResults() == null) {
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
