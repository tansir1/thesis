package thesis.core.world;

import thesis.core.common.RoadNetwork;

public class World
{
   private Havens havens;

   private RoadNetwork roadNet;

   private WorldGIS worldGIS;

   public World()
   {
      worldGIS = new WorldGIS();
      roadNet = new RoadNetwork();
      havens = new Havens();
   }

   public Havens getHavens()
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
