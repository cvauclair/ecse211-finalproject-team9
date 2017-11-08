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
  
  /**
   * This creates and instance of Navigation class. 
   * @param driver Driver object
   * @param collisionAvoidance CollisionAvoidance object
   */
  public Navigation(Driver driver, CollisionAvoidance collisionAvoidance){
    this.driver = driver;
    this.collisionAvoidance = collisionAvoidance;
    this.collisionAvoidance.setNavigation(this);
    this.points = new LinkedList<Point2D.Double>();
    this.counter = 0;
    this.lock = new Object();
  }
  /**
   * Method that starts the navigation sequence
   */
  public void navigate(){
    while(this.getCounter() < this.points.size() && this.navigate){
      this.driver.travelTo(points.get(this.getCounter()).getX(),points.get(this.getCounter()).getY());
      this.decrementCounter();
    }
  }
  
  /**
   * Method that adds a point to the array of points that the robot has to navigate to
   * @param x float
   * @param y float
   */
  public void addPoint(float x, float y){
    this.points.add(new Point2D.Double(x, y));
  }
  /**
   * Method that adds a point to the array of points that the robot has to navigate to
   * @param point List<Point2D.Double>
   */
  public void addPoint(Point2D.Double point){
    this.points.add(point);
  }
  
  /**
   * Method that set the counter that follows the points that have been navigated 
   * @param counter int
   */
  public void setCounter(int counter){
    synchronized(lock){
      this.counter = counter;
    }
  }
  
  /** 
   * Method that return the counter that follows the points that have been navigated
   * @return
   */
  public int getCounter(){
    int tmp;
    synchronized(lock){
      tmp = this.counter;
    }
    return tmp;
  }
  /**
   * Method that increments the counter when it is called
   */
  public void incrementCounter(){
    synchronized(lock){
      this.counter++;
    }
  }
  /**
   * Method that decrements the counter when it is called
   */
  public void decrementCounter(){
    synchronized(lock){
      this.counter--;
    }
  }
  /**
   * Method that sets the Navigate boolean to true or false depending on wether the robot has finished navigating through the list of points
   * @param navigate boolean
   */
  public void setNavigate(boolean navigate){
    this.navigate = navigate;
  }
}
