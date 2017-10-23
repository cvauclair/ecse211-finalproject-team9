package ca.mcgill.ecse211.finalproject;

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;

public class ObjectDetection implements TimerListener{
  private Odometer odometer;
  private SampleProvider usSensor;
  private float[] usData;
  
  public ObjectDetection(Odometer ocometer, SampleProvider usSensor, float[] usData){
    this.odometer = odometer;
    this.usSensor = usSensor;
    this.usData = usData;
  }
  
  public void timedOut(){
    usSensor.fetchSample(usData, 0);
    System.out.println(usData[0]*100 + ',' + odometer.getTheta());
  }
}
