package ca.mcgill.ecse211.sensor;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Created by Christophe Vauclair on 27/10/2017
 */


public class UltrasonicSensor extends Sensor{
  public UltrasonicSensor(String port, String mode) {
    super();
    this.sensor = new EV3UltrasonicSensor(LocalEV3.get().getPort(port));
    this.sampleProvider = this.sensor.getMode(mode);
    this.sensorData = new float[this.sampleProvider.sampleSize()];
  }
}
