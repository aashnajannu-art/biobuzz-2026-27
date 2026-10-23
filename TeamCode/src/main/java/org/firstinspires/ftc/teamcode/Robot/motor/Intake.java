/*This code provides the methods for the intake on our robot*/
package org.firstinspires.ftc.teamcode.Robot.motor;




import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Intake {
   //initialize all variables
   private DcMotorEx intake;
   private DcMotorEx intake2;


   private double speed = 1;


   //initialize motors
   public void init(HardwareMap hardwareMap) {
       intake = hardwareMap.get(DcMotorEx.class, "intake");
       intake2 = hardwareMap.get(DcMotorEx.class, "intake2");


       //if a roller pushes balls out instead of pulling them in, flip its direction to FORWARD
       intake.setDirection(DcMotorSimple.Direction.FORWARD);
       intake2.setDirection(DcMotorSimple.Direction.FORWARD);


       intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       intake2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

       intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       intake2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


       stop();
   }
   //pull balls in
   public void goForward(){
       intake.setPower(speed);
       intake2.setPower(speed);
   }
   //push balls back out
   public void goBackward(){
       intake.setPower(-speed);
       intake2.setPower(-speed);
   }
   public void stop(){
       intake.setPower(0.0);
       intake2.setPower(0.0);
   }




}
