package thesis.core.entities.uav.dubins;

import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class DubinsPath
{
   public WorldPose start;
   public WorldPose end;
   
   public PathType type;
   public double length;
   
   public WorldCoordinate waypoint1, waypoint2;
   
   public double[] segmentLengths;
   
   public DubinsPath()
   {
      type = PathType.NO_PATH;
      start = new WorldPose();
      end = new WorldPose();
      waypoint1 = new WorldCoordinate();
      waypoint2 = new WorldCoordinate();
      length = Double.POSITIVE_INFINITY;
      segmentLengths = new double[3];
   }
}
