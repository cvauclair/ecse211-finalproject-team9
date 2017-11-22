package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.objectdetection.ObjectDetection;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;

/**
 * Created by Christophe Vauclair on 17/10/2017
 */

public class ObjectDetectionTest extends Robot{
  private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A")); 
  private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

  private static final Port usPort = LocalEV3.get().getPort("S1");      

  private Odometer odometer;
  private Driver driver;
  private UltrasonicSensor usSensor;
  private LightSensor lightSensor;
  private ObjectDetection objectDetection;
  
  public static void main(String[] args){
    (new ObjectDetectionTest()).run();
  }

  public ObjectDetectionTest(){
    super();
    this.odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    this.driver = new Driver(odometer,leftMotor,rightMotor,WHEEL_RADIUS, BASE_WIDTH);
    
    this.usSensor = new UltrasonicSensor("S1", "Distance");

    this.lightSensor = new LightSensor("S2", "Red");

    this.objectDetection = new ObjectDetection(driver,odometer,usSensor,lightSensor);

  }
  
  public void run(){
	super.run();
    Timer odometerTimer = new Timer(25, odometer);
    
    odometerTimer.start();

    driver.setRotateSpeed(100);

    this.objectDetection.findFlag();

    System.exit(0);
  }
}
