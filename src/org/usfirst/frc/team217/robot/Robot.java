package org.usfirst.frc.team217.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
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
	CANTalon flyWheel,lifter,kicker,wheelOfDoom;
	Joystick driver;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		flyWheel = new CANTalon(2);
		lifter = new CANTalon(1);
		kicker = new CANTalon(3);
		wheelOfDoom = new CANTalon(6);
		
		flyWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		flyWheel.reverseSensor(true);
		
		flyWheel.configNominalOutputVoltage(+0.0f,  -0.0f);
		flyWheel.configPeakOutputVoltage(+12.0f, -12.0f);
		
		flyWheel.setProfile(0);
		flyWheel.setF(.0651537);
		flyWheel.setD(0);
		flyWheel.setI(0);
		flyWheel.setP(0.25);
		
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
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
	}

	@Override
	public void teleopInit(){
		flyWheel.setEncPosition(0);
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		double flyWheelRPM = -4090;
		flyWheel.setF(fGain(flyWheelRPM));
		//double speed = deadBand(driver.getY());
		if(driver.getRawButton(1)){
		kicker.set(-1);
		lifter.set(1);
		flyWheel.changeControlMode(TalonControlMode.Speed);
		flyWheel.set(flyWheelRPM);
		}
		else{
			kicker.set(0);
			lifter.set(0);
			flyWheel.changeControlMode(TalonControlMode.PercentVbus);
			flyWheel.set(0);
		}
		
		if(driver.getRawButton(2)){
			wheelOfDoom.set(.5);
		}
		else{
			wheelOfDoom.set(0);
		}
		
		System.out.println(flyWheel.getSpeed());
		/*
		double targetSpeed = speed * 2200;
		flyWheel.changeControlMode(TalonControlMode.Speed);
		flyWheel.set(targetSpeed);
		*/
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	double deadBand(double joyStick){
		if(joyStick > -0.08 && joyStick < 0.08){
			joyStick = 0;
		}
		return joyStick;
	}
	
	double fGain(double rpm){
		double FGain;
		FGain = 1023/(4096*rpm/600);
		return FGain;
		
	}
}

