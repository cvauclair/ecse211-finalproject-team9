package ca.mcgill.ecse211.sensor;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * Created by Christophe Vauclair on 27/10/2017
 */

public class Sensor {
  SensorModes sensor;
  SampleProvider sampleProvider;
  float[] sensorData;
  Object lock;
  
  /**
   * Constructor for sensor object that needs no parameters
   */
  public Sensor(){
    lock = new Object();
  }
  
  /**
   * Method to take a single measurement from sensor (thread safe)
   * @return float
   */
  public float getSample(){
    float sample;
    synchronized(lock){
      this.sampleProvider.fetchSample(this.sensorData, 0);
      sample = this.sensorData[0];
    }
    return sample;
  }

  /**
   * Method that takes n measurements and returns the average value
   * @param n int
   * @return float
   */
  public float getAverageSample(int n){
    float sum = 0;
    for(int i = 0; i < n; i++){
      sum += this.getSample();
    }
    return sum/n;
  }
}
