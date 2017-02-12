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
	
	double motorRatio = 0.6;        // sets the maximum speed for drive motors
	
	final double motorLagSpeed = 1;     // go-to multiplier when making robot turn by slowing down one side
	
	// END CONSTANTS SECTION
	
	int i = 1;
	
	double FBLSpeed, FBRSpeed;
	double allSticks;
	
	// Booleans for motorRatio code
	
	boolean key1 = false,
			key2 = false,
			key3 = false,
			key4 = false,
			key5 = false,
			key6 = false,
			key7 = false,
			key8 = false,
			key9 = false,
			key10 = false;
	
	// End booleans for motorRatio code
	
	CANTalon frontLeft;
	CANTalon backLeft;
	CANTalon frontRight;
	CANTalon backRight;
	
	DoubleSolenoid FLDrop, FRDrop, BLDrop, BRDrop;
	
	Joystick driver, operator;
	
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
		
		key1 = false;
		key2 = false;
		key3 = false;
		key4 = false;
		key5 = false;
		key6 = false;
		key7 = false;
		key8 = false;
		key9 = false;
		key10 = false;
		
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
		
		FLDrop.set(DoubleSolenoid.Value.kForward);  // Forward drops tractions, reverse drops omnis
		FRDrop.set(DoubleSolenoid.Value.kForward);
		BLDrop.set(DoubleSolenoid.Value.kReverse);
		BRDrop.set(DoubleSolenoid.Value.kReverse);
		
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
					
					backLeft.set(0.5);
					backRight.set(0.5);
					frontRight.set(0.5);
					frontLeft.set(0.5);
					
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

	/**
	 * This function is called periodically during operator control
	 * 
	 */
	
	@Override
	public void teleopPeriodic() {
		
		// Special code for motorRatio
		
		if(driver.getPOV() == 0)
			key1 = true;
		
		if(key1 && driver.getPOV() == 0)
			key2 = true;
		else if(!anyButtonPressed())
			key1 = true;
		else
			key1 = false;
		
		if(key2 && driver.getPOV() == 180)
			key3 = true;
		else if(!anyButtonPressed())
			key2 = true;
		else {
			key1 = false;
			key2 = false;
		}
		
		if(key3 && driver.getPOV() == 180)
			key4 = true;
		else if(!anyButtonPressed())
			key3 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
		}
		
		if(key4 && driver.getPOV() == 270)
			key5 = true;
		else if(!anyButtonPressed())
			key4 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
		}
		
		if(key5 && driver.getPOV() == 90)
			key6 = true;
		else if(!anyButtonPressed())
			key5 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
			key5 = false;
		}
		
		if(key6 && driver.getPOV() == 270)
			key7 = true;
		else if(!anyButtonPressed())
			key6 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
			key5 = false;
			key6 = false;
		}
		
		if(key7 && driver.getPOV() == 90)
			key8 = true;
		else if(!anyButtonPressed())
			key7 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
			key5 = false;
			key6 = false;
			key7 = false;
		}
		
		if(key8 && driver.getRawButton(3))
			key9 = true;
		else if(!anyButtonPressed())
			key8 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
			key5 = false;
			key6 = false;
			key7 = false;
			key8 = false;
		}
		
		if(key9 && driver.getRawButton(1))
			key10 = true;
		else if(!anyButtonPressed())
			key9 = true;
		else {
			key1 = false;
			key2 = false;
			key3 = false;
			key4 = false;
			key5 = false;
			key6 = false;
			key7 = false;
			key8 = false;
			key9 = false;
		}
		
		if(key10) {
			motorRatio = 1;
			System.out.println("Motor Speed: 100%");
		} else {
			motorRatio = 0.6;
			System.out.println("Motor Speed: 60%");
		}
		
		// End special code for motorRatio
		
		FBLSpeed = driver.getY() - driver.getZ();
		FBRSpeed = -driver.getY() - driver.getZ();
		
		allSticks = Math.abs(driver.getY()) + Math.abs(driver.getZ());   //initializes a variable for the following if() statement
		
		if(allSticks > 1 / motorRatio) {    //makes sure the value sent to the motors are never above 1
			motorRatio = 1 / allSticks;
		}
		
		frontLeft.set(FBLSpeed * motorRatio);  //slows the bot to be usable and stable
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

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		
		frontLeft.set(-motorRatio);
		frontRight.set(motorRatio);
		backLeft.set(-motorRatio);
		backRight.set(motorRatio);
		
	}
	
}

