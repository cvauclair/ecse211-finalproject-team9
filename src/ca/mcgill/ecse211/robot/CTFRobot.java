package ca.mcgill.ecse211.robot;

import java.util.Map;

import ca.mcgill.ecse211.wifi.WifiConnection;

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
  private static final String SERVER_IP = "192.168.2.3";
  private static final int TEAM_NUMBER = 9;
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
  public static void main(){
    CTFRobot robot = new CTFRobot();
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

        //BELOW ARE EXAMPLES ON HOW TO USE DATA
        //
        //
        // Example 1: Print out all received data
        System.out.println("Map:\n" + data);

        // Example 2 : Print out specific values
        int redTeam = ((Long) data.get("RedTeam")).intValue();
        System.out.println("Red Team: " + redTeam);

        int og = ((Long) data.get("OG")).intValue();
        System.out.println("Green opponent flag: " + og);

        // Example 3: Compare value
        int sh_ll_x =  ((Long) data.get("SH_LL_x")).intValue();
        if (sh_ll_x < 5) {
          System.out.println("Shallow water LL zone X < 5");
        }
        else {
          System.out.println("Shallow water LL zone X >= 5");
        }

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
    robot.run();
  }
  /**
   * Constructor. CTDRRobot extends the Robot class
   */
  public CTFRobot(){
    super();
  }
  
  /**
   * Method that starts the game Capture-the-flag
   */
  public void run(){
    // Gameplay logic
  }
}
