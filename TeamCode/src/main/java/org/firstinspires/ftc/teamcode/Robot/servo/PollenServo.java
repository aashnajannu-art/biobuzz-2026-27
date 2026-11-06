package org.firstinspires.ftc.teamcode.Robot.servo;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class PollenServo {
    public static double BLOCK_POSITION = 0;
    public static double OPEN_POSITION = 0.5;

    private Servo pollenServo;

    public void init(HardwareMap hardwareMap) {
        pollenServo = hardwareMap.get(Servo.class, "pollenservo");
        block();
    }

    public void block() {
        pollenServo.setPosition(BLOCK_POSITION);
    }

    public void open() {
        pollenServo.setPosition(OPEN_POSITION);
    }
}
