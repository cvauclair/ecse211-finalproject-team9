package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.Relocalization;
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
import ca.mcgill.ecse211.zipline.ZiplineControl;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

public class DemoTest extends Robot{
  public static final int ziplineX0 = 0;
  public static final int ziplineY0 = 6;
  public static final double ziplineXc = 1;
  public static final double ziplineYc = 6;
  
  public static void main(String args[]){
    (new DemoTest()).run();
  }
  
  public DemoTest(){
    super();
  }
  
  public void run(){
    super.run();
    
    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LightSensor backLightSensor = new LightSensor("S4", "Red");
    
    LineDetector lineDetector = new LineDetector(lightSensor, -20, 8);
    LineDetector backLineDetector = new LineDetector(backLightSensor, -47, 8);
    
    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    EV3LargeRegulatedMotor ziplineMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
    EV3MediumRegulatedMotor usSensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
    
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);

    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,35);
    Navigation navigation = new Navigation(driver, collisionAvoidance);
    
    Relocalization relocalization = new Relocalization(odometer, backLineDetector, driver, SQUARE_WIDTH);

    UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer, driver, usSensor);
    LightLocalizer lightLocalizer = new LightLocalizer(odometer, driver, lineDetector);

    ZiplineControl ziplineControl = new ZiplineControl(ziplineMotor, driver);
    
//    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
//    collisionAvoidanceTimer.start();

    
    Timer lineDetectorTimer = new Timer(50,lineDetector);
    lineDetectorTimer.start();

    Timer backLineDetectorTimer = new Timer(50,backLineDetector);
    backLineDetectorTimer.start();
    
    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

//  Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
//  odometryCorrectionTimer.start();
    
    usLocalizer.localize(0);
    lightLocalizer.localize(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);
    
    driver.setForwardSpeed(200);
    
    driver.travelTo(ziplineX0 * SQUARE_WIDTH, ziplineY0 * SQUARE_WIDTH);
    
    relocalization.doReLocalization();
    driver.turnTo(0);
    
    driver.travelTo(ziplineXc * SQUARE_WIDTH, ziplineYc * SQUARE_WIDTH);
    
    ziplineControl.traverseZipline();
  }
}
