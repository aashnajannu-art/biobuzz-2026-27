/*This code provides the methods for the outtake flywheel on our robot*/
package org.firstinspires.ftc.teamcode.Robot.motor;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Outtake {
    //initialize all variables
    //two motors share one axle, so they always get the same power
    private DcMotorEx outtakeBottom;
    private DcMotorEx outtakeTop;


    //encoder resolution of the bare goBILDA 6000 RPM motor (counts per rev of the output shaft)
    //verified on the robot by hand-turning the shaft one full revolution
    private static final double TICKS_PER_REV = 28.0;


    //free-spin max RPM, measured on the bench with testOuttake (don't trust the datasheet
    //number - re-measure if the motor/gearing changes). Used only to compute the F term below.
    private static final double MAX_RPM = 6000.0;
    private static final double MAX_TICKS_PER_SEC = (MAX_RPM / 60.0) * TICKS_PER_REV;


    public static final double FAST_RPM = 4500; //for the back
    public static final double MED_RPM = 4000;//for the front
    public static final double SlOW_RPM = 3700  ;//for close by

    public static final double AUTON_RPM=4500;//for auton speed at full battery


    //how close actual RPM has to be to targetRpm to count as "at speed"
    private static final double RPM_TOLERANCE = 100.0;


    private static final double PIDF_P = 70;
    private static final double PIDF_I = 2.0;
    private static final double PIDF_D = 0.0;
    private static final double PIDF_F = 32767.0 / MAX_TICKS_PER_SEC;


    //how fast the commanded setpoint is allowed to climb/fall, in RPM per second.
    //this ramps the target smoothly on spin-up / fast<->slow switches instead of
    //slamming the PIDF straight to the new value. tune this - lower = slower ramp.
    public static double RAMP_RATE_RPM_PER_SEC = 2000.0;


    //--- burst holdback: all 3 balls fire in one ~0.33s burst and each one measurably saps
    //RPM. we tried boosting the target UP after ball 1 to compensate for that sag, but ball 1
    //fires right at the start of the burst - there's no time for any boost to take effect
    //before it's already launched - so ball 1 kept going out at the full resting RPM while
    //balls 2/3 went out slower under sag. Chasing the sag after the fact wasn't closing the gap.
    //
    //easier fix: don't give ball 1 the advantage in the first place. Hold the WHOLE burst at a
    //reduced target instead of the full resting RPM, so ball 1 fires at roughly the same speed
    //balls 2/3 naturally settle to under sag - all three end up close together (at a slightly
    //lower, more consistent speed) instead of one fast ball and two trailing behind it.
    public static double BURST_HOLDBACK_RPM = 300.0; //RPM below resting target, held for the whole burst - TUNE on the field


    private double targetRpm = 0;
    //the ramped setpoint actually being sent to the motors right now
    private double commandedRpm = 0;
    private final ElapsedTime rampTimer = new ElapsedTime();


    private boolean burstActive = false;
    private double burstBaseRpm = 0;
    private final ElapsedTime burstTimer = new ElapsedTime();


    //initialize motors
    public void init(HardwareMap hardwareMap) {
        outtakeBottom = hardwareMap.get(DcMotorEx.class, "outtake_bottom");
        outtakeTop = hardwareMap.get(DcMotorEx.class, "outtake_top");


        //the two motors face opposite ways across the axle, so opposite directions turn the
        //shaft the same way. if they fight each other, flip outtakeTop to REVERSE.
        outtakeBottom.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeTop.setDirection(DcMotorSimple.Direction.REVERSE);


        //let the flywheel coast down instead of braking when we command 0 velocity
        outtakeBottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        outtakeTop.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


        //RUN_USING_ENCODER is required for setVelocity()/PIDF to actually close the loop
        outtakeBottom.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeTop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        outtakeBottom.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        outtakeTop.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);


        outtakeBottom.setVelocity(0);
        outtakeTop.setVelocity(0);


        commandedRpm = 0;
        rampTimer.reset();
        burstActive = false;
    }


    //sets the goal RPM. does NOT push it to the motors directly - call update() every
    //loop for the ramped setpoint to actually reach the motors.
    public void spinUp(double rpm) {
        targetRpm = rpm;
    }


    public void runFast() {
        spinUp(FAST_RPM);
    }


    public void runMed() {
        spinUp(MED_RPM);
    }
    public void runSlow(){
        spinUp(SlOW_RPM);
    }

    public void runAuton(){
        spinUp(AUTON_RPM);
    }


    public void stop() {
        targetRpm = 0;
        commandedRpm = 0;
        burstActive = false;
        outtakeBottom.setVelocity(0);
        outtakeTop.setVelocity(0);
    }


    //call this once per loop, with whether latch+intake are both currently feeding
    //into the outtake (not just floor pickup). on the rising edge, kicks off the
    //burst boost ramp from whatever target is currently active. going false does
    //NOT cancel it - the boost holds until stop() fully resets things.
    public void setBurstFeeding(boolean feeding) {
        if (feeding && !burstActive) {
            burstActive = true;
            burstBaseRpm = targetRpm;
            burstTimer.reset();
        }
    }


    //call this once per loop. advances the commanded setpoint toward targetRpm at
    //RAMP_RATE_RPM_PER_SEC and pushes it to the motors. this is what actually makes
    //the flywheel ramp up smoothly instead of jumping straight to the target.
    //during a burst, the target is held flat at burstBaseRpm - BURST_HOLDBACK_RPM for the
    //whole burst (all 3 balls fire at the same reduced speed - see comment on
    //BURST_HOLDBACK_RPM above for why), skipping the normal slew limiter since we want that
    //reduced target applied immediately, not ramped into.
    public void update() {
        double dt = rampTimer.seconds();
        rampTimer.reset();


        if (burstActive) {
            targetRpm = burstBaseRpm - BURST_HOLDBACK_RPM;
            commandedRpm = targetRpm;
        } else {
            double maxStep = RAMP_RATE_RPM_PER_SEC * dt;
            if (commandedRpm < targetRpm) {
                commandedRpm = Math.min(commandedRpm + maxStep, targetRpm);
            } else if (commandedRpm > targetRpm) {
                commandedRpm = Math.max(commandedRpm - maxStep, targetRpm);
            }
        }


        double ticksPerSec = (commandedRpm / 60.0) * TICKS_PER_REV;
        outtakeBottom.setVelocity(ticksPerSec);
        outtakeTop.setVelocity(ticksPerSec);
    }


    //true once both motors have caught up to the last commanded target RPM (within tolerance).
    //not used for gating anything right now - kept as a utility for tuning telemetry.
    public boolean isAtTargetVelocity(double toleranceRpm) {
        if (targetRpm <= 0) return false;
        double[] v = getVelocityOuttake();
        return Math.abs(v[0] - targetRpm) <= toleranceRpm && Math.abs(v[1] - targetRpm) <= toleranceRpm;
    }


    public boolean isAtTargetVelocity() {
        return isAtTargetVelocity(RPM_TOLERANCE);
    }


    public double getTargetRpm() {
        return targetRpm;
    }


    //measured speed of both motors in RPM (negative when spinning in reverse)
    //index 0 is outtake_bottom, index 1 is outtake_top
    public double[] getVelocityOuttake() {
        double bottomRPM = (outtakeBottom.getVelocity() / TICKS_PER_REV) * 60.0;
        double topRPM = (outtakeTop.getVelocity() / TICKS_PER_REV) * 60.0;
        return new double[]{bottomRPM, topRPM};
    }
}

