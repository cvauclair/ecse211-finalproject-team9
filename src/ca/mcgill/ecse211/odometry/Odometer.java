package ca.mcgill.ecse211.odometry;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.TimerListener;

/*
 *  Edited by Christophe Vauclair on 27/10/2017 
 */

/**
 * This class represents an Odometer that keeps track of the position of a robot.
 * It uses the robot's tachometer to keep track of the wheels rotation
 * and uses this data to calculate the current distance achieved by the robot
 * and the angle at which it is going.
 *
 */
public class Odometer implements TimerListener {
  // Static attributes
  private static EV3LargeRegulatedMotor leftMotor;
  private static EV3LargeRegulatedMotor rightMotor;
  private static double wheelRadius;    // Wheel radius in cm
  private static double wheelBase;          // Wheel base in cm
  
  // Odometer calculations related values (declared as attributes so as to not reallocate the 
  // memory every update cycle)
  private double distL;     // Distance traveled by left wheel
  private double distR;     // Distance traveled by right wheel
  private double deltaD;    // Total displacement of robot
  private double deltaT;    // Change in heading
  private double newX;
  private double newY;
  private double newTheta;
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  private int lastLeftMotorTachoCount;
  private int lastRightMotorTachoCount;

  // Actual odometer values
  private double x;  
  private double y;
  private double theta;

  // Thread safety lock
  private Object lock; /*lock object for mutual exclusion*/

  /**
   * This creates an Odometer object configured for a particular robot setup
   * @param leftMotor 	a leftMotor instance which is the robot's left motor
   * @param rightMotor 	a rightMotor instance which is the robot's right motor
   * @param wheelRadius 	a double which is the radius of the robot's wheel
   * @param wheelBase 	a double which is the width of the robot's base (i.e.: the distance between its two wheels)
   */
  public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius, double wheelBase) {
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.wheelRadius = wheelRadius;
    this.wheelBase = wheelBase;

    this.distL = 0;
    this.distR = 0;
    this.deltaT = 0;
    this.newX = 0;
    this.newY = 0;
    this.newTheta = 0;
    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;
    
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    
    this.lastLeftMotorTachoCount = this.leftMotor.getTachoCount();
    this.lastRightMotorTachoCount = this.rightMotor.getTachoCount();
    
    this.x = 0;
    this.y = 0;
    this.theta = 0.0;
    
    lock = new Object();
  }

  /**
   * This method updates the Odometer's values by reading the tachometer values of the motors and comparing them to 
   * the last measurements.
   */
  public void timedOut() {    
    // Update motor tacho counts
    this.leftMotorTachoCount = this.leftMotor.getTachoCount();
    this.rightMotorTachoCount = this.rightMotor.getTachoCount();

    // Calculate distances travelled by right and left wheels
    this.distL = Math.PI * wheelRadius * (this.leftMotorTachoCount - this.lastLeftMotorTachoCount)/180;
    this.distR = Math.PI * wheelRadius * (this.rightMotorTachoCount - this.lastRightMotorTachoCount)/180;

    // Update last moto tacho counts
    this.lastLeftMotorTachoCount = this.leftMotorTachoCount;
    this.lastRightMotorTachoCount = this.rightMotorTachoCount;

    // Calculate distance travelled by robot (average of distance travelled by both wheels)
    this.deltaD = 0.5 * (this.distL + this.distR);

    // Calculate orientation of robot and convert it to degrees
    this.deltaT = Math.toDegrees((this.distL-this.distR)/wheelBase); // Calculate variation of theta and convert it to degrees

    // Calculate the new value of theta
    this.newTheta = this.getTheta() + this.deltaT;

    // Adjust the new value of theta so it stays between 0 and 359.9
    if(this.newTheta > 359.9){
      this.newTheta -= 359.9;
    }else if(this.newTheta < 0){
      this.newTheta += 359.9;
    }

    // Calculate the new values of x and y
    this.newX = this.getX() + this.deltaD * Math.sin(Math.toRadians(this.newTheta));
    this.newY = this.getY() + this.deltaD * Math.cos(Math.toRadians(this.newTheta));

    synchronized (lock) {
      this.theta = this.newTheta;
      this.x = this.newX;
      this.y = this.newY;
    }
  }

  /**
   * This method fetches the values of the Odometer 
   * @param position		a double array of size three that will be filled with the x, y and theta values of the odometer
   * @param update 		a boolean array of size three that indicates whether each value of the position array should be updated
   */
  public void getPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        position[0] = x;
      if (update[1])
        position[1] = y;
      if (update[2])
        position[2] = theta;
    }
  }

  /**
   * This method gets the Odometer's x value in a thread safe way
   * @return result 		a double that is the Odometer's x value
   */
  public double getX() {
    double result;

    synchronized (lock) {
      result = x;
    }

    return result;
  }

  /**
   * This method gets the Odometer's y value in a thread safe way
   * @return result		a double that is the Odometer's y value 
   */
  public double getY() {
    double result;

    synchronized (lock) {
      result = y;
    }

    return result;
  }
  
  /**
   * This method gets the Odometer's theta value in a thread safe way
   * @return result		a double that is the Odometer's theta value 
   */
  public double getTheta() {
    double result;

    synchronized (lock) {
      result = theta;
    }

    return result;
  }

  /**
   * This method sets one or many odometer value
   * @param position 		an array of double of size three containing the new x, y and theta values of the Odometer
   * @param update 			a boolean array indicating which value of the position array should be set on the Odometer
   */
  public void setPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        x = position[0];
      if (update[1])
        y = position[1];
      if (update[2])
        theta = position[2];
    }
  }

  /**
   * This method sets the Odometer's x value in a thread safe way
   * @param x 		a double that is the Odometer's new x value
   */
  public void setX(double x) {
    synchronized (lock) {
      this.x = x;
    }
  }

  /**
   * This method sets the Odometer's y value in a thread safe way
   * @param y 		a double that is the Odometer's new y value
   */
  public void setY(double y) {
    synchronized (lock) {
      this.y = y;
    }
  }

  /**
   * This method sets the Odometer's theta value in a thread safe way
   * @param theta 	a double that is the Odometer's new theta value
   */
  public void setTheta(double theta) {
    synchronized (lock) {
      this.theta = theta;
    }
  }

  /**
   * This method returns the left motor's tachometer value
   * @return leftMotorTachoCount		an int that is the left motor's tachometer value
   */
  public int getLeftMotorTachoCount() {
    return leftMotorTachoCount;
  }

  /**
   * This method sets the left motor's tachometer value in a thread safe way
   * @param leftMotorTachoCount 		an int that is the left motor's tachometer value
   */
  public void setLeftMotorTachoCount(int leftMotorTachoCount) {
    synchronized (lock) {
      this.leftMotorTachoCount = leftMotorTachoCount;
    }
  }

  /**
   * This method returns the right motor's tachometer value
   * @return rightMotorTachoCount		an int that is the right motor's tachometer value
   */
  public int getRightMotorTachoCount() {
    return rightMotorTachoCount;
  }

  /**
   * This method sets the right motor's tachometer value in a thread safe way
   * @param rightMotorTachoCount 		an int that is the right motor's tachometer value
   */
  public void setRightMotorTachoCount(int rightMotorTachoCount) {
    synchronized (lock) {
      this.rightMotorTachoCount = rightMotorTachoCount;
    }
  }
}
