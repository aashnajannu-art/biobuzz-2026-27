package org.firstinspires.ftc.teamcode.auton.pathing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//Blue uses the exact same moves as RedAuton because the BioBuzz field is rotated 180 degrees for blue (not mirrored).
//Change the numbers in RedAuton and both alliances update.
@Autonomous(name = "BlueAuton") //shoots, goes through the garden, shoots again, parks
public class BlueAuton extends RedAuton {
}
