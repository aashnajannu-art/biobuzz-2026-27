package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Robot.AutoAim;
import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.Robot.servo.PollenServo;

//Autos built exactly like the safe autos: a list of moves (drive X inches, strafe X inches, turn).
//The Pinpoint only measures each move by itself, the same way the safe autos use it.
//Blue uses the exact same moves as red because the BioBuzz field is rotated 180 degrees for blue (not mirrored).
public abstract class SimplePathAuto extends LinearOpMode {
    protected final MecanumDrivetrain drive = new MecanumDrivetrain();
    protected final Intake intake = new Intake();
    protected final Outtake outtake = new Outtake();
    protected final PollenServo pollenServo = new PollenServo();
    protected final AutoAim autoAim = new AutoAim();

    private static final int[] RED_TAGS = {30, 31, 32, 33, 34, 35, 36, 37};  // RED SCORING 30-33 + RED AUDIENCE 34-37
    private static final int[] BLUE_TAGS = {38, 39, 40, 41, 42, 43, 44, 45}; // BLUE AUDIENCE 38-41 + BLUE SCORING 42-45

    //TUNING (same power as the safe autos)
    public static double POWER = 0.6;
    // Longest to wait for the outtake to reach its target RPM before feeding anyway.
    public static double SPIN_UP_SEC = 2.0;
    // Intake runs this long during each shot to push the pollen into the outtake.
    public static double FEED_SEC = 2.0;
    // For every shot after the first: longest to wait for our tags to be visible before skipping the shot.
    public static double TAG_WAIT_SEC = 2.0;
    // Skip any shot that would start after this many seconds, so there's always time left to park.
    public static double LAST_SHOT_SEC = 18.0;

    private final boolean blue;
    private final ElapsedTime matchTimer = new ElapsedTime();

    protected SimplePathAuto(boolean blue) {
        this.blue = blue;
    }

    //the moves for this auto
    protected abstract void runPath();

    //where to place the robot (shown on the Driver Station during INIT)
    protected abstract String placement();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        pollenServo.init(hardwareMap);
        autoAim.init(hardwareMap, blue ? BLUE_TAGS : RED_TAGS);

        telemetry.addData("Alliance", blue ? "BLUE" : "RED");
        telemetry.addData("Place robot", placement());
        telemetry.addLine("Keep the robot still for 2 seconds after INIT (Pinpoint is calibrating)");
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

    // -------------------- MOVES (same as the safe autos) --------------------

    //+ = forward, - = backward
    protected void forward(double inches) {
        if (!opModeIsActive() || inches == 0) return;
        drive.driveInches(inches, POWER, this);
        show(String.format("forward %.1f", inches));
    }

    //+ = right, - = left
    protected void strafe(double inches) {
        if (!opModeIsActive() || inches == 0) return;
        drive.strafeInches(inches, POWER, this);
        show(String.format("strafe %.1f", inches));
    }

    //turn to an angle measured from the direction the robot started in (0 = starting direction,
    //+ = left, - = right). Same gyro turn the safe autos use, but measured from the start so the little
    //turn auto aim makes before a shot doesn't get added onto the next turn.
    protected void face(double degreesFromStart) {
        if (!opModeIsActive()) return;
        drive.pinpoint.update();
        double turn = AngleUnit.normalizeDegrees(degreesFromStart - drive.getHeadingDegrees());
        drive.turnDegrees(turn, POWER, this);
        show(String.format("face %.0f", degreesFromStart));
    }

    //first shot: spin up, auto aim at our tags, wait for speed, then run the intake to push the pollen in
    protected void shoot() {
        if (!opModeIsActive()) return;
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();

        autoAim.aim(drive, this);
        fire(spinUp);
        show("shoot");
    }

    //every shot after the first: only shoot once our tags are visible (waits up to TAG_WAIT_SEC while the outtake
    //spins up). If they aren't seen, it skips the shot and keeps the pollen. Also skipped if it's too late to park.
    protected void shootWhenTagVisible() {
        if (!opModeIsActive()) return;
        if (matchTimer.seconds() > LAST_SHOT_SEC) {
            show("skipped shot (saving time to park)");
            return;
        }
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();

        while (opModeIsActive() && !autoAim.hasTarget() && spinUp.seconds() < TAG_WAIT_SEC) {
            idle();
        }
        if (!autoAim.hasTarget()) {
            outtake.stop();
            show("skipped shot (tags not visible)");
            return;
        }

        autoAim.aim(drive, this);
        fire(spinUp);
        show("shoot");
    }

    //wait for the outtake to reach speed (or SPIN_UP_SEC since it started), then open the pollen gate and feed with the intake
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

    //last step, time used, and how far the Pinpoint thinks the robot has moved since the start
    private void show(String step) {
        drive.pinpoint.update();
        telemetry.addData("Step", step);
        telemetry.addData("Time (s)", "%.1f", matchTimer.seconds());
        telemetry.addData("Moved from start", "forward %.1f  left %.1f  turned %.0f",
                drive.getX(), drive.getY(), drive.getHeadingDegrees());
        telemetry.update();
    }
}
