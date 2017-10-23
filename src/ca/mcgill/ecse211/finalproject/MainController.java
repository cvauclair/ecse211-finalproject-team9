package ca.mcgill.ecse211.finalproject;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;

public class MainController {
  private final static double BASE_WIDTH = 9.5;
  private final static double WHEEL_RADIUS = 2.1;
  
  private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A")); 
  private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

  private static final Port usPort = LocalEV3.get().getPort("S1");      
  
  public static void main(String[] args){
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    Driver driver = new Driver(odometer,leftMotor,rightMotor,WHEEL_RADIUS, BASE_WIDTH);
    
    // Ultrasonic sensor initialization
    SensorModes usSensor = new EV3UltrasonicSensor(usPort);
    SampleProvider usDistance = usSensor.getMode("Distance");
    float[] usData = new float[usDistance.sampleSize()];
    
    ObjectDetection objectDetection = new ObjectDetection(odometer,usDistance,usData);
    Timer objectDetectionTimer = new Timer(50, objectDetection);
    
    odometer.start();
    
    driver.setRotateSpeed(100);
    
    Button.waitForAnyPress();
    
    objectDetectionTimer.start();
    driver.turnBy(90, false);
    driver.turnBy(-180, false);
    driver.turnBy(90, false);
    
    System.exit(0);
  }
}
