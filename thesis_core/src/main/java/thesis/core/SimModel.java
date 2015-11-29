package thesis.core;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.Distance;
import thesis.core.common.WorldPose;
import thesis.core.entities.TargetMgr;
import thesis.core.entities.uav.UAV;
import thesis.core.entities.uav.UAVMgr;
import thesis.core.serialization.entities.EntityTypes;
import thesis.core.serialization.world.WorldConfig;
import thesis.core.utilities.LoggerIDs;
import thesis.core.utilities.SimModelConfig;
import thesis.core.world.World;

public class SimModel
{
   private Logger logger;
   private World world;

   private TargetMgr tgtMgr;
   private UAVMgr uavMgr;

   private EntityTypes entTypes;
   private WorldConfig worldCfg;

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
    * @see #reset(SimModelConfig)
    */
   public SimModel()
   {
      logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      tgtMgr = new TargetMgr();
      uavMgr = new UAVMgr();
   }

   /**
    * Initialize the model with the necessary configuration parameters.
    *
    * @param randomSeed
    *           Initialize the random number generator with this value.
    * @param worldCfg
    *           Configuration data for the world.
    * @param entTypes
    *           The types of entities within the world.
    */
   public void reset(int randomSeed, WorldConfig worldCfg, EntityTypes entTypes, float commsRngPercent,
         float commsRelayProb)
   {
      randGen = new Random(randomSeed);

      this.entTypes = entTypes;
      this.worldCfg = worldCfg;

      logger.debug("EntityTypes initialized with:\n{}", entTypes);
      logger.debug("World model intiliazed with:\n{}", worldCfg);

      world = new World(worldCfg);
      tgtMgr.reset(entTypes, worldCfg, randGen);

      final Distance maxComsRng = worldCfg.getMaxWorldDistance();
      maxComsRng.scale(commsRngPercent);

      // FIXME Load/Derive the number of hops?
      uavMgr.reset(entTypes, worldCfg, 5, randGen, maxComsRng, commsRelayProb);

      // TEMPORARY! Initializes all UAVs with a pose to fly to for development
      // testing purposes.
      for (UAV uav : uavMgr.getAllUAVs())
      {
         WorldPose pose = new WorldPose();
         Distance north = new Distance();
         Distance east = new Distance();

         north.setAsMeters(randGen.nextDouble() * world.getWidth().asMeters());
         east.setAsMeters(randGen.nextDouble() * world.getHeight().asMeters());

         pose.getCoordinate().setCoordinate(north, east);
         pose.getHeading().setAsDegrees(randGen.nextInt(360));
         uav.TEMP_setDestination(pose);
      }
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

   public TargetMgr getTargetManager()
   {
      return tgtMgr;
   }

   public UAVMgr getUAVManager()
   {
      return uavMgr;
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

   /**
    * Step the simulation forward by one frame's worth of time.
    *
    * @param deltaTimeMS
    *           Advance the simulation forward by this many milliseconds.
    *
    * @see #SIM_STEP_RATE_MS
    */
   public void stepSimulation()
   {
      logger.trace("-------------------Simulation stepping.-----------------");
      tgtMgr.stepSimulation();
      uavMgr.stepSimulation();
   }

   /**
    *
    * @return
    */
   public WorldConfig getWorldConfig()
   {
      return worldCfg;
   }

   public EntityTypes getEntityTypes()
   {
      return entTypes;
   }
}
