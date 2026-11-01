package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//Blue Down path with simple moves (no Pedro tuning needed). See DownPathAuto for the moves.
@Autonomous(name = "BlueDownAuto")
public class BlueDownAuto extends DownPathAuto {
    public BlueDownAuto() {
        super(true);
    }
}
