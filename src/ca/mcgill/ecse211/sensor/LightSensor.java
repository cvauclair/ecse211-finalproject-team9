package ca.mcgill.ecse211.sensor;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */
/**
 * This class is used to make an instance of a light sensor. It extends the Sensor class
 *
 */
public class LightSensor extends Sensor{
  float[] lineDetectionValues;
  float movingAverage;
  float lastMovingAverage;
  float derivative;
  int counter;
  
  /**
   * Constructor that creates a light sensor object
   * @param port
   *             a String corresponding to the port used to connect the sensor
   * @param mode 	a string that specifies which mode will be used to read the values fetched from the sensor
   */
  public LightSensor(String port, String mode) {
    super();
    this.sensor = new EV3ColorSensor(LocalEV3.get().getPort(port));
    this.sampleProvider = this.sensor.getMode(mode);
    this.sensorData = new float[this.sampleProvider.sampleSize()];
  }
}
