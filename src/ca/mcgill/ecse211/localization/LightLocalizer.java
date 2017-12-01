package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
/**
 * This class starts the initial localization process with the help of 
 * a light sensor using a soft-hard-coded technique. The robot will first
 * keep moving forward to find the first horizontal line and set the odometer's Y
 * value to 0. It will then rotate clockwise until the back line detector detects 
 * the same line that the front one detected. At this point, the robot is facing 
 * the 90 degrees direction and the odometer's value is corrected. The robot then 
 * turns by 45 degrees clockwise and starts moving until its front line detector sees 
 * a line (which at that point will be the vertical line and sets the odometer's x value to 0.
 * The robot then executes a similar maneuver to when it detected the horizontal line, using 
 * its back sensor to make the robot face the 0 degrees orientation. Finally, the robot is
 * moved to (0,0).
 */
public class LightLocalizer {
  private Odometer odometer;
  private Driver driver;
  private LineDetector frontLineDetector;
  private LineDetector backLineDetector;

  enum LineOrientation {Horizontal, Vertical};

  /**
   * This creates an instance of LightLocalizer. It is used to correct the odometer's x and y position only when the game starts
   * @param odometer 	an Odometer instance
   * @param driver 		a Driver instance
   * @param cs  			a SampleProvider for color sensor
   * @param csData  		a float array to store values received from sensor
   */
  public LightLocalizer(Odometer odometer, Driver driver, LineDetector frontLineDetector, LineDetector backLineDetector){
    this.odometer = odometer;
    this.driver = driver;
    this.frontLineDetector = frontLineDetector;
    this.backLineDetector = backLineDetector;
  }

  /**
   * This method localizes the robot to (x0,y0) (in cm) using a "soft-hard-coded" technique
   * @param x0 		a double for the x-position
   * @param y0 		a double for the y-position
   */
  public void localize(double x0, double y0){

    // Slow down robot so that it does not miss the line
    driver.setForwardSpeed(150);

    // Correct the robot's odometer's Y position by finding a horizontal line
    driver.forward();   
    this.frontLineDetector.reset();
    while(!this.frontLineDetector.checkLine()){};
    driver.stop();
    Sound.beep();
    driver.forward(1,false);
    odometer.setY(0);
    
    // Angle adjust
    this.driver.rotateClockwise();
    this.backLineDetector.reset();
    while(!this.backLineDetector.checkLine()){};
    this.driver.stop();    
    Sound.beep();
    this.odometer.setTheta(90);
    
    // Correct the robot's odometer's X position by finding a vertical line
    this.driver.turnBy(45, false);
    this.driver.forward(4,false);
    this.driver.forward();    

    this.frontLineDetector.reset();
    while(!this.frontLineDetector.checkLine()){};
    driver.stop();
    Sound.beep();
    driver.forward(1,false);
    odometer.setX(0);
    
    this.driver.rotateCounterClockwise();
    this.backLineDetector.reset();
    while(!this.backLineDetector.checkLine()){};
    this.driver.stop();    
    Sound.beep();
    this.odometer.setTheta(0);
    
    // Once the odometer's position is correctly set, travel to (x0,y0) and orient the robot correctly
    driver.travelTo(0,0);
    driver.turnTo(0);
    odometer.setX(x0);
    odometer.setY(y0);
  }
}
