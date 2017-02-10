package org.usfirst.frc.team217.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	// BEGIN CONSTANTS SECTION
	final int BLTalonPort = 1;            // set these variables to the ports of the wheel motors
	final int BRTalonPort = 9;
	final int FRTalonPort = 3;
	final int FLTalonPort = 0;
	
	final double motorRatio = 0.5;        // sets the maximum speed for drive motors
	
	final double motorLagSpeed = 1;     // go-to multiplier when making robot turn by slowing down one side
	// END CONSTANTS SECTION
	
	int i = 1;
	
	CANTalon frontLeft;
	CANTalon backLeft;
	CANTalon frontRight;
	CANTalon backRight;
	
	Joystick driver;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	double target = 0, position = 0, PIDSpeed = 0, error = 0, cumulativeError = 0;
	
	double normPID(double target, double position, double kP, double kI) {
		
		error = target - position;
		cumulativeError = error;
		
		double PIDPout = error * kP;
		double PIDIout = error * kI;
		double PIDSpeed = (PIDPout - PIDIout);
		
		if (Math.abs(error) < 5) {
			PIDSpeed = 0;
		}
		
		return PIDSpeed;
	}

	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		frontLeft = new CANTalon(FLTalonPort);
		frontRight = new CANTalon(FRTalonPort);
		backLeft = new CANTalon(BLTalonPort);
		backRight = new CANTalon(BRTalonPort);
		
		driver = new Joystick(0);
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
		
		backLeft.setEncPosition(0);
		backRight.setEncPosition(0);
		
		i = 1;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
		switch (autoSelected) {
		case customAuto:
			
			break;
		case defaultAuto:
			
			switch(i) {
			
			case 1:
				
				backLeft.set(-normPID(6400, backLeft.getEncPosition(), 0.000350, 0));
				backRight.set(-normPID(-6000, backRight.getEncPosition(), 0.000500, 0));
				frontLeft.set(-normPID(6400, backLeft.getEncPosition(), 0.000350, 0));
				frontRight.set(-normPID(-6000, backRight.getEncPosition(),0.000500, 0));
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() >= 6200 && backLeft.getEncPosition() <= 6600 && backRight.getEncPosition() <= -6000 && backRight.getEncPosition() >= -6400) {
					backLeft.setEncPosition(0);
					backRight.setEncPosition(0);
					
					i++;
				}
				
				break;
			/*	
			case 3:
				
				backLeft.set(-normPID(750, backLeft.getEncPosition(), 0.000650, 0));
				backRight.set(-normPID(-950, backRight.getEncPosition(), 0.000650, 0));
				frontLeft.set(-normPID(750, backLeft.getEncPosition(), 0.000650, 0));
				frontRight.set(-normPID(-950, backRight.getEncPosition(),0.000650, 0));
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() >= 700 && backLeft.getEncPosition() <= 900 && backRight.getEncPosition() >= -1050 && backRight.getEncPosition() <= -950) {
					backLeft.setEncPosition(0);
					backRight.setEncPosition(0);
					
					i++;
				}
				
				break;
				*/
			case 2:
				
				backLeft.set(-normPID(-750, backLeft.getEncPosition(), 0.000650, 0));
				backRight.set(-normPID(2000, backRight.getEncPosition(), 0.000650, 0));
				frontLeft.set(-normPID(-750, backLeft.getEncPosition(), 0.000650, 0));
				frontRight.set(-normPID(2000, backRight.getEncPosition(),0.000650, 0));
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() >= -750 && backLeft.getEncPosition() <= -850 && backRight.getEncPosition() >= 2000 && backRight.getEncPosition() <= 2100) {
					backLeft.setEncPosition(0);
					backRight.setEncPosition(0);
					
					i++;
				}
				
				break;
				
			default:
					
				backLeft.set(0);
				backRight.set(0);
				frontLeft.set(0);
				frontRight.set(0);
			}

			break;
			
		default:
			backLeft.set(0);
			
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		frontLeft.set(0.5);
		frontRight.set(-0.5);
		backLeft.set(0.5);
		backRight.set(-0.5);
	}
}

