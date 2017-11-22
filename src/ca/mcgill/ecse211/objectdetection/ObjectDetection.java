package ca.mcgill.ecse211.objectdetection;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

/**
 * Created by Christophe Vauclair on 23/10/2017
 */

/**
 * This class includes all the logic to locate objects by collecting values form
 * the ultrasonic sensor and test them to see if they are flags.
 * 
 *
 */
public class ObjectDetection implements TimerListener{
  private Driver driver;
  private Odometer odometer;
  private UltrasonicSensor usSensor;
  private LightSensor lightSensor;
  private double[] data;

  private double angleLeft;
  private double startingAngle;
  private float currentDistance;
  private float previousDistance;
  private double fallingEdgeOrientation;
  private double risingEdgeOrientation;
  private boolean fallingEdgeDetected;
  private boolean passedZero;
  private double xVal;
  private double yVal;
  private double deltaD;

  /**
   * Creates an ObjectDetection instance
   * @param driver 		a Driver instance that is the robot's driver object
   * @param usSensor 	a UltrasonicSensor instance
   * @param odometer 	a Odometer instance is the robot's Odometer
   * @param lightSensor 	a lightSensor instance is the robot's horizontal LightSensor
   */
  public ObjectDetection(Driver driver, Odometer odometer, UltrasonicSensor usSensor, LightSensor lightSensor){
    this.driver = driver;
    this.odometer = odometer;
    this.usSensor = usSensor;
    this.lightSensor = lightSensor;
  }
  
  /**
   * This method executes the flag finding logic and will return when the flag is found or all located objects have been 
   * tested. It is to be called after the robot has crossed into enemy territory.
   */
  public void findFlag(){

    this.passedZero = false;
    this.startingAngle = this.odometer.getTheta();
    this.driver.rotateClockwise();
    
    while(!(this.odometer.getTheta() > this.startingAngle && this.passedZero)){
      this.currentDistance = this.usSensor.getSample() * 100;
      if(this.currentDistance > 1000){
        this.currentDistance  = this.previousDistance;
      }
      
      if(this.currentDistance - this.previousDistance < -20){
        this.fallingEdgeOrientation = this.odometer.getTheta();
        this.fallingEdgeDetected = true;
      }
      if(this.currentDistance - this.previousDistance > 20 && this.fallingEdgeDetected){
        this.driver.stop();
        
        this.risingEdgeOrientation = this.odometer.getTheta();
        this.fallingEdgeDetected = false;
        
        this.driver.turnBy(-(this.risingEdgeOrientation - this.fallingEdgeOrientation)/2, false);
        
        this.testObject();
        this.driver.rotateClockwise();
      }
      
      if(this.odometer.getTheta() < this.startingAngle){
        this.passedZero = true;
      }
    }
    this.driver.stop();

  }
  
  /**
   * Method that takes a distance measurement and adds it to the array
   */
  public void timedOut(){
    System.out.println(this.usSensor.getSample()*100 + ',' + odometer.getTheta());
  }
  
  /**
   * Method that calculates the approximate location of surrounding objects based on the measurements in data
   * @return location	a double[][] which is the approximate location of the object
   */
  public double[][] getObjectsLocation(){

	double location[][] = new double[2][1];

    return location;
  }
  
  /**
   * This method uses the horizontal light sensor to check if the object in front of the robot is the flag or not.
   * It does not check whether or not an object is present in front of the robot.
   * @return isObject	a boolean which is true if the object detected is the flag, false otherwise
   */
  public boolean testObject(){
    this.xVal = this.odometer.getX();
    this.yVal = this.odometer.getY();

    this.driver.setSpeed(80);
    this.driver.forward();
    while(usSensor.getSample() > 10){}
    this.driver.stop();
    
    // Check flag
    
    this.deltaD = Math.sqrt(Math.pow(this.odometer.getX() - xVal, 2) + Math.pow(this.odometer.getY() - yVal, 2));
    this.driver.forward(-this.deltaD, false);

    boolean isObject = false;
	return isObject;
  }
}
