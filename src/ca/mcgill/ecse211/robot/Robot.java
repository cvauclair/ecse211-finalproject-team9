package ca.mcgill.ecse211.robot;

/**
 * Created by Christophe Vauclair on 27/10/2017
 */

import lejos.hardware.Button;

/**
 * This class is a template class for all robots, in which the constructor sets up things that are needed for all 
 * robots (for now, it only sets up an escape thread to terminate the robot anytime by pressing escape).
 * All robots should be implemented by extending this class and overriding the run() method.
 * See ObjectDetectionCalibration for example.
 */
public class Robot {
  protected final static double BASE_WIDTH = 12.5;
  protected final static double WHEEL_RADIUS = 2.1;
  protected final static double SQUARE_WIDTH = 30.48;

  /**
   * Creates an instance of Robot. The Robot class has a special feature that allows the program to terminate when
   * the escape button is pressed.
   */
  public Robot(){
    // Start escape thread
    (new Thread() {
      public void run() {
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);
      }
    }).start();
  }
  /**
   * Method to start the Robot. The method waits for the user to press a button to start.
   */
  public void run(){
    // Wait for enter button to start
    while(Button.waitForAnyPress() != Button.ID_ENTER){}
  }
}
