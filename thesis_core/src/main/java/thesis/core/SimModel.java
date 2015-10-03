package thesis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.world.World;

public class SimModel
{
   private Logger logger;
   private World world;

   /**
    * Create an uninitialized world.
    * 
    * The world must be initialized after being constructed.
    * 
    * @see #init(SimModelConfig)
    */
   public SimModel()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
   }

   /**
    * Initialize the model with the necessary configuration parameters.
    * 
    * @param cfg
    *           Configuration values will be read from this object.
    */
   public void init(SimModelConfig cfg)
   {
      world = new World(cfg.getWorldWidth(), cfg.getWorldHeight(), cfg.getNumWorldRows(), cfg.getNumWorldCols());
      logger.debug("Sim model initialized with:\n{}", cfg);
   }

   /**
    * Get the world map submodel.
    * 
    * @return The world map of the simulation.
    */
   public World getWorld()
   {
      return world;
   }
}
