package ca.mcgill.ecse211.navigation;

import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.TimerListener;

/**
 * This class runs in parallel to the system. It polls the ultrasonic sensor and when an obstacle is detected,
 * it takes control of the motors to avoid it.
 * @author christophe
 *
 */
public class CollisionAvoidance implements TimerListener{
  private static Driver driver;
  private static Odometer odometer;
  private static UltrasonicSensor usSensor;
  private static EV3MediumRegulatedMotor usSensorMotor;
  private static Navigation navigation;
  private boolean enabled;
  private float[] distances;
  private float newDistance;
  private int counter;
  private boolean arrayFilled;
  private int numberOfSamples;
  private float currentMovingAverage;
  private float lastMovingAverage;
  private int threshold;
  private double oldTheta;
  private static final int bandCenter = 10;
  private static final int bandWidth = 3;
  private static final int maxCorrection = 50;
  private static final int propConst = 10;

  /**
   * This creates an instance of CollisionAvoidance
   * 
   * @param driver is the robot's Driver object
   * @param usSensor is the robot's UltrasonicSensor object
   */
  public CollisionAvoidance(Driver driver, Odometer odometer, UltrasonicSensor usSensor, EV3MediumRegulatedMotor usSensorMotor, int n, int threshold){
    this.driver = driver;
    this.odometer = odometer;
    this.usSensor = usSensor;
    this.usSensorMotor = usSensorMotor;
    this.distances = new float[n];
    this.counter = 0;
    this.arrayFilled = false;
    this.numberOfSamples = n;
    this.currentMovingAverage = 0;
    this.lastMovingAverage = 0;
    this.threshold = threshold;
    this.oldTheta = 0;
  }

  /**
   * This method is called by a timer and checks whether or not an object must be avoided
   */
  public void timedOut(){
    if(obstacleDetected()){
      oldTheta = odometer.getTheta();
      avoidObstacle();
    }
  }

  /**
   * This method gets called from the class when an obstacle is detected and makes the robot avoid the collision
   */
  public void avoidObstacle(){
    // Decide go left or right
    // For now turn right 
    driver.turnBy(70, false);
    usSensorMotor.rotate(-45);

    // P-Controller -> follow obstacle
    double turnAngle = 0;
    double distError = 0;
    int correction = 0;
    while(true){
      turnAngle = this.oldTheta - odometer.getTheta();
      if (turnAngle <= -260 || turnAngle >= 100) { //Assuming the object has now been avoided if the difference of angles is 90 degrees (Using approx 100 degrees)
        Sound.beep();
        //        pStyleMode = false;
        driver.stop();
        usSensorMotor.rotate(60);
        // We reduce the count so they go back to the point you were travelling to
        // before you encountered the obstacle
        navigation.decrementCounter();
        navigation.setNavigate(true);
        break;
      } else { //Implementation of P-Style Controller
        distError = bandCenter - this.currentMovingAverage; //Error = reference control value - measured distance from the wall  
        if(Math.abs(distError) <= bandWidth) {
          driver.setForwardSpeed(200);
          driver.forward();
        }
        else if (distError > 0) { //Too close to the wall
          correction = correction(distError);
          if(this.getMovingAverage() < 18) {    //Mechanism to avoid hitting the wall (If closer than 18cm)
            driver.backward();
          }
          driver.getLeftMotor().setSpeed(driver.getForwardSpeed() + correction); 
          driver.getRightMotor().setSpeed(driver.getForwardSpeed() - correction); 
          driver.getRightMotor().backward();    //Backward motion to turn on the spot / more precise turning
          driver.getLeftMotor().forward();
        }
        else if (distError < 0) { //Too far from the wall
          correction = correction(distError);
          driver.getLeftMotor().setSpeed(driver.getForwardSpeed() - correction); 
          driver.getRightMotor().setSpeed(driver.getForwardSpeed() + correction);
          driver.getLeftMotor().forward();
          driver.getRightMotor().forward();
        }
      }
    }

    driver.stop();
  }
  
  /**
   * Helper method that detects an Obstacle by computing the moving average and derivative of the sensors values. 
   * @return returns True when object is detected
   */
  private boolean obstacleDetected(){
    // Return true if obstacle is detected
    if(this.getMovingAverage() < this.threshold && this.arrayFilled){
      return true;
    }else{
      return false;
    }
  }
  /**
   * Helper method to calculate the moving average
   * @return float
   */
  private float getMovingAverage(){
    this.distances[this.counter] = Math.min(usSensor.getSample() * 100, 100);

    // Compute moving average and derivative only if first n values have been measured
    if(this.arrayFilled){ 
      // If first time moving average is computed
      if(this.lastMovingAverage == 0){
        this.lastMovingAverage = this.distances[this.numberOfSamples-1];
      }

      // Calculate the new moving average
      this.currentMovingAverage = this.lastMovingAverage 
          + (this.distances[(this.counter+1 == this.numberOfSamples ? 0 : this.counter+1)] 
              - this.distances[this.counter])/this.numberOfSamples;
    }

    // Update counter
    this.counter++;
    if(this.counter == this.numberOfSamples){
      this.counter = 0;
      this.arrayFilled = true;
    }

    this.lastMovingAverage = this.currentMovingAverage;

    return this.currentMovingAverage;
  }

  /**
   * This method is used to enable or disable the collision avoidance instance
   * @param enable is the enable flag
   */
  public void setEnable(boolean enable){
    this.enabled = enable;
  }
  
  /**
   * Helper method that sets the reference to the Navigation class 
   * @param navigation
   */
  public void setNavigation(Navigation navigation){
    this.navigation = navigation;
  }
  
  /**
   * Helper method that returns the proportional correction that needs to be provided to the speed 
   * @param error double
   * @return integer
   */
  private int correction(double error) {     
    int correction = (int) (propConst * (double)Math.abs(error)); //Corrected delta speed
    if (correction > maxCorrection) { //If correction too high, set a bound
      correction = maxCorrection;
    }
    return correction;
  }
}
