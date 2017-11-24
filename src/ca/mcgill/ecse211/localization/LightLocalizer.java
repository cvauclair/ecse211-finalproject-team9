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
 * value to 0. It will turn to 45 degrees and move forward until it fins the vertical
 * line and set the odometer's x value to 0.
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
//    lineLocalization(LineOrientation.Horizontal);
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
    
//    // Turn robot so that the next line the robot crosses will be a vertical line
//    driver.turnBy(45,false);
//
//    // Get away from the last line so the robot does not detect it again
//    driver.forward(4,false);
//    driver.forward();

    // Correct the robot's odometer's X position by finding a vertical line
//    lineLocalization(LineOrientation.Vertical);
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
    
//    System.out.println("oldX: " + oldX);
//    System.out.println("oldY: " + oldY);
//    System.out.println("newX: " + newX);
//    System.out.println("newY: " + newY);
    
    // Theta correction
//    double newTheta = odometer.getTheta() - 45 + Math.toDegrees(Math.atan(Math.abs(newY-oldY)/Math.abs(newX-oldX)));
//    if(newTheta > 359.9) newTheta -= 359.9;
//    if(newTheta < 0) newTheta += 359.9;
//    odometer.setTheta(newTheta);
//    odometer.setTheta(Math.toDegrees(Math.atan(Math.abs(newY-oldY)/Math.abs(newX-oldX))));
    
    // Once the odometer's position is correctly set, travel to (x0,y0) and orient the robot correctly
    driver.travelTo(0,0);
    driver.turnTo(0);
    odometer.setX(x0);
    odometer.setY(y0);
  }

  /**
   * Method that sets the odometer's Y position by detecting a horizontal line (horizontalLine = true) or
   * its X position by detecting a vertical line (horizontalLine = false). The odometer's Y value (or 
   * X value depending on horizaontalLine) is set to initialValue
   * @param lineOrientation 		a LineOrientation instance that is either Horizontal or Vertical
   */
  private void lineLocalization(LineOrientation lineOrientation){    
    // Set robot to driver forward 
    driver.forward();

    // Wait to detect line
    this.frontLineDetector.reset();
    while(!this.frontLineDetector.checkLine()){};
    System.out.println("Line detected");

    // Stop robot
    driver.stop();

    // Correct the odometer's Y or X value depending on whether the crossed line is vertical or horizontal
    if(lineOrientation == LineOrientation.Horizontal){
      odometer.setY(0);
    }else if(lineOrientation == LineOrientation.Vertical){
      odometer.setX(0);
    }
  }

  /**
   * Helper method that returns when a line is crossed
   * @param cs 					a SampleProvider for color sensor
   * @param csData 				a float array to store values detected by color sensor
   * @param samplingFrequency 	an int that represents the frequency at which the sensor polls the values
   * @param threshold 			an int used as limit (if limit is reached, the line is detected)
   * @param n 					an int that represents values to keep for moving average
   */
  private static void detectLine(LineDetector lineDetector){
    while(lineDetector.checkLine()){}
  }
}
