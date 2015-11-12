package thesis.core.entities.uav.dubins;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class DubinsPath
{
   protected WorldPose start;
   protected WorldPose end;
   
   protected PathType type;
   //public double length;
   
   public WorldCoordinate waypoint1, waypoint2;
   
   protected Distance[] segmentLengths;
   
   public DubinsPath()
   {
      type = PathType.NO_PATH;
      start = new WorldPose();
      end = new WorldPose();
      waypoint1 = new WorldCoordinate();
      waypoint2 = new WorldCoordinate();
      //length = Double.POSITIVE_INFINITY;
      
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
}
