package thesis.core.serialization;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.world.RoadSegment;

/**
 *Container for all of the configuration parameters necessary to initialize a world model.
 */
public class WorldConfig
{
   public Distance width;
   public Distance height;

   public int numRows;
   public int numColums;

   public int randSeed;
   
   public List<RoadSegment> roadSegments;

   public List<WorldCoordinate> havens;
   
   public List<TargetEntityConfig> targetCfgs;
   
   public List<UAVEntityConfig> uavCfgs;
   
   public WorldConfig()
   {
      width = new Distance();
      height = new Distance();
      
      roadSegments = new ArrayList<RoadSegment>();
      havens = new ArrayList<WorldCoordinate>();
      targetCfgs = new ArrayList<TargetEntityConfig>();
      uavCfgs = new ArrayList<UAVEntityConfig>();
   }
}
