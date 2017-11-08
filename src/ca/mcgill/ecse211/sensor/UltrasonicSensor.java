package ca.mcgill.ecse211.sensor;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/*
 * Created by Christophe Vauclair on 27/10/2017
 */

/**
 * 
 *This class is used to make an instance of a Ultrasonic sensor. It extends the Sensor class
 */
public class UltrasonicSensor extends Sensor{
	
  /**
   * Constructor that creates a Ultrasonic sensor object 
   * @param port is a String corresponding to the port used to connect the sensor
   * @param mode is a string that specifies which mode will be used to read the values fetched from the sensor
   */
  public UltrasonicSensor(String port, String mode) {
    super();
    this.sensor = new EV3UltrasonicSensor(LocalEV3.get().getPort(port));
    this.sampleProvider = this.sensor.getMode(mode);
    this.sensorData = new float[this.sampleProvider.sampleSize()];
  }
}
