package org.firstinspires.ftc.teamcode.auton.pathing;


import static com.pedropathing.api.Paths.*;


import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;


//Red up path from the visualizer, written for Pedro 3.0 (exact same points and headings)
public class PedroRedUp {
    public Path MainChain;

    //start pose (same point as the visualizer, heading 90)
    public final Pose start = new Pose(56.079, 10.597, Math.toRadians(90));

    //The visualizer's first piece went from (56.079, 10.597) to the same point, turning 90 -> 180.
    //Pedro 3.0 crashes on a zero-length line, so it's a turn in place instead: follower.hold(startTurned)
    public final Pose startTurned = new Pose(56.079, 10.597, Math.toRadians(180));

    public final Path toGarden;
    public final Path toTop;
    public final Path toPark;

    //The visualizer's last piece starts near the start line, not where toPark ends (the red box),
    //so it's kept out of MainChain. Otherwise the robot would drive from the red box through the hive to reach it.
    public final Path returnNudge;


    public PedroRedUp(Follower follower) {
        toGarden = line(
                new Pose(56.079, 10.597),
                new Pose(9.593, 8.675)
        ).tangent();

        toTop = curve(
                new Pose(9.593, 8.675),
                new Pose(22.331, 83.637),
                new Pose(60.400, 129.947)
        ).linear(Math.toRadians(180), Math.toRadians(270));

        toPark = curve(
                new Pose(60.400, 129.947),
                new Pose(29.888, 97.547),
                new Pose(5.295, 115.024)
        ).linear(Math.toRadians(270), Math.toRadians(270));

        returnNudge = line(
                new Pose(55.858, 10.376),
                new Pose(56.079, 10.597)
        ).linear(Math.toRadians(90), Math.toRadians(90));

        MainChain = path(toGarden, toTop, toPark);
    }
}
