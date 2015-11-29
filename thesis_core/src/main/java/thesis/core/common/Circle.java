package thesis.core.common;

public class Circle
{
   private WorldCoordinate center;
   private Distance radius;

   public Circle()
   {
      center = new WorldCoordinate();
      radius = new Distance();
   }

   public WorldCoordinate getCenter()
   {
      return center;
   }

   public Distance getRadius()
   {
      return radius;
   }

   @Override
   public String toString()
   {
      return center.toString() + ", " + radius.toString();
   }
}
