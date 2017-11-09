package ca.mcgill.ecse211.sensor;

/**
 * Created by Christophe Vauclair on 27/10/2017
 */

public class LineDetector {
  private LightSensor colorSensor;
  private int threshold;
  private float[] colorSensorValues;
  private float currentMovingAverage;
  private float lastMovingAverage;
  private float derivative;
  private int numberOfSamples;
  private int counter;
  private boolean arrayFilled;
  
  /**
   * Constructor for the Line detector object
   * @param colorSensor LightSensor
   * @param threshold int
   * @param n int
   */
  public LineDetector(LightSensor colorSensor, int threshold, int n){
    this.colorSensor = colorSensor;
    this.threshold = threshold;
    this.colorSensorValues = new float[n];
    this.currentMovingAverage = 0;
    this.lastMovingAverage = 0;
    this.derivative = 0;
    this.numberOfSamples = n;
    this.counter = 0;
    this.arrayFilled = false;
  }
  
  /**
   * Method that checks if a line was crossed (note that this method will always return false the
   * first 'numberOfSamples' times it is called as the method will wait to get this amount of data
   * points to calculate the moving average)
   * @return boolean
   */
  public boolean checkLine(){
    // Get new sample
    float sample = colorSensor.getSample();
    
    // Shift values and add new sample value
    for(int i = this.numberOfSamples-1; i > 0; i--){
        this.colorSensorValues[i] = this.colorSensorValues[i-1];
    }
    this.colorSensorValues[0] = sample * 1000;
    
    // Increment counter
    this.counter++;
//    if(this.counter == this.numberOfSamples){
//      this.counter = 0;
//      this.arrayFilled = true;
//    }
    
    // Compute moving average and derivative only if first n values have been measured
    if(this.counter >= this.numberOfSamples){ 
      // If first time moving average is computed
      if(this.lastMovingAverage == 0){
          this.lastMovingAverage = this.colorSensorValues[0];
      }

      // Calculate the moving average
      this.currentMovingAverage = this.lastMovingAverage + (this.colorSensorValues[0] - this.colorSensorValues[this.numberOfSamples-1])/this.numberOfSamples;
//      this.currentMovingAverage = this.lastMovingAverage 
//          + (this.colorSensorValues[(this.counter+1 == this.numberOfSamples ? 0 : this.counter+1)] 
//              - this.colorSensorValues[this.counter])/this.numberOfSamples;
      
      // Calculate poor man's derivative
      this.derivative = this.currentMovingAverage - this.lastMovingAverage;

      // Return true if line is detected
      if(this.derivative > this.threshold){
        return true;
      }
    }
    
    this.lastMovingAverage = this.currentMovingAverage;
    
    return false;
  }
}
