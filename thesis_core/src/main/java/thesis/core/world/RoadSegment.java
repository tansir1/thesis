package thesis.core.world;

public class RoadSegment
{
   private WorldCoordinate start;
   private WorldCoordinate end;
   
   public RoadSegment()
   {
      start = new WorldCoordinate();
      end = new WorldCoordinate();
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
