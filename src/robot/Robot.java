package robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    //Variable declarations
    Spark leftFront, leftBack, rightFront, rightBack, climberMotor;
    SendableChooser<Boolean> robotCentric = new SendableChooser<>();
    Servo gearSlideMotor;
    Joystick primaryStick;
    MecanumDrive mecanumDrive;
    AHRS navX;

    public void robotInit() {
        //Motors and Servos
        leftFront = new Spark(2);
        leftBack = new Spark(1);
        rightFront = new Spark(0);
        rightBack = new Spark(3);
        climberMotor = new Spark(4);
        gearSlideMotor = new Servo(5);

        navX = new AHRS(SPI.Port.kMXP);
        primaryStick = new Joystick(0);
        //MecanumDrive
        mecanumDrive = new MecanumDrive(leftFront, leftBack, rightFront, rightBack);
        mecanumDrive.setDeadband(0.18); //Sets deadzone for Mecanum Drive

        robotCentric.addObject("Robot-Centric", true);
        robotCentric.addDefault("Robot-Centric", false);

    }

    public void autonomousInit() {
    }

    public void autonomousPeriodic() {
    }

    public void teleopPeriodic() {
        SmartDashboard.putNumber("NavX Angle: ", navX.getAngle());
        SmartDashboard.putData(robotCentric);
        drive();
        climber();
        gearSlide();
        angleAdjustment();
        liveWindow(false);
    }

    public void testPeriodic() {
        liveWindow(true);
    }

    public void liveWindow(boolean motors) {
        LiveWindow.add(mecanumDrive);
        LiveWindow.add(navX);
        if (motors) {
            LiveWindow.add(gearSlideMotor);
            LiveWindow.add(climberMotor);
            LiveWindow.add(leftFront);
            LiveWindow.add(leftBack);
            LiveWindow.add(rightFront);
            LiveWindow.add(rightBack);
        }


    }

    //--------------------------------------------Robot Methods------------------------------------------//
    public void drive() {
        double turnThrottle, gyroAngle;
        double speedMultiplier = ((primaryStick.getThrottle() + 1) / 2);
        //If the trigger is pressed, turn according to the joystick twist

        if (primaryStick.getRawButton(1)) {
            turnThrottle = 0.6 * primaryStick.getTwist();
        } else {
            turnThrottle = 0.0;
        }

        if (primaryStick.getPOV() == -1) {
            if (primaryStick.getRawButton(1)) {
                turnThrottle = 0.6 * primaryStick.getTwist();
            } else {
                turnThrottle = 0.0;
            }
        } else {
            turnThrottle = limit(angleDifference(navX.getAngle(), primaryStick.getPOV()) * 0.02, 0.4);
        }

        if (!robotCentric.getSelected()) gyroAngle = (-1) * (navX.getAngle());
        else gyroAngle = 0.0;

        mecanumDrive.driveCartesian(primaryStick.getX() * speedMultiplier, -primaryStick.getY() * speedMultiplier, turnThrottle, gyroAngle);
    }

    public void gearSlide() {
        //Left buttons on the joystick
        //If both buttons are pressed, go to middle value.
        //If upper button is pressed, go to the upper position... ect....
        if (primaryStick.getRawButton(5) && primaryStick.getRawButton(3)) {
            gearSlideMotor.set(0.48);
        } else if (primaryStick.getRawButton(5)) {
            gearSlideMotor.set(0.73);
        } else if (primaryStick.getRawButton(3)) {
            gearSlideMotor.set(0.35);
        }
    }

    public void climber() {
        //Right Buttons on the joystick
        if (primaryStick.getRawButton(6)) {
            climberMotor.set(1);
        } else if (primaryStick.getRawButton(4)) {
            climberMotor.set(-0.3);
        } else {
            climberMotor.set(0);
        }
    }


    public void angleAdjustment() {
        if (primaryStick.getRawButton(11)) {
            navX.setAngleAdjustment(navX.getAngleAdjustment() + 1.125);
        }
        if (primaryStick.getRawButton(12)) {
            navX.setAngleAdjustment(navX.getAngleAdjustment() - 1.125);
        }
        if (primaryStick.getRawButton(10)) {
            navX.reset();
        }
    }

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