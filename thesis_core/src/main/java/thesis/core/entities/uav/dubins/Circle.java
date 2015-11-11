package thesis.core.entities.uav.dubins;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;

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

}
