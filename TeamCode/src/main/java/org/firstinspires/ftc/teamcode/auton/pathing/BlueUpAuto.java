package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//Blue Up path with simple moves (no Pedro tuning needed). See UpPathAuto for the moves.
@Autonomous(name = "BlueUpAuto")
public class BlueUpAuto extends UpPathAuto {
    public BlueUpAuto() {
        super(true);
    }
}
