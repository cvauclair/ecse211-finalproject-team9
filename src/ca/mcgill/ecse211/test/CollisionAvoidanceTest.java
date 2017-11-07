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

public class CollisionAvoidanceTest extends Robot{  
  private final static double BASE_WIDTH = 12.5;
  private final static double WHEEL_RADIUS = 2.1;
  private final static double SQUARE_WIDTH = 30.48;
  
  public static void main(String[] args){
    CollisionAvoidanceTest collisionAvoidanceTest = new CollisionAvoidanceTest();
    collisionAvoidanceTest.run();
  }
  
  public void run(){
    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 20, 8);
    
    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    EV3MediumRegulatedMotor usSensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
    
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);
    
    Driver driver = new Driver(odometer, rightMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,14);
    Navigation navigation = new Navigation(driver, collisionAvoidance);
    
    
  }
}
