package thesis.core.common;

public class PathSegment
{
   private WorldCoordinate start;
   private WorldCoordinate end;

   public PathSegment()
   {
      start = new WorldCoordinate();
      end = new WorldCoordinate();
   }

   public Distance pathLength()
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
