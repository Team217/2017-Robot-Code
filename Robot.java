package org.usfirst.frc.team217.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.*;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import java.lang.Math;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String redSide = "Red Auto";
	final String blueSide = "Blue Auto";
	final String ballsOut = "40 Ball Auto";
	final String gearShoot = "Gear and Shoot Auto";
	final String pos1 = "Position 1";
	final String pos2 = "Position 2";
	final String pos3 = "Position 3";

	String sideSelected, autoSelected, positionSelected;
	boolean turretFlip=true;

	//Joysticks
	Joystick oper, driver,test;
	Preferences prefs;

	//Joystick Variables
	final int buttonSquare = 1;
	final int buttonX = 2;
	final int buttonCircle = 3;
	final int buttonTriangle = 4;
	final int leftBumper = 5;
	final int rightBumper = 6;
	final int leftTrigger = 7;
	final int rightTrigger = 8;
	final int buttonShare = 9;
	final int buttonOption = 10;
	final int leftAnalog = 11;
	final int rightAnalog = 12;
	final int buttonPS = 13;
	final int touchpad = 14;


	double wheelRPM, hoodValue, flyP,flyI,flyD,flyF,autoForwardLeft,autoForwardRight,climbSpeed;

	double ballsOutSum[] = {0};

	//Drive Motors
	CANTalon rightMaster, rightSlave, leftMaster, leftSlave;

	//Shooting Motors
	CANTalon flyWheel, flyWheel2, hood, turret, lifter, kicker, intakeArm, wheelOfDoom;

	//Climbing Motors
	CANTalon climber, climber2;

	//Gear Motors
	CANTalon gearArm, gearIntake;

	//Solenoids
	Solenoid backSolenoid, frontSolenoid, armSolenoid, gearSolenoid;

	//Gyro
	ADXRS450_Gyro horzGyro;

	DigitalInput resetSwitch;

	//Compressor
	Compressor compressor;

	Preferences pref;

	AnalogInput hoodEnc;

	NetworkTable table,visionTable;
	Timer autoTime,turretTime,forwardTime;

	double intakeSpeed,robotSpeed,visionKP,autoP,visionKI,wheelOfDoomSpeed,gearAutoTime,autoRPM;
	boolean camNum = false,autonEncReset = true;
	double flyWheelRPM = -4400;

	enum BallsOut{
		reset,
		forward,
		turn,
		tacticalReload,
		visionTrack,
		stopAndShoot
	};
	BallsOut ballsOutAuton;

	enum GearAuto{
		forward,
		turn,
		pivot,
		place,
		drop,
		moveAndShoot,
		dontMove
	}
	GearAuto gearBallAuto;

	enum DriveForward{
		drive,
		stop
	}
	DriveForward driveAuto;
	Timer gearTime;

	double ballsOutForward1Left,ballsOutForward1Right,ballsOutTurn,ballsOutTurret,directionMultiplier;
	double gearsTurn,gearForwardLeft,gearForwardRight,gearTurret;

	

	SendableChooser<String> side = new SendableChooser<>();
	SendableChooser<String> auton = new SendableChooser<>();
	SendableChooser<String> position = new SendableChooser<>();

	@Override
	public void robotInit() {

		gearTime = new Timer();

		side.addDefault("Red Alliance", redSide);
		side.addObject("Blue Alliance", blueSide);
		SmartDashboard.putData("Auton Side Selection", side);

		auton.addDefault("40 Ball", ballsOut);
		auton.addObject("10 Ball Gear Auto", gearShoot);
		SmartDashboard.putData("Auton Selection",auton);

		position.addDefault("Position 1", pos1);
		position.addObject("Position 2", pos2);
		position.addObject("Position 3", pos3);
		SmartDashboard.putData("Position Selection",position);


		pref = Preferences.getInstance();

		hoodEnc = new AnalogInput(1);

		autoTime = new Timer();
		turretTime = new Timer();
		forwardTime = new Timer();

		//Gear Manipulator
		gearArm = new CANTalon(8);
		gearArm.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

		gearIntake = new CANTalon(7);

		//Joysticks
		driver = new Joystick(0);
		oper = new Joystick(1);
		test = new Joystick(2);

		resetSwitch = new DigitalInput(1);

		//Drive Motors
		rightMaster = new CANTalon(15);
		rightMaster.setFeedbackDevice(FeedbackDevice.QuadEncoder);

		rightSlave = new CANTalon(14);
		rightSlave.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightSlave.changeControlMode(TalonControlMode.Follower);
		rightSlave.set(15);

		leftMaster = new CANTalon(0);
		leftMaster.setFeedbackDevice(FeedbackDevice.QuadEncoder);

		leftSlave = new CANTalon(1);
		leftSlave.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		leftSlave.changeControlMode(TalonControlMode.Follower);
		leftSlave.set(0);

		//Shooting Motors
		flyWheel = new CANTalon(10);
		flyWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		flyWheel.reverseSensor(false);
		flyWheel2 = new CANTalon(11);
		flyWheel2.changeControlMode(TalonControlMode.Follower);
		flyWheel2.set(10);

		hood = new CANTalon(9);
		turret= new CANTalon(6);
		turret.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		lifter = new CANTalon(5);
		kicker = new CANTalon(4);
		intakeArm =  new CANTalon(13);
		wheelOfDoom = new CANTalon(12);

		//flyWheel Settings
		flyWheel.configNominalOutputVoltage(+0.0f,  -0.0f);
		flyWheel.configPeakOutputVoltage(+12.0f, -12.0f);

		flyWheel.setProfile(0);
		flyWheel.setP(flyP); //0.1
		flyWheel.setI(flyI); //0.0004895
		flyWheel.setD(flyD); //0.5
		flyWheel.setF(0);

		//Climbing Motors
		climber = new CANTalon(2);
		climber2 = new CANTalon(3);
		climber2.changeControlMode(TalonControlMode.Follower);
		climber2.set(2);


		//Solenoids
		frontSolenoid = new Solenoid(2);
		backSolenoid = new Solenoid(1);
		armSolenoid = new Solenoid(4);
		gearSolenoid = new Solenoid(0);

		//Gyro
	    horzGyro = new ADXRS450_Gyro();

	    table = NetworkTable.getTable("Vision");
	    
		table.putBoolean("Camera_Type", camNum);
		
		visionTable = NetworkTable.getTable("Vision");

		UsbCamera camera1;
	    camera1 = CameraServer.getInstance().startAutomaticCapture();
	    camera1.setResolution(160, 120);
	    camera1.setFPS(30);
	    horzGyro.calibrate();
		smartDash();
		rightMaster.setEncPosition(0);
		rightSlave.setEncPosition(0);
		leftMaster.setEncPosition(0);
		leftSlave.setEncPosition(0);
		turret.setEncPosition(0);
	}

	@Override
	public void autonomousInit() {

		sideSelected = (String) side.getSelected();
		autoSelected = (String) auton.getSelected();
		positionSelected = (String) position.getSelected();

		//System.out.println(sideSelected + " " +autoSelected + " "+ positionSelected);

		switch(sideSelected){
			case redSide:
				switch(autoSelected){
					case ballsOut:
						switch(positionSelected){
							case pos1: //close hopper bin red side
								ballsOutForward1Left = -2391;
								ballsOutForward1Right = 2484;
								ballsOutTurn = 90;
								ballsOutTurret = 8350;
								directionMultiplier = 1;
								smartDash();
								turret.setEncPosition(0);

								frontSolenoid.set(true);//This deploys omni if not already deployed.
								backSolenoid.set(false); // ^
								turretTime.start();

								ballsOutAuton = BallsOut.reset;

								break;
							case pos2: //far hopper bin red side
								ballsOutForward1Left = -4300;
								ballsOutForward1Right = 4500;
								ballsOutTurn = 87;
								ballsOutTurret = -2530;
								directionMultiplier = -1;
								smartDash();
								turret.setEncPosition(0);
								climbSpeed=.65;
								//visionLock = true;
								//intakeFlap.set(false);
								frontSolenoid.set(true);//This deploys omni if not already deployed.
								backSolenoid.set(false); // ^
								turretTime.start();
								armSolenoid.set(true);
								autoRPM = -4250;
								ballsOutAuton = BallsOut.reset;
								break;
							case pos3:
								break;
						}
						break;
					case gearShoot:
						switch(positionSelected){
						case pos1:
							gearsTurn =-56;
							gearForwardLeft =-7470;
							gearForwardRight =7670;
							horzGyro.reset();
							leftMaster.setEncPosition(0);
							rightMaster.setEncPosition(0);
							gearArm.setEncPosition(0);
							gearTime.reset();
							gearTime.start();
							gearAutoTime = 6.5;
							frontSolenoid.set(false);
							backSolenoid.set(false);
							gearBallAuto = GearAuto.forward;
							break;
						case pos2:
							horzGyro.reset();
							leftMaster.setEncPosition(0);
							rightMaster.setEncPosition(0);
							gearArm.setEncPosition(0);
							gearTime.reset();
							gearTime.start();
							frontSolenoid.set(false);
							backSolenoid.set(false);
							turret.setEncPosition(0);
							gearTurret = 8000;
							gearForwardLeft = -3370;
							gearForwardRight =3470;
							gearAutoTime= 4.2;
							gearBallAuto = GearAuto.forward;
							break;
						case pos3:
							break;
					}
						break;
				}
				break;
			case blueSide:
				switch(autoSelected){
					case ballsOut:
						switch(positionSelected){
						case pos1:
							ballsOutForward1Left = 2491;
							ballsOutForward1Right = -2584;
							ballsOutTurn = -90;
							ballsOutTurret = 6350;
							directionMultiplier = -1;
							smartDash();
							turret.setEncPosition(0);

							frontSolenoid.set(true);//This deploys omni if not already deployed.
							backSolenoid.set(false); // ^
							turretTime.start();

							ballsOutAuton = BallsOut.reset;
							break;
						case pos2:
							ballsOutForward1Left = 4250;
							ballsOutForward1Right = -4450;
							ballsOutTurn = -90;
							ballsOutTurret =-2590;
							directionMultiplier = 1;
							smartDash();
							turret.setEncPosition(0);
							autoRPM = -4625;
							frontSolenoid.set(true);//This deploys omni if not already deployed.
							backSolenoid.set(false); // ^
							turretTime.start();
							climbSpeed=0;

							ballsOutAuton = BallsOut.reset;
							break;
						case pos3:
							break;
				}
					break;
				case gearShoot:
					switch(positionSelected){
					case pos1:
						gearsTurn =82;
						gearForwardLeft =-6400;
						gearForwardRight =6600;
						horzGyro.reset();
						leftMaster.setEncPosition(0);
						rightMaster.setEncPosition(0);
						gearArm.setEncPosition(0);
						gearTime.reset();
						gearTime.start();
						frontSolenoid.set(false);
						backSolenoid.set(false);
						gearBallAuto = GearAuto.forward;
						break;
					case pos2:
						horzGyro.reset();
						leftMaster.setEncPosition(0);
						rightMaster.setEncPosition(0);
						gearArm.setEncPosition(0);
						gearTime.reset();
						gearTime.start();
						frontSolenoid.set(false);
						backSolenoid.set(false);
						turret.setEncPosition(0);
						gearTurret = 2200;
						gearAutoTime = 4.1;
						gearForwardLeft = -3370;
						gearForwardRight =3470;
						gearBallAuto = GearAuto.forward;
						break;
					case pos3:
						gearsTurn =-82;
						gearForwardLeft =-5550;
						gearForwardRight =5750;
						horzGyro.reset();
						leftMaster.setEncPosition(0);
						rightMaster.setEncPosition(0);
						gearArm.setEncPosition(0);
						turret.setEncPosition(0);
						gearTurret = 2500;
						gearTime.reset();
						gearTime.start();
						frontSolenoid.set(false);
						backSolenoid.set(false);
						gearBallAuto = GearAuto.forward;
						break;
				}
					break;
			}
			break;
	}


	}

	@Override
	public void autonomousPeriodic() {

		if(autoSelected.equalsIgnoreCase("Gear and Shoot Auto")){
			Gear10();
		}
		else{
			if(autoSelected.equalsIgnoreCase("40 Ball Auto")){
				BallsOut();
			}
		}
	}

	@Override
	public void teleopInit() {
		flyWheel.setEncPosition(0);
		rightMaster.setEncPosition(0);
		rightSlave.setEncPosition(0);
		
		leftMaster.setEncPosition(0);
		leftSlave.setEncPosition(0);	
		leftMaster.setCurrentLimit(40);
		rightMaster.setCurrentLimit(40);
		leftSlave.setCurrentLimit(40);
		rightSlave.setCurrentLimit(40);
	}

	@Override
	public void teleopPeriodic() {
		//System.out.println("rightMaster:  " + rightMaster.getEncPosition() + "leftMaster:  " + leftMaster.getEncPosition() + "rightSlave:  " + rightSlave.getEncPosition() + "leftSlave:  " + leftSlave.getEncPosition());
		
		if(turret.getEncPosition()<= -4125){
			turretFlip= false;
		}

		
		if(driver.getRawButton(leftTrigger)){
			climber.set(1);
		}else{
			climber.set(0);
		}
		//System.out.println("Turret Enc: "+ turret.getEncPosition());
		gearManipulator();
		shooter();
		hood();
		drivebase();
		smartDash();
		armSolenoid.set(false);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	void BallsOut(){
		switch(ballsOutAuton){
		case reset:
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
			turret.setEncPosition(0);
			gearArm.setEncPosition(0);
			horzGyro.reset();
			if(leftMaster.getEncPosition() == 0){
				turret.setEncPosition(0);
				ballsOutAuton = BallsOut.forward;
			}
			break;
		case forward:
			turret.set(normPID(ballsOutTurret,turret.getEncPosition(),0.0011,0));
			leftMaster.set(normPID(ballsOutForward1Left,leftMaster.getEncPosition(),0.00217,0));
			rightMaster.set(normPID(ballsOutForward1Right,rightMaster.getEncPosition(),0.00217,0));
			//System.out.println("Front Left: " + leftMaster.getEncPosition() + " Front Right: " + rightMaster.getEncPosition() + " Back Left: " + leftSlave.getEncPosition() + " Back Right: " + rightSlave.getEncPosition() + " Gyro: "+ horzGyro.getAngle());
			System.out.println("Left Position: " + leftMaster.getEncPosition() +" Left Comparision(greater or equal to this): " + (Math.abs(ballsOutForward1Left)-6) + " Right Position: " + rightMaster.getEncPosition() + " Right Comparision: " + (Math.abs(ballsOutForward1Right)-4));
			if((Math.abs(leftMaster.getEncPosition()) >= ((Math.abs(ballsOutForward1Left))-6)) || ((Math.abs(rightMaster.getEncPosition()) >= ((Math.abs(ballsOutForward1Right))-4)))){
				leftMaster.set(0);
				rightMaster.set(0);
				rightMaster.setEncPosition(0);
				rightSlave.setEncPosition(0);
				leftMaster.setEncPosition(0);
				leftSlave.setEncPosition(0);
				turret.set(0);
				ballsOutAuton = BallsOut.turn;
			}


			break;
		case turn:
				leftMaster.set(-normPID(ballsOutTurn,horzGyro.getAngle(),autoP,autoP*.005, ballsOutSum));
				rightMaster.set(-normPID(ballsOutTurn,horzGyro.getAngle(),autoP,autoP*.005, ballsOutSum));
				System.out.println("WE MADE IT TO TURN" + "Gyro: " + horzGyro.getAngle());
				

				armSolenoid.set(true);
				if(Math.abs(horzGyro.getAngle()) >= (Math.abs(ballsOutTurn) -4)){
					leftMaster.set(0);
					rightMaster.set(0);
					rightMaster.setEncPosition(0);
					rightSlave.setEncPosition(0);
					leftMaster.setEncPosition(0);
					leftSlave.setEncPosition(0);
					autoTime.start();

					ballsOutAuton = BallsOut.tacticalReload;
				}

			 break;
		case tacticalReload:


			System.out.println("TACTICAL RELOAD");
			gearArm.set(normPID(0,gearArm.getEncPosition(),0.00049,0));
			//System.out.println("Front Left: " + leftMaster.getEncPosition() + " Front Right: " + rightMaster.getEncPosition() + " Back Left: " + leftSlave.getEncPosition() + " Back Right: " + rightSlave.getEncPosition() + " Gyro: "+ horzGyro.getAngle());

			leftMaster.set(.8 * directionMultiplier);//.8
			rightMaster.set(-.8 * directionMultiplier);//-.8
			turret.set(normPID(ballsOutTurret,turret.getEncPosition(),0.0011,0));


			if(autoTime.get() >= .9 && turret.getEncPosition() >= (ballsOutTurret-50)){
				leftMaster.set(0);
				rightMaster.set(0);
				turret.set(0);
				flyWheel.changeControlMode(TalonControlMode.Speed);
				flyWheel.set(autoRPM);//-4450
				forwardTime.reset();
				forwardTime.start();
				ballsOutAuton = BallsOut.visionTrack;
			}
			break;
case visionTrack: 
       leftMaster.set(0.2 * this.directionMultiplier);
       rightMaster.set(-0.2* this.directionMultiplier);
       if (forwardTime.get() >= 2.5) {
         leftMaster.set(0.0);
         rightMaster.set(0.0);
       }
       lifter.set(-0.95);
       kicker.set(-0.95);
       this.climber.set(this.climbSpeed);
       smartDash();
       
       turret.set(visionPID(1.0, table.getNumber("COG_X", 0.0), visionKP));
       if ((table.getNumber("COG_X", 0.0) > -3.0) && (table.getNumber("COG_X", 0.0) < 5.0)) {
         turret.set(0.0D);
         ballsOutAuton = BallsOut.stopAndShoot;
       }
       
       break;
		case stopAndShoot:
			leftMaster.set(0.2* directionMultiplier);//.2
			rightMaster.set(-0.2* directionMultiplier);
			if(forwardTime.get()>=2.5){
				leftMaster.set(0);
				rightMaster.set(0);
			}
//			flyWheel.changeControlMode(TalonControlMode.Speed);
//			flyWheel.set(autoRPM);//-4450
			lifter.set(-.7);
			kicker.set(-.7);
			climber.set(climbSpeed);
			smartDash();
			

			if (flyWheel.getSpeed() >= 3650.0) {
         wheelOfDoom.set(-0.65);
         turret.set(0.0);
         turret.setEncPosition(0);
         hood.set(0.0);
       }
       else {
         wheelOfDoom.set(0.0);
       }
			break;
	}

	}

	void Gear10(){
		switch(gearBallAuto){
		case forward:
			leftMaster.set(.4 + normPID(0,horzGyro.getAngle(),0.00217,0));
			rightMaster.set(-.45 - normPID(0,horzGyro.getAngle(),0.00317,0));
			smartDash();
			if((-leftMaster.getEncPosition() <= gearForwardLeft) || (-rightMaster.getEncPosition() >=gearForwardRight) ){
				leftMaster.set(0);
				rightMaster.set(0);
				gearBallAuto = GearAuto.turn;
			}

			break;
		case turn:
			if(positionSelected.equalsIgnoreCase(pos2)){
				leftMaster.set(0);
				rightMaster.set(0);
				gearBallAuto = GearAuto.pivot;
			}
			leftMaster.set(-normPID(gearsTurn,horzGyro.getAngle(),.02,0));
			rightMaster.set(-normPID(gearsTurn,horzGyro.getAngle(),.02,0));
			if(horzGyro.getAngle() >= (Math.abs(gearsTurn)-24)){
				leftMaster.set(0);
				rightMaster.set(0);
				gearBallAuto = GearAuto.pivot;
			}
			break;
		case pivot:
			gearArm.set(normPID(330,gearArm.getEncPosition(),0.00097,0));
			if(gearArm.getEncPosition() >= 310){
				gearArm.set(0);
				leftMaster.setEncPosition(0);
				rightMaster.setEncPosition(0);
				gearBallAuto = GearAuto.place;
			}
			break;
		case place:
			leftMaster.set(.2 + normPID(0,horzGyro.getAngle(),0.00217,0));
			rightMaster.set(-.25 - normPID(0,horzGyro.getAngle(),0.00317,0));
			gearArm.set(normPID(330,gearArm.getEncPosition(),0.00097,0));

			smartDash();
			if(gearTime.get()>=gearAutoTime){
				leftMaster.set(0);
				rightMaster.set(0);
				gearBallAuto = GearAuto.drop;
			}
			break;

		case drop:
			gearArm.set(normPID(1100,gearArm.getEncPosition(),0.00097,0));
			if(gearTime.get() >=(gearAutoTime+.8)){

			leftMaster.set(-.35 );
			rightMaster.set(.4);
			}
			turret.set(normPID(-gearTurret,turret.getEncPosition(),0.0011,0));
			flyWheel.changeControlMode(TalonControlMode.Speed);
			flyWheel.set(-4850);
			//lifter.set(-1);
			//kicker.set(-1);
			//hood.set(-hoodPID(222,hoodEnc.getValue()));
			//leftMaster.getEncPosition() >= 6000 && rightMaster.getEncPosition() <= -6100
			if(gearTime.get() >=(gearAutoTime+1.7)){
				frontSolenoid.set(true);//This deploys omni if not already deployed.
				backSolenoid.set(true);
				leftMaster.set(0);
				rightMaster.set(0);
				gearArm.set(0);
				gearBallAuto = GearAuto.moveAndShoot;
			}


			break;
		case moveAndShoot:
			//leftMaster.set(.3);
			//rightMaster.set(.3);
//			flyWheel.changeControlMode(TalonControlMode.Speed);
//			flyWheel.set(2550);

			//same turn as on blue 40 ball for shooting

			if(gearTime.get() >= 9.6){
				leftMaster.set(0);
				rightMaster.set(0);
				turret.setEncPosition(0);
			}
			if(gearTime.get() >= 9.9){
				//turret.set(visionPID(165,table.getNumber("COG_X",0),visionKP));
				wheelOfDoom.set(-1);
				lifter.set(-.7);
				kicker.set(-.7);
			}
			break;
		case dontMove:
			leftMaster.set(0);
			rightMaster.set(0);
			break;
		}
	}
	void drivebase(){
		double speed = deadBand(-driver.getY());
		double turn = deadBand(-driver.getZ());

		leftMaster.set((-speed + turn));
		rightMaster.set((speed + turn));

		//Circle as a punch it buttona dn left trigger as a slow

		//Solenoids
		if(driver.getRawButton(leftBumper)){
			frontSolenoid.set(true); //omni
			backSolenoid.set(true);  //omni
		}

		if(driver.getRawButton(rightBumper)){
			frontSolenoid.set(false);//traction
			backSolenoid.set(false);//traction
		}

		if(driver.getRawButton(buttonX)){
			frontSolenoid.set(true);
			backSolenoid.set(false);
		}

		if(driver.getRawButton(rightTrigger)){
			frontSolenoid.set(false);
			backSolenoid.set(true);
		}
//		if(test.getRawButton(1)){
//			armSolenoid.set(true);
//		}
//		if(test.getRawButton(2)){
//			armSolenoid.set(false);
//		}


		if(driver.getRawButton(1)){
			rightMaster.setEncPosition(0);
			rightSlave.setEncPosition(0);
			leftMaster.setEncPosition(0);
			leftSlave.setEncPosition(0);
		}


	}

	void hood(){
		//System.out.println("Hood Position: " +hoodEnc.getValue());
		//hood.set(-hoodPID(770,hoodEnc.getValue()));
		if(oper.getPOV() == 0){
			flyWheelRPM = -3550;
		}
		if(oper.getPOV() == 90){
			flyWheelRPM = -3600;
		}
		if(oper.getPOV() == 180){
			flyWheelRPM = -3650;
		}
		if(oper.getPOV() == 270){
			flyWheelRPM = -3700;
		}
		
		if(oper.getRawButton(rightBumper)){
			hood.set(.5);
		}
		else{
			if(oper.getRawButton(rightTrigger)){
				hood.set(-.5);
			}
			else{
				hood.set(0);
			}
		}

	}


	void shooter(){
		if(driver.getPOV()==90){
			turret.set(-.45);
		}
		else{
			if(driver.getPOV()==270){
				turret.set(.45);
			}
			else{
				if(turretFlip&&(turret.getEncPosition()>-4725)){
					turret.set(-.4);

				}
				else{
					turret.set(0);
				}
			}
		}


		intakeArm.set(-(deadBand(oper.getRawAxis(5))));

		//shooting

		if(oper.getRawButton(buttonTriangle)){
			flyWheel.changeControlMode(TalonControlMode.Speed);
			//flyWheel.set(flyWheelRPM);
			flyWheel.set(-wheelRPM);
			lifter.set(-.85);
			kicker.set(-.85);


		}else{
			flyWheel.changeControlMode(TalonControlMode.PercentVbus);
			flyWheel.set(0);
			lifter.set(0);
			kicker.set(0);
		}
		//System.out.println(flyWheel.getSpeed());
		if(oper.getRawButton(buttonCircle)){//3100
			//System.out.println("WE ARE HERE");
			wheelOfDoom.set(-wheelOfDoomSpeed);
		}
		else{
			if(oper.getRawButton(rightAnalog)){
				wheelOfDoom.set(wheelOfDoomSpeed);
			}
			else{
			wheelOfDoom.set(0);
			}
		}
	

		if(oper.getRawButton(touchpad)){
			turret.set(normPID(2,table.getNumber("COG_X",0),visionKP,visionKI,ballsOutSum));
		}

		}

	void gearManipulator(){

		if(oper.getRawButton(buttonSquare)){ //up position for gear arm
			gearArm.set(normPID(-1400,gearArm.getEncPosition(),0.00049,0));
		}
		else{
			if(oper.getRawButton(buttonX)){ //deliver position for gear arm
				gearArm.set(normPID(-900,gearArm.getEncPosition(),0.00069,0));
			}
			else{
				if(gearArm.getEncPosition() <= -1400){
					gearArm.set(normPID(-1350,gearArm.getEncPosition(),0.00317,0));
				}
				else{
					gearArm.set(0.4*deadBand(oper.getY())); //left stick operator
				}
			}
		}
		if(oper.getRawButton(leftBumper)){
			gearIntake.set(-.75);
		}
		else{
			if(oper.getRawButton(leftTrigger)){
				gearIntake.set(.75);
			}
			else{
				gearIntake.set(0);
			}
		}
		if(oper.getRawButton(buttonPS)){
			gearArm.setEncPosition(0);
			turret.setEncPosition(0);
		}
	}
	double visionPID(double target,double position,double kP){
		double error = target - position;
		double speed = error *kP;
		return speed;

	}

	void smartDash(){

		SmartDashboard.putNumber("Speed", flyWheel.getSpeed());
		SmartDashboard.putNumber("COG_X", table.getNumber("COG_X",0));
		SmartDashboard.putNumber("COG_Y", table.getNumber("COG_Y",0));
		SmartDashboard.putNumber("Gear Arm Encoder", gearArm.getEncPosition());
		SmartDashboard.putNumber("Hood Encoder", hoodEnc.getValue());
		SmartDashboard.putNumber("Gyro Angle", horzGyro.getAngle());
		SmartDashboard.putNumber("Front Left Encoder", leftMaster.getEncPosition());
		SmartDashboard.putNumber("Back Left Encoder", leftSlave.getEncPosition());
		SmartDashboard.putNumber("Front Right Encoder", rightMaster.getEncPosition());
		SmartDashboard.putNumber("Back Right Encoder", rightSlave.getEncPosition());
		SmartDashboard.putNumber("Turret Spin Encoder", turret.getEncPosition());
		SmartDashboard.putNumber("fly 1 Current", flyWheel.getOutputCurrent());
		SmartDashboard.putNumber("fly 2 Current", flyWheel2.getOutputCurrent());
		SmartDashboard.putNumber("wod Current", wheelOfDoom.getOutputCurrent());


		//System.out.println(table.getNumber("COG_X",216));
		//System.out.println(table.getNumber("COG_Y",216));

		flyP = pref.getDouble("P",0.008);
		flyI = pref.getDouble("I",0.000023);
		flyD = pref.getDouble("D",0.07);
		flyF = pref.getDouble("F",10);

		wheelOfDoomSpeed = pref.getDouble("WheelOfDoom", 1);

		intakeSpeed = pref.getDouble("Intake", 1);
		robotSpeed = pref.getDouble("Speed",1);
		wheelRPM = pref.getDouble("RPM", 3240);
		hoodValue = pref.getDouble("Hood", 650);
		visionKP = pref.getDouble("VisionP", 0.00217);
		autoP = pref.getDouble("AutonGyroP", 0.0099);
		visionKI = pref.getDouble("VisionI", 0.00217);

		autoForwardLeft = pref.getDouble("ForwardLeft", 27);
		autoForwardRight = pref.getDouble("ForwardRight", 27);


		flyWheel.setP(flyP); //0.1
		flyWheel.setI(flyI); //0.0004895
		flyWheel.setD(flyD); //0.5
		flyWheel.setF(flyF);
	}


	double normPID(double target, double position, double pVal, double iVal){
		double PIDerror = target - position;
		//ballsOutSum += PIDerror;
		double pOutput = (PIDerror*pVal);
		double iOutput = (PIDerror*iVal);
		double speed = (pOutput+iOutput);
		if(Math.abs(PIDerror) < 1){
			speed = 0;
		}

		return speed;
	}

	double normPID(double target, double position, double pVal, double iVal, double[] sum){
		double PIDerror = target - position;
		sum[0] += PIDerror;
		double pOutput = (PIDerror*pVal);
		double iOutput = (sum[0]*iVal);
		double speed = (pOutput+iOutput);
		if(Math.abs(PIDerror) < 1){
			speed = 0;
		}

		return speed;
	}

	double deadBand(double joyStick){
		if(joyStick > -0.2 && joyStick < 0.2){
			joyStick = 0;
		}
		return joyStick;
	}

	double hoodPID(double target, double position){
		double error = target - position;
		double speed = error * 0.17;

		if(speed < 0.5 && error >10){
			speed*=2;
		}
		if(speed > 1){
			speed = 1;
		}

		return speed;
	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {
		if(resetSwitch.get() == false){
			horzGyro.calibrate();
			leftMaster.setEncPosition(0);
			rightMaster.setEncPosition(0);
			turret.setEncPosition(0);
			gearArm.setEncPosition(0);
		}
	}
}
