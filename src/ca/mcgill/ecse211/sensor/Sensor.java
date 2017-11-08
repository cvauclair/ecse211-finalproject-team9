package ca.mcgill.ecse211.sensor;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */
/**
 * This class creates an instance of a Lejos Sensor
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
   * @return sample 		a float which is a single measurement
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
   * @param n 		an int which is the number of measurement to average from
   * @return avg		a float which is the average of the n measurements
   */
  public float getAverageSample(int n){
    float sum = 0;
    for(int i = 0; i < n; i++){
      sum += this.getSample();
    }
    float avg = sum/n;
    return avg;
  }
}
