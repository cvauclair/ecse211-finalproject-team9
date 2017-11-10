package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.navigation.CollisionAvoidance;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

public class CollisionAvoidanceTest extends Robot{  
  private final static double BASE_WIDTH = 12.5;
  private final static double WHEEL_RADIUS = 2.1;
  private final static double SQUARE_WIDTH = 30.48;
  
  public static void main(String[] args){
    (new CollisionAvoidanceTest()).run();
  }
  
  public CollisionAvoidanceTest(){
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
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,14);
    Navigation navigation = new Navigation(driver, collisionAvoidance);
    
    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
    collisionAvoidanceTimer.start();
    
    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();
    
    Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
    odometryCorrectionTimer.start();
    
    navigation.addPoint(2 * SQUARE_WIDTH, 2 * SQUARE_WIDTH);
    navigation.navigate();
  }
}
