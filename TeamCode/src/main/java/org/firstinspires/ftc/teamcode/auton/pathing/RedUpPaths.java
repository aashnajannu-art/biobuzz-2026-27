package org.firstinspires.ftc.teamcode.auton.pathing;


import static com.pedropathing.api.Paths.*;


import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;


//Red up auton, drawn in visualizer.pedropathing.com (inches, 0,0 = bottom-left corner, 90 = facing up)
//Heading is where the FRONT (intake) points. The shooter is on the back.
//Visualizer file: RedUp.pp in this folder (load it with the visualizer's file manager to edit, then update the poses here)
//Order: shoot preload -> toGarden -> intakeGarden -> backToShoot -> shoot again -> park
public class RedUpPaths {


    private final PoseFactory poseFactory = PoseFactory.degrees();


    //start against the bottom wall, intake facing the wall so the shooter (back) points up at the red side of the hive
    public final Pose start = poseFactory.of(56, 8, 270);
    //in front of the 4 balls in the bottom-left corner, facing left so the intake points at them
    private final Pose gardenApproach = poseFactory.of(28, 12, 180);
    //drove through all 4 balls
    private final Pose gardenEnd = poseFactory.of(10, 9, 180);
    //inside the red box on the left wall
    private final Pose park = poseFactory.of(9, 106, 270);


    //curve control points (keep the robot off the wall while it turns)
    private final Pose toGardenControl = poseFactory.of(45, 25, 0);
    private final Pose backToShootControl = poseFactory.of(40, 25, 0);
    private final Pose parkControl = poseFactory.of(18, 30, 0);


    public Path toGarden() {
        return curve(start, toGardenControl, gardenApproach).linear(start, gardenApproach);
    }


    //run the intake during this one, and drive it slowly so the balls get picked up
    public Path intakeGarden() {
        return line(gardenApproach, gardenEnd).constant(gardenApproach);
    }


    public Path backToShoot() {
        return curve(gardenEnd, backToShootControl, start).linear(gardenEnd, start);
    }


    //curves left around the hive and up into the red box
    public Path park() {
        return curve(start, parkControl, park).constant(park);
    }
}
