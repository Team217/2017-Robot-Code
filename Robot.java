package org.usfirst.frc.team217.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.*;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.*;

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
	CANTalon FR, FL, BR, BL;
	
	ADXRS450_Gyro Hgyro;
	Joystick Driver, Op;
	boolean SafeCheck, ballsOut, gearGone;
	
	enum AutonEnum{
		Fire10,
		placeGear,
		LoadBalls,
		FireMore,
		stop
	};
	
	AutonEnum AutonState = AutonEnum.Fire10;
	
	enum StartPosEnum{
		Spos1,
		Spos2,
		Spos3
	};
	
	StartPosEnum SPos;
	
	enum GearPosEnum{
		Gpos1,
		Gpos2,
		Gpos3
	};
	
	GearPosEnum GPos;
	
	enum SideEnum{
		red,
		blue
	};
	
	SideEnum Side;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		FR = new CANTalon(0);
		FL = new CANTalon(1);
		BR = new CANTalon(2);
		BL = new CANTalon(3);
		
		Driver = new Joystick(0);
		Op = new Joystick(1);
		
		Hgyro = new ADXRS450_Gyro();
		
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
		
		Hgyro.reset();
		
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
			
			switch(AutonState){
				case Fire10:
					
					Fire10();
					
					if(ballsOut){ //Check if we are doing ballsOut auton
										//ballsOut is the name for 40 point auton
						
						AutonState = AutonEnum.LoadBalls;
						
					}
					else{
						
						AutonState = AutonEnum.placeGear;
						
					}
					break;
				case placeGear:
					
					PlaceGear();
					
					if(gearGone){ //Check if the gear has been raised from the robot
						
						AutonState = AutonEnum.stop;
						
					}
					
					break;
				case LoadBalls:
					
					LoadBalls();
					
					break;
				case FireMore:
					
					FireMore();
					
					AutonState = AutonEnum.stop;
					
					break;
					
				case stop:
					
					FR.set(0);
					FL.set(0);
					BR.set(0);
					BL.set(0);
					
					break;
					
			}
			
			
			
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
	}
	
	public void Fire10() {
		//Auton method for firing the first 10
		switch(Side)
		{
			case red:
			{
					switch(SPos)
					{
						case Spos1:
							break;
						case Spos2:
							break;
						case Spos3:
							break;
					}
			}
			break;
			
			case blue:
			{
					switch(SPos)
					{
						case Spos1:
							break;
						case Spos2:
							break;
						case Spos3:
							break;
					}
			}
			break;
		}
	}
	
	public void PlaceGear() {
		//Auton method for driving up and placing gear
		
		switch(GPos)
		{
			case Gpos1:
				break;
			case Gpos2:
				break;
			case Gpos3:
				break;
		}
		
	}
	
	public void LoadBalls() {
		//Auton method to drive to the hopper and load balls
		
		switch(Side)
		{
			case red:
			{
					switch(SPos)
					{
						case Spos1:
							break;
						case Spos2:
							break;
						case Spos3:
							break;
					}
			}
			break;
			
			case blue:
			{
					switch(SPos)
					{
						case Spos1:
							break;
						case Spos2:
							break;
						case Spos3:
							break;
					}
			}
			break;
		}
		
	}
	
	public void FireMore() {
		//Auton method to fire the balls from the hopper
		
		
		
	}
	
}

