package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Timer;

public class OdometerTest extends Robot {
  public static void main(String args[]){
    (new OdometerTest()).run();
  }

  public OdometerTest(){
    super();
  }

  public void run(){
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 50, 8);

    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, LocalEV3.get().getTextLCD());

    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);

    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

    odometryDisplay.start();
    
    driver.travelTo(0 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);
    driver.travelTo(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);
    driver.travelTo(1 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
    driver.travelTo(0 * SQUARE_WIDTH, 0 * SQUARE_WIDTH);
  }
}
