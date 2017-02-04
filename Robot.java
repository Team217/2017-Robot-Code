package org.usfirst.frc.team217.robot;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.ctre.CANTalon;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Comp bot code for FIRST STEAMworks
 * 
 * @author Giulio Vario
 *
 */
public class Robot extends IterativeRobot {
	
	CANTalon frontLeft,frontRight,backLeft,backRight,intakeArm;
	
	Joystick driver, operator;
	
	Solenoid FRS, FLS, BRS, BLS, intakeLeft;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Live Stream OpenCV Testing
		/*
				new Thread(() -> {
		            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		            camera.setResolution(640, 480);
		            
		            CvSink cvSink = CameraServer.getInstance().getVideo();
		            CvSource outputStream = CameraServer.getInstance().putVideo("cam0", 640, 480);
		            
		            Mat source = new Mat();
		            Mat output = new Mat();
		            
		            while(true) {
		                cvSink.grabFrame(source);
		                Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2RGB);
		                outputStream.putFrame(output);
		            }
		        }).start();
		        */
		frontLeft = new CANTalon(2);
		frontRight = new CANTalon(1);
		backLeft = new CANTalon(3);
		backRight = new CANTalon(0);
		
		intakeArm = new CANTalon(7);
		
		driver = new Joystick(0);
		operator = new Joystick(1);
		
		FRS = new Solenoid(1);
		FLS = new Solenoid(2);
		BRS = new Solenoid(3);
		BLS = new Solenoid(4);
		intakeLeft = new Solenoid(5);
	}

	
	@Override
	public void autonomousInit() {
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		driveBase();
		
		intakeArm.set(operator.getY());
		
		if(driver.getRawButton(9)){
			FRS.set(true);
			FLS.set(true);
			BRS.set(true);
			BLS.set(true);
		}
		else{
			FRS.set(false);
			FLS.set(false);
			BRS.set(false);
			BLS.set(false);
		}
		
		if(operator.getRawButton(7)){
			intakeLeft.set(true);
		}
		else{
			intakeLeft.set(false);
		}
		
	}
	
	void driveBase(){
		//right side is reversed
		double speed = driver.getY();
		double turn = -driver.getZ();
		
		frontLeft.set(speed + turn);
		backLeft.set(speed + turn);
		frontRight.set(-speed + turn);
		backRight.set(-speed + turn);
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

