package org.usfirst.frc.team217.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.*;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import java.lang.Math;

public class Robot extends IterativeRobot {
	
	final String boilerHopperB = "Boiler Hopper Auton - Blue";
	final String boilerGearB = "Boiler Gear Auton - Blue";
	final String centralGearB = "Central Gear Auton - Blue";
	final String gearHopperB = "Loader Gear and Hopper - Blue";
	final String boilerHopperR = "Boiler Hopper Auton - Red";
	final String boilerGearR = "Boiler Gear Auton - Red";
	final String centralGearR = "Central Gear Auton - Red";
	final String gearHopperR = "Loader Gear and Hopper - Red";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	double motorRatio = 0.6;            // sets the maximum speed for drive motors
	
	// Start auton enumerations
	
	enum boilerHopperAutonB {      // Gets balls from the hopper closest to boiler and shoots
		toHopper,
		hitHopper,
		getHopper,
		turntoBoiler,
		shoot
	}
	boilerHopperAutonB boilerHopperStateB;
	
	enum boilerGearAutonB {
		toGear,
		dropGear,
		turntoBoiler,
		shoot
	}
	boilerGearAutonB boilerGearStateB;
	
	enum centralGearAutonB {
		toGear,
		dropGear,
		turntoBoiler,
		shoot
	}
	centralGearAutonB centralGearStateB;
	
	enum gearHopperAutonB {
		toGear,
		dropGear,
		turntoHopper,
		toHopper,
		hitHopper,
		getHopper
	}
	gearHopperAutonB gearHopperStateB;
	
	enum boilerHopperAutonR {      // Gets balls from the hopper closest to boiler and shoots
		toHopper,
		hitHopper,
		getHopper,
		turntoBoiler,
		shoot
	}
	boilerHopperAutonB boilerHopperStateR;
	
	enum boilerGearAutonR {
		toGear,
		dropGear,
		turntoBoiler,
		shoot
	}
	boilerGearAutonB boilerGearStateR;
	
	enum centralGearAutonR {
		toGear,
		dropGear,
		turntoBoiler,
		shoot
	}
	centralGearAutonB centralGearStateR;
	
	enum gearHopperAutonR {
		toGear,
		dropGear,
		turntoHopper,
		toHopper,
		hitHopper,
		getHopper
	}
	gearHopperAutonB gearHopperStateR;
	
	// End auton enumerations
	
	double FBLSpeed, FBRSpeed;          // variables to hold the speed of the left and right motors
	double allSticks;                   // variable to make sure motor speed is never set to greater than 1
	double joystick;                    // double for the deadband
	
	// Drive Motors
	CANTalon rightMaster, rightSlave, leftMaster, leftSlave;
	
	// Solenoids
	Solenoid backSolenoid, frontSolenoid, armSolenoid, gearSolenoid;
	
	// Gamepads
	Joystick driver, oper;
	
	// Gyro
	ADXRS450_Gyro horzGyro;
	
	// Compressors
	Compressor compressor;
	
	// Begin PID Section
	
	double target, position, PIDSpeed, error, cumulativeError, PIDPout, PIDIout;
	
	double normPID(double target, double position, double kP, double kI) {
		
		error = target - position;
		cumulativeError = error;
		
		PIDPout = error * kP;
		PIDIout = error * kI;
		PIDSpeed = (PIDPout - PIDIout);
		
		if (Math.abs(error) < 5) {
			PIDSpeed = 0;
		}
		
		return PIDSpeed;
	}
	
	// End PID Section
	
	
	double key = 0;
	
	boolean anyButtonPressed() {
		
		// Checks if any buttons on the Joysticks are currenty pressed
		
		for(int j = 1; j <= 14; j++) {
			if(driver.getRawButton(j))
				return true;
		}
		
		for(int j = 0; j <= 360; j++) {
			if(driver.getPOV() == j)
				return true;
		}
		
		return false;
	}
	
	void __() {if(driver.getPOV() == 0 && key == 0) key++;
		if(key == 1 && driver.getPOV() == 0) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 2 && driver.getPOV() == 180) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 3 && driver.getPOV() == 180) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 4 && driver.getPOV() == 270) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 5 && driver.getPOV() == 90) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 6 && driver.getPOV() == 270) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 7 && driver.getPOV() == 90) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 8 && driver.getRawButton(3)) key++;
		else if(anyButtonPressed()) key = 0;
		if(key == 9 && driver.getRawButton(1)) key++;
		else if(anyButtonPressed()) key = 0;
		if(driver.getRawButton(2)) key = 0;
		if(key == 10) motorRatio = 1;
		else motorRatio = 0.6;}
	
	
	@Override
	public void robotInit() {
		
		chooser.addDefault("Boiler Hopper Auton - Blue", boilerHopperB);
		chooser.addObject("Boiler Gear Auton - Blue", boilerGearB);
		chooser.addObject("Central Gear Auton - Blue", centralGearB);
		chooser.addObject("Loader Gear and Hopper - Blue", gearHopperB);
		chooser.addObject("Boiler Hopper Auton - Red", boilerHopperR);
		chooser.addObject("Boiler Gear Auton - Red", boilerGearR);
		chooser.addObject("Central Gear Auton - Red", centralGearR);
		chooser.addObject("Loader Gear and Hopper - Red", gearHopperR);
		SmartDashboard.putData("Auto choices", chooser);
		
		// Drive Motors (masters in front, slaves in back)
		rightMaster = new CANTalon(0);
		rightMaster.setFeedbackDevice(FeedbackDevice.QuadEncoder);  // sets the feedback/encoder device to be a Quad Encoder
		
		rightSlave = new CANTalon(15);
		rightSlave.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightSlave.changeControlMode(TalonControlMode.Follower);    // sets the motor to be a slave of a CANTalon
		rightSlave.set(0);                                          // sets the motor to be a slave of CANTalon 0
		
		leftMaster = new CANTalon(1);
		leftMaster.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		leftSlave = new CANTalon(14);
		leftSlave.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftSlave.changeControlMode(TalonControlMode.Follower);
		leftSlave.set(1);
		
		// Solenoids
		frontSolenoid = new Solenoid(2);
		backSolenoid = new Solenoid(1);
		armSolenoid = new Solenoid(3);
		gearSolenoid = new Solenoid(0);
		
		// Gamepads
		driver = new Joystick(0);
		oper = new Joystick(1);
		
		// Gyro
	    horzGyro = new ADXRS450_Gyro();
		
		compressor = new Compressor();
		
		key = 0;
		
	}
	
	@Override
	public void autonomousInit() {
		
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		
		rightMaster.setEncPosition(0);
		rightSlave.setEncPosition(0);
		leftMaster.setEncPosition(0);
		leftSlave.setEncPosition(0);
		
		boilerHopperStateB = boilerHopperAutonB.toHopper;
		boilerGearStateB = boilerGearAutonB.toGear;
		centralGearStateB = centralGearAutonB.toGear;
		gearHopperStateB = gearHopperAutonB.toGear;
		
		backSolenoid.set(true);
		frontSolenoid.set(false);
		
	}
	
	@Override
	public void autonomousPeriodic() {
		
		switch (autoSelected) {
			
		case boilerHopperB:
			
			switch(boilerHopperStateB) {
			
			case toHopper:
				
				toHopperAutonB();
				break;
			
			case hitHopper:
				
				hitHopperAutonB();
				break;
				
			case getHopper:
				
				getHopperAutonB();
				break;
				
			case turntoBoiler:
				
				turntoBoilerAutonB();
				break;
				
			case shoot:
				
				shootAutonB();
				break;
				
			default:
					
				leftSlave.set(0);
				rightSlave.set(0);
				leftMaster.set(0);
				rightMaster.set(0);
				
				rightMaster.setEncPosition(0);
				rightSlave.setEncPosition(0);
				leftMaster.setEncPosition(0);
				leftSlave.setEncPosition(0);
				
				break;
				
			}
			
			break;
			
		case boilerGearB:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
			
			break;
			
		case centralGearB:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
			
			break;
			
		case gearHopperB:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
			
			break;
			
		case boilerHopperR:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
			
			break;
			
		case boilerGearR:
	
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
	
			break;
			
		case centralGearR:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
	
			break;
			
		case gearHopperR:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
	
			break;
		
		default:
			
			leftSlave.set(0);
			rightSlave.set(0);
			leftMaster.set(0);
			rightMaster.set(0);
			
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
			
			break;
			
		}
	}
	
	@Override
	public void teleopPeriodic() {
		
		__();
		
		FBLSpeed = deadBand(driver.getY() - driver.getZ());
		FBRSpeed = deadBand(-driver.getY() - driver.getZ());
		
		allSticks = Math.abs(driver.getY()) + Math.abs(driver.getZ());  // initializes a variable for the following if() statement
		
		if(allSticks > 1 / motorRatio)                                // makes sure the value sent to the motors are never above 1
			motorRatio = 1 / allSticks;
		
		System.out.println("Max Motor Speed: " + (motorRatio * 100) + "%");
		
		leftMaster.set(FBLSpeed * motorRatio);                           // slows the bot to be usable and stable
		leftSlave.set(FBLSpeed * motorRatio);
		rightMaster.set(FBRSpeed * motorRatio);
		rightSlave.set(FBRSpeed * motorRatio);
		
		if(driver.getRawButton(5)){   // Left trigger activates front to omni
			frontSolenoid.set(true);  // true is omni, false is traction
		}
		
		if(driver.getRawButton(7)){   // Left bumper activates back to omni
			backSolenoid.set(true);
		}
		
		if(driver.getRawButton(6)){  // Right trigger activates front to traction
			frontSolenoid.set(false);
		}
		
		if(driver.getRawButton(8)){  // Right bumper activates back to traction
			backSolenoid.set(false);
		}
		
	}
	
	double deadBand(double joyStick){
		if(joyStick > -0.08 && joyStick < 0.08){
			joyStick = 0;
		}
		return joyStick;
	}
	
	
	@Override
	public void testPeriodic() {
		
		// Test for all motors, setting them to go forwards at motorRatio speed
		
		leftMaster.set(-motorRatio);
		rightMaster.set(motorRatio);
		leftSlave.set(-motorRatio);
		rightSlave.set(motorRatio);
		
	}
	
	// START Auton Case Functions
	
	void toHopperAutonB() {

		// This case moves the robot to the hopper trigger and turns
		
		leftSlave.set(-normPID(6400, leftSlave.getEncPosition(), 0.000350, 0));
		rightSlave.set(-normPID(-6000, rightSlave.getEncPosition(), 0.000500, 0));
		leftMaster.set(-normPID(6400, leftMaster.getEncPosition(), 0.000350, 0));
		rightMaster.set(-normPID(-6000, rightMaster.getEncPosition(),0.000500, 0));
		
		System.out.println("Back Left Encoder: " + leftSlave.getEncPosition());
		System.out.println("Back Right Encoder: " + rightSlave.getEncPosition());
		
		if(nextState(6150, 6600, 6150, 6600, -6400, -5850, -6400, -5850)) {     // LM Min, LM Max, LS Min, LS Max, RM Min, RM Max, RS Min, RS Max
			
			leftSlave.set(0);
			rightSlave.set(0);
			rightMaster.set(0);
			leftMaster.set(0);
			
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
			
			boilerHopperStateB = boilerHopperAutonB.hitHopper;
			
		}
		
	}
	
	void hitHopperAutonB() {

		// This case makes the robot turn so the back hits the hopper
		
		leftSlave.set(-normPID(500, leftSlave.getEncPosition(), 0.001200, 0));
		rightSlave.set(0);
		leftMaster.set(-normPID(500, leftMaster.getEncPosition(), 0.001200, 0));
		rightMaster.set(0);
		
		System.out.println("Back Left Encoder: " + leftSlave.getEncPosition());
		System.out.println("Back Right Encoder: " + rightSlave.getEncPosition());
		
		if(nextState(400, 600, 400, 600, 0, 0, 0, 0)) {
			
			leftSlave.set(0.25);          // Keeps motors going to reach the hopper in time for the balls
			rightSlave.set(0.25);
			rightMaster.set(0.25);
			leftMaster.set(0.25);
			
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
			
			boilerHopperStateB = boilerHopperAutonB.getHopper;
			
		}
		
	}
	
	void getHopperAutonB() {

		// This case moves the robot to the hopper itself to gather the balls
		
		leftSlave.set(-normPID(1150, leftSlave.getEncPosition(), 0.000650, 0));
		rightSlave.set(-normPID(-1450, rightSlave.getEncPosition(), 0.000660, 0));
		leftMaster.set(-normPID(1150, leftMaster.getEncPosition(), 0.000650, 0));
		rightMaster.set(-normPID(-1450, rightMaster.getEncPosition(),0.000660, 0));
		
		System.out.println("Back Left Encoder: " + leftSlave.getEncPosition());
		System.out.println("Back Right Encoder: " + rightSlave.getEncPosition());
		
		if(nextState(1150, 1350, 1150, 1350, -1650, -1450, -1650, -1450)) {
			
			leftSlave.set(0);
			rightSlave.set(0);
			rightMaster.set(0);
			leftMaster.set(0);
			
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
			
			boilerHopperStateB = boilerHopperAutonB.turntoBoiler;
			
		}
		
	}
	
	void turntoBoilerAutonB() {
		
		boilerHopperStateB = boilerHopperAutonB.shoot;
		
	}
	
	void shootAutonB() {
		
		leftSlave.set(0);
		rightSlave.set(0);
		leftMaster.set(0);
		rightMaster.set(0);
		
	}
	
	// nextState determines whether or not to continue to the next state based off of a range of encoder values
	
	int LSEncMin, LMEncMin, RSEncMin, RMEncMin, LSEncMax, LMEncMax, RSEncMax, RMEncMax;    // variables for the parameters for the min and max enc positions to continue to next state
	
	boolean nextState(int LMEncMin, int LMEncMax, int LSEncMin, int LSEncMax, int RMEncMin, int RMEncMax, int RSEncMin, int RSEncMax) {
		
		if(leftSlave.getEncPosition() >= LMEncMin && leftSlave.getEncPosition() <= LMEncMax && rightSlave.getEncPosition() <= RSEncMax && rightSlave.getEncPosition() >= RSEncMin && rightMaster.getEncPosition() <= RMEncMax && rightMaster.getEncPosition() >= RMEncMin && leftSlave.getEncPosition() >= LSEncMin && leftSlave.getEncPosition() <= LSEncMax)
			return true;
		else
			return false;
		
	}
	
	// END Auton Case Functions
	
}
