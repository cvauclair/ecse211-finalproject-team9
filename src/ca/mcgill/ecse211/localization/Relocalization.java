package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.Sound;


public class Relocalization {
	
	// implementing color sensor
	private LineDetector lineDetector;
	
	//Physical measurements of robot
	private static double sizeofRobot = 12.0;
	//implementing arrays
	private static double[] angles;
	public static double[] obtainAngle;
	
	
	//setting up variables
	public static double ytheta;
	public static double xtheta;
	public static double excessangle;
	private static boolean running;
	// implementing classes
	private Odometer odometer;
	private Driver driver;

	
	public Relocalization(Odometer odo, LineDetector lineDetector, Driver drive){
		
		this.odometer = odo;
		this.lineDetector = lineDetector;
		this.driver = drive;
		angles = new double[4];
		obtainAngle= new double[4];
		running = false;
		

	}
	
	 /*plan:
	   * 1) Determine by how much to move to go to node
	   * 2) Rotate robot to 45 degrees
	   * 3) fetch theta values to an array and use them to calculate the distance to 0,0
	   * 4) navigate to 0,0 and turn to 0 degrees
	   * 
	   * */
	
	public void doRandomLocalization(){
		int initialAngle = 45;
		int angle_index = 0;
		
		driver.turnTo(initialAngle);
		
		running = true;
		
		while(running){
			driver.justrotateClockwise();
			if(lineDetector.checkLine()){
				Sound.beep();
				angles[angle_index]= odometer.getTheta();    // fetching angle values to the robot
			    angle_index ++;
			    if(angle_index == 4){
			    	driver.stop();
			    	wait(2000);
				   	excessangle= odometer.getTheta();
				    break;
			    }
			    wait(400);
			}
		}//end of while running loop
		
		// calculating distance to 0,0 and fetching theta and distance value to navigation lab
		double ytheta= angles[1]-angles[3]; 
		double xtheta= angles[0]-angles[2];
		double extracorrection= excessangle- angles[3];
		
	}
	
	/**
	 * Find the correct position for either x or y 
	 * @param angle
	 * @return
	 */
	private double position(double angle) {     // mehod to find the position
        double position = -sizeofRobot *Math.cos((Math.toRadians(angle)/2));
        return position;
        
    }
	
	/**
	 * Tells the robot to wait
	 * @param time
	 */
	private static void wait(int time){ 
		    try {
	          Thread.sleep(time);
	      } catch (InterruptedException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      }
	}
	
	
}