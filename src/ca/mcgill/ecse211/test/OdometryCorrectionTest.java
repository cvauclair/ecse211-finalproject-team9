package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Timer;

public class OdometryCorrectionTest extends Robot{
	
	 private final static double BASE_WIDTH = 12.5;
	 private final static double WHEEL_RADIUS = 2.1;
	 private final static double SQUARE_WIDTH = 30.48;
	
	public void run(){
		 super.run();
		 
		 final TextLCD t = LocalEV3.get().getTextLCD();
		 
		 
		 LightSensor lightSensorFront = new LightSensor("S2","Red");
		 LineDetector lineDetectorFront = new LineDetector(lightSensorFront, 12, 8);
		 LightSensor lightSensorBack = new LightSensor("S4", "Red");
		 LineDetector lineDetectorBack = new LineDetector(lightSensorBack, 16, 8);
		 
		 EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		 EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		 
		 Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 OdometryDisplay display = new OdometryDisplay(odometer, t);
		 OdometryCorrection odometerCorrection = new OdometryCorrection(odometer, lineDetectorFront,SQUARE_WIDTH, 0);
		 
		 Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 
		 Timer odoTimer = new Timer(50,odometer);
		 Timer odoCorrectionTimer = new Timer(50, odometerCorrection);
		 odoCorrectionTimer.start();
		 odoTimer.start();
		 display.start();
		 
		 //Square driver, make sure to place the robot in the middle of the tile
		 driver.travelTo(0*SQUARE_WIDTH, 2*SQUARE_WIDTH);
		 driver.travelTo(2*SQUARE_WIDTH, 2*SQUARE_WIDTH);
		 driver.travelTo(2*SQUARE_WIDTH, 0*SQUARE_WIDTH);
		 driver.travelTo(0*SQUARE_WIDTH, 0*SQUARE_WIDTH);
	}
}
