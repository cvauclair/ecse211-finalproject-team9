package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.localization.Relocalization;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class RelocalizationTest extends Robot{
	
	 private final static double BASE_WIDTH = 12.5;
	 private final static double WHEEL_RADIUS = 2.1;
	 private final static double SQUARE_WIDTH = 30.48;
	 
	 public static void main(String args[]){
		 RelocalizationTest relocalizeTest = new RelocalizationTest();
		 relocalizeTest.run();
	 }
	 
	 public RelocalizationTest(){
		 super();
	 }
	
	public void run(){
		
		 LightSensor lightSensor = new LightSensor("S4", "Red");
		 LineDetector lineDetector = new LineDetector(lightSensor, 20, 8);
		 
		 EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		 EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		 
		 Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 
		 Driver driver = new Driver(odometer, rightMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 
		 Relocalization relocalize = new Relocalization(odometer, lineDetector, driver, SQUARE_WIDTH);
		
		 
		 driver.travelTo(1 * SQUARE_WIDTH, 3 * SQUARE_WIDTH);
		 driver.travelTo(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
		 relocalize.doRandomLocalization();
		 driver.travelTo(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);    
	}

}
