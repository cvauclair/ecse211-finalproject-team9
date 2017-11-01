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
 * This class includes all the logic to locate objects and test them to see if they are flags
 * @author christophe
 *
 */
public class ObjectDetection implements TimerListener{
  private Driver driver;
  private Odometer odometer;
  private UltrasonicSensor usSensor;
  private LightSensor lightSensor;
  private double[] data;
  
  /**
   * Creates an ObjectDetection instance
   * @param driver is the robot's Driver instance
   * @param odometer is the robot's Odometer instance
   * @param usSensor is the robot's UltrasonicSensor instance
   * @param lightSensor is the robot's horizontal LightSensor instance
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
    
  }
  
  /**
   * Method that takes a distance measurement and adds it to the array
   */
  public void timedOut(){
    System.out.println(this.usSensor.getSample()*100 + ',' + odometer.getTheta());
  }
  
  /**
   * Method that calculates the approximate location of surrounding objects based on the measurements in data
   * @return
   */
  public double[][] getObjectsLocation(){
    return new double[2][];
  }
  
  /**
   * This method uses the horizontal light sensor to check if the object in front of the robot is the flag or not.
   * It does not check whether or not an object is present in front of the robot.
   */
  public void testObject(){
    
  }
}
