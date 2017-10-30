package ca.mcgill.ecse211.odometry;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.TimerListener;

/**
 *  Edited by Christophe Vauclair on 27/10/2017 
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
    this.lastLeftMotorTachoCount = 0;
    this.lastRightMotorTachoCount = 0;

    this.x = 0;
    this.y = 0;
    this.theta = 0.0;
    
    lock = new Object();
  }

  // run method (required for Thread)
  public void timedOut() {
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    this.lastLeftMotorTachoCount = this.leftMotor.getTachoCount();
    this.lastRightMotorTachoCount = this.rightMotor.getTachoCount();

    // Update motor tacho counts
    this.leftMotorTachoCount = this.leftMotor.getTachoCount();
    this.rightMotorTachoCount = this.rightMotor.getTachoCount();

    // Calculate distances travelled by right and left wheels
    this.distL = 3.14159 * wheelRadius * (this.leftMotorTachoCount - this.lastLeftMotorTachoCount)/180;
    this.distR = 3.14159 * wheelRadius * (this.rightMotorTachoCount - this.lastRightMotorTachoCount)/180;

    // Update last moto tacho counts
    this.lastLeftMotorTachoCount = this.leftMotorTachoCount;
    this.lastRightMotorTachoCount = this.rightMotorTachoCount;

    // Calculate distance travelled by robot (average of distance travelled by both wheels)
    this.deltaD = 0.5 * (this.distL + this.distR);

    // Calculate orientation of robot and convert it to degrees
    this.deltaT = (180/Math.PI) * (this.distL-this.distR)/wheelBase; // Calculate variation of theta and convert it to degrees

    // Calculate the new value of theta
    this.newTheta = this.getTheta() + this.deltaT;

    // Adjust the new value of theta so it stays between 0 and 359.9
    if(this.newTheta > 359.9){
      this.newTheta -= 359.9;
    }else if(this.newTheta < 0){
      this.newTheta += 359.9;
    }

    // Calculate the new values of x and y
    this.newX = this.getX() + this.deltaD * Math.sin(this.newTheta * (Math.PI/180));
    this.newY = this.getY() + this.deltaD * Math.cos(this.newTheta * (Math.PI/180));

    synchronized (lock) {
      this.theta = this.newTheta;
      this.x = this.newX;
      this.y = this.newY;
    }
  }

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

  public double getX() {
    double result;

    synchronized (lock) {
      result = x;
    }

    return result;
  }

  public double getY() {
    double result;

    synchronized (lock) {
      result = y;
    }

    return result;
  }

  public double getTheta() {
    double result;

    synchronized (lock) {
      result = theta;
    }

    return result;
  }

  // mutators
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

  public void setX(double x) {
    synchronized (lock) {
      this.x = x;
    }
  }

  public void setY(double y) {
    synchronized (lock) {
      this.y = y;
    }
  }

  public void setTheta(double theta) {
    synchronized (lock) {
      this.theta = theta;
    }
  }

  /**
   * @return the leftMotorTachoCount
   */
  public int getLeftMotorTachoCount() {
    return leftMotorTachoCount;
  }

  /**
   * @param leftMotorTachoCount the leftMotorTachoCount to set
   */
  public void setLeftMotorTachoCount(int leftMotorTachoCount) {
    synchronized (lock) {
      this.leftMotorTachoCount = leftMotorTachoCount;
    }
  }

  /**
   * @return the rightMotorTachoCount
   */
  public int getRightMotorTachoCount() {
    return rightMotorTachoCount;
  }

  /**
   * @param rightMotorTachoCount the rightMotorTachoCount to set
   */
  public void setRightMotorTachoCount(int rightMotorTachoCount) {
    synchronized (lock) {
      this.rightMotorTachoCount = rightMotorTachoCount;
    }
  }
}
