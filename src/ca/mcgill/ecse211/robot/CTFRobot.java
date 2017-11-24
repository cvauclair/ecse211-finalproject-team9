package ca.mcgill.ecse211.robot;

import java.util.Map;
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.Relocalization;
import ca.mcgill.ecse211.localization.UltrasonicLocalizer;
import ca.mcgill.ecse211.navigation.CollisionAvoidance;
import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.objectdetection.ObjectDetection;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.odometry.OdometryCorrection;
import ca.mcgill.ecse211.odometry.OdometryDisplay;
import ca.mcgill.ecse211.sensor.LightSensor;
import ca.mcgill.ecse211.sensor.LineDetector;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import ca.mcgill.ecse211.wifi.WifiConnection;
import ca.mcgill.ecse211.zipline.ZiplineControl;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.utility.Timer;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */
/**
 * This class creates and instance of a robot that is able to play 
 * capture the flag. It contains one thread that should contain all the 
 * logic for successfully doing the competition.
 *
 */
public class CTFRobot extends Robot{
  boolean bridgeFirst = false;
  
  private int corner= 1;
  private int startingPoint[] = {0,0};
  private int startingAngle = 0;
  
  private int greenLLx = 0;   // Green zone lower left corner x value
  private int greenLLy = 0;   // Green zone lower left corner y value
  
  private int greenURx = 0;   // Green zone upper right corner x value
  private int greenURy = 0;   // Green zone upper right corner y value

  private int searchZoneLL[] = {0,0};
  private int searchZoneUR[] = {0,0};
  
  private int zipline0Red[] = {0,0};
  private int ziplineCRed[] = {0,0};

  private int zipline0Green[] = {3,4};
  private int ziplineCGreen[] = {3,5};
    
  private int hCrossingLL[] = {8,9};
  private int hCrossingUR[] = {11,10};
  private int vCrossingLL[] = {10,5};
  private int vCrossingUR[] = {11,10};
  
  private int flag_color = 0;  // Opponent's flag color
  
  private static final String SERVER_IP = "192.168.2.49";
  private static final int TEAM_NUMBER = 9;

  private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;

  public static void main(String args[]){
    (new CTFRobot()).run();
  }
  /**
   * Constructor. CTDRRobot extends the Robot class
   */
  public CTFRobot(){
    super();
    WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);
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

      //This contains all parameters
      //List of all parameters in System Doc
      Map data = conn.getData();

      // Read team specific data
    if(((Long) data.get("RedTeam")).intValue() == TEAM_NUMBER){
      this.bridgeFirst = true;
      this.corner = ((Long) data.get("RedCorner")).intValue();
      
      this.searchZoneLL[0] = ((Long) data.get("SG_LL_x")).intValue();
      this.searchZoneLL[1] = ((Long) data.get("SG_LL_y")).intValue();
      this.searchZoneUR[0] = ((Long) data.get("SG_UR_x")).intValue();
      this.searchZoneUR[1] = ((Long) data.get("SG_UR_y")).intValue();
      
      this.flag_color = ((Long) data.get("OR")).intValue();
      
    }else if(((Long) data.get("GreenTeam")).intValue() == TEAM_NUMBER){
      this.bridgeFirst = false;
      
      this.corner = ((Long) data.get("GreenCorner")).intValue();
          
      this.searchZoneLL[0] = ((Long) data.get("SR_LL_x")).intValue();
      this.searchZoneLL[1] = ((Long) data.get("SR_LL_y")).intValue();
      this.searchZoneUR[0] = ((Long) data.get("SR_UR_x")).intValue();
      this.searchZoneUR[1] = ((Long) data.get("SR_UR_y")).intValue();
      
      this.flag_color = ((Long) data.get("OR")).intValue();
    }
    // Get zipline points in red zone
    this.zipline0Red[0] = ((Long) data.get("ZO_R_x")).intValue();
    this.zipline0Red[1] = ((Long) data.get("ZO_R_y")).intValue();
    this.ziplineCRed[0] = ((Long) data.get("ZC_R_x")).intValue();
    this.ziplineCRed[1] = ((Long) data.get("ZC_R_y")).intValue();
    
    // Get zipline points in green zone
    this.zipline0Green[0] = ((Long) data.get("ZO_G_x")).intValue();
    this.zipline0Green[1] = ((Long) data.get("ZO_G_y")).intValue();
    this.ziplineCGreen[0] = ((Long) data.get("ZC_G_x")).intValue();
    this.ziplineCGreen[1] = ((Long) data.get("ZC_G_y")).intValue();
    
    // Get horizontal crossing data
    this.hCrossingLL[0] = ((Long) data.get("SH_LL_x")).intValue();
    this.hCrossingLL[1] = ((Long) data.get("SH_LL_y")).intValue();
    this.hCrossingUR[0] = ((Long) data.get("SH_UR_x")).intValue();
    this.hCrossingUR[1] = ((Long) data.get("SH_UR_y")).intValue();
    
    // Get vertical crossing data
    this.vCrossingLL[0] = ((Long) data.get("SV_LL_x")).intValue();
    this.vCrossingLL[1] = ((Long) data.get("SV_LL_y")).intValue();
    this.vCrossingUR[0] = ((Long) data.get("SV_UR_x")).intValue();
    this.vCrossingUR[1] = ((Long) data.get("SV_UR_y")).intValue();
        
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  /**
   * Method that starts the game Capture-the-flag
   */
  public void run(){
    super.run();

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
    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, LocalEV3.get().getTextLCD());

    Driver driver = new Driver(odometer, leftMotor, rightMotor, WHEEL_RADIUS, BASE_WIDTH);

    CollisionAvoidance collisionAvoidance = new CollisionAvoidance(driver,odometer,usSensor,usSensorMotor,3,35);
    Navigation navigation = new Navigation(driver, collisionAvoidance);

    Relocalization relocalization = new Relocalization(odometer, backLineDetector, driver, SQUARE_WIDTH);

    UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(odometer, driver, usSensor);
    LightLocalizer lightLocalizer = new LightLocalizer(odometer, driver, lineDetector, backLineDetector);

    ZiplineControl ziplineControl = new ZiplineControl(ziplineMotor, driver, odometer);

    ObjectDetection objectDetection = new ObjectDetection(driver,odometer,usSensor,lightSensor);
    
    //    Timer collisionAvoidanceTimer = new Timer(50, collisionAvoidance);
    //    collisionAvoidanceTimer.start();


    Timer lineDetectorTimer = new Timer(50,lineDetector);
    lineDetectorTimer.start();

    Timer backLineDetectorTimer = new Timer(50,backLineDetector);
    backLineDetectorTimer.start();

    Timer odometerTimer = new Timer(50, odometer);
    odometerTimer.start();

    odometryDisplay.start();

    //  Timer odometryCorrectionTimer = new Timer(50, odometryCorrection);
    //  odometryCorrectionTimer.start();

    switch(corner){
      case 0:
        this.startingPoint[0] = 1;
        this.startingPoint[1] = 1;
        this.startingAngle = 0;
        break;
      case 1:
        this.startingPoint[0] = 11;
        this.startingPoint[1] = 1;
        this.startingAngle = 270;        // Robot starts in bottom left corner
        break;
      case 2:
        this.startingPoint[0] = 11;
        this.startingPoint[1] = 11;
        this.startingAngle = 180;
        break;
      case 3:
        this.startingPoint[0] = 1;
        this.startingPoint[1] = 11;
        this.startingAngle = 90;
        break;
      default:
        break;
    }

    // Robot starts in bottom left corner
    usLocalizer.localize(0);

    // Start light localizer
    lightLocalizer.localize(this.startingPoint[0] * SQUARE_WIDTH, this.startingPoint[1] * SQUARE_WIDTH);

    // Set actual angle
    odometer.setTheta(this.startingAngle);

    driver.setForwardSpeed(200);
    driver.setRotateSpeed(150);

    if(bridgeFirst){
      navigation.addPoint(this.hCrossingLL[0] * SQUARE_WIDTH, (this.hCrossingLL[1] + 0.5) * SQUARE_WIDTH);
      navigation.addPoint((this.hCrossingUR[0] - 0.5) * SQUARE_WIDTH, (this.hCrossingUR[1] - 0.5) * SQUARE_WIDTH);
      navigation.addPoint((this.vCrossingLL[0] + 0.5) * SQUARE_WIDTH, (this.hCrossingLL[1]-1) * SQUARE_WIDTH);
      navigation.navigate();

      // Go to search zone
//      objectDetection.findFlag(flag_color);
      
      navigation.addPoint(this.zipline0Green[0] * SQUARE_WIDTH, this.zipline0Green[1] * SQUARE_WIDTH);
      navigation.addPoint(this.ziplineCGreen[0] * SQUARE_WIDTH, this.ziplineCGreen[1] * SQUARE_WIDTH);
      navigation.navigate();
      ziplineControl.traverseZipline();
//      relocalization.doReLocalization();
    }else{
      navigation.addPoint(this.zipline0Green[0] * SQUARE_WIDTH, this.zipline0Green[1] * SQUARE_WIDTH);
      navigation.addPoint(this.ziplineCGreen[0] * SQUARE_WIDTH, this.ziplineCGreen[1] * SQUARE_WIDTH);
      navigation.navigate();
      ziplineControl.traverseZipline();
//      relocalization.doReLocalization();
      // Go to search zone
      
      objectDetection.findFlag(flag_color);
      
      navigation.addPoint(this.hCrossingLL[0] * SQUARE_WIDTH, (this.hCrossingLL[1] + 0.5) * SQUARE_WIDTH);
      navigation.addPoint((this.hCrossingUR[0] - 0.5) * SQUARE_WIDTH, (this.hCrossingUR[1] - 0.5) * SQUARE_WIDTH);
      navigation.addPoint((this.vCrossingLL[0] + 0.5) * SQUARE_WIDTH, (this.hCrossingLL[1]-1) * SQUARE_WIDTH);
      navigation.navigate();
    }
    
    navigation.addPoint(this.startingPoint[0] * SQUARE_WIDTH, this.startingPoint[1] * SQUARE_WIDTH);
    navigation.navigate();
  }
}
