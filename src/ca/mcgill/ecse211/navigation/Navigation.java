package ca.mcgill.ecse211.navigation;

import java.util.LinkedList;
import java.util.List;
import java.awt.geom.Point2D;

public class Navigation {
  private static Driver driver;
  private static CollisionAvoidance collisionAvoidance;
  private List<Point2D.Double> points;
  private int counter;
  private boolean navigate = true;
  private Object lock;
  
  public Navigation(Driver driver, CollisionAvoidance collisionAvoidance){
    this.driver = driver;
    this.collisionAvoidance = collisionAvoidance;
    this.collisionAvoidance.setNavigation(this);
    this.points = new LinkedList<Point2D.Double>();
    this.counter = 0;
    this.lock = new Object();
  }
  
  public void navigate(){
    while(this.getCounter() < this.points.size() && this.navigate){
      this.driver.travelTo(points.get(this.getCounter()).getX(),points.get(this.getCounter()).getY());
      this.decrementCounter();
    }
  }
  
  public void addPoint(float x, float y){
    this.points.add(new Point2D.Double(x, y));
  }
  
  public void addPoint(Point2D.Double point){
    this.points.add(point);
  }
  
  public void setCounter(int counter){
    synchronized(lock){
      this.counter = counter;
    }
  }
  
  public int getCounter(){
    int tmp;
    synchronized(lock){
      tmp = this.counter;
    }
    return tmp;
  }
  
  public void incrementCounter(){
    synchronized(lock){
      this.counter++;
    }
  }
  
  public void decrementCounter(){
    synchronized(lock){
      this.counter--;
    }
  }
  
  public void setNavigate(boolean navigate){
    this.navigate = navigate;
  }
}
