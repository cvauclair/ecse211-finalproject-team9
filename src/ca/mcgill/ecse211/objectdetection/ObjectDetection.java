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
 */
public class ObjectDetection{
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
   * tested. It is to be called after the robot has crossed into the enemy search zone.
   * 
   * @param flagColor   the color of the enemy's flag
   */
  public void findFlag(int flagColor){
    this.passedZero = false;
    this.startingAngle = this.odometer.getTheta();
    this.driver.turnBy(400,false);
    
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
        
        this.testObject(flagColor);
        this.driver.rotateClockwise();
      }
      
      if(this.odometer.getTheta() < this.startingAngle){
        this.passedZero = true;
      }
    }
    this.driver.stop();
  }
  
  /**
   * This method drives the robot forward until it reaches the object previously detected 
   * and uses the horizontal light sensor to check if the object in front of the robot is the flag or not.
   * It then returns to its initial position.
   * 
   * @param flagColor   the color of the enemy's flag
   * 
   * @return isObject	a boolean which is true if the object detected is the flag, false otherwise
   */
  public boolean testObject(int flagColor){
    this.xVal = this.odometer.getX();
    this.yVal = this.odometer.getY();

    // Move forward until object close enough
    this.driver.setSpeed(80);
    this.driver.forward();
    while(usSensor.getSample() > 10){}
    this.driver.stop();
    
    // Check flag
    boolean isFlag = false;
    if(lightSensor.getSample() == flagColor){
      isFlag = true;
    }
    
    // Go back to original position
    this.deltaD = Math.sqrt(Math.pow(this.odometer.getX() - xVal, 2) + Math.pow(this.odometer.getY() - yVal, 2));
    this.driver.forward(-this.deltaD, false);

	return isFlag;
  }
}
