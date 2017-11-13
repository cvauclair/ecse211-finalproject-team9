package ca.mcgill.ecse211.test;

import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.Relocalization;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.CollisionAvoidance;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import ca.mcgill.ecse211.zipline.ZiplineControl;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

public class ZiplineControlTest extends Robot{
  public static void main(String args[]){
    (new DemoTest()).run();
  }

  public ZiplineControlTest(){
    super();
  }

  public void run(){
    super.run();

    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LineDetector lineDetector = new LineDetector(lightSensor, 50, 8);

    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    EV3LargeRegulatedMotor ziplineMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
    EV3MediumRegulatedMotor usSensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));

    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);

    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);

    ZiplineControl ziplineControl = new ZiplineControl(ziplineMotor, driver);

    //    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
    //    collisionAvoidanceTimer.start();

    Timer lineDetectorTimer = new Timer(50,lineDetector);
    lineDetectorTimer.start();

    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

    ziplineControl.traverseZipline();
  }
}
