/*pollen gate servo (stops the balls before they move forward into the outtake)*/
package org.firstinspires.ftc.teamcode.Robot.servo;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class PollenServo {
    //expansion hub servo port 0


    //TUNING
    //Servo position (0 to 1) that blocks the balls. Change these until the gate lines up on the robot.
    public static double BLOCK_POSITION = 1;
    //Servo position (0 to 1) that lets the balls through to the outtake.
    public static double OPEN_POSITION = 0;

    private Servo pollenServo;
    private boolean open = false;


    //initialize servo (starts closed so the balls stay back)
    public void init(HardwareMap hardwareMap) {
        pollenServo = hardwareMap.get(Servo.class, "pollenservo");
        block();
    }

    //stop the balls before the outtake
    public void block() {
        open = false;
        pollenServo.setPosition(BLOCK_POSITION);
    }

    //let the balls through to the outtake
    public void open() {
        open = true;
        pollenServo.setPosition(OPEN_POSITION);
    }

    public boolean isOpen() {
        return open;
    }

    public double getPosition() {
        return pollenServo.getPosition();
    }
}
