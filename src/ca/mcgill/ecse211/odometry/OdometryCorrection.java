package ca.mcgill.ecse211.odometry;

import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.Sound;

/**
 * Created by Allison Mejia on 20/10/2017
 * Edited by Christophe Vauclair on 27/10/2017
 */

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

/**
 * This class is used to correct an Odometer instance with the help of a LineDetector instance. 
 * It has the necessary logic to detect whether it has crossed a vertical or horizontal line. 
 * Since this class can only correct either x or y, no correction is provided when a corner of 
 * a tile is crossed.
 *
 */
public class OdometryCorrection implements TimerListener{
  private static LineDetector lineDetector;
  private static final long CORRECTION_PERIOD = 10;
  private static Odometer odometer;
  private static double tileSize;
  private static double dist2wheel;
  private double x0, y0; //values returned by the odometer
  private double lineTreshold; //in cm = ~ 4.88 cm

  /**
   * This class runs in parallel to the system and corrects the robot's odometer instance every time the robot
   * crosses a line.
   * 
   * @param odometer 	an Odometer instance which is the robot's odometer
   * @param lineDetector	a LineDetector instance
   * @param tileS		a double that is the size of a wooden tile
   * @param dist2wheel 	a double that is the distance between the wheel axis and the light sensor
   */
  public OdometryCorrection(Odometer odometer, LineDetector lineDetector, double tileS, double dist2W){
    this.odometer = odometer;
    this.lineDetector = lineDetector;
    this.tileSize = tileS;
    this.dist2wheel = dist2W;
    this.x0 = 0;
    this.y0 = 0;
    this.lineTreshold = (double) 5/this.tileSize;
  }
  
  /**
   * This method implements the necessary logic to correct the value of an odometer instance every time a line is crossed
   */
  public void timedOut(){
    // Correct Odometer value if line is detected
    if(lineDetector.checkLine()){
//      Sound.beep();
      //range [0,12]
      //double filterInitial = 3.0;
      double thetaOdometer = odometer.getTheta();
      
      if((thetaOdometer >= 80 && thetaOdometer <170)|| (thetaOdometer >=260 && thetaOdometer < 359)){
    	  Sound.twoBeeps(); //for debug purposes
          //odometer.setX((x0 - (x0 % 1.0)) * tileSize );
    	  double xOdometer = odometer.getX();
    	  this.x0 = (double) ( xOdometer  / tileSize);
          double roundX = Math.round(this.x0);
          odometer.setX(roundX  * tileSize);
          System.out.println("thetaOdo = "+ thetaOdometer+" Xodo = "+roundX+"\n");
      }
      if(thetaOdometer < 80 || (thetaOdometer >=160 && thetaOdometer < 260)){
    	  Sound.beep(); //for debug purposes
          //odometer.setY((y0- (y0 % 1.0) * tileSize) );
    	  double yOdometer = odometer.getY();
    	  this.y0 = (double) ( yOdometer / tileSize);
    	  double roundY = Math.round(this.y0);
    	  odometer.setY(roundY * tileSize);
    	  System.out.println("thetaOdo = "+ thetaOdometer+" Yodo = "+roundY+"\n");
      }
      lineDetector.reset();
    }
  }
}