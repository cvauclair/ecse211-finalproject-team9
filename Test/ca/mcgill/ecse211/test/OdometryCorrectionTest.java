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

public class OdometryCorrectionTest extends Robot {
  public static void main(String args[]){
    (new OdometryCorrectionTest()).run();
  }

  public OdometryCorrectionTest(){
    super();
  }

  public void run(){
	super.run();
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, -20, 8);
    Timer lineDetect = new Timer(50, lineDetector);

    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, LocalEV3.get().getTextLCD());
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    lineDetect.start();
    
    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

    Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
    odometryCorrectionTimer.start();
    
    odometryDisplay.start();
    
    driver.travelTo(0 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
    driver.travelTo(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
    driver.travelTo(2 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
    driver.travelTo(0 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
  }
}
