package thesis.core.common;

public class Circle
{
   private WorldCoordinate center;
   /**
    * Meters.
    */
   private double radius;

   public Circle()
   {
      center = new WorldCoordinate();
      radius = 0;
   }

   public WorldCoordinate getCenter()
   {
      return center;
   }

   /**
    * @return Meters
    */
   public double getRadius()
   {
      return radius;
   }

   /**
    * @param radius Meters
    */
   public void setRadius(double radius)
   {
      this.radius = radius;
   }

   @Override
   public String toString()
   {
      return String.format("%s, %.2fm", center.toString(), radius);
   }
}
