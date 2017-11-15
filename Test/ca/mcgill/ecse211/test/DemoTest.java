package ca.mcgill.ecse211.test;

import java.util.Map;
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.Relocalization;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.CollisionAvoidance;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.robot.Robot;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import ca.mcgill.ecse211.wifi.WifiConnection;
import ca.mcgill.ecse211.zipline.ZiplineControl;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

public class DemoTest extends Robot{
  private int corner = 0;
  private int ziplineX0 = 0;
  private int ziplineY0 = 0;
  private int ziplineXc = 0;
  private int ziplineYc = 0;
  private int flagZoneX = 0;
  private int flagZoneY = 0;

  // ** Set these as appropriate for your team and current situation **
//  private static final String SERVER_IP = "192.168.2.3";
  private static final String SERVER_IP = "192.168.2.3";
  private static final int TEAM_NUMBER = 9;

  // Enable/disable printing of debug info from the WiFi class
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

  public static void main(String args[]){
    (new DemoTest()).run();
  }

  public DemoTest(){
    super();

    WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

    // Connect to server and get the data, catching any errors that might occur
    try {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
       * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
       * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
       * but receives corrupted data or a message from the server saying something went wrong. For
       * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
       * will receive a message saying an invalid team number was specified and getData() will throw
       * an exception letting you know.
       */
      Map data = conn.getData();

      // Example 1: Print out all received data
//      System.out.println("Map:\n" + data);

      // Example 2 : Print out specific values
//      int redTeam = ((Long) data.get("RedTeam")).intValue();
//      System.out.println("Red Team: " + redTeam);

      // Read team specific data
      if(((Long) data.get("RedTeam")).intValue() == TEAM_NUMBER){
        corner = ((Long) data.get("RedCorner")).intValue();
        ziplineX0 = ((Long) data.get("ZO_R_x")).intValue();
        ziplineY0 = ((Long) data.get("ZO_R_y")).intValue();
        ziplineXc = ((Long) data.get("ZC_R_x")).intValue();
        ziplineYc = ((Long) data.get("ZC_R_y")).intValue();
      }else if(((Long) data.get("GreenTeam")).intValue() == TEAM_NUMBER){
        corner = ((Long) data.get("GreenCorner")).intValue();
        ziplineX0 = ((Long) data.get("ZO_G_x")).intValue();
        ziplineY0 = ((Long) data.get("ZO_G_y")).intValue();
        ziplineXc = ((Long) data.get("ZC_G_x")).intValue();
        ziplineYc = ((Long) data.get("ZC_G_y")).intValue();        
      }
      
//      int og = ((Long) data.get("OG")).intValue();
//      System.out.println("Green opponent flag: " + og);

      // Example 3: Compare value
//      int sh_ll_x =  ((Long) data.get("SH_LL_x")).intValue();
//      if (sh_ll_x < 5) {
//        System.out.println("Shallow water LL zone X < 5");
//      }
//      else {
//        System.out.println("Shallow water LL zone X >= 5");
//      }

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

  }

  public void run(){
//    super.run();

    UltrasonicSensor usSensor = new UltrasonicSensor("S1", "Distance");
    LightSensor lightSensor = new LightSensor("S2", "Red");
    LightSensor backLightSensor = new LightSensor("S4", "Red");

    LineDetector lineDetector = new LineDetector(lightSensor, -20, 8);
    LineDetector backLineDetector = new LineDetector(backLightSensor, -47, 8);

    EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
    EV3LargeRegulatedMotor ziplineMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
    EV3MediumRegulatedMotor usSensorMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));

    Odometer odometer = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);
    OdometryCorrection odometryCorrection = new OdometryCorrection(odometer,lineDetector,SQUARE_WIDTH,0);

    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);

    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,35);
    Navigation navigation = new Navigation(driver, collisionAvoidance);

    Relocalization relocalization = new Relocalization(odometer, backLineDetector, driver, SQUARE_WIDTH);

    UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer, driver, usSensor);
    LightLocalizer lightLocalizer = new LightLocalizer(odometer, driver, lineDetector);

    ZiplineControl ziplineControl = new ZiplineControl(ziplineMotor, driver);

    //    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
    //    collisionAvoidanceTimer.start();


    Timer lineDetectorTimer = new Timer(50,lineDetector);
    lineDetectorTimer.start();

    Timer backLineDetectorTimer = new Timer(50,backLineDetector);
    backLineDetectorTimer.start();

    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

    //  Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
    //  odometryCorrectionTimer.start();

    switch(corner){
      case 0:
        // Robot starts in bottom left corner
        usLocalizer.localize(0);
        
        // Start light localizer
        lightLocalizer.localize(1*SQUARE_WIDTH,1*SQUARE_WIDTH);
        
        odometer.setTheta(0);
        break;
      case 1:
        // Robot starts in bottom left corner
        usLocalizer.localize(0);
        
        // Start light localizer
        lightLocalizer.localize(7*SQUARE_WIDTH,1*SQUARE_WIDTH);
        
        odometer.setTheta(270);
        break;
      case 2:
        // Robot starts in bottom left corner
        usLocalizer.localize(0);
        
        // Start light localizer
        lightLocalizer.localize(7*SQUARE_WIDTH,7*SQUARE_WIDTH);
        
        odometer.setTheta(180);
        break;
      case 3:
        // Robot starts in bottom left corner
        usLocalizer.localize(0);
        
        // Start light localizer
        lightLocalizer.localize(1*SQUARE_WIDTH,7*SQUARE_WIDTH);
        
        odometer.setTheta(90);
        break;
      default:
        break;
    }

    
//    usLocalizer.localize(0);
//    lightLocalizer.localize(1 * SQUARE_WIDTH, 1 * SQUARE_WIDTH);

    driver.setForwardSpeed(200);

    driver.travelTo(ziplineX0 * SQUARE_WIDTH, ziplineY0 * SQUARE_WIDTH);
//    driver.forward(-10, false);
//    driver.turnBy(-90, false);
//    driver.forward(SQUARE_WIDTH/3,false);
//    driver.turnBy(90, false);
//    lightLocalizer.localize(ziplineX0 * SQUARE_WIDTH, ziplineY0 * SQUARE_WIDTH);
    
//    relocalization.doReLocalization();
//    driver.turnTo(0);

    driver.travelTo(ziplineXc * SQUARE_WIDTH, ziplineYc * SQUARE_WIDTH);

    ziplineControl.traverseZipline();
    
//    relocalization.doReLocalization();
  }
}
