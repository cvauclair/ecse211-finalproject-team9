package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.CollisionAvoidance;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

public class LocalizationTest extends Robot{
  public static void main(String args[]){
    (new LocalizationTest()).run();
  }

  public LocalizationTest(){
    super();
  }
  
  public void run(){
    super.run();
    
    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 50, 8);
    
    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
        
    Timer lineDetectorTimer = new Timer(50,lineDetector);
    lineDetectorTimer.start();
        
    UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer, driver, usSensor);
    LightLocalizer lightLocalizer = new LightLocalizer(odometer, driver, lineDetector);
    
    usLocalizer.fallingEdge(0);
    lightLocalizer.localize(0, 0);
    
    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();
    
    Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
    odometryCorrectionTimer.start();
    
    driver.travelTo(1, 1);
    driver.turnTo(90);
  }
}
