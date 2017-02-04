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
	
	Solenoid fRS, fLS, bRS, bLS, intakeLeft;

	boolean driveChange = true;
	
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

		//Drive Base
		frontLeft = new CANTalon(2);
		frontRight = new CANTalon(1);
		backLeft = new CANTalon(3);
		backRight = new CANTalon(0);

		//Front Arm
		intakeArm = new CANTalon(7);


		//Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);


		//Butterfly Solenoids
		fRS = new Solenoid(1);
		fLS = new Solenoid(2);
		bRS = new Solenoid(3);
		bLS = new Solenoid(4);

		//Arm toggle
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
		
		//moves intake arm
		if(operator.getRawButton(7)){
			intakeLeft.set(true);
		}
		else{
			if(driver.getRawButton(8)){
				intakeLeft.set(false);
			}
		}
		
	}
	
	void driveBase(){
		//right side is reversed
		double speed = driver.getY(); //magnitude of left joystick in Y direction
		double turn = -driver.getZ(); //magnitude of right joystick in Z direction
		
		//basic arcade drive
		frontLeft.set(speed + turn);
		backLeft.set(speed + turn);
		frontRight.set(-speed + turn);
		backRight.set(-speed + turn);


		//butterfly switch from omni to traction and vice versa
		if(driver.getRawButton(9)){
			fRS.set(true);
			fLS.set(true);
			bRS.set(true);
			bLS.set(true);
		}
		else{
			if(driver.getRawButton(10){
				fRS.set(false);
				fLS.set(false);
				bRS.set(false);
				bLS.set(false);
			}
		}
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

