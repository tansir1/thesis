package thesis.core.world;

import java.util.List;

import thesis.core.common.RoadNetwork;
import thesis.core.common.WorldCoordinate;
import thesis.core.serialization.world.WorldConfig;

public class World
{
   /**
    * The locations of all safe havens for targets.
    */
   private List<WorldCoordinate> havens;

   private RoadNetwork roadNet;

   private WorldGIS worldGIS;

   /**
    *
    * @param cfg
    *           Configuration data describing the world.
    */
   public World(WorldConfig cfg)
   {
      if (cfg == null)
      {
         throw new NullPointerException("World configuration data cannot be null.");
      }

      worldGIS = new WorldGIS(cfg.getWorldWidth(), cfg.getWorldHeight(), cfg.getNumRows(), cfg.getNumColumns());

      roadNet = cfg.getRoadNetwork();

      havens = cfg.getHavens();
   }

   /**
    * Get the location of all safe havens in the world.
    *
    * @return The location of each haven.
    */
   public List<WorldCoordinate> getHavenLocations()
   {
      return havens;
   }

   public RoadNetwork getRoadNetwork()
   {
      return roadNet;
   }

   public WorldGIS getWorldGIS()
   {
      return worldGIS;
   }

}
