package ca.mcgill.ecse211.test;

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

public class NavigationTest extends Robot{
  public static void main(String args[]){
    (new NavigationTest()).run();
  }
  
  public NavigationTest(){
    super();
  }
  
  public void run(){
    super.run();
    
    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 50, 8);
    
    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    EV3MediumRegulatedMotor usSensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
    
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
//    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, LocalEV3.get().getTextLCD());
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,35);
    Navigation navigation = new Navigation(driver, collisionAvoidance);
    
//    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
//    collisionAvoidanceTimer.start();
    
    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();
    
//    Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
//    odometryCorrectionTimer.start();

    odometryDisplay.start();
    
    navigation.addPoint(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);
    navigation.addPoint(0 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
    navigation.addPoint(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
    navigation.addPoint(2 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
    navigation.navigate();

//    driver.travelTo(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);
//    driver.travelTo(0 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
//    driver.travelTo(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
//    driver.travelTo(2 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
  }
}
