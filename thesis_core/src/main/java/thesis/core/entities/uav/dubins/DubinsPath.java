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
      for(int i=0; i<segmentLengths.length; ++i)
      {
         segmentLengths[i] = new Distance();
      }
   }
   
   public Distance getPathLength()
   {
      Distance len = new Distance();
      for(int i=0; i<segmentLengths.length; ++i)
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
}
