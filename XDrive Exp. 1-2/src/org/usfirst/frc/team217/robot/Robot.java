package org.usfirst.frc.team217.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//This is MULTI DRIVE (use L1 and R1 to switch b/t TANKADE and ARCADE)

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	CANTalon frontLeft;
	CANTalon backLeft;
	CANTalon frontRight;
	CANTalon backRight;
	
	Joystick gamepad;
	
	double motorRatio;
	double FLSpeed, FRSpeed, BLSpeed, BRSpeed;
	double allSticks, allSticksRight;
	double FBLStrafe, FBRStrafe;
	
	boolean driveMode;

	public void xArcade() {
		/**
		 * The theory behind my XDrive method is as follows: Assuming that all
		 * motors are speed 1 when going forwards, joystick x = 0 and y = 1. All
		 * motor speeds are therefore y + x or y - x. The bottom left and top
		 * right motors are the same as are the bottom right and top left.
		 * However, each ones that are the "same" are actually opposite. That's
		 * later. If the joystick is right, x = 1 and y = 0. FL and BR have to
		 * be +1, and FR and BL have to be -1. This means FL and BR are y + x
		 * while FR and BL are y - x. Also, we are assuming that BR is opposite
		 * of FL due to it's being rotated by 180 degrees. Same between BL and
		 * FR respectively. Testing is to be done.
		 */
		
		motorRatio = 0.75;
		
		FLSpeed = -gamepad.getY() + gamepad.getX() + gamepad.getZ();
		BLSpeed = -gamepad.getY() - gamepad.getX() + gamepad.getZ();
		BRSpeed = gamepad.getY() - gamepad.getX() + gamepad.getZ();
		FRSpeed = gamepad.getY() + gamepad.getX() + gamepad.getZ();
		
		allSticks = Math.abs(gamepad.getX()) + Math.abs(gamepad.getY()) + Math.abs(gamepad.getZ());   //initializes variable for the following if() statement
		
		if(allSticks > (1 / motorRatio))       //makes sure the value sent to the motors is never above 1
			motorRatio = 1 / allSticks;
		
		frontLeft.set(FLSpeed * motorRatio);
		backRight.set(BRSpeed * motorRatio);
		frontRight.set(FRSpeed * motorRatio);
		backLeft.set(BLSpeed * motorRatio);
	}
	
	public void xTankAde() {
		motorRatio = 0.75;            //slows the motors
		
		FBLStrafe = 0;
		FBRStrafe = 0;
		
		if(gamepad.getX() < 0)                     //Makes it so both wheels move to strafe
			FBLStrafe = FBLStrafe + gamepad.getX();
		else if(gamepad.getX() > 0)
			FBRStrafe = FBLStrafe + gamepad.getX();
		
		if(gamepad.getZ() < 0)
			FBLStrafe = FBLStrafe + gamepad.getZ();
		else if(gamepad.getZ() > 0)
			FBRStrafe = FBRStrafe + gamepad.getZ();
		
		FLSpeed = -gamepad.getY() + FBLStrafe + FBRStrafe;      //set the power
		BLSpeed = -gamepad.getY() - FBLStrafe - FBRStrafe;
		BRSpeed = gamepad.getRawAxis(5) - FBRStrafe - FBLStrafe;
		FRSpeed = gamepad.getRawAxis(5) + FBRStrafe + FBLStrafe;
		
		allSticks = Math.abs(gamepad.getY()) + Math.abs(gamepad.getX()) + Math.abs(gamepad.getZ());  //initializes the variable for the following if() statement
		allSticksRight = Math.abs(gamepad.getX()) + Math.abs(gamepad.getRawAxis(5)) + Math.abs(gamepad.getZ());
		
		if(allSticks > (1 / motorRatio) && allSticks >= allSticksRight)    //makes sure the value sent to the motor is never above 1
			motorRatio = 1 / allSticks;
		else if(allSticksRight > (1 / motorRatio) && allSticksRight > allSticks)
			motorRatio = 1 / allSticksRight;
		
		frontLeft.set(FLSpeed * motorRatio);
		backRight.set(BRSpeed * motorRatio);
		frontRight.set(FRSpeed * motorRatio);
		backLeft.set(BLSpeed * motorRatio);
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		frontLeft = new CANTalon(3);
		frontRight = new CANTalon(9);
		backLeft = new CANTalon(2);
		backRight = new CANTalon(0);
		
		gamepad = new Joystick(0);
		
		driveMode = false;
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if(gamepad.getRawButton(5))
			driveMode = false;          //false makes it run Arcade
		if(gamepad.getRawButton(6))
			driveMode = true;           //true makes it run TankAde
		
		if(driveMode == false)
			xArcade();
		else if(driveMode == true)
			xTankAde();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
