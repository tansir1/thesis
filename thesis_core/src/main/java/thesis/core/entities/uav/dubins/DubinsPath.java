package thesis.core.entities.uav.dubins;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class DubinsPath
{
   private WorldPose start;
   private WorldPose end;

   protected PathType type;

   private WorldCoordinate waypoint1, waypoint2;

   protected Distance[] segmentLengths;

   public DubinsPath()
   {
      type = PathType.NO_PATH;
      start = new WorldPose();
      end = new WorldPose();
      waypoint1 = new WorldCoordinate();
      waypoint2 = new WorldCoordinate();

      segmentLengths = new Distance[3];
      for (int i = 0; i < segmentLengths.length; ++i)
      {
         segmentLengths[i] = new Distance();
      }
   }

   public Distance getPathLength()
   {
      Distance len = new Distance();
      for (int i = 0; i < segmentLengths.length; ++i)
      {
         len.add(segmentLengths[i]);
      }
      return len;
   }

   public WorldPose getStartPose()
   {
      return start;
   }

   public WorldPose getEndPose()
   {
      return end;
   }

   public PathType getPathType()
   {
      return type;
   }

   public WorldCoordinate getWaypoint1()
   {
      return waypoint1;
   }

   public WorldCoordinate getWaypoint2()
   {
      return waypoint2;
   }

   /**
    * Get the length of segment of the path.
    * 
    * @param phase
    * @return
    */
   public Distance getSegmentLength(PathPhase phase)
   {
      Distance len = null;
      switch (phase)
      {
      case Phase1:
         len = segmentLengths[0];
         break;
      case Phase2:
         len = segmentLengths[1];
         break;
      case Phase3:
         len = segmentLengths[2];
         break;
      }
      return len;
   }   
   
   /**
    * Get the waypoint corresponding to the specified path phase.
    * 
    * @param phase
    * @return
    */
   public WorldCoordinate getWaypoint(PathPhase phase)
   {
      WorldCoordinate wypt = null;
      switch (phase)
      {
      case Phase1:
         wypt = waypoint1;
         break;
      case Phase2:
         wypt = waypoint2;
         break;
      case Phase3:
         wypt = end.getCoordinate();
         break;
      }
      return wypt;
   }
}
