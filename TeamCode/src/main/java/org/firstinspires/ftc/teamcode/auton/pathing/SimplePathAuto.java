package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Robot.AutoAim;
import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.Robot.servo.PollenServo;

//Blue uses the same moves as red because the BioBuzz field is rotated 180 degrees for blue (not mirrored).
public abstract class SimplePathAuto extends LinearOpMode {
    protected final MecanumDrivetrain drive = new MecanumDrivetrain();
    protected final Intake intake = new Intake();
    protected final Outtake outtake = new Outtake();
    protected final PollenServo pollenServo = new PollenServo();
    protected final AutoAim autoAim = new AutoAim();

    private static final int[] RED_TAGS = {30, 31, 32, 33, 34, 35, 36, 37};
    private static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45};

    public static double POWER = 0.6;
    public static double SPIN_UP_SEC = 2.0;
    public static double FEED_SEC = 2.0;
    public static double TAG_WAIT_SEC = 2.0;
    //skip shots that would start after this, to leave time to park
    public static double LAST_SHOT_SEC = 18.0;

    private final boolean blue;
    private final ElapsedTime matchTimer = new ElapsedTime();

    protected SimplePathAuto(boolean blue) {
        this.blue = blue;
    }

    protected abstract void runPath();

    protected abstract String placement();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        pollenServo.init(hardwareMap);
        autoAim.init(hardwareMap, blue ? BLUE_TAGS : RED_TAGS);

        telemetry.addData("Place robot", placement());
        telemetry.update();

        waitForStart();
        matchTimer.reset();

        runPath();

        drive.drive(0, 0, 0);
        intake.stop();
        outtake.stop();
        pollenServo.block();
        autoAim.stop();
    }

    //+ = forward, - = backward
    protected void forward(double inches) {
        if (!opModeIsActive() || inches == 0) return;
        drive.driveInches(inches, POWER, this);
    }

    //+ = right, - = left
    protected void strafe(double inches) {
        if (!opModeIsActive() || inches == 0) return;
        drive.strafeInches(inches, POWER, this);
    }

    //angle from the starting direction: + = left, - = right
    protected void face(double degreesFromStart) {
        if (!opModeIsActive()) return;
        drive.pinpoint.update();
        double turn = AngleUnit.normalizeDegrees(degreesFromStart - drive.getHeadingDegrees());
        drive.turnDegrees(turn, POWER, this);
    }

    protected void shoot() {
        if (!opModeIsActive()) return;
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();

        autoAim.aim(drive, this);
        fire(spinUp);
    }

    //skips the shot if our tags aren't visible or it's too late to park
    protected void shootWhenTagVisible() {
        if (!opModeIsActive()) return;
        if (matchTimer.seconds() > LAST_SHOT_SEC) {
            return;
        }
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();

        while (opModeIsActive() && !autoAim.hasTarget() && spinUp.seconds() < TAG_WAIT_SEC) {
            idle();
        }
        if (!autoAim.hasTarget()) {
            outtake.stop();
            return;
        }

        autoAim.aim(drive, this);
        fire(spinUp);
    }

    private void fire(ElapsedTime spinUp) {
        while (opModeIsActive() && !outtake.isAtSpeed() && spinUp.seconds() < SPIN_UP_SEC) {
            idle();
        }
        pollenServo.open();
        intake.goForward();
        ElapsedTime feed = new ElapsedTime();
        while (opModeIsActive() && feed.seconds() < FEED_SEC) {
            idle();
        }
        intake.stop();
        pollenServo.block();
        outtake.stop();
    }
}
