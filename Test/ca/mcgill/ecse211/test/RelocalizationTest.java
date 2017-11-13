package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.localization.Relocalization;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Timer;

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
		 super.run();
		 
		 final TextLCD t = LocalEV3.get().getTextLCD();
		 
		 LightSensor lightSensor1 = new LightSensor("S4", "Red");
		 LineDetector lineDetector1 = new LineDetector(lightSensor1, -47, 8); 
		 //-47 for dark board, -52 for light board
		 Timer lineDetect1 = new Timer(50, lineDetector1);
		 
		 EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		 EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		 
		 Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 OdometryDisplay display = new OdometryDisplay(odometer, t);
		 
		 Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
		 
		 Relocalization relocalize = new Relocalization(odometer, lineDetector1, driver, SQUARE_WIDTH);
		
		 Timer odoTimer = new Timer(50,odometer);
		 lineDetect1.start();
		 odoTimer.start();
		 display.start();
		// driver.travelTo(0 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
		driver.travelTo(1*SQUARE_WIDTH, 1*SQUARE_WIDTH);
		 try {
	          Thread.sleep(1000);
	      } catch (InterruptedException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      }
		 relocalize.doReLocalization();
		 //driver.travelTo(1*SQUARE_WIDTH, 1*SQUARE_WIDTH);
		 driver.travelTo(2*SQUARE_WIDTH, 1*SQUARE_WIDTH);
		 
		// driver.turnTo(-88);
		// odometer.setTheta(90.0);
		// driver.travelTo(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);    
	}

}
