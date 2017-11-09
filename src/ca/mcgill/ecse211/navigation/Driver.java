package ca.mcgill.ecse211.navigation;

import ca.mcgill.ecse211.odometry.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is used to move the robot and includes various methods to make controlling the wheel motors easier
 */
public class Driver {
  private Odometer odometer;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double wheelRadius;
  private double baseWidth;
  private double sizeOfRobot;
  private int forwardSpeed;
  private int rotateSpeed;
  
  /**
   * This creates a Driver instance for the robot
   * @param odometer 	an Odometer instance which is the robot's odometer
   * @param leftMotor 	a leftMotor instance which is the robot's left motor
   * @param rightMotor 	a rightMotor instance which is the robot's right motor
   * @param wheelRadius 	a double which is the radius of the robot's wheel
   * @param baseWidth 	a double which is the width of the robot's base (i.e.: the distance between its two wheels)
   */
  public Driver(Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius, double baseWidth){
    this.odometer = odometer;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.wheelRadius = wheelRadius;
    this.baseWidth = baseWidth;
    this.sizeOfRobot = 12; //12cm default value
    this.forwardSpeed = 250;    // Default value
    this.rotateSpeed = 150;     // Default value
  }
  
  /**
   *  Method to move the robot forward indefinitely
   */
  public void forward(){
    // Set forward speed
    this.setSpeed(this.forwardSpeed);
    
    // Move forward
    this.leftMotor.forward();
    this.rightMotor.forward();
  }
  
  /**
   * Method that makes the robot move backwad
   */
  public void backward(){
    // Set forward speed
    this.setSpeed(this.forwardSpeed);
    
    // Move forward
    this.leftMotor.backward();
    this.rightMotor.backward();
  }
  
  /**
   *  Method to stop both motors
   */
  public void stop(){
    // Stop motors
    this.leftMotor.stop(true);
    this.rightMotor.stop(false);
  }
  
  /**
   * Method to move the robot to the desired point (in cm)
   * @param x 	a double which is the x axis location (in cm)
   * @param y 	a double which is the y axis location (in cm)
   */
  public void travelTo(double x, double y){
    double deltaX = x - this.odometer.getX();
    double deltaY = y - this.odometer.getY();
    double deltaD = Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2));

//    System.out.println("X=" + odometer.getX() + " Y=" + odometer.getY());
//    System.out.println("deltaX=" + deltaX + " deltaY=" + deltaY + " deltaD=" + deltaD);
    
    // Determine target angle
    double theta = 0;
    if(deltaX >= 0 & deltaY >= 0){
        theta = (180/Math.PI) * Math.atan(deltaX/deltaY);
    }else if(deltaX >= 0 & deltaY < 0){
        theta = 90 + (180/Math.PI) * -Math.atan(deltaY/deltaX);
    }else if(deltaX < 0 & deltaY < 0){
        theta = 180 + (180/Math.PI) * Math.atan(deltaX/deltaY);
    }else{
        theta = 270 + (180/Math.PI) * -Math.atan(deltaY/deltaX);
    }
    
    // Turn theta orientation
    this.turnTo(theta);
    
    // Move forward by deltaD cm
    this.forward(deltaD, false);
  }
  
  /**
   * Method to move the robot forward by a set distance
   * @param distance		 a double which is the distance by which the robot is to be moved forward (in cm)
   */
  public void forward(double distance, boolean immediateReturn){
    // Set forward speed
    this.setSpeed(this.forwardSpeed);
    
    // Move forward
    this.leftMotor.rotate(convertDistance(this.wheelRadius, distance), true);
    this.rightMotor.rotate(convertDistance(this.wheelRadius, distance), immediateReturn);
  }
  
  /**
   * Method to turn the robot to an certain orientation
   * @param theta 	a double which is the target orientation
   */
  public void turnTo(double theta){
    double currentTheta = odometer.getTheta();
    double deltaT = 0;
    
    // Check which direction is the change in angle the smallest
    deltaT = theta - currentTheta;
    if(deltaT > 180){
      deltaT = deltaT-360;
    }
    
    this.turnBy(deltaT,false);
  }
  
  /**
   * Method to turn the robot by a certain angle
   * @param theta 			a double which is the angle by which the robot is to be turned
   * @param immediateReturn	a boolean which is a flag that indicates if the method should wait for the robot to be done turning
   * before returning
   */
  public void turnBy(double theta, boolean immediateReturn){
    // Set rotate speed
    this.setSpeed(this.rotateSpeed);
    
    // Rotate the robot
    this.leftMotor.rotate(convertAngle(this.wheelRadius, this.baseWidth, theta), true);
    this.rightMotor.rotate(-convertAngle(this.wheelRadius, this.baseWidth, theta), immediateReturn);
  }
  
  /**
   * Method to get the robot's left motor instance
   * @return leftMotor 	a leftMotor instance which is the robot's left motor
   */
  public EV3LargeRegulatedMotor getLeftMotor(){
    return this.leftMotor;
  }
  
  /**
   * Method to get the robot's right motor instance
   * @return rightMotor	a rightMotor instance which is the robot's right motor
   */
  public EV3LargeRegulatedMotor getRightMotor(){
    return this.rightMotor;
  }
  
  /**
   * Method to set the speed used by the robot when moving forward
   * @param forwardSpeed 	an int which is the forward speed of the robot
   */
  public void setForwardSpeed(int forwardSpeed){
    this.forwardSpeed = forwardSpeed;
  }
  
  /**
   * Method to get the speed used by the robot when moving forward
   * @return forwardSpeed 	an int which is the forward speed of the robot
   */
  public int getForwardSpeed(){
    return this.forwardSpeed;
  }
  
  /**
   * Method to set the speed used by the robot when rotating on its center of rotation
   * @param rotateSpeed		an int which is the rotation speed of the robot
   */
  public void setRotateSpeed(int rotateSpeed){
    this.rotateSpeed = rotateSpeed;
  }
  
  /**
   * Method to directly set the speed of the robot's motors (does not overwrite forwardSpeed or rotateSpeed)
   * @param speed 	an int which is the speed of the motors
   */
  public void setSpeed(int speed){
    this.leftMotor.setSpeed(speed);
    this.rightMotor.setSpeed(speed);
  }
  
  /**
   * Method to convert the distance to travel into wheel rotations
   * @param radius		a double which is the radius of the wheel
   * @param distance		a double which is is the distance to be traveled by the wheel (in cm)
   * @return rotations	an int which is the amount of rotations (in degrees) the wheel needs 
   * to rotate by to move forward by 'distance'
   */
  private static int convertDistance(double radius, double distance) {
    // Wheel rotation in degrees = 360 * distance/circumference
	int rotations = (int) ((180.0 * distance) / (Math.PI * radius));
    return rotations;
  }

  /**
   * Method to convert the angle by which the robot needs to rotate into wheel rotations
   * @param radius		a double which is the radius of the wheel
   * @param width 		a double which is the width of the robot (i.e.: the distance between the two wheels)
   * @param angle 		a double which is the angle by which the robot needs to rotate
   * @return rotations	an int which is the amount of rotations (in degrees) the wheel 
   * needs to rotate by to rotate the robot by 'angle'
   */
  private static int convertAngle(double radius, double width, double angle) {
    // Distance each wheel needs to travel = circumference * angle/360 
    // (ie: each wheel needs to move by arc length of robot rotation)
	int rotations = convertDistance(radius, Math.PI * width * angle / 360.0);
    return rotations;
  }
  
  /**
   * Method that makes the robot to rotate Clockwise for an indefinite amount of time
   */
  public void justrotateClockwise(){ // tells the robot to keep rotating
      leftMotor.setSpeed(rotateSpeed);
      rightMotor.setSpeed(rotateSpeed);
      leftMotor.forward();
      rightMotor.backward();
	  }
}
