package org.usfirst.frc.team217.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	// BEGIN CONSTANTS SECTION
	
	double motorRatio = 0.6;            // sets the maximum speed for drive motors
	
	final double motorLagSpeed = 1;     // go-to multiplier when making robot turn by slowing down one side
	
	// END CONSTANTS SECTION
	
	int i = 1;                          // variable to determine whether to continue to next auton case
	
	double FBLSpeed, FBRSpeed;          // variables to hold the speed of the left and right motors
	double allSticks;                   // variable to make sure motor speed is never set to greater than 1
	
	CANTalon frontLeft;
	CANTalon backLeft;
	CANTalon frontRight;
	CANTalon backRight;
	
	DoubleSolenoid FLDrop, FRDrop, BLDrop, BRDrop;
	
	Joystick driver, operator;
	
	// Begin PID Section
	
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
	
	// End PID Section
	
	// Begin Special Motor Code Functions and Variables
	
	double key = 0;                     // variable to control the next key state
	
	boolean anyButtonPressed() {
		
		int j;
		
		for(j = 0; j <= 12; j++) {
			if(driver.getRawButton(j))
				return true;
		}
		
		for(j = 0; j <= 360; j++) {
			if(driver.getPOV() == j)
				return true;
		}
		
		return false;
	}
	
	// End Special Motor Code Functions and Variables

	@Override
	public void robotInit() {
		
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		frontLeft = new CANTalon(0);
		frontRight = new CANTalon(3);
		backLeft = new CANTalon(1);
		backRight = new CANTalon(9);
		
		FLDrop = new DoubleSolenoid(0, 1);
		FRDrop = new DoubleSolenoid(6, 7);
		BLDrop = new DoubleSolenoid(2, 3);
		BRDrop = new DoubleSolenoid(4, 5);
		
		driver = new Joystick(0);
		operator = new Joystick(1);
		
		key = 0;
		
	}

	@Override
	public void autonomousInit() {
		
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		
		backLeft.setEncPosition(0);
		backRight.setEncPosition(0);
		
		FLDrop.set(DoubleSolenoid.Value.kForward);  // Forward drops tractions, reverse drops omnis
		FRDrop.set(DoubleSolenoid.Value.kForward);
		BLDrop.set(DoubleSolenoid.Value.kReverse);
		BRDrop.set(DoubleSolenoid.Value.kReverse);
		
		i = 1;
		
	}

	@Override
	public void autonomousPeriodic() {
		
		switch (autoSelected) {
		
		case customAuto:
			
			break;
			
		case defaultAuto:
			
			switch(i) {
			
			case 1:
				
				// This case moves the robot to the hopper trigger and turns
				
				backLeft.set(-normPID(6400, backLeft.getEncPosition(), 0.000350, 0));
				backRight.set(-normPID(-6000, backRight.getEncPosition(), 0.000500, 0));
				frontLeft.set(-normPID(6400, backLeft.getEncPosition(), 0.000350, 0));
				frontRight.set(-normPID(-6000, backRight.getEncPosition(),0.000500, 0));
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() >= 6150 && backLeft.getEncPosition() <= 6600 && backRight.getEncPosition() <= -5850 && backRight.getEncPosition() >= -6400) {
					
					backLeft.set(0);
					backRight.set(0);
					frontRight.set(0);
					frontLeft.set(0);
					
					backLeft.setEncPosition(0);
					backRight.setEncPosition(0);
					
					i++;
					
				}
				
				break;
			
			case 2:
				
				// This case makes the robot turn so the back hits the hopper
				
				backLeft.set(-normPID(500, backLeft.getEncPosition(), 0.001200, 0));
				backRight.set(0);
				frontLeft.set(-normPID(500, backLeft.getEncPosition(), 0.001200, 0));
				frontRight.set(0);
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() <= 600 && backLeft.getEncPosition() >= 400) {
					
					backLeft.set(0.25);          // keeps motors going to reach the hopper in time for the balls
					backRight.set(0.25);
					frontRight.set(0.25);
					frontLeft.set(0.25);
					
					backLeft.setEncPosition(0);
					backRight.setEncPosition(0);
					
					i++;
					
				}
				
				break;
				
			case 3:
				
				// This case moves the robot to the hopper itself to gather the balls
				
				backLeft.set(-normPID(1150, backLeft.getEncPosition(), 0.000650, 0));
				backRight.set(-normPID(-1450, backRight.getEncPosition(), 0.000660, 0));
				frontLeft.set(-normPID(1150, backLeft.getEncPosition(), 0.000650, 0));
				frontRight.set(-normPID(-1450, backRight.getEncPosition(),0.000660, 0));
				
				System.out.println("Back Left Encoder: " + backLeft.getEncPosition());
				System.out.println("Back Right Encoder: " + backRight.getEncPosition());
				
				if(backLeft.getEncPosition() >= 1150 && backLeft.getEncPosition() <= 1350 && backRight.getEncPosition() <= -1450 && backRight.getEncPosition() >= -1650) {
					
					backLeft.set(0);
					backRight.set(0);
					frontRight.set(0);
					frontLeft.set(0);
					
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
				
				break;
				
			}
			
			break;
			
		default:
			
			backLeft.set(0);
			backRight.set(0);
			frontLeft.set(0);
			frontRight.set(0);
			
			break;
		}
	}
	
	@Override
	public void teleopPeriodic() {
		
		// Special useless code for motorRatio
		
		if(driver.getPOV() == 0 && key == 0)
			key++;
		
		if(key == 1 && driver.getPOV() == 0)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 2 && driver.getPOV() == 180)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 3 && driver.getPOV() == 180)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 4 && driver.getPOV() == 270)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 5 && driver.getPOV() == 90)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 6 && driver.getPOV() == 270)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 7 && driver.getPOV() == 90)
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 8 && driver.getRawButton(3))
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(key == 9 && driver.getRawButton(1))
			key++;
		else if(anyButtonPressed())
			key = 0;
		
		if(driver.getRawButton(2)) {        //button 2 switches back to 60% speed
			key = 0;
		}
		
		if(key == 10)
			motorRatio = 1;
		else
			motorRatio = 0.6;
		
		// End special useless code for motorRatio
		
		FBLSpeed = driver.getY() - driver.getZ();
		FBRSpeed = -driver.getY() - driver.getZ();
		
		allSticks = Math.abs(driver.getY()) + Math.abs(driver.getZ());  // initializes a variable for the following if() statement
		
		if(allSticks > 1 / motorRatio) {                                // makes sure the value sent to the motors are never above 1
			motorRatio = 1 / allSticks;
		}
		
		System.out.println("Max Motor Speed: " + (motorRatio * 100) + "%");
		
		frontLeft.set(FBLSpeed * motorRatio);                           // slows the bot to be usable and stable
		backLeft.set(FBLSpeed * motorRatio);
		frontRight.set(FBRSpeed * motorRatio);
		backRight.set(FBRSpeed * motorRatio);
		
		if(driver.getRawButton(4)) {                   // Top Button activates semiomni mode
			
			FLDrop.set(DoubleSolenoid.Value.kForward);
			FRDrop.set(DoubleSolenoid.Value.kForward);
			BLDrop.set(DoubleSolenoid.Value.kReverse);
			BRDrop.set(DoubleSolenoid.Value.kReverse);
			
		}
		
		if(driver.getRawButton(5)) {                   // Left Trigger activates traction mode
			
			FLDrop.set(DoubleSolenoid.Value.kForward);
			FRDrop.set(DoubleSolenoid.Value.kForward);
			BLDrop.set(DoubleSolenoid.Value.kForward);
			BRDrop.set(DoubleSolenoid.Value.kForward);
			
		}
		
		if(driver.getRawButton(6)) {                   // Right Triggers activates omni mode
			
			FLDrop.set(DoubleSolenoid.Value.kReverse);
			FRDrop.set(DoubleSolenoid.Value.kReverse);
			BLDrop.set(DoubleSolenoid.Value.kReverse);
			BRDrop.set(DoubleSolenoid.Value.kReverse);
			
		}
		
	}

	
	@Override
	public void testPeriodic() {
		
		// Test for all motors, setting them to go forwards at motorRatio speed
		
		frontLeft.set(-motorRatio);
		frontRight.set(motorRatio);
		backLeft.set(-motorRatio);
		backRight.set(motorRatio);
		
	}
	
}

