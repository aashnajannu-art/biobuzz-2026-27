package org.firstinspires.ftc.teamcode.auton.pathing;


import static com.pedropathing.api.Paths.*;


import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;


//Red up path from the visualizer, rewritten for Pedro 3.0 (same coordinates)
//Heading is where the FRONT (intake) points. The shooter is on the back, so 270 = shooter facing the hive.
public class PedroRedUp {


    private final PoseFactory poseFactory = PoseFactory.degrees();


    //start against the bottom wall, shooter facing the hive
    public final Pose start = poseFactory.of(56.079, 10.597, 270);
    //same spot turned so the intake faces the garden.
    //This was a zero-length path in the visualizer, so do it with follower.hold(startTurned) instead.
    public final Pose startTurned = poseFactory.of(56.079, 10.597, 180);
    private final Pose garden = poseFactory.of(9.593, 8.675, 180);
    private final Pose topControl = poseFactory.of(22.331, 83.637, 0);
    //above the hive: intake facing up so the shooter (back) faces down at the hive for the second shot
    private final Pose top = poseFactory.of(60.400, 129.947, 90);
    private final Pose parkControl = poseFactory.of(29.888, 97.547, 0);
    //same heading as top so it doesn't spin while parking
    private final Pose park = poseFactory.of(5.295, 115.024, 90);
    private final Pose returnStart = poseFactory.of(55.858, 10.376, 270);
    private final Pose returnEnd = poseFactory.of(56.079, 10.597, 270);


    //drive into the garden, intake leading
    public Path toGarden() {
        return line(startTurned, garden).tangent();
    }


    public Path toTop() {
        return curve(garden, topControl, top).linear(garden, top);
    }


    public Path toPark() {
        return curve(top, parkControl, park).constant(park);
    }


    //garden -> top -> park, back to back
    public Path mainChain() {
        return path(toGarden(), toTop(), toPark());
    }


    //last segment from the visualizer. It doesn't connect to the park spot (it starts back near the start),
    //so it's kept separate and isn't part of mainChain()
    public Path returnNudge() {
        return line(returnStart, returnEnd).constant(returnEnd);
    }
}
