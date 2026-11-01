package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//Red Up path with simple moves (no Pedro tuning needed). See UpPathAuto for the moves.
@Autonomous(name = "RedUpAuto")
public class RedUpAuto extends UpPathAuto {
    public RedUpAuto() {
        super(false);
    }
}
