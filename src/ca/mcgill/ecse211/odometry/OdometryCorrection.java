package ca.mcgill.ecse211.odometry;

import ca.mcgill.ecse211.sensor.LineDetector;

/**
 * Created by Allison Mejia on 20/10/2017
 * Edited by Christophe Vauclair on 27/10/2017
 */

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

public class OdometryCorrection implements TimerListener{
  private static LineDetector lineDetector;
  private static final long CORRECTION_PERIOD = 10;
  private static Odometer odometer;
  private static double tileSize;
  private static double dist2wheel;
  private double x0, y0; //values returned by the odometer
  private double lineTreshold; //in cm = ~ 4.88 cm

  public OdometryCorrection(Odometer odometer, LineDetector lineDetector, double tileS, double dist2W){
    this.odometer = odometer;
    this.lineDetector = lineDetector;
    this.tileSize = tileS;
    this.dist2wheel = dist2W;
    this.x0 = 0;
    this.y0 = 0;
    this.lineTreshold = (double) 5/this.tileSize;
  }

  public void timedOut(){
    // Correct Odometer value if line is detected
    if(lineDetector.checkLine()){
      //range [0,12]
      this.x0 = (double) ( (odometer.getX()-dist2wheel)  / tileSize);
      this.y0 = (double) ( (odometer.getY()-dist2wheel) / tileSize);

      if( this.x0 % 1.0 <= lineTreshold && this.y0 % 1.0 <=this.lineTreshold ){
        // Do nothing, we crossed a corner

      }
      else if( this.x0 % 1.0 <= lineTreshold){
        // We crossed a vertical line
        odometer.setX(((this.x0 - (this.x0 % 1.0)) * tileSize) + dist2wheel );
      }
      else if( this.y0 % 1.0 <=lineTreshold){
        // We crossed a horizontal line
        odometer.setY(((y0- (y0 % 1.0)) * tileSize) + dist2wheel );
      }
    }
  }

  /* EVERYTHING BELOW THIS LINE IS DEPRECATED */

  // Helper method that returns when a line is crossed, n indicates number of values to keep for moving average
  //Created by Christophe 

  private static void detectLine(SampleProvider cs, float[] csData, int samplingFrequency, int threshold, int n){
    float[] csValues = new float[n];
    float movingAverage = 0;
    float lastMovingAverage = 0;
    float derivative = 0;
    int counter = 0;

    double Xo, Yo; //values returned by the odometer
    double lineTreshold = (double) 5/tileSize; //in cm = ~ 4.88 cm

    while(true){
      cs.fetchSample(csData, 0);

      // Shift values and add new value
      for(int i = n-1; i > 0; i--){
        csValues[i] = csValues[i-1];
      }
      csValues[0] = csData[0] * 1000;

      // Increment counter
      counter++;

      // Compute moving average and derivative only if first n values have been measured
      if(counter >= n){ 
        // If first time moving average is computed
        if(lastMovingAverage == 0){
          lastMovingAverage = csValues[n-1];
        }

        // Calculate the moving average
        movingAverage = lastMovingAverage + (csValues[0] - csValues[n-1])/n;

        // Calculate poor man's derivative
        derivative = movingAverage - lastMovingAverage;

        // Correct Odometer value if line is detected
        if(derivative > lineTreshold){
          //range [0,12]
          Xo = (double) ( (odometer.getX()-dist2wheel)  / tileSize);
          Yo = (double) ( (odometer.getY()-dist2wheel) / tileSize);

          if( Xo%1.0 <= lineTreshold && Yo%1.0 <=lineTreshold ){
            //do nothing, we crossed a corner

          }
          else if( Xo%1.0 <= lineTreshold){
            //we crossed a vertical line
            odometer.setX(((Xo - (Xo%1.0)) * tileSize) + dist2wheel );
          }
          else if( Yo%1.0 <=lineTreshold){
            //we crossed a horizonal line
            odometer.setY(((Yo- (Yo%1.0)) * tileSize) + dist2wheel );
          }
        }
      }

      try {
        Thread.sleep(samplingFrequency);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}