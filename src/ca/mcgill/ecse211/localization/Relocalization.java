package ca.mcgill.ecse211.localization;

import ca.mcgill.ecse211.navigation.Driver;
import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.LineDetector;
import lejos.hardware.Sound;

/**
 * This class relocalizes a robot at any point of the game board by collecting the 4 angles of 
 * the odometer for when the robot detects a line. This requires that the robot is close enough 
 * to a tile corner in order to get the 4 lines. Simple trigonometry is used to correct the 
 * values of the robot's odometer.
 * 
 */
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
	
	//physical values of gameboard
	private double tileSize;
	
	/**
	 * This creates an instance of Relocalization. It can relocalize no matter where the robot is on the game field
	 * @param odo 			an Odometer instance
	 * @param lineDetector 	a LineDetector instance
	 * @param drive 			a Drive instance
	 * @param tileS 			a double that represents the size of one tile`
	 */
	public Relocalization(Odometer odo, LineDetector lineDetector, Driver drive, double tileS){
		
		this.odometer = odo;
		this.lineDetector = lineDetector;
		this.driver = drive;
		this.tileSize = tileS;
		angles = new double[4];
		obtainAngle= new double[4];
		running = false;
		

	}
	
	 /*plan:
	   * 1) Determine by how much to move to go to node
	   * 2) Rotate robot to 45 degrees
	   * 3) fetch theta values to an array and use them to calculate the distance to 0,0
	   * 
	   * */
	
	/**
	 * Method that relocalizes the robot independently on the current point of the robot 
	 */
	public void doReLocalization(){
		int initialAngle = 45;
		int angle_index = 0;
		
		driver.turnTo(initialAngle);
		
		running = true;
		lineDetector.reset();
		while(running){
			driver.rotateClockwise();
			if(lineDetector.checkLine()){
				Sound.beep();
				double newangle = odometer.getTheta();
				if(angle_index == 0){
					angles[angle_index]= newangle;
					angle_index++;
				}
				else if(Math.abs(newangle - angles[angle_index -1] )> 45){
					angles[angle_index]= newangle;    // fetching angle values to the robot
					angle_index ++;
				}
			    if(angle_index == 4){
			    	driver.stop();
			    	wait(2000);
				   	excessangle= odometer.getTheta();
				    break;
			    }
			    wait(200);
			}
		}//end of while running loop
		
		//fetch odometer values
		double xOdo = odometer.getX() / tileSize;
		double yOdo = odometer.getY() / tileSize;
		//find line node at which we localized (not 0,0) 
		double xTile = round(xOdo);
		double yTile = round(yOdo);
		// calculating distance to 0,0 and fetching theta and distance value to navigation lab
		double ytheta= angles[3]-angles[1]; 
		double xtheta= angles[2]-angles[0];
		double extracorrection= excessangle- angles[3];
		double Xo= position(xtheta) + (xTile * tileSize) -1.0;
		double Yo= position(ytheta) + (yTile * tileSize);
		double thetaO= (angles[1]-angles[3])/2 -angles[2]+ extracorrection;  //or angle 1 - angle 3
        double correcttheta = odometer.getTheta()+ thetaO ;
        double thetaFinal =angleCorrection(correcttheta ) ;
        odometer.setPosition(new double[] {Xo,  Yo, thetaFinal},
        		new boolean[]{true, true, true});

		
	}
	
	/**
	 * Helper function to calculate the correction of the x or y value of the odometer
	 * @param angle 			a double that is the current angle
	 * @return position		a double that is the corrected position
	 */
	private double position(double angle) {     // mehod to find the position
		
		double position = -sizeofRobot *Math.cos((Math.toRadians(angle)/2));
	    return position;
	        
	}
	

	/**
	 * This method corrects the theta value of the odometer
	 * @param angle 			a double that is the current angle
	 * @return correction 	a double that needs to be provided to the angle to correct it
	 */
	private static double angleCorrection(double angle){  
	    if (angle > 360) {
	      return angle - 360;
	    } else if (angle < 0) {
	      return angle + 360;
	    } else {
	      return angle;
	    }
	    
	}
	
	/**
	 * Helper function that tells the robot to wait
	 * @param time		an int for time to sleep, measured in milliseconds
	 */
	private static void wait(int time){ 
		    try {
	          Thread.sleep(time);
	      } catch (InterruptedException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	      }
	}
	
	/**
	 * method that rounds a double n 
	 * @param n 			a double to round the number
	 * @return rounded 	a double which is the rounded number
	 */
	private static double round(double n){
		double rounded = 0.0;
		int roundedAsInt = (int) (n + 0.5);
		rounded = (double) roundedAsInt;
		
		return rounded;
	}
	
	
}