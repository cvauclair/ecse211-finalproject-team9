package ca.mcgill.ecse211.navigation;

import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.utility.TimerListener;

/**
 * This class runs in parallel to the system. It polls the ultrasonic sensor and when an obstacle is detected,
 * it takes control of the motors to avoid it.
 * @author christophe
 *
 */
public class CollisionAvoidance implements TimerListener{
  private Driver driver;
  private UltrasonicSensor usSensor;
  
  /**
   * This creates an instance of CollisionAvoidance
   * 
   * @param driver is the robot's Driver object
   * @param usSensor is the robot's UltrasonicSensor object
   */
  public CollisionAvoidance(Driver driver, UltrasonicSensor usSensor){
    
  }
  
  /**
   * This method is called by a timer and checks whether or not an object must be avoided
   */
  public void timedOut(){
    
  }
  
  /**
   * This method gets called from the class when an obstacle is detected and makes the robot avoid the collision
   */
  public void avoidObstacle(){
    
  }
  
  /**
   * This method is used to enable or disable the collision avoidance instance
   * @param enable is the enable flag
   */
  public void setEnable(boolean enable){
    
  }
}
