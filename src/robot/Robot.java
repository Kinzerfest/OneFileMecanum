package robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

//import edu.wpi.first.wpilibj.SPI;
//import com.kauailabs.navx.frc.AHRS;
public class Robot extends TimedRobot {
    //Variable declarations
    VictorSP leftFront, leftBack, rightFront, rightBack;
    Joystick primaryStick;
    MecanumDrive mecanumDrive;

    //AHRS navX;
    public void robotInit() {
        //Motors and Servos
        leftFront = new VictorSP(3);
        leftBack = new VictorSP(2);
        rightFront = new VictorSP(0);
        rightBack = new VictorSP(1);
        //navX = new AHRS(SPI.Port.kMXP);
        primaryStick = new Joystick(0);
        //MecanumDrive
        mecanumDrive = new MecanumDrive(leftFront, leftBack, rightFront, rightBack);
        mecanumDrive.setDeadband(0.18); //Sets deadzone for Mecanum Drive
    }

    public void autonomousInit() {
    }

    public void autonomousPeriodic() {
    }

    public void teleopPeriodic() {
        drive();
        //angleAdjustment();
    }

    public void testPeriodic() {
    }

    //--------------------------------------------Robot Methods------------------------------------------//
    public void drive() {
        double turnThrottle = 0.0;
        double speedMultiplier = (primaryStick.getZ() + 1) / 2;
        //If the trigger is pressed, turn according to the joystick twist

        if (primaryStick.getRawButton(1)) {
            turnThrottle = 0.6 * primaryStick.getTwist();
        } else {
            turnThrottle = 0.0;
        }
        /*
        if (primaryStick.getPOV() == -1) {
        if(primaryStick.getRawButton(1)) {
        turnThrottle = 0.6*primaryStick.getTwist();
        } 
        else {
        turnThrottle = 0.0;
        }
        }else {
            turnThrottle = limit(angleDifference(navX.getAngle(), primaryStick.getPOV()) * 0.02, 0.4);
        }
        */
        mecanumDrive.driveCartesian(primaryStick.getX() * speedMultiplier, -primaryStick.getY() * speedMultiplier, turnThrottle, 0);
    }

    /*
    public void angleAdjustment() {
    	if(primaryStick.getRawButton(11)) {
    	navX.setAngleAdjustment(navX.getAngleAdjustment()+1.125);
    	}
    	if(primaryStick.getRawButton(12)) {
        navX.setAngleAdjustment(navX.getAngleAdjustment()-1.125);
        }
    }
    */
    //--------------------------------------------Utility Methods---------------------------------------//
    public static double limit(double value, double limit) {
        if (value > limit) {
            return limit;
        } else if (value < -limit) {
            return -limit;
        } else {
            return value;
        }
    }

    public static double angleDifference(double startingAngle, double desiredAngle) {
        double difference;
        difference = startingAngle - desiredAngle;
        if (difference > -180 && difference <= 180)
            return -difference;
        else if (difference <= -180)
            return -(difference + 360);
        else if (difference > 180)
            return -(difference - 360);
        else
            return 0;
    }
}
