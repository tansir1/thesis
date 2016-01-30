package thesis.core.serialization.world;

import java.util.ArrayList;
import java.util.List;

import thesis.core.world.World;

/**
 * Container for all of the configuration parameters necessary to initialize a
 * world model.
 */
public class WorldConfig
{
   private World world;
   private List<UAVStartCfg> uavStartCfgs;
   private List<TargetStartCfg> tgtStartCfgs;

   public WorldConfig()
   {
      world = new World();
      uavStartCfgs = new ArrayList<UAVStartCfg>();
      tgtStartCfgs = new ArrayList<TargetStartCfg>();
   }

   public World getWorld()
   {
      return world;
   }

   public List<UAVStartCfg> getUAVCfgs()
   {
      return uavStartCfgs;
   }

   public List<TargetStartCfg> getTargetCfgs()
   {
      return tgtStartCfgs;
   }
}
