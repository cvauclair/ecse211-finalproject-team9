package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryTest {
  public static double WHEEL_RADIUS = 0;
  public static double WHEEL_BASE = 0;
  
  public static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
  public static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
  
  private static final Port usPort = LocalEV3.get().getPort("S1");      
  private static final Port csPort = LocalEV3.get().getPort("S4");  
  
  public static void main(String[] args){
    // Ultrasonic sensor initialization
    SensorModes usSensor = new EV3UltrasonicSensor(usPort);
    SampleProvider usDistance = usSensor.getMode("Distance");
    float[] usData = new float[usDistance.sampleSize()];

    // Color sensor initialization
    SensorModes csSensor = new EV3ColorSensor(csPort);
    SampleProvider csLight = csSensor.getMode("Red");
    float[] csData = new float[csLight.sampleSize()];  
    
    Odometer odometer = new Odometer(leftMotor,rightMotor,WHEEL_RADIUS,WHEEL_BASE);
    OdometryCorrection odometryCorrection;
    OdometryDisplay odometryDisplay;
    Driver driver;
  }
}
