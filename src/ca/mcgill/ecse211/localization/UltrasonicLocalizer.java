package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * This class executes initial localization with the help of the Ultrasonic sensor.
 * Two methods are provided to localize with the ultrasonic sensor: falling and rising 
 * edge. The robot should turn on itself until it sees a wall. Based on the method used,
 * The robot will be able to (Poor man's) localize the corner with respect to the robot's
 * current position. With the help of the corner's position, the robot can then 
 * know its angle.
 *
 */
public class UltrasonicLocalizer {
  private static Odometer odometer;
  private static Driver driver;
  private static UltrasonicSensor usSensor;

  private static final int ANGLE_INTERVAL = 8;    // Angle intervals at which samples should be taken
  private static final int EDGE_VALUE = 30;
  private static final int NOISE_MARGIN = 2;

  /**
   * UlrasonicLocalizer corrects the odometer's angle 
   * @param odometer		an Odometer instance
   * @param driver		a Driver instance
   * @param us			a SampleProvider for ultrasonic sensor
   * @param usData		a float array to store the data fetched from the sensor
   */
  public UltrasonicLocalizer(Odometer odometer, Driver driver, UltrasonicSensor usSensor){
    this.odometer = odometer;
    this.driver = driver;
    this.usSensor = usSensor;
  }
  
  /**
   * This method determines which localization method has to be used based on the
   * orientation of the robot
   * @param initialOrientation  int 
   */
  public void localize(int initialOrientation){
    // Get valid sample
    //    float sample = usSensor.getSample() * 100;
    //    while(sample > 100){
    //      sample = usSensor.getSample() * 100;
    //    }
    float sample = Math.min(usSensor.getSample() * 100, 100);

    if(sample < EDGE_VALUE){
//      System.out.println("Rising edge chosen (" + sample + ")");
      this.risingEdge(initialOrientation);
    }else{
//      System.out.println("Falling edge chosen (" + sample + ")");
      this.fallingEdge(initialOrientation);
    }
  }

  /**
   * Localization with falling edge
   * @param initialOrientation		an int that represents the initial angle in degrees
   */
  public void fallingEdge(int initialOrientation){
    double backWallAngle = 0;
    double leftWallAngle = 0;
    double theta = 0;

    // Reduce rotating speed of robot to minimize wheel sliding when turning
    driver.setRotateSpeed(100);

    // Find back wall falling edge
    backWallAngle = findFallingEdge(true);

    // Return to starting orientation
    driver.turnTo(0);

    // Find left wall falling edge
    leftWallAngle = findFallingEdge(false);

    // Return to starting position
    driver.turnTo(0);

    // Correct the odometer's theta value depending on angle results (adjustment values of 215 and 205 gotten through experimentation)
    if(360-leftWallAngle > backWallAngle){
      odometer.setTheta(200-(backWallAngle+leftWallAngle)/2);
    }else{
      odometer.setTheta(220-(backWallAngle+leftWallAngle)/2);
    }
    // Orient the robot correctly
    driver.turnTo(0);

    // Set the actual theta passed by argument by the caller
    odometer.setTheta(initialOrientation);
  }

  /**
   * Localization with rising edge
   * @param initialOrientation		an int that represents the initial angle in degrees
   */
  public void risingEdge(int initialOrientation){
    double backWallAngle = 0;
    double leftWallAngle = 0;

    // Reduce rotating speed of robot to minimize wheel sliding when turning
    driver.setRotateSpeed(100);

    // Find left wall falling edge
    leftWallAngle = findRisingEdge(true);

    // Return to starting orientation
    driver.turnTo(0);

    // Find back wall falling edge
    backWallAngle = findRisingEdge(false);

    // Return to starting orientation
    driver.turnBy(360 - this.odometer.getTheta(), false);

    // Correct the odometer's theta value depending on angle results (adjustment values of 35 and 55 gotten through experimentation)
    if(360-backWallAngle > leftWallAngle){
      odometer.setTheta(55-(backWallAngle+leftWallAngle)/2);
    }else{
      odometer.setTheta(35-(backWallAngle+leftWallAngle)/2);
    }

    // Orient the robot correctly
    driver.turnTo(0);

    // Set the actual theta passed by argument by the caller
    odometer.setTheta(initialOrientation);
  }

  /**
   * Method that returns the angle at which the falling edge was located
   * @param clockwise 	a boolean describing clockwise or counterclockwise
   * @return angle 		a float angle which is the average angle
   */
  private double findFallingEdge(boolean clockwise){
    boolean inNoiseMargin = false;  // Flag indicating if data values are in the noise margin
    double enteringAngle = 0;  // Angle at which data values enter the noise margin
    double exitingAngle = 0;   // Angle at which data values leave the noise margin
    float currentValue = 0;

    // Start turning, do not wait for the motors to finish
    if(clockwise){
      driver.rotateClockwise();
    }else{
      driver.rotateCounterClockwise();
    }

    while(true){
      // Take the average of four measurements to reduce uncertainty
      currentValue = Math.min(usSensor.getSample() * 100, 100);
      //      if(currentValue > 100) continue;
      if(currentValue < EDGE_VALUE + NOISE_MARGIN){
        if(!inNoiseMargin){
          // If the current data value was previously outside the noise margin, set the entering angle
          enteringAngle = odometer.getTheta();
        }
        inNoiseMargin = true;
//        System.out.println("In noise margin (" + currentValue + ")");
      }else{
        // If the values leave the noise margin without going all the way through, set flag to false
        inNoiseMargin = false;
//        System.out.println("Out of noise margin (" + currentValue + ")");
      }

      if(inNoiseMargin && currentValue < EDGE_VALUE - NOISE_MARGIN){
        // If current data has passed through the noise margin, set the exiting point and exit the loop
        exitingAngle = odometer.getTheta();
//        System.out.println("Edge detected (" + currentValue + ")");
        break;
      }

      try {
        Thread.sleep(25);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // Stop rotating
    driver.stop();
    double angle = (enteringAngle + exitingAngle)/2.0;
    return angle;
  }

  /**
   * Method that returns the angle at which the falling edge was located
   * @param clockwise 	a boolean describing clockwise or counterclockwise
   * @return angle 		a float angle which is the average angle
   */
  private double findRisingEdge(boolean clockwise){
    boolean inNoiseMargin = false;  // Flag indicating if data values are in the noise margin
    double enteringAngle = 0;  // Angle at which data values enter the noise margin
    double exitingAngle = 0;   // Angle at which data values leave the noise margin
    float currentValue = 0;

    // Start turning, do not wait for the motors to finish
    if(clockwise){
      driver.rotateClockwise();
    }else{
      driver.rotateCounterClockwise();
    }

    // Detect the rising edge
    while(true){
      // Take the average of four measurements to reduce uncertainty
      currentValue = Math.min(usSensor.getSample() * 100, 100);
      //      if(currentValue > 500) continue;
      if(currentValue > EDGE_VALUE - NOISE_MARGIN){
        if(!inNoiseMargin){
          // If the current data value was previously outside the noise margin, set the entering angle
          enteringAngle = odometer.getTheta();
        }
        inNoiseMargin = true;
//        System.out.println("In noise margin (" + currentValue + ")");
      }else{
        // If the values leave the noise margin without going all the way through, set flag to false
        inNoiseMargin = false;
//        System.out.println("Out of noise margin (" + currentValue + ")");
      }

      if(inNoiseMargin && currentValue > EDGE_VALUE + NOISE_MARGIN){
        // If current data has passed through the noise margin, set the exiting point and exit the loop
        exitingAngle = odometer.getTheta();
//        System.out.println("Edge detected (" + currentValue + ")");
        break;
      }

      try {
        Thread.sleep(25);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // Stop rotating
    driver.stop();
    double angle = (enteringAngle + exitingAngle)/2.0;
    return angle;
  }
}
