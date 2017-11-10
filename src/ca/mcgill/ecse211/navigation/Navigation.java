package ca.mcgill.ecse211.navigation;

import java.util.LinkedList;
import java.util.List;
import java.awt.geom.Point2D;
/**
 * This class is used to keep track of the coordinates that have to be visited by the robot 
 */
public class Navigation {
  private static Driver driver;
  private static CollisionAvoidance collisionAvoidance;
  private List<Point2D.Double> points;
  private int counter;
  private boolean navigate = true;
  private Object lock;
  
  /**
   * This creates and instance of Navigation class. 
   * @param driver 				a Driver instance that is the robot's driver object
   * @param collisionAvoidance 	a CollisionAvoidance instance
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
   * @param x 	a float that is the x-coordinate
   * @param y 	a float that is the y-coordinate
   */
  public void addPoint(float x, float y){
    this.points.add(new Point2D.Double(x, y));
  }
  /**
   * Method that adds a point to the array of points that the robot has to navigate to
   * @param point 	a Point2D.Double that will be added to the current list of Point2D.Double
   */
  public void addPoint(Point2D.Double point){
    this.points.add(point);
  }
  
  /**
   * Method that set the counter that follows the points that have been navigated 
   * @param counter		an int that is a counter for points that have been navigated
   */
  public void setCounter(int counter){
    synchronized(lock){
      this.counter = counter;
    }
  }
  
  /** 
   * Method that return the counter that follows the points that have been navigated
   * @return tmp		an int that is a counter for points that have been navigated
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
   * Method that sets the Navigate boolean to true or false depending on whether the robot has finished navigating through the list of points
   * @param navigate 	a boolean that is true if robot is done navigating, false otherwise
   */
  public void setNavigate(boolean navigate){
    this.navigate = navigate;
  }
}
