package thesis.core.entities.uav.dubins;

import thesis.core.common.WorldCoordinate;

public class PathSegment
{
   private WorldCoordinate start;
   private WorldCoordinate end;

   public PathSegment()
   {
      start = new WorldCoordinate();
      end = new WorldCoordinate();
   }

   public double pathLength()
   {
      return start.distanceTo(end);
   }

   public WorldCoordinate getStart()
   {
      return start;
   }

   public WorldCoordinate getEnd()
   {
      return end;
   }

}
