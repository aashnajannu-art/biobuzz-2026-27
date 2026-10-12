package org.firstinspires.ftc.teamcode.auton.pathing;


import static com.pedropathing.api.Paths.*;


import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;


public class Paths {


    private final PoseFactory poseFactory = PoseFactory.degrees();


    private final Pose start = poseFactory.of(82.5, 8.25, 90);
    private final Pose path1 = poseFactory.of(136, 33, 90);


    public Path path1() {
        return line(start, path1).linear(start, path1);
    }
}


//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//
//
//import org.firstinspires.ftc.teamcode.Robot.Launcher;
//import org.firstinspires.ftc.teamcode.Robot.MecanumDrivetrain;
//
//
//import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
//import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
//
//
//import org.firstinspires.ftc.teamcode.Robot.motor.Intake;
//import org.firstinspires.ftc.teamcode.Robot.motor.Outtake;
//import org.firstinspires.ftc.teamcode.Robot.servo.Hood;
//import org.firstinspires.ftc.teamcode.Robot.servo.Sort;
//
//
//import org.firstinspires.ftc.teamcode.auton.vision.TagDetection;
//import org.firstinspires.ftc.teamcode.util.ColorSequence;