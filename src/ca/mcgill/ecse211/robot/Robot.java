package ca.mcgill.ecse211.robot;

/**
 * Created by Christophe Vauclair on 27/10/2017
 */

import lejos.hardware.Button;

/*
 * This class is a template class for all robots, in which the constructor sets up things that are needed for all 
 * robots (for now, it only sets up an escape thread to terminate the robot anytime by pressing escape).
 * All robots should be implemented by extending this class and overriding the run() method.
 * See ObjectDetectionCalibration for example.
 */

public class Robot {
  public Robot(){
    // Start escape thread
    (new Thread() {
      public void run() {
        while (Button.waitForAnyPress() != Button.ID_ESCAPE);
        System.exit(0);
      }
    }).start();
  }
  
  public void run(){
    
  }
}
