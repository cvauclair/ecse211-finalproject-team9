package ca.mcgill.ecse211.sensor;

import lejos.utility.TimerListener;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */
/**
 * The line detector class is used to detect lines with the light sensor
 */
public class LineDetector implements TimerListener{
  private LightSensor colorSensor;
  private int threshold;
  private float[] colorSensorValues;
  private float currentMovingAverage;
  private float lastMovingAverage;
  private float derivative;
  private int numberOfSamples;
  private int counter;
  private boolean arrayFilled;
  private boolean lineDetected;
  private Object lock;
  
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
    this.lineDetected = true;
    this.lock = new Object();
  }
  
  /**
   * Method that checks if a line was crossed (note that this method will always return false the
   * first 'numberOfSamples' times it is called as the method will wait to get this amount of data
   * points to calculate the moving average)
   * @return wasCrossed		a boolean that is true if a line was crossed, false otherwise
   */
  public boolean checkLine(){
    return this.getLineDetected();
  }
  
  public boolean getLineDetected(){
    boolean tmp;
    synchronized(lock){
      tmp = this.lineDetected;
    }
    return tmp;
  }
  
  public void setLineDetected(boolean lineDetected){
    synchronized(lock){
      this.lineDetected = lineDetected;
    }
  }
}
