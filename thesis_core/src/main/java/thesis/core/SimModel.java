package thesis.core;

import java.util.Random;

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
    * A shared random number generator that is initialized with a known seed
    * value for reproducible experiments.
    */
   private Random randGen;

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
      randGen = new Random(cfg.getRandomSeed());

      world = new World(cfg.getWorldWidth(), cfg.getWorldHeight(), cfg.getNumWorldRows(), cfg.getNumWorldCols(),
            randGen);

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

   /**
    * Get the shared random number generator.
    * 
    * @return The simulation's random number generator.
    */
   public Random getRandomGenerator()
   {
      return randGen;
   }
}
