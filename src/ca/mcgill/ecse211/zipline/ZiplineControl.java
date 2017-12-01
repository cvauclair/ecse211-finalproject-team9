package ca.mcgill.ecse211.zipline;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class handles the procedure to get on and traverse the zipline. It uses a Driver object to 
 * mount and the robot on and off the zipline and uses a reference to the pulley's motor to traverse 
 * the zipline. The amount of rotation the class is telling the pulley motor to turn by was chosen by
 * dividing the length of the zipline by the circumference of the pulley and then increasing that value 
 * by around 25% to account for any slippage or delays in mounting the zipline. 
 */
public class ZiplineControl {
  private static EV3LargeRegulatedMotor pulleyMotor;
  private static Driver driver;
  private Odometer odometer;
  
  /**
   * The constructor takes as input a reference to the pulley's motor and a reference to the robot's 
   * driver and odometer
   * 
   * @param pulleyMotor
   * @param driver
   * @param odometer
   */
  public ZiplineControl(EV3LargeRegulatedMotor pulleyMotor, Driver driver, Odometer odometer){
    this.pulleyMotor = pulleyMotor;
    this.driver = driver;
    this.odometer = odometer;
  }
  
  /**
   * This method is to be called when the robot is at the zipline mount point and is facing the zipline.
   * It will attempt to cross the zipline by powering both the wheel motors and the pulley's motor at the same
   * time until the pulley has turned a fixed number of turns, by which the robot will have (hopefully) reached
   * the end of the zipline.
   */
  public void traverseZipline(){
    // Driver a bit forward to help the pulley get on zipline
    driver.setForwardSpeed(200);
    driver.forward();
    
    // Note: rotations are negative because motor is built the wrong way
    // Rotate slowly for the first third of the rotations 
    pulleyMotor.setSpeed(80); //80
    pulleyMotor.rotate(-2160);
    
    // Rotate quicker for the second third of the rotations
    pulleyMotor.setSpeed(120); //120
    pulleyMotor.rotate(-2160);
    
    // Rotate slowly for the last third of the rotations
    pulleyMotor.setSpeed(80);
    pulleyMotor.rotate(-2160);

    // Move a bit forward to move away from zipline
    driver.forward(2,false);
    driver.setForwardSpeed(80); //120
  }
}
