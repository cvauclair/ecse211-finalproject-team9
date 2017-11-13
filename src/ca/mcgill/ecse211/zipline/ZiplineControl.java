package ca.mcgill.ecse211.zipline;

import ca.mcgill.ecse211.navigation.Driver;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class ZiplineControl {
  private static EV3LargeRegulatedMotor pulleyMotor;
  private static Driver driver;
  
  public ZiplineControl(EV3LargeRegulatedMotor pulleyMotor, Driver driver){
    this.pulleyMotor = pulleyMotor;
    this.driver = driver;
  }
  
  public void traverseZipline(){
    // Driver a bit forward to help the pulley get on zipline
    driver.setForwardSpeed(150);
    driver.forward();
    
    // Note: rotations are negative because motor is built the wrong way
    // Rotate slowly for the first third of the rotations 
    pulleyMotor.setSpeed(80);
    pulleyMotor.rotate(-2160);
    
    // Rotate quicker for the second third of the rotations
    pulleyMotor.setSpeed(120);
    pulleyMotor.rotate(-2160);
    
    // Rotate slowly for the last third of the rotations
    pulleyMotor.setSpeed(80);
    pulleyMotor.rotate(-2160);

    // Move a bit forward to move away from zipline
    driver.forward(2,false);
    driver.setForwardSpeed(120);
  }
}
