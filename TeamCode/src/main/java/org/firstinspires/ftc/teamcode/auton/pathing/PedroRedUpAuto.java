package org.firstinspires.ftc.teamcode.auton.pathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.AutoAim;
import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
import org.firstinspires.ftc.teamcode.pedro.Constants;

//Red up: shoot preload -> intake the garden -> drive above the hive -> shoot -> park in the red box
//The shooter is on the BACK of the robot, the intake is on the front.
@Autonomous(name = "PedroRedUpAuto")
public class PedroRedUpAuto extends LinearOpMode {
    private PedroRedUp paths;
    private final Intake intake = new Intake();
    private final Outtake outtake = new Outtake();
    private final AutoAim autoAim = new AutoAim();
    private Follower follower;

    private static final int[] RED_TAGS = {30, 31, 32, 33, 34, 35, 36, 37}; // RED SCORING 30-33 + RED AUDIENCE 34-37

    //TUNING
    // Longest to wait for the outtake to reach its target RPM before feeding anyway (aiming happens during the spin up).
    public static double SPIN_UP_SEC = 2.0;
    // Intake runs this long to feed the balls into the outtake.
    public static double FEED_SEC = 2.0;
    // Stop aiming after this long and shoot anyway.
    public static double AIM_TIMEOUT_SEC = 1.5;
    // Give up on a path after this long so a stuck robot doesn't push forever.
    public static double PATH_TIMEOUT_SEC = 6.0;
    // Turn in place counts as done within this many degrees.
    public static double TURN_TOLERANCE_DEG = 3.0;

    @Override
    public void runOpMode() throws InterruptedException {
        follower = Constants.create(hardwareMap);
        paths = new PedroRedUp(follower);
        intake.init(hardwareMap);
        outtake.init(hardwareMap);
        autoAim.init(hardwareMap, RED_TAGS);

        follower.setPose(paths.start);
        follower.update();

        telemetry.addLine("Place robot at the red up start (heading 90, front facing up the field)");
        telemetry.update();

        waitForStart();
        follower.setPose(paths.start);
        follower.update();

        //1. shoot the preload from the start
        shoot();

        //2. turn in place so the intake faces the garden
        turnTo(paths.startTurned);

        //3. drive into the garden with the intake running
        intake.goForward();
        followPath(paths.toGarden);
        intake.stop();

        //4. drive above the hive and shoot again
        followPath(paths.toTop);
        shoot();

        //5. park in the red box
        followPath(paths.toPark);

        follower.stop();
        intake.stop();
        outtake.stop();
        autoAim.stop();
    }

    //spin up the outtake, auto aim at the tag while it spins up, then feed with the intake
    private void shoot() {
        outtake.run();
        ElapsedTime spinUp = new ElapsedTime();

        aim();

        //hold still where we aimed, and wait until the outtake reaches its target RPM (or SPIN_UP_SEC runs out)
        follower.hold(follower.pose());
        while (opModeIsActive() && !outtake.isAtSpeed() && spinUp.seconds() < SPIN_UP_SEC) {
            follower.update();
        }

        intake.goForward();
        ElapsedTime feed = new ElapsedTime();
        while (opModeIsActive() && feed.seconds() < FEED_SEC) {
            follower.update();
        }
        intake.stop();
        outtake.stop();
    }

    //turn in place until the Limelight says the shooter is on the tag (or timeout)
    private void aim() {
        ElapsedTime timer = new ElapsedTime();
        autoAim.resetAim();
        while (opModeIsActive() && timer.seconds() < AIM_TIMEOUT_SEC && !autoAim.isAimed()) {
            //AutoAim gives + = turn right, Pedro turn is + = turn left
            follower.manual(0, 0, -autoAim.getTurnPower());
            follower.update();
            telemetry.addData("Aiming", autoAim.hasTarget() ? String.format("%.1f deg off", autoAim.getAimError()) : "no tag");
            telemetry.update();
        }
        follower.manual(0, 0, 0);
        follower.update();
    }

    private void turnTo(Pose target) {
        follower.hold(target);
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < PATH_TIMEOUT_SEC) {
            follower.update();
            double error = Math.toDegrees(Math.abs(angleWrap(target.heading() - follower.pose().heading())));
            if (error < TURN_TOLERANCE_DEG) {
                break;
            }
        }
    }

    private void followPath(Path path) {
        follower.follow(path);
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.seconds() < PATH_TIMEOUT_SEC) {
            follower.update();
            if (follower.atParametricEnd()) {
                break;
            }
            telemetry.addData("Pose", follower.pose());
            telemetry.update();
        }
    }

    private static double angleWrap(double radians) {
        while (radians > Math.PI) radians -= 2 * Math.PI;
        while (radians < -Math.PI) radians += 2 * Math.PI;
        return radians;
    }
}
