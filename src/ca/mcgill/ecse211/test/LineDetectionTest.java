package ca.mcgill.ecse211.test;

import java.util.concurrent.TimeUnit;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class LineDetectionTest extends Robot{
  public static void main(String args[]){
    (new LineDetectionTest()).run();
  }
  
  public LineDetectionTest(){
    super();
  }
  
  public void run(){
    super.run();
    
    LightSensor lightSensor = new LightSensor("S4", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 50, 8);
    
    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
   
    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    
    driver.forward(3 * SQUARE_WIDTH, true);
    
    while(true){
      if(lineDetector.checkLine()){
        Sound.beep();
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
