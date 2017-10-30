package ca.mcgill.ecse211.objectdetection;

import ca.mcgill.ecse211.odometry.Odometer;
import ca.mcgill.ecse211.sensor.UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

/**
 * Created by Christophe Vauclair on 23/10/2017
 */

public class ObjectDetection implements TimerListener{
  private Odometer odometer;
  private UltrasonicSensor usSensor;
  
  public ObjectDetection(Odometer odometer, UltrasonicSensor usSensor){
    this.odometer = odometer;
    this.usSensor = usSensor;
  }
  
  public void timedOut(){
    System.out.println(this.usSensor.getSample()*100 + ',' + odometer.getTheta());
  }
}
