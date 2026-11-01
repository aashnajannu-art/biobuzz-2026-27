package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//Red Down path with simple moves (no Pedro tuning needed). See DownPathAuto for the moves.
@Autonomous(name = "RedDownAuto")
public class RedDownAuto extends DownPathAuto {
    public RedDownAuto() {
        super(false);
    }
}
