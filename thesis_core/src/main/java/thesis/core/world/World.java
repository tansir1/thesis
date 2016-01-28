package thesis.core.world;

import java.util.ArrayList;
import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.common.RoadNetwork;

public class World
{
   /**
    * The locations of all safe havens for targets.
    */
   private List<CellCoordinate> havens;

   private RoadNetwork roadNet;

   private WorldGIS worldGIS;

   public World()
   {
      worldGIS = new WorldGIS();
      roadNet = new RoadNetwork();
      havens = new ArrayList<CellCoordinate>();
   }

   /**
    * Get the location of all safe havens in the world.
    *
    * @return The location of each haven.
    */
   public List<CellCoordinate> getHavenLocations()
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
