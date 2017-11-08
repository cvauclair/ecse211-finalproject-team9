package ca.mcgill.ecse211.sensor;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */
/**
 * The line detector class is used to detect lines with the light sensor
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
   * @param colorSensor 		a LightSensor instance
   * @param threshold 		an int that specifies the threshold that needs to be obtained in order to consider  particlar data
   * @param n 				an int that specifies the number of samples collected from the sensor
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
   * @return wasCrossed		a boolean that is true if a line was crossed, false otherwise
   */
  public boolean checkLine(){
    // Get new sample
	boolean wasCrossed = false;
    float sample = colorSensor.getSample();
    System.out.println(sample * 1000);
    
    // Shift values and add new sample value
    for(int i = this.numberOfSamples-1; i > 0; i--){
        this.colorSensorValues[i] = this.colorSensorValues[i-1];
    }
    this.colorSensorValues[0] = sample * 1000;
    
    // Increment counter
    this.counter++;
//    if(this.counter = this.numberOfSamples){
//      this.counter = 0;
//      this.arrayFilled = true;
//    }
    
    // Compute moving average and derivative only if first n values have been measured
    if(this.counter >= this.numberOfSamples){ 
      // If first time moving average is computed
      if(this.lastMovingAverage == 0){
          this.lastMovingAverage = this.colorSensorValues[this.numberOfSamples-1];
      }

      // Calculate the moving average
      this.currentMovingAverage = this.lastMovingAverage + (this.colorSensorValues[0] - this.colorSensorValues[this.numberOfSamples-1])/this.numberOfSamples;
//      this.currentMovingAverage = this.lastMovingAverage 
//          + (this.colorSensorValues[(this.counter+1 == this.numberOfSamples ? 0 : this.counter+1)] 
//              - this.colorSensorValues[this.counter])/this.numberOfSamples;
      
      // Calculate poor man's derivative
      this.derivative = this.currentMovingAverage - this.lastMovingAverage;
      this.lastMovingAverage = this.currentMovingAverage;

      // Return true if line is detected
      if(this.derivative > this.threshold){
    	  	wasCrossed = true;
      }
    }
    
    return wasCrossed;
  }
}
